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
 * File:                org.anon.smart.channels.tcp.netty.TCPNettyRequestReader
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A reader of tcp requests from netty
 *
 * ************************************************************
 * */

package org.anon.smart.channels.tcp.netty;

import java.io.InputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;

import static org.jboss.netty.buffer.ChannelBuffers.*;

import org.anon.smart.channels.Route;
import org.anon.smart.channels.data.PData;
import org.anon.smart.channels.netty.NettyRoute;
import org.anon.smart.channels.tcp.TCPMessageReader;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class TCPNettyRequestReader implements TCPMessageReader
{
    public TCPNettyRequestReader()
    {
    }

    public InputStream contentStream(Object msg)
        throws CtxException
    {
        ChannelBuffer buffer = (ChannelBuffer)msg;
        ChannelBufferInputStream stream = new ChannelBufferInputStream(buffer);
        return stream;
    }

    public Object transmitObject(PData[] resp, Route r)
        throws CtxException
    {
        try
        {
            NettyRoute nroute = (NettyRoute)r;
            ChannelBufferOutputStream bout = new ChannelBufferOutputStream(dynamicBuffer(512, nroute.getChannel().getConfig().getBufferFactory()));
            for (int i = 0; i < resp.length; i++)
            {
                StringBuffer buff = io().readStream(resp[i].cdata().data());
                byte[] write = buff.toString().getBytes();
                bout.write(write);
            }

            return bout.buffer();
        }
        catch (Exception e)
        {
            except().rt(e, new CtxException.Context("Error in transmit object.", "Exception"));
        }

        return null;
    }

    public Object transmitDefault()
        throws CtxException
    {
        return buffer(10);
    }

    public Object transmitException(Throwable t)
        throws CtxException
    {
        return buffer(10);
    }
}

