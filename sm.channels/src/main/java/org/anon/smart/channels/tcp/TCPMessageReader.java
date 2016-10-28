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
 * File:                org.anon.smart.channels.tcp.TCPMessageReader
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                25-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A data reader of TCP requests
 *
 * ************************************************************
 * */

package org.anon.smart.channels.tcp;

import java.io.InputStream;

import org.anon.smart.channels.MessageReader;

import org.anon.utilities.exception.CtxException;

public interface TCPMessageReader extends MessageReader
{
    public InputStream contentStream(Object msg)
        throws CtxException;
}

