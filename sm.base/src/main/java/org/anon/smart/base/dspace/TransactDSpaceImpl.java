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
 * File:                org.anon.smart.base.dspace.TransactDSpaceImpl
 * Author:              rsankar
 * Revision:            1.0
 * Date:                15-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A default transact space used by tenants
 *
 * ************************************************************
 * */

package org.anon.smart.base.dspace;

import org.anon.smart.d2cache.D2CacheConfig;
import org.anon.smart.d2cache.D2CacheScheme;
import org.anon.smart.d2cache.DataFilter;

import org.anon.utilities.exception.CtxException;

public class TransactDSpaceImpl extends AbstractDSpace
{
    public TransactDSpaceImpl(String name, String filetype, D2CacheConfig cfg)
        throws CtxException
    {
        super(name, filetype, cfg);
    }

     public TransactDSpaceImpl(String name, DataFilter[] filters, String filetype, D2CacheConfig cfg)
        throws CtxException
    {
        super(name, filters, filetype, cfg);
    }


    protected D2CacheScheme.scheme getCacheScheme()
    {
	    //return D2CacheScheme.scheme.memstoreind;
	    return D2CacheScheme.scheme.memmysqlind;
    }
    
    protected D2CacheScheme.scheme getFileCacheScheme()
    {
        return D2CacheScheme.scheme.filestore;
    }

    protected D2CacheScheme.scheme getNonCacheScheme()
    {
        return D2CacheScheme.scheme.storemysql;
    }
}

