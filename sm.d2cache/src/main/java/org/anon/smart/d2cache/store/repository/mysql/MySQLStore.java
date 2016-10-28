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
 * File:                org.anon.smart.d2cache.store.repository.mysql.MySQLStore
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A store for mysql
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.mysql;

import java.util.List;

import org.anon.smart.d2cache.store.Store;
import org.anon.smart.d2cache.store.AbstractStore;
import org.anon.smart.d2cache.store.StoreConnection;
import org.anon.utilities.exception.CtxException;
import org.anon.utilities.utils.Repeatable;
import org.anon.utilities.utils.RepeaterVariants;

public class MySQLStore extends AbstractStore
{
    public MySQLStore(StoreConnection conn)
    {
        super(conn);
    }

	@Override
	public void create(String name, Class cls) throws CtxException {
		// TODO Auto-generated method stub
        System.out.println("Calling create: " + name + ":" + cls);

	}

	@Override
	public Repeatable repeatMe(RepeaterVariants parms) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Store.SearchResult> search(String group, Object query, int size, int pn, int ps, String sby, boolean asc) throws CtxException {
		return getConnection().search(group, query, size, pn, ps, sby, asc);
	}

    public int searchOrder()
    {
        return 0;
    }

    public boolean searchHasOnlyKeys()
    {
        return false;
    }
}

