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
 * File:                org.anon.smart.d2cache.StoreOnlyCache
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                24-09-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A cache with just a repository store
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache;

import org.anon.smart.d2cache.segment.CSegment;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;

public class StoreOnlyCache extends AbstractD2Cache
{
	public StoreOnlyCache(String name, int flags) 
        throws CtxException 
    {
		this(name, flags, null);
	}

	public StoreOnlyCache(String name, int flags, D2CacheConfig config)
        throws CtxException 
    {
		super(name, flags, config);
		
		_segments = new CSegment[1];
        _segments[0] = createStoreSegment();
	}


	@Override
	public void cleanupMemory() throws CtxException {
		// TODO Auto-generated method stub
        for (int i = 0; i < _segments.length; i++)
            _segments[i].cleanup();

        _segments = null;
	}

}

