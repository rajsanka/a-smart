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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.SelectQuery
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A query object to select from table or a set of tables
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream;

import java.util.List;
import java.util.ArrayList;

import org.anon.smart.d2cache.CacheableObject;

import org.anon.smart.d2cache.store.repository.datasource.DSErrorCodes;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.pstream.DataReader.QueryData;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class SelectQuery<T extends CacheableObject> implements DSErrorCodes
{
    public class Condition
    {
        Conditions belongTo;
        String attr;
        String colName;
        String oper;
        Object value;
        String table;

        String sqlFragment;

        Condition(String col, Conditions belong, String tbl, DataMetadata meta)
            throws CtxException
        {
            belongTo = belong;
            attr = col;
            table = tbl;
            AttributeMetadata attribute = meta.metadataFor(col);
            assertion().assertNotNull(attribute, this, INVALID_ATTRIBUTE, "The attribute is not present");
            colName = attribute.columnName();
            sqlFragment = null;
        }

        Condition(Conditions belong, String sql)
        {
            belongTo = belong;
            sqlFragment = sql;
        }

        private void setValue(Object val)
        {
            value = val;
            belongTo.addValue(val);
        }

        public Conditions eq(Object val)
        {
            oper = " = ";
            setValue(val);
            return belongTo;
        }

        public Conditions like(String val)
        {
            oper = " LIKE ";
            val = val.replaceAll("\\*", "\\%");
            setValue(val);
            return belongTo;
        }

        public Conditions gt(Object val)
        {
            oper = " > ";
            setValue(val);
            return belongTo;
        }

        public Conditions lt(Object val)
        {
            oper = " < ";
            setValue(val);
            return belongTo;
        }

        String getSQLFragment()
        {
            if (sqlFragment == null)
                sqlFragment = table + "." + colName + oper + " ? ";
            return sqlFragment;
        }

        Object getValue() { return value; }
    }

    /*
    public class BasicConditions extends Conditions<BasicConditions>
    {
        BasicConditions(SelectQuery q)
        {
            super(q);
        }
    }
    */

    public class Conditions
    {
        //currently all are and
        protected List<Condition> _conditions;
        protected List<Object> _values;
        protected SelectQuery _select;

        Conditions(SelectQuery q)
        {
            _conditions = new ArrayList<Condition>();
            _values = new ArrayList<Object>();
            _select = q;
        }

        void addValue(Object val)
        {
            _values.add(val);
        }

        public Condition get(String col)
            throws CtxException
        {
            Condition cond = new Condition(col, this, _select._meta.table(), _select._meta);
            _conditions.add(cond);
            return cond;
        }

        public String getSQLFragment()
        {
            String ret = "";
            String connect = "";
            for (int i = 0; i < _conditions.size(); i++)
            {
                ret += connect + _conditions.get(i).getSQLFragment();
                connect = " AND ";
            }

            return ret;
        }

        public Conditions sort(String col, boolean asc)
        {
            _select._order = col;
            _select._ascending = asc;
            return this;
        }

        public Conditions retrievePage(int page, int ps)
        {
            int start = (page - 1) * ps;
            _select._start = start;
            _select._count = ps;
            return this;
        }

        public PersistableDataStream<T> search()
            throws CtxException
        {
            return _select.search(this);
        }

        public Object[] paramValues()
        {
            return _values.toArray();
        }
    }

    protected Class<T> _myInterface;
    protected Class<T> _myType;
    protected DataMetadata _meta;
    protected PersistableManager _stream;
    protected boolean _rootOnly;
    protected String _order;
    protected boolean _ascending;
    protected int _start;
    protected int _count;

    public SelectQuery(Class<T> cls, PersistableManager str) 
        throws CtxException
    {
        _myType = cls;
        _meta = PersistableData.metadataFor(_myType);
        _stream = str;
    }

    public SelectQuery(Class<T> cls, PersistableManager str, boolean root) 
        throws CtxException
    {
        this(cls, str);
        _rootOnly = root;
    }

    public Conditions where()
        throws CtxException
    {
        Conditions ret = (new Conditions(this));
        return ret;
    }

    protected void createQuery(QueryData parent, AttributeMetadata subattr, String where)
    {
        AttributeMetadata rel = subattr.referencedAttribute();
        String sql = subattr.linkSQL().sql();
        sql += " AND " + where + " ORDER BY " + rel.columnName() ;

        QueryData q = parent.addLink(subattr.attributeName(), rel.attributeField(), subattr.attributeType(), sql);

        AttributeMetadata[] sub = subattr.associatedMetadata().subAttributes();
        for (int i = 0; (sub != null) && (i < sub.length); i++)
            createQuery(q, sub[i], where);
    }

    PersistableDataStream<T> search(Conditions conds)
        throws CtxException
    {
        String sql = _meta.selectSQL();
        String where = conds.getSQLFragment();
        sql += " WHERE " + where;
        if ((_order != null) && (_order.length() > 0))
        {
            sql += " ORDER BY " + _order;
            if (!_ascending) sql += " DESC ";
        }

        if (_count > 0)
        {
            //this is very MySQL specific.
            sql += " LIMIT " + _start + ", " + _count;
        }

        QueryData<T> query = new QueryData<T>(_myType, sql);
        if (!_rootOnly)
        {
            AttributeMetadata[] subattrs = _meta.subAttributes();
            for (int i = 0; (subattrs != null) && (i < subattrs.length); i++)
                createQuery(query, subattrs[i], where);
        }

        return _stream.search(query, conds.paramValues());
    }

    T lookup(Object key)
        throws CtxException
    {
        //this will ignore all other conditions
        String where = _meta.table() + "." + _meta.keySQLFragment();
        String sql = _meta.lookupSQL();

        QueryData<T> query = new QueryData<T>(_myType, sql);

        AttributeMetadata[] subattrs = _meta.subAttributes();
        for (int i = 0; (subattrs != null) && (i < subattrs.length); i++)
            createQuery(query, subattrs[i], where);

        return _stream.lookup(query, key);
    }

    int count(Conditions conds)
        throws CtxException
    {
        //TODO: 
        return 0;
    }
}

