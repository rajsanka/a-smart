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
 * File:                org.anon.smart.kernel.config.RepositoryConfig
 * Author:              rsankar
 * Revision:            1.0
 * Date:                21-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration for repository
 *
 * ************************************************************
 * */

package org.anon.smart.kernel.config;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;

import org.anon.utilities.verify.VerifiableObject;
import org.anon.utilities.exception.CtxException;

public class RepositoryConfig implements VerifiableObject, java.io.Serializable
{
    private String server;
    private int port;
    private String user;
    private String password;
    private String database;

    public boolean _isVerified;

    public RepositoryConfig()
    {
    }

    public String getServer() { return server; }
    public int getPort() { return port; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public String getDatabase() { return database; }

    public boolean isVerified() { return _isVerified; }

    public boolean verify()
        throws CtxException
    {
        assertion().assertNotNull(server, "Database Server name cannot be null");
        assertion().assertTrue((port >= 0), "Database Port cannot be null");
        assertion().assertNotNull(user, "Database user cannot be null");
        assertion().assertNotNull(password, "Database password cannot be null");
        assertion().assertNotNull(database, "Database cannot be null");

        _isVerified = true;
        return _isVerified;
    }
}

