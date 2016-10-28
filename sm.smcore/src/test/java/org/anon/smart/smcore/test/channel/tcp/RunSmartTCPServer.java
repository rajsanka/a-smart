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
 * File:                org.anon.smart.smcore.test.channel.tcp.RunSmartTCPServer
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                27-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A startup for tcp channels
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.test.channel.tcp;

import java.util.Map;
import java.util.HashMap;

import org.anon.smart.base.test.testanatomy.BaseStartConfig;
import org.anon.smart.smcore.test.StartCoreServerRunner;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;

public class RunSmartTCPServer extends StartCoreServerRunner
{
    private int _tcpport;

    public RunSmartTCPServer(boolean b, int port)
    {
        super(b, port);
        _tcpport = 9087;
        _tenants.add("newtenant");
        _tenants.add("errortenant");
    }

    @Override
    protected BaseStartConfig getConfig()
    {
        String[] tens = tenants();
        Map<String, String[]> enable = new HashMap<String, String[]>();
        enable.put("ReviewFlow.soa", tens); //by default enable for all. If any custom override and change
        try
        {
            TestStartTCPConfig cfg = new TestStartTCPConfig(new String[] { "ReviewFlow.soa" }, tens, enable, _port, _tcpport);
            addTenantConfigs(tens, cfg);
            return cfg;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    protected void addTenantConfigs(String[] tenants, BaseStartConfig cfg)
    {
        cfg.addConfig(tenants[0], "org.anon.smart.smcore.test.channel.FirstTestConfig");
    }
}

