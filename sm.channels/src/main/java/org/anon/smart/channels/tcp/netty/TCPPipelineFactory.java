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
 * File:                org.anon.smart.channels.tcp.netty.TCPPipelineFactory
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                Dec 9, 2014
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * 
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

public class TCPPipelineFactory implements ChannelPipelineFactory
{
    private SCShell _shell;
    private SCConfig _config;
    private UUID _channelID;

    public TCPPipelineFactory(UUID channelID, SCShell shell, SCConfig cfg)
    {
        _shell = shell;
        _config = cfg;
        _channelID = channelID;
    }

    public ChannelPipeline getPipeline()
        throws Exception
    {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("response", new TCPResponder(_shell, _config, _channelID));
        pipeline.addLast("tcphandler", new NettyInstinctHandler(_shell, _config, new TCPNettyRequestReader(), _channelID));

        return pipeline;
    }

}
