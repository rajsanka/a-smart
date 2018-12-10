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
 * File:                org.anon.smart.d2cache.store.repository.hbase.RelatedObject
 * Author:              vjaasti
 * Revision:            1.0
 * Date:                May 6, 2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * <Purpose>
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.hbase;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.utilities.exception.CtxException;

public class RelatedObject implements java.io.Serializable, CacheableObject {

	private String _relatedKey;
	
	public RelatedObject()
	{
		
	}
	
	public RelatedObject(Object relatedKey)
	{
		_relatedKey = ""+relatedKey;
	}
	
	public String getRelatedKey()
	{
		return _relatedKey;
	}

	@Override
	public void smart___initOnLoad() throws CtxException {
		// TODO Auto-generated method stub
		
	}

    public boolean smart___isNew() {
        return false;
    }
}
