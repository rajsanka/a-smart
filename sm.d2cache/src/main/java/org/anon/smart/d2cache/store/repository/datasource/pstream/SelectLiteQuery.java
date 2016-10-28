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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.SelectLiteQuery
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A query builder for lite objects
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.pstream.DataReader.QueryData;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class SelectLiteQuery<T extends CacheableObject> extends SelectQuery<T>
{
    public class ParentConditions extends LiteConditions
    {
        private Conditions _from;

        ParentConditions(SelectLiteQuery lt, Conditions from)
        {
            super(lt);
            _from = from;
        }

        @Override
        public Condition get(String col)
            throws CtxException
        {
            SelectLiteQuery lt = (SelectLiteQuery)_select;
            Condition cond = new Condition(col, _from, lt._parentMetadata.table(), lt._parentMetadata);
            _from._conditions.add(cond);
            return cond;
        }
    }

    public class LiteConditions extends Conditions
    {
        LiteConditions(SelectLiteQuery lt)
        {
            super(lt);
        }

        public ParentConditions parent()
            throws CtxException
        {
            //TODO: This only goes one level
            SelectLiteQuery lite = (SelectLiteQuery)_select;
            lite._useLinkSQL = true;
            ParentConditions conds = new ParentConditions(lite, this);
            return conds;
        }

    }

    private Class _parentInterface;
    private Class _parentClass;
    private DataMetadata _parentMetadata;
    private AttributeMetadata _relatedAttribute;
    private Object _related;
    private boolean _useLinkSQL = false;

    public SelectLiteQuery(Class<T> datacls, Class parentcls, String attribute, Object relate, PersistableManager mgr) 
        throws CtxException
    {
        //asumption here is that the reference to the lite is present in the parent and not the other
        //way round.
        super(datacls, mgr);
        _related = relate;
        _parentClass = parentcls;

        _parentMetadata = PersistableData.metadataFor(_parentClass);
        _relatedAttribute = _parentMetadata.metadataFor(attribute);
        assertion().assertNotNull(_relatedAttribute, this, INVALID_ATTRIBUTE, "Cannot find attribute: " + attribute + " in " + parentcls);
    }

    @Override
    public Conditions where()
        throws CtxException
    {
        //by default add the condition for the parent object.
        LiteConditions conds = new LiteConditions(this);
        conds.get(_relatedAttribute.referenceColumnName()).eq(_related);
        return conds;
    }

    @Override
    PersistableDataStream<T> search(Conditions conds)
        throws CtxException
    {
        String sql = _meta.selectSQL();
        String add = " WHERE ";
        if (_useLinkSQL)
        {
            sql = _relatedAttribute.linkSQL().sql();
            add = " AND ";
        }

        String where = conds.getSQLFragment();
        sql += add + where;

        QueryData<T> query = new QueryData<T>(_myType, sql);
        //for now the assumption is that we load all the sub objects
        //of the lite.
        AttributeMetadata[] subattrs = _meta.subAttributes();
        for (int i = 0; (subattrs != null) && (i < subattrs.length); i++)
            createQuery(query, subattrs[i], where);

        return _stream.search(query, conds.paramValues());
    }
}

