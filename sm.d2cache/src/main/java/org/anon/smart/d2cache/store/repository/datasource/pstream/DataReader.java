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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.DataReader
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

package org.anon.smart.d2cache.store.repository.datasource.pstream;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Field;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.CompiledSQL;

import org.anon.utilities.exception.CtxException;
import org.anon.utilities.utils.Repeatable;

public interface DataReader extends Repeatable
{
    public static class QueryData<T extends CacheableObject>
    {
        private Field _mappedAttribute;
        private String _attribute;
        private String _sql;
        private Class<T> _datacls;
        private Set<QueryData> _linked;
        private CompiledSQL parent;

        public QueryData(Class<T> cls, String q)
        {
            _datacls = cls;
            _sql = q;
            _linked = ConcurrentHashMap.newKeySet();
            _attribute = "root";
        }

        public QueryData(String attr, Class<T> cls, String q)
        {
            this(cls, q);
            _attribute = attr;
        }

        public <R extends CacheableObject> QueryData<R> addLink(String attr, Field mattr, Class<R> cls, String sql)
        {
            QueryData<R> q = new QueryData<R>(attr, cls, sql);
            q._mappedAttribute = mattr;
            _linked.add(q);
            return q;
        }

        public void setParent(CompiledSQL p)  { parent = p; }
        public CompiledSQL getParent() { return parent; }
        public Set<QueryData> links() { return _linked; }
        public Class<T> dataClass() { return _datacls; }
        public String sql() { return _sql; }
        public String attribute() { return _attribute; }
        public Field mappedAttribute() { return _mappedAttribute; }
    }

    public <T extends CacheableObject> T lookup(Class<T> datacls, Object key)
        throws CtxException;

    public <T extends CacheableObject> T lookup(QueryData<T> query, Object key)
        throws CtxException;

    public <T extends CacheableObject> PersistableDataStream<T> queryDB(Class<T> datacls, String sql, Object ... params)
        throws CtxException;

    public <T extends CacheableObject> PersistableDataStream<T> queryDB(QueryData<T> query, Object ... params)
        throws CtxException;
}

