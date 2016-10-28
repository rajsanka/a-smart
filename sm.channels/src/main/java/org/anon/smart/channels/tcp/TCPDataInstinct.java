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
 * File:                org.anon.smart.channels.tcp.TCPDataInstinct
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                25-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A rectification for tcp requests
 *
 * ************************************************************
 * */

package org.anon.smart.channels.tcp;

import org.anon.smart.channels.Route;
import org.anon.smart.channels.data.PData;
import org.anon.smart.channels.MessageReader;
import org.anon.smart.channels.distill.Distillate;
import org.anon.smart.channels.distill.Rectifier;
import org.anon.smart.channels.distill.RectifierInstinct;

import org.anon.utilities.exception.CtxException;

public class TCPDataInstinct extends RectifierInstinct
{
    private TCPDataFactory _factory;

    public TCPDataInstinct(Rectifier rect, TCPDataFactory fact)
    {
        super(rect);
        _factory = fact;
    }

    protected Distillate createStart(Route chnl, Object msg, MessageReader rdr)
        throws CtxException
    {
        TCPMessageReader hrdr = (TCPMessageReader)rdr;

        TCPDScope dscope = _factory.createDScope(chnl, msg, rdr);
        PData prime = dscope.primary();
        return new Distillate(prime);
    }
}

