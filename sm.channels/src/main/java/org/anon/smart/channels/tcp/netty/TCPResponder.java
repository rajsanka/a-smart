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
 * File:                org.anon.smart.channels.tcp.netty.TCPResponder
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A response handler for tcp
 *
 * ************************************************************
 * */

package org.anon.smart.channels.tcp.netty;

import java.util.UUID;

import static org.jboss.netty.channel.Channels.*;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import org.anon.smart.channels.shell.SCShell;
import org.anon.smart.channels.shell.SCConfig;

public class TCPResponder implements ChannelDownstreamHandler
{
    private UUID _channelID;
    private SCShell _shell;
    private SCConfig _config;

    public TCPResponder(SCShell shell, SCConfig conf, UUID id)
    {
        _shell = shell;
        _channelID = id;
        _config = conf;
    }

    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt) 
        throws Exception
    {
        if (!(evt instanceof MessageEvent))
        {
            ctx.sendDownstream(evt);
            return;
        }
        MessageEvent e = (MessageEvent) evt;
        Object originalMessage = e.getMessage();
        write(ctx, e.getFuture(), originalMessage, e.getRemoteAddress());
    }
}

