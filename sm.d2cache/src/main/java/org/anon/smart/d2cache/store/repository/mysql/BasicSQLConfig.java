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
 * File:                org.anon.smart.d2cache.store.repository.mysql.BasicSQLConfig
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                23-09-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A default configuration for mysql
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.mysql;

public class BasicSQLConfig implements MySQLConfig
{
    private String _name;
    private String _server;
    private int _port;
    private String _user;
    private String _password;
    private String _db;

    public BasicSQLConfig(String name, String svr, int p, String u, String pass, String db)
    {
        _name = name;
        _server = svr;
        _port = p;
        _user = u;
        _password = pass;
        _db = db;
    }

    public String getName()
    {
        return _name;
    }

    public String getDatabase()
    {
        return _db;
    }

    public String getServer()
    {
        return _server;
    }

    public int getPort()
    {
        return _port;
    }

    public String getUser()
    {
        return _user;
    }

    public String getPassword()
    {
        return _password;
    }
}

