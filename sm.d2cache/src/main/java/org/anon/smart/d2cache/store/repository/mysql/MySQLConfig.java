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
 * File:                org.anon.smart.d2cache.store.repository.mysql.MySQLConfig
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration file for mysql
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.mysql;

import org.anon.smart.d2cache.store.StoreConfig;

public interface MySQLConfig extends StoreConfig
{
    public String getName();
    public String getDatabase();
    public String getServer();
    public int getPort();
    public String getUser();
    public String getPassword();

    default String getURI()
    {
        return "jdbc:mysql://" + getServer() + ":" + getPort() + "/" + getDatabase();
    }

    default String getDriver()
    {
        return "com.mysql.jdbc.Driver";
    }
}

