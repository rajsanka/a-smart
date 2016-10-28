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
 * File:                org.anon.smart.d2cache.store.AbstractStore
 * Author:              rsankar
 * Revision:            1.0
 * Date:                02-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An abstract implementation of the store
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store;

import java.util.List;

import static org.anon.utilities.services.ServiceLocator.*;
import org.anon.utilities.exception.CtxException;

public abstract class AbstractStore implements Store
{
    private String _name;
    protected StoreConfig _config;
    protected transient StoreConnection _connection;
    

    public AbstractStore(StoreConnection conn)
    {
        _connection = conn;
    }

    public void setup(String name, StoreConfig cfg)
        throws CtxException
    {
        try
        {
            _name = name;
            _config = cfg;
            assertion().assertNotNull(_connection, "The connection object is null");
            _connection.connect(cfg);
            _connection.open(name);
        }
        catch (Exception e)
        {
            except().rt(e, new CtxException.Context("AbstractStore.setup", "Exception"));
        }
    }

    public StoreConnection getConnection() { return _connection; }

    public void close()
        throws CtxException
    {
        _connection.close();
    }

    public int searchOrder()
    {
        return Integer.MAX_VALUE;
    }

    public boolean searchHasOnlyKeys()
    {
        return true;
    }

    public List<Store.SearchResult> search(String group, Object query, int size, int pn, int ps, String sby, boolean asc)
        throws CtxException
    {
        //not supported.
        return null;
    }
}

