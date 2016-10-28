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

package org.anon.smart.d2cache.store.repository.datasource.pstream.cacheutils;

import java.util.Collection;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.pstream.DataWriter;
import org.anon.smart.d2cache.store.repository.datasource.pstream.UpdateFuture;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager.CacheObjectData;

import org.anon.utilities.exception.CtxException;

public abstract class CacheWriter implements DataWriter
{
    public CacheWriter() 
    {
    }

    public <T extends CacheableObject> int update(T obj)
        throws CtxException
    {
        return 0;
    }

    public <T extends CacheableObject> int insert(T obj)
        throws CtxException
    {
        return 0;
    }

    public <T extends CacheableObject> UpdateFuture insertAll(Collection<T> objs)
        throws CtxException
    {
        return null;
    }

    public <T extends CacheableObject> UpdateFuture updateAll(Collection<CacheObjectData> objs)
        throws CtxException
    {
        return null;
    }

    public <T extends CacheableObject> void flush(PersistableDataStream<T> stream)
        throws CtxException
    {
    }
}

