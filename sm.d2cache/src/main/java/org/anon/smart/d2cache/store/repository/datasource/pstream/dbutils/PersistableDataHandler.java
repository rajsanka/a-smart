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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.PersistableDataHandler
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration file for attributes of tables
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils;

import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.reflect.Field;

import org.apache.commons.dbutils.ResultSetHandler;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.pstream.EOS;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream;

import org.anon.utilities.exception.CtxException;
import org.anon.utilities.reflect.ClassTraversal;

import static org.anon.utilities.services.ServiceLocator.*;

public class PersistableDataHandler<T extends CacheableObject> implements ResultSetHandler<Integer>
{
    private DBUtilsReaderStream<T> _streamer;
    private Class<T> _dataClass;
    private DataMetadata _metadata;
    private Map<String, PersistableDataStream> _subStreams;

    public PersistableDataHandler(Class<T> datacls) 
        throws CtxException
    {
        _streamer = new DBUtilsReaderStream<T>(datacls);
        _streamer.setHandler(this);
        _dataClass = datacls;
        _metadata = PersistableData.metadataFor(datacls);
    }

    public PersistableDataHandler(Class<T> datacls, Map<String, PersistableDataStream> linked, Field mapped)
        throws CtxException
    {
        this(datacls);
        _subStreams = linked;
        _streamer.mapAttribute(mapped);
    }

    @Override
    public Integer handle(ResultSet rs) 
        throws SQLException
    {
        int count = 0;
        String except = null;
        try
        {
            while (rs.next())
            {
                System.out.println("Reading row.");
                ResultToObjectVisitor handler = new ResultToObjectVisitor(rs, _metadata, _subStreams);
                ClassTraversal traversal = new ClassTraversal(_dataClass, handler);
                T data = _dataClass.cast(traversal.traverse());
                //data.smart___resetNew(); //these objects are not new.
                System.out.println("PersistableDataHandler: writing read data : " + data);
                _streamer.writeData(data);
                count++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            except = e.getMessage();
            throw new SQLException(e);
        }
        finally
        {
            //write out even if there is an exception, so that
            //the reader will finish
            EOS eos = new EOS(count, except);
            try
            {
                _streamer.writeData(eos);
            }
            catch (Exception sqe)
            {
                throw new SQLException(sqe);
            }
        }
        System.out.println("PersistableDataHandler: Read: " + count);
        return count;
    }

    public DBUtilsReaderStream<T> getStream() { return _streamer; }
}

