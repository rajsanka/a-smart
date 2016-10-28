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
 * File:                org.anon.smart.smcore.channel.server.ConfigTCPMapper
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                25-02-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A mapper that maps from configuration
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.channel.server;

import java.util.Map;

public class ConfigTCPMapper
{
    private String _tenant;
    private String _flow;
    private String _event;

    public ConfigTCPMapper(String parms)
    {
        String[] split = parms.split(",");
        _tenant = split[0];
        _flow = split[1];
        _event = split[2];
    }

    public String getTenant(Map m)
    {
        return _tenant;
    }

    public String getFlow(Map m)
    {
        return _flow;
    }

    public String mapEvent(Map m)
    {
        return _event;
    }
}

