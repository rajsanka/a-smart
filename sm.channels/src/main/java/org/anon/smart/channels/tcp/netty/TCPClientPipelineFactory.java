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
 * File:                org.anon.smart.channels.tcp.netty.TCPClientPipelineFactory
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A pipeline for clients
 *
 * ************************************************************
 * */

package org.anon.smart.channels.tcp.netty;

import java.util.UUID;

import static org.jboss.netty.channel.Channels.*;

import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import org.anon.smart.channels.shell.SCShell;
import org.anon.smart.channels.shell.SCConfig;
import org.anon.smart.channels.netty.NettyInstinctHandler;

public class TCPClientPipelineFactory implements ChannelPipelineFactory
{
    private SCShell _shell;
    private SCConfig _config;
    private UUID _channelID;
    private TCPNettyRequestReader _reader;
    private TCPClient _client;

    public TCPClientPipelineFactory(UUID channelID, SCShell shell, SCConfig cfg, TCPClient clnt)
    {
        _shell = shell;
        _config = cfg;
        _channelID = channelID;
        _reader = new TCPNettyRequestReader();
        _client = clnt;
        _client.setReader(_reader);
    }

    public ChannelPipeline getPipeline()
        throws Exception
    {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("response", new TCPResponder(_shell, _config, _channelID));
        pipeline.addLast("handler", new NettyInstinctHandler(_shell, _config, _reader, _channelID));

        return pipeline;
    }
}

