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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A persistence manager that can be used to read data
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.DataSchema;
import org.anon.smart.d2cache.store.repository.datasource.DSErrorCodes;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.pstream.DataReader.QueryData;
import org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.DBUtilsReader;
import org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.DBUtilsWriter;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager.ConnectionPoolEntity;

import org.anon.utilities.exception.CtxException;
import org.anon.utilities.utils.RepeaterVariants;
import org.anon.utilities.exception.StandardExceptionHandler;

import static org.anon.utilities.services.ServiceLocator.*;

public class PersistableManager implements AutoCloseable, DSErrorCodes
{
    public static String _globalName = "";
    public static String _dbName = "";

    @FunctionalInterface
    public static interface RepeatParams extends RepeaterVariants
    {
        public Object paramFor(int index);
    }

    private static enum dbaccessimpl
    {
        //so we can change this easily if reqd
        dbutils(new DBUtilsReader(), new DBUtilsWriter());

        private DataReader _reader;
        private DataWriter _writer;

        private dbaccessimpl(DataReader rdr, DataWriter write)
        {
            _reader = rdr;
            _writer = write;
        }

        public DataReader getReader(ConnectionPoolEntity cpe)
            throws CtxException
        {
            return (DataReader)_reader.repeatMe(new RepeatParams() {
                public Object paramFor(int ind) { return cpe; }
            });
        }

        public DataWriter getWriter(ConnectionPoolEntity cpe)
            throws CtxException
        {
            return (DataWriter)_writer.repeatMe(new RepeatParams() {
                public Object paramFor(int ind) { return cpe; }
            });
        }

        public DataReader getReader(String dbname)
            throws CtxException
        {
            return (DataReader)_reader.repeatMe(new RepeatParams() { 
                public Object paramFor(int ind) { return dbname; }
            });
        }

        public DataWriter getWriter(String dbname)
            throws CtxException
        {
            return (DataWriter)_writer.repeatMe(new RepeatParams() {
                public Object paramFor(int ind) { return dbname; }
            });
        }
    }

    private static final dbaccessimpl CURRENTIMPL = dbaccessimpl.dbutils;

    private static class CacheObjectKey
    {
        private Class _dataclazz;
        private Object _key;

        CacheObjectKey(Object obj, Object k)
        {
            _dataclazz = obj.getClass(); 
            _key = k;
        }

        CacheObjectKey(Class cls, Object k)
        {
            _dataclazz = cls;
            _key = k;
        }

        public int hashCode()
        {
            int hash = _dataclazz.hashCode() + 
                (31 * _key.hashCode());
            return hash;
        }

        public boolean equals(Object obj)
        {
            boolean ret = false;
            if (obj instanceof CacheObjectKey)
            {
                CacheObjectKey k = (CacheObjectKey)obj;
                ret = k._dataclazz.equals(_dataclazz);
                ret = ret && k._key.equals(_key);
            }

            return ret;
        }

        public String toString()
        {
            return _dataclazz + ":" + _key;
        }
    }

    public static class CacheObjectData
    {
        private CacheableObject _object;
        private CacheableObject _original;
        private boolean _isNew;
        private Object[] _keys;

        private CacheObjectData(CacheableObject obj, CacheableObject orig, boolean isnew, Object[] keys)
        {
            _object = obj;
            _original = orig;
            _isNew = isnew;
            _keys = keys;
        }

        private CacheObjectData(CacheableObject obj, CacheableObject orig, boolean isnew)
        {
            _object = obj;
            _original = orig;
            _isNew = isnew;
        }

        public CacheableObject getObject() { return _object; }
        public CacheableObject getOriginal() { return _original; }
        public boolean isNew() { return _isNew; }
        public void resetNew() { _isNew = false; }
        public Object[] getKeys() { return _keys; }
    }

    private Map<CacheObjectKey, CacheObjectData> _currentObjects;
    private DataReader _streamReader;
    private DataWriter _streamWriter;
    private String _gname;

    public PersistableManager(String dbname, String gname) 
        throws CtxException
    {
        this(dbname, CURRENTIMPL, gname);
    }

    protected PersistableManager(String dbname, dbaccessimpl impl, String gname)
        throws CtxException
    {
        _currentObjects = new ConcurrentHashMap<CacheObjectKey, CacheObjectData>();
        _streamReader = impl.getReader(dbname);
        _streamWriter = impl.getWriter(dbname);
        _gname = gname;
        _globalName = gname;
        _dbName = dbname;
    }

    public PersistableManager(ConnectionPoolEntity cpe)
        throws CtxException
    {
        _currentObjects = new ConcurrentHashMap<CacheObjectKey, CacheObjectData>();
        _streamReader = CURRENTIMPL.getReader(cpe);
        _streamWriter = CURRENTIMPL.getWriter(cpe);
    }

    public <U extends CacheableObject> PersistableManager append(U data, U orig, boolean isnew, Object[] keys)
        throws CtxException
    {
        //Object[] keys = data.smart___keys();
        assertion().assertNotNull(keys, this, INVALID_DATA, "Please provide atleast one key");
        assertion().assertTrue((keys.length > 0), this, INVALID_DATA, "Please provide atleast one key");
        CacheObjectKey put = new CacheObjectKey(data, keys[0]);

        CacheObjectData d = new CacheObjectData(data, orig, isnew, keys);
        _currentObjects.put(put, d);
        return this;
    }

    private Class getCollectionClass(Field fld)
        throws CtxException
    {
        Class ret = null;
        Type t = fld.getGenericType();
        if (t instanceof ParameterizedType)
        {
            ParameterizedType pt = (ParameterizedType)t;
            int len = pt.getActualTypeArguments().length;
            ret = (Class)pt.getActualTypeArguments()[len - 1];
        }

        return ret;
    }

    private CacheableObject[] openupData(Object obj, Field fld)
        throws CtxException
    {
        Set<CacheableObject> set = new HashSet<CacheableObject>();
        if (obj instanceof CacheableObject)
        {
            set.add((CacheableObject)obj);
        }
        else if (obj instanceof Collection)
        {
            Collection coll = (Collection)obj;
            assertion().assertNotNull(fld, this, INVALID_DATA, "The data has to be part of an attribute since it is a collection." + obj + ":" + fld);
            Class collcls = getCollectionClass(fld);
            assertion().assertTrue((type().isAssignable(collcls, CacheableObject.class)), this, INVALID_DATA, "The data is not a persistable data." + fld);
            set.addAll(coll);
        }
        else if (obj instanceof Map)
        {
            Map m = (Map)obj;
            assertion().assertNotNull(fld, this, INVALID_DATA, "The data has to be part of an attribute since it is a collection." + obj + ":" + fld);
            Class collcls = getCollectionClass(fld);
            assertion().assertTrue((type().isAssignable(collcls, CacheableObject.class)), this, INVALID_DATA, "The data is not a persistable data." + fld);
            set.addAll(m.values());
        }
        else if (obj.getClass().isArray())
        {
            Object[] array = (Object[])obj;
            for (int i = 0; i < array.length; i++)
            {
                set.add((CacheableObject)array[i]);
            }
        }
        else
        {
            assertion().assertTrue(false, this, INVALID_DATA, "The data type cannot be recognized for persistence." + obj + ":" + fld);
        }

        return set.toArray(new CacheableObject[0]);
    }

    private void addObjectTo(Object obj, Field ofld, Set<CacheableObject> insert, Set<CacheObjectData> update, Object orig, boolean isnew)
        throws CtxException
    {
        //add this object first
        try
        {
            CacheableObject[] add = openupData(obj, ofld);
            for (int i = 0; (add != null) && (i < add.length); i++)
            {
                CacheableObject d = add[i];
                if (d != null) createTable(d.getClass());
                if (isnew) { 
                    insert.add(d); 
                } else {
                    CacheObjectData cd = new CacheObjectData((CacheableObject)d, (CacheableObject)orig, isnew);
                    update.add(cd);
                }
                //add the sub objects into the list so that they also get stored.
                DataMetadata meta = PersistableData.metadata(d);
                AttributeMetadata[] flds = meta.subAttributes();
                for (int j = 0; (flds != null) && (j < flds.length); j++)
                {
                    Field fld = flds[j].attributeField();
                    fld.setAccessible(true);
                    Object sub = fld.get(d);
                    Object suborig = null;
                    if (orig != null) suborig = fld.get(orig);
                    if (sub != null) addObjectTo(sub, fld, insert, update, suborig, isnew);
                }
            }
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("PersistableManager", "addObjectTo"));
        }
    }

    public UpdateFuture[] flush()
        throws CtxException
    {
        Set<CacheableObject> insert = ConcurrentHashMap.newKeySet();
        Set<CacheObjectData> update = ConcurrentHashMap.newKeySet();
        StandardExceptionHandler handler = new StandardExceptionHandler(this, INVALID_UPDATE);
        _currentObjects.values().parallelStream().forEach( (CacheObjectData d) ->
            {
                try
                {
                    System.out.println("Object: " + insert + ":" + update + ":" + d.getOriginal());
                    addObjectTo(d.getObject(), null, insert, update, d.getOriginal(), d.isNew());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    handler.handleException(e);
                }
            });

        handler.hasException();
        UpdateFuture future = _streamWriter.insertAll(insert);
        UpdateFuture ufuture = _streamWriter.updateAll(update);
        System.out.println("Updating managers: " + insert + ":" + update);
        return new UpdateFuture[] { future, ufuture };
    }

    public void createTable(String exists, String sql)
        throws CtxException
    {
        _streamWriter.createNotExists(exists, sql);
    }

    public void clearAll()
    {
        _currentObjects.clear();
    }

    public <U extends CacheableObject> U lookup(Class<U> datacls, Object key)
        throws CtxException
    {
        createTable(datacls);
        return select(datacls)
            .lookup(key);
        //return _streamReader.lookup(datacls, key);
    }

    public <U extends CacheableObject> U lookup(QueryData<U> query, Object key)
        throws CtxException
    {
        PersistableDataStream<U> stream = _streamReader.queryDB(query, new Object[] { key });
        return stream.readNextData();
    }

    public <U extends CacheableObject> U lookup(String group, Object key)
        throws CtxException
    {
        Class<? extends CacheableObject> datacls = DataSchema.getClassFor(group);
        System.out.println("Got class as: " + datacls + ":" + group);
        /*if (datacls == null)
            return null; //not registered and cannot find the class.
            */

        return (U) lookup(datacls, key);
    }

    public <U extends CacheableObject> SelectQuery<U> select(Class<U> cls)
        throws CtxException
    {
        return new SelectQuery<U>(cls, this);
    }

    public <U extends CacheableObject> SelectQuery<U> select(String group)
        throws CtxException
    {
        Class<? extends CacheableObject> cls = DataSchema.getClassFor(group);
        return new SelectQuery<U>((Class<U>)cls, this);
    }

    public <U extends CacheableObject> SelectQuery<U> selectRoot(Class<U> cls)
        throws CtxException
    {
        return new SelectQuery<U>(cls, this, true);
    }

    public <U extends CacheableObject> SelectLiteQuery<U> selectLite(Class parent, String attribute, Object val)
        throws CtxException
    {
        DataMetadata pmeta = PersistableData.metadataFor(parent);
        AttributeMetadata meta = pmeta.metadataFor(attribute);
        assertion().assertNotNull(meta, this, INVALID_ATTRIBUTE, "Cannot find the attribute for: " + attribute + " in " + parent);
        return new SelectLiteQuery(meta.associatedMetadata().clazz(), parent, attribute, val, this);
    }

    public <U extends CacheableObject> PersistableManager done(U data, Object[] keys)
        throws CtxException
    {
        //Object[] keys = data.smart___keys();
        assertion().assertNotNull(keys, this, INVALID_DATA, "Please provide atleast one key");
        assertion().assertTrue((keys.length > 0), this, INVALID_DATA, "Please provide atleast one key");
        CacheObjectKey put = new CacheObjectKey(data, keys[0]);
        _currentObjects.remove(put);
        return this;
    }

    public <U extends CacheableObject> U get(Class<U> datacls, Object key)
        throws CtxException
    {
        CacheObjectKey put = new CacheObjectKey(datacls, key);
        return (U) _currentObjects.get(put).getObject();
    }

    public <U extends CacheableObject> PersistableDataStream<U> search(QueryData<U> query, Object[] parms)
        throws CtxException
    {
        //assumption is that all the sqls are of linked sqls and hence have the same parameters.
        createTablesFor(query);
        return _streamReader.queryDB(query, parms);
    }

    private <U extends CacheableObject> void createTablesFor(QueryData<U> query)
        throws CtxException
    {
        createTable(query.dataClass());
        Set<QueryData> lnks = query.links();
        for (QueryData l : lnks)
        {
            createTablesFor(l);
        }
    }

    public <U extends CacheableObject> Object retrieveKey(U val)
        throws CtxException
    {
        return PersistableData.metadata(val).keyValueFor(val);
    }

    public <U extends CacheableObject> void createTable(Class<U> datacls)
        throws CtxException
    {
        DataMetadata pmeta = PersistableData.metadataFor(datacls);
        pmeta.createTable(_gname, _dbName);
    }

    public void close()
    {
    }
}

