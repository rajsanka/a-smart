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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.DBUtilsHandler
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A db handler for connections 
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils;

import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager.ConnectionPoolEntity;

import org.anon.utilities.exception.CtxException;

public class DBUtilsHandler
{
    protected String _dbName;
    protected ConnectionPoolEntity _connection;
    protected boolean _release;

    protected DBUtilsHandler()
    {
    }

    protected DBUtilsHandler(String dbname) 
    {
        _dbName = dbname;
        _release = true;
    }

    protected DBUtilsHandler(ConnectionPoolEntity conn)
    {
        _connection = conn;
        _release = false;
    }

    protected ConnectionPoolEntity getCPE()
        throws CtxException
    {
        ConnectionPoolEntity cpe = _connection;
        if (cpe == null)
            cpe = ResourceManager.connectionFor(_dbName);
        return cpe;
    }

    protected void releaseCPE(ConnectionPoolEntity cpe)
        throws CtxException
    {
        if (_release)
            cpe.pool().unlockone(cpe);
    }
}

