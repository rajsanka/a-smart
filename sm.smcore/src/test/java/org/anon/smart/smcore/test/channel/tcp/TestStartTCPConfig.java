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
 *
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.anon.smart.smcore.test.channel.tcp.TestStartTCPConfig
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                27-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration for starting up TCP channel along with the http
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.test.channel.tcp;

import org.anon.smart.d2cache.D2CacheConfig;
import org.anon.smart.base.test.testanatomy.BaseStartConfig;
import org.anon.smart.channels.shell.ExternalConfig;
import org.anon.smart.smcore.anatomy.SMCoreConfig;
import org.anon.smart.smcore.channel.server.EventServerConfig;
import org.anon.smart.smcore.channel.server.CustomTCPConfig;
import org.anon.utilities.exception.CtxException;

import java.util.Map;

public class TestStartTCPConfig extends BaseStartConfig implements SMCoreConfig 
{
    protected ExternalConfig[] _channels;

    public TestStartTCPConfig(String[] deploy, String[] tenants, Map<String, String[]> enable, int port ,int tcpport)
        throws CtxException
    {
        super(deploy, tenants, enable);
        _channels = new ExternalConfig[2];
        _channels[0] = new EventServerConfig("Test", port, false);
        //TODO: change the translator
        _channels[1] = new CustomTCPConfig("Test", tcpport, "org.anon.smart.smcore.test.channel.tcp.TestTCPTranslator",
                "org.anon.smart.smcore.test.channel.tcp.TestTCPMapper", "");
    }

    public ExternalConfig[] startChannels()
            throws CtxException
    {
        return _channels;
    }

    public boolean firstJVM()
    {
        return true;
    }

    public boolean initTenants()
    {
        return false; //will be initialized by smcore
    }

    public D2CacheConfig repository() {
        return null;
    }
}

