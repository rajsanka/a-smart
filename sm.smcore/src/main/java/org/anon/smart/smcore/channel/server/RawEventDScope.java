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
 * File:                org.anon.smart.smcore.channel.server.RawEventDScope
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                27-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A tcp data scope
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.channel.server;

import java.util.Map;
import java.util.UUID;
import java.io.InputStream;

import org.anon.smart.channels.Route;
import org.anon.smart.channels.tcp.TCPMessageReader;
import org.anon.smart.channels.tcp.TCPDataFactory;
import org.anon.smart.channels.tcp.TCPDScope;

import org.anon.smart.smcore.events.CrossLinkEventLegend;

import static org.anon.utilities.services.ServiceLocator.*;

import org.anon.utilities.crosslink.CrossLinkAny;
import org.anon.utilities.exception.CtxException;

public class RawEventDScope extends TCPDScope
{
    private String _tenant;
    private String _flow;
    private String _eventName;
    private UUID _sessionId;
    private CrossLinkAny _config;

    public RawEventDScope(Route r, Object msg, TCPMessageReader rdr, TCPDataFactory fact, CrossLinkAny cfg)
        throws CtxException
    {
        super(r, msg, rdr, fact);
        //TODO: find and fill the tenant, flow etc.
        _config = cfg;
    }

    public Object eventLegend(ClassLoader ldr)
        throws CtxException
    {
        CrossLinkEventLegend legend = new CrossLinkEventLegend(_sessionId, "raw", ldr);
        legend.stampReceived(primary().receivedTime());
        return legend.link();
    }

    public void setupForStream(Map in, RawEventPData rdata)
        throws CtxException
    {
        _tenant = (String)_config.invoke("getTenant", new Class[] { Map.class }, new Object[] { in });
        _flow = (String)_config.invoke("getFlow", new Class[] { Map.class }, new Object[] { in });
        _eventName = (String)_config.invoke("mapEvent", new Class[] { Map.class }, new Object[] { in });

        rdata.setupFrom(this);
    }

    public String tenant() { return _tenant; }
    public String flow() { return _flow; }
    String eventName() { return _eventName; }
    UUID sessionId() { return _sessionId; }
}

