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
 * File:                org.anon.smart.d2cache.store.repository.datasource.metadata.DefaultMetadata
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                27-09-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A default metadata for classes who are not explicitly mapped
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.metadata;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.DataSchema;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager;

import org.anon.utilities.exception.CtxException;

public class DefaultMetadata
{
    public static interface ParameterFinder
    {
        public String getGroup(Class<? extends CacheableObject> datacls);
        public String getTableName(String gname, Class<? extends CacheableObject> datacls);
    }

    public static ParameterFinder FINDER = null;

    private DefaultMetadata()
    {
    }

    public static String getGroupFor(Class<? extends CacheableObject> datacls)
    {
        String group = datacls.getSimpleName();
        if (FINDER != null)
            group = FINDER.getGroup(datacls);
        return group;
    }

    public static String getTableNameFor(String gname, Class<? extends CacheableObject> datacls)
    {
        String tbl = null;
        if (FINDER != null)
            tbl = FINDER.getTableName(gname, datacls);
        return tbl;
    }

    public static synchronized boolean createTable(String existsql, String createsql, String gname, String dbname)
        throws CtxException
    {
        if ((existsql != null) && (existsql.length() > 0) && (createsql != null) && (createsql.length() > 0))
        {
            try (PersistableManager mgr = new PersistableManager(dbname, gname))
            {
                mgr.createTable(existsql, createsql);
            }
        }
        return true;
    }

    public static synchronized void registerMetadata(Class<? extends CacheableObject> datacls, String gname, String dbname)
        throws CtxException
    {
        if (!DataSchema.checkExists(datacls))
        {
            MetadataTraverse data = MetadataTraverse.createMetadata(gname, datacls);
            DataMetadata meta = data.getMetadata();
            String group = getGroupFor(datacls);
            DataSchema.registerFor(group, datacls, meta);
        }
    }
}

