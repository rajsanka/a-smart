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
 * File:                org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata
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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;

import org.anon.utilities.exception.CtxException;
import org.anon.utilities.reflect.ClassTraversal;

import static org.anon.utilities.services.ServiceLocator.*;

public class LookupHandler<T extends CacheableObject> implements ResultSetHandler<T>
{
    private Class _dataClass;
    private DataMetadata _metadata;

    public LookupHandler(Class<T> datacls) 
        throws CtxException
    {
        _dataClass = datacls;
        _metadata = PersistableData.metadataFor(datacls);
    }

    public String lookupSQL()
    {
        return _metadata.lookupSQL();
    }

    @Override
    public T handle(ResultSet rs) 
        throws SQLException
    {
        try
        {
            if (!rs.next())
                return null;

            ResultToObjectVisitor handler = new ResultToObjectVisitor(rs, _metadata);
            ClassTraversal traversal = new ClassTraversal(_dataClass, handler);
            Object ret = traversal.traverse();
            T data = (T)ret;
            return data;
        }
        catch (Exception e)
        {
            throw new SQLException(e);
        }
    }
}

