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
 * File:                org.anon.smart.d2cache.store.repository.datasource.DataSchema
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

package org.anon.smart.d2cache.store.repository.datasource;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.metadata.DefaultMetadata;

import static org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager.*;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

//We want this to be a singleton for each tenant not for the whole application??
public class DataSchema implements DSErrorCodes
{
    private static final DataSchema SCHEMA = new DataSchema();

    private Map<Class<? extends CacheableObject>, DataMetadata> _schema;
    private Map<String, Class<? extends CacheableObject>> _groupMap;
    private String _name;

    private DataSchema() 
    {
        _schema = new ConcurrentHashMap<Class<? extends CacheableObject>, DataMetadata>();
        _groupMap = new ConcurrentHashMap<String, Class<? extends CacheableObject>>();

    }

    private DataMetadata getMetadata(Class<? extends CacheableObject> data, String gname, String dbname)
        throws CtxException
    {
        //assertion().assertTrue(_schema.containsKey(data), this, "Please use the correct classloader so this is populated." + data);
        if ((!data.isInterface()) && type().isAssignable(data, CacheableObject.class) && !_schema.containsKey(data))
        {
            DefaultMetadata.registerMetadata(data, gname, dbname);
        }
        DataMetadata meta = _schema.get(data);
        if (meta != null)
            return meta.replicateMe(gname);

        return meta;
    }

    public static boolean checkExists(Class<? extends CacheableObject> data)
        throws CtxException
    {
        return (SCHEMA._schema.containsKey(data));
    }

    public static void registerFor(Class<? extends CacheableObject> data, DataMetadata meta)
        throws CtxException
    {
        SCHEMA._schema.put(data, meta);
    }

    public static void registerFor(String group, Class<? extends CacheableObject> data, DataMetadata meta)
        throws CtxException
    {
        SCHEMA._schema.put(data, meta);
        SCHEMA._groupMap.put(group, data);
        System.out.println("Adding metadata for: " + group + ":" + data + ":" + meta + ":" + SCHEMA._schema.get(data));
    }

    public static Class<? extends CacheableObject> getClassFor(String group)
    {
        return SCHEMA._groupMap.get(group);
    }

    public static DataMetadata metadataFor(Class<? extends CacheableObject> data, String gname, String dbname)
        throws CtxException
    {
        return SCHEMA.getMetadata(data, gname, dbname);
    }

    public static void createStack(Class cls, Stack<Class> run, Map<Class, Class[]> ret)
        throws CtxException
    {
        DataMetadata meta = SCHEMA.getMetadata(cls, globalName(), dbName());
        Class[] rel = meta.related();
        boolean add = !(ret.containsKey(cls));
        ret.put(cls, rel);
        if (rel != null) 
        {
            for (int i = 0; i < rel.length; i++)
            {
                createStack(rel[i], run, ret);
            }
        }

        if (add) run.push(cls);
    }

    public static Stack<Class> sequence(Set<Class> updates)
        throws CtxException
    {
        Map<Class, Class[]> ret = new HashMap<Class, Class[]>();
        Stack<Class> run = new Stack<Class>();
        for (Class cls : updates)
        {
            if (!ret.containsKey(cls)) 
                createStack(cls, run, ret);
        }

        return run;
    }
}

