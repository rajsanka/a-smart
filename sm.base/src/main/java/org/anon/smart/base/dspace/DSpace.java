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
 * File:                org.anon.smart.base.dspace.DSpace
 * Author:              rsankar
 * Revision:            1.0
 * Date:                04-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A data space where data is stored
 *
 * ************************************************************
 * */

package org.anon.smart.base.dspace;

import java.util.List;

import org.anon.smart.d2cache.Reader;
import org.anon.smart.d2cache.D2Cache;
import org.anon.smart.d2cache.DataFilter;

import org.anon.utilities.exception.CtxException;

public interface DSpace
{
    public Reader myReader()
        throws CtxException;

    public Reader myReader(boolean inMemory)
            throws CtxException;

    public Reader getBrowsableReader()
            throws CtxException;

    public Reader nonCachedReader()
        throws CtxException;

    public D2Cache cacheImpl();

    public void cleanup()
        throws CtxException;
    
    public D2Cache fsCacheImpl() 
    		throws CtxException;

    public String name();
}

