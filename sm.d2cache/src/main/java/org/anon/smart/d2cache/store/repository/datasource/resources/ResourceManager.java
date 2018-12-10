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
 * File:                org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * Manages the database connections
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.resources;

import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentHashMap;

import org.anon.smart.d2cache.store.repository.datasource.DSErrorCodes;
import org.anon.smart.d2cache.store.repository.datasource.resources.threads.PThreadExecutor;

import org.anon.utilities.pool.Pool;
import org.anon.utilities.pool.EntityCreator;
import org.anon.utilities.pool.EntityPool;
import org.anon.utilities.pool.PoolEntity;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class ResourceManager implements DSErrorCodes
{
    private static ResourceManager MANAGER = null;
    
    static
    {
        try
        {
            MANAGER = new ResourceManager();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static class ConnectionPoolEntity implements PoolEntity
    {
        private PoolEntity _next;
        private Pool _pool;
        private Connection _connection;
        private ResourceConfig _config;
        private boolean _isConnected;

        public ConnectionPoolEntity(ResourceConfig cfg)
        {
            _config = (ResourceConfig)cfg;
            _isConnected = false;
        }

        public void connect()
            throws CtxException
        {
            getConnection();
        }

        public boolean isConnected()
        {
            return _isConnected;
        }

        public Connection getConnection()
            throws CtxException
        {
            try
            {
                if (!_isConnected)
                {
                    Class.forName(_config.getDriver());
                    _connection = DriverManager.getConnection(_config.getURI(), _config.getUser(), _config.getPwd());
                    _connection.setAutoCommit(false);
                    _isConnected = true;
                }

                return _connection;
            }
            catch (Exception e)
            {
                except().rt(e, this, new CtxException.Context("ResourceManager", "getConnection"));
            }

            return null;
        }

        public void closeConnection()
        {
            try
            {
                if (_isConnected && (_connection != null))
                {
                    _isConnected = false;
                    _connection.close();
                    _connection = null;
                }
            }
            catch (Exception e)
            {
                //except().rt(e, this, new CtxException.Context("ResourceManager", "getConnection"));
                e.printStackTrace();
            }
        }

        public PoolEntity nextEntity() { return _next; }
        public void setNextEntity(PoolEntity entity) { _next = entity; }
        public Pool pool() { return _pool; }
        public void storePool(Pool p) { _pool = p; }
    }

    static class ConnectionCreator implements EntityCreator
    {
        private ResourceConfig _config;

        ConnectionCreator(ResourceConfig cfg)
        {
            _config = cfg;
        }

        public PoolEntity newEntity()
            throws CtxException
        {
            return new ConnectionPoolEntity(_config);
        }
    }

    private ExecutorService _dbExecutionThreadPool = new PThreadExecutor("persistence");
    private Map<String, Pool> _connectionPools = new ConcurrentHashMap<String, Pool>();

    private ResourceManager() 
        throws CtxException
    {
        //TODO: need to find how to get default configured resources.
    }

    public static void registerConnectionPool(ResourceConfig cfg)
        throws CtxException
    {
        Pool connectionPool = new EntityPool(new ConnectionCreator(cfg));
        MANAGER._connectionPools.put(cfg.getName(), connectionPool);
    }

    public static boolean isRegistered(String nm)
    {
        Pool pool = MANAGER._connectionPools.get(nm);
        return (pool != null);
    }

    public static ConnectionPoolEntity connectionFor(String nm)
        throws CtxException
    {
        Pool pool = MANAGER._connectionPools.get(nm);
        assertion().assertNotNull(pool, MANAGER, INVALID_DBNAME, "Please register a pool before using it.");
        ConnectionPoolEntity ret = (ConnectionPoolEntity)pool.lockone();
        ret.storePool(pool);
        return ret;
    }

    public static ExecutorService executionPool()
        throws CtxException
    {
        //do this to limit the number of threads going to the db.
        return MANAGER._dbExecutionThreadPool;
    }
}

