/**
 * SMART - State Machine ARchiTecture
 *
 * Copyright (C) 2012 Individual contributors as indicated by
 * the @authors tag
 *
 * This file is a part of SMART.
 *
 * SMART is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SMART is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * */
 
/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A data stream reader for data from the datasource
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream;

import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Field;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.DSErrorCodes;

import org.anon.utilities.exception.CtxException;
import org.anon.utilities.exception.StandardExceptionHandler;

import static org.anon.utilities.services.ServiceLocator.*;

public class PersistableDataStream<T extends CacheableObject> implements DSErrorCodes
{
    private Class<T> _datacls;
    private Queue<CacheableObject> _queue;
    private List<CacheableObject> _readData;

    private Field _mapByAttribute;
    private Map<Object, Set<CacheableObject>> _mappedData;

    //map the attribute to the stream
    private Map<String, PersistableDataStream> _subStreams;

    private boolean _writtenEnd;
    private boolean _readEOS;

    private CtxException _writeException;

    public PersistableDataStream(Class<T> cls) 
        throws CtxException
    {
        try
        {
            _datacls = cls;
            _queue = new ConcurrentLinkedQueue<CacheableObject>();
            _readData = new ArrayList<CacheableObject>();
            _writtenEnd = false;
            _readEOS = false;
            _subStreams = new ConcurrentHashMap<String, PersistableDataStream>();

            //Note here though only the writer thread is writing into this object,
            //the reader thread is also parallely reading it, hence we need concurrency
            //as opposed to the _readData attribute which is only being written or read from
            //always.
            _mappedData = new ConcurrentHashMap<Object, Set<CacheableObject>>();
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("PersistableDataStream", "constructor"));
        }
    }

    public void mapAttribute(Field attr) { _mapByAttribute = attr; }

    public void addSubStream(String attr, PersistableDataStream stream) { _subStreams.put(attr, stream); }
    public Map<String, PersistableDataStream> subStreams() { return _subStreams; }

    public void writeData(CacheableObject data)
        throws CtxException
    {
        try
        {
            assertion().assertTrue(!_writtenEnd, this, END_REACHED, "Cannot write after end written");
            assertion().assertTrue(((data.getClass().equals(_datacls)) || (data instanceof EOS)), this, INVALID_DATA, "Cannot write data of this type.");
            _queue.offer(data);
            _writtenEnd = (data instanceof EOS);
            if (!(data instanceof EOS))
            {
                _readData.add(data);
                if (_mapByAttribute != null)
                {
                    _mapByAttribute.setAccessible(true);
                    Object val = _mapByAttribute.get(data);
                    Set<CacheableObject> vals = _mappedData.get(val);
                    if (vals == null)
                    {
                        //has to be concurrent so we can handle parallely
                        vals = ConcurrentHashMap.newKeySet();
                    }

                    vals.add(data);
                    _mappedData.put(val, vals);
                }
            }
        }
        catch (Exception e)
        {
            try
            {
                except().rt(e, this, new CtxException.Context("PersistableDataStream", "writeData"));
            }
            catch (CtxException we)
            {
                _writeException = we;
                throw we;
            }
        }
    }

    public T readNextData()
        throws CtxException
    {
        return readNextData(500); //timeout after 500ms.
    }

    public T readNextData(int timeout)
        throws CtxException
    {
        //propagate the write exception to the reader also?
        if (_writeException != null)
            throw _writeException;

        try
        {
            CacheableObject pdata = _queue.poll();
            //currently it is polled, have to change to see if we can block
            int waited = 0;
            while ((pdata == null) && (!_readEOS) && (waited < timeout))
            {
                Thread.currentThread().sleep(100); //poll 100 ms till we get it or timeout?
                waited += 100;
                pdata = _queue.poll();
            }

            if (((pdata == null) && (_writtenEnd)) || (pdata instanceof EOS))
            {
                //if data read is null and end is not written, then it means we are timing out?
                //if data read is null and we have written end means we have an empty stream. So close out.
                closeStream();
                _readEOS = true;
                if (pdata != null)
                {
                    EOS eos = (EOS)pdata;
                    if ((eos.getException() != null) && (eos.getException().length() > 0))
                    {
                        except().te(this, "There has been an exception in retrieving:" + eos.getException(), new CtxException.Context("PersistableDataStream", "readNextData"));
                    }
                }
                return null;
            }

            //we are currently initializing here. If there is multiple read will this initialize multiple??
            if (pdata != null) pdata.smart___initOnLoad();
            return (T)pdata;
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("PersistableDataStream", "readNextData"));
        }

        return null;
    }

    public Set readRelatedData(Object related)
        throws CtxException
    {
        try
        {
            while ((!_readEOS) && (!_mappedData.containsKey(related)))
            {
                //Note: It is possible that we have to read the whole sub stream
                //before the current relation is got, shd we do this not sure. For now 
                //the logic is this. TODO: change later.
                readNextData();
                Thread.currentThread().sleep(100); //yield.
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            except().rt(e, this, new CtxException.Context("PersistableDataStream", "readRelatedData"));
        }

        return _mappedData.get(related);
    }

    public void closeStream()
        throws CtxException
    {
    }

    public Iterator<T> iterator()
    {
        //means that the whole data set is already readand it is a re-read
        if (_readEOS)
        {
            return (Iterator<T>)_readData.iterator();
        }

        return new PersistableDataStreamIterator<T>(this);
    }

    public Stream<T> stream()
    {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator(), Spliterator.CONCURRENT), false);
    }

    public class PersistableDataStreamIterator<U extends CacheableObject> implements Iterator<U>
    {
        //we will try to forward read the current object into the iterator
        //so that we can find if we have a next object. The hasNext call will
        //block to retrieve. The next call then returns immediately returning 
        //this current object.
        private U _currentObject;
        private PersistableDataStream<U> _stream;
        private StandardExceptionHandler _handler;

        PersistableDataStreamIterator(PersistableDataStream<U> stream)
        {
            _stream = stream;
            _handler = new StandardExceptionHandler(this, ERROR_READING);
        }

        public boolean hasNext()
        {
            try
            {
                _currentObject = _stream.readNextData();
                return (_currentObject != null);
            }
            catch (Exception e)
            {
                _currentObject = null;
                _handler.handleException(e);
            }

            return false;
        }

        public U next()
            throws NoSuchElementException
        {
            try
            {
                _handler.hasException();
                return _currentObject;
            }
            catch (Exception e)
            {
                throw new NoSuchElementException(e.getMessage());
            }
        }
    }

}

