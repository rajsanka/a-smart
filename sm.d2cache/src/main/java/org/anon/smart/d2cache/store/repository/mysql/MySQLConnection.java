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
 * File:                org.anon.smart.d2cache.store.repository.mysql.MySQLConnection
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A connection to be established to mysql
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.mysql;

import java.util.Map;
import java.util.Iterator;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.ListParams;
import org.anon.smart.d2cache.QueryObject;
import org.anon.smart.d2cache.QueryObject.QueryItem;
import org.anon.smart.d2cache.store.Store;
import org.anon.smart.d2cache.store.StoreConfig;
import org.anon.smart.d2cache.store.StoreConnection;
import org.anon.smart.d2cache.store.StoreTransaction;

import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager;
import org.anon.smart.d2cache.store.repository.datasource.pstream.SelectQuery;
import org.anon.smart.d2cache.store.repository.datasource.pstream.SelectQuery.Conditions;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceConfig;

import org.anon.utilities.utils.Repeatable;
import org.anon.utilities.utils.RepeaterVariants;
import org.anon.utilities.exception.CtxException;
import org.anon.utilities.exception.StandardExceptionHandler;

import static org.anon.utilities.services.ServiceLocator.*;

public class MySQLConnection implements StoreConnection
{
    private PersistableManager _manager;
    private String _dbName;
    private StoreConfig _config;

    //overriding map on the global map?
    private Map<String, Class<? extends CacheableObject>> _groupMap;

    public MySQLConnection()
    {
    }

	public Repeatable repeatMe(RepeaterVariants vars) throws CtxException {
		return new MySQLConnection();
	}

    public void connect(StoreConfig cfg)
        throws CtxException
    {
        _config = cfg;
        _groupMap = new ConcurrentHashMap<String, Class<? extends CacheableObject>>();
        if (cfg instanceof MySQLConfig) 
        {
            MySQLConfig mycfg = (MySQLConfig)cfg;
            _dbName = mycfg.getName();
            if (!ResourceManager.isRegistered(_dbName))
            {
                ResourceConfig rcfg = new ResourceConfig(_dbName, mycfg.getURI(), mycfg.getUser(), mycfg.getPassword(), mycfg.getDatabase(), mycfg.getDriver());
                ResourceManager.registerConnectionPool(rcfg);
            }
        }
        else
        {
            except().te(this, "Need MySQLConfig to connect");
        }
    }

    public void open(String name)
        throws CtxException
    {
        _manager = new PersistableManager(_dbName, name);
    }

    public void createMetadata(String name, Class cls)
        throws CtxException
    {
        _manager.createTable((Class<? extends CacheableObject>)cls);
    }

    public StoreTransaction startTransaction(UUID txn)
        throws CtxException
    {
        _manager.clearAll();
        return new MySQLTransaction(txn, this, _manager);
    }

    public void close()
        throws CtxException
    {
        _manager.close();
    }

    /* Data Access */
    public Object find(String group, Object key)
        throws CtxException
    {
        Class<? extends CacheableObject> datacls = _groupMap.get(group);
        System.out.println("Looking up in: " + _manager + ":" + datacls + ":" + key);
        if (datacls != null)
        {
            Object ret = _manager.lookup(datacls, key);
            System.out.println("Looked up in: " + _manager + ":" + datacls + ":" + key + ":" + ret);
            return ret;
        }

        return _manager.lookup(group, key);
    }

    private static final String WILD_CHAR = "*";

    public List<Store.SearchResult> search(String group, Object query, int size, int pn, int ps, String sby, boolean asc)
        throws CtxException
    {
        //TODO:
        Class<? extends CacheableObject> datacls = _groupMap.get(group);
        SelectQuery q = _manager.select(group);
        if (datacls != null)
            q = _manager.select(datacls);

		QueryObject qo = (QueryObject)query;
        Conditions conds = q.where();
        List<QueryItem> items = qo.getQuery();

        for (QueryItem item : items)
        {
            if (item.value().contains(WILD_CHAR))
            {
                conds = conds.get(item.attribute()).like(item.value());
            }
            else
            {
                String oper = item.operator();
                conds = conds.get(item.attribute()).add(oper, item.objectValue());
            }
        }

        List<Store.SearchResult> ret = new ArrayList<Store.SearchResult>();

        int page = 1;
        int pagesz = size;
        if (pn >= 0) 
        {
            page = pn;
            pagesz = ps;
        }

        StandardExceptionHandler handler = new StandardExceptionHandler(this, 0x90000010);
        conds.sort(sby, asc)
            .retrievePage(page, pagesz)
            .search()
            .stream()
            .forEach((Object o) -> {
                try
                {
                    Store.SearchResult res = new Store.SearchResult(_manager.retrieveKey((CacheableObject)o), o);
                    ret.add(res);
                }
                catch (Exception e)
                {
                    handler.handleException(e);
                }
            });

        handler.hasException();

        return ret;
    }
    
    public void remove(String group, Object key)
    	throws CtxException
    {
        //cannot remove? Should we inactivate
    }

    public boolean exists(String group, Object key)
        throws CtxException
    {
        Object obj = find(group, key);
        return (obj != null);
    }

    public Iterator<Object> getListings(String group, String sortBy,
            int listingsPerPage, int pageNum)
       throws CtxException
   {
       return null;
   }

    public Iterator<Object> list(ListParams parms)
        throws CtxException
    {
        return null;
    }

    public void registerMetadata(String group, Class<? extends CacheableObject> datacls)
        throws CtxException
    {
        _groupMap.put(group, datacls);
    }
}

