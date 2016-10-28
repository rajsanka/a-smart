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
 * File:                org.anon.smart.channels.tcp.TCPDScope
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                25-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A data scope for TCP requests
 *
 * ************************************************************
 * */

package org.anon.smart.channels.tcp;

import java.util.UUID;
import java.util.List;
import java.util.Map;

import org.anon.smart.channels.Route;
import org.anon.smart.channels.data.DScope;
import org.anon.smart.channels.data.CData;
import org.anon.smart.channels.data.PData;
import org.anon.smart.channels.data.Source;
import org.anon.smart.channels.data.Responder;
import org.anon.smart.channels.data.ContentData;
import org.anon.smart.channels.data.BaseResponder;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;
import org.anon.utilities.atomic.AtomicCounter;
import org.anon.utilities.exception.CtxException;

public abstract class TCPDScope implements DScope
{
    protected UUID _requestID;
    protected PData _pdata;
    protected Responder _responder;
    protected AtomicCounter _dataCount;

    protected transient Object _msg;
    protected transient Route _route;
    protected transient Source _source;
    protected transient TCPMessageReader _reader;

    public TCPDScope(Route r, Object msg, TCPMessageReader rdr, TCPDataFactory fact)
        throws CtxException
    {
        _requestID = UUID.randomUUID();
        _reader = rdr;
        _route = r;
        _msg = msg;
        CData cd = new ContentData(rdr.contentStream(msg));

        _source = new Source(Source.src.ex, origin());
        _pdata = fact.createPrimal(this, cd);
        _responder = new BaseResponder(_requestID);
        _dataCount = anatomy().jvmEnv().createAtomicCounter(_requestID.toString() + "-EventCounter", 0);
    }

    public Responder responder()
    {
        return _responder;
    }

    public void processingData(UUID did)
        throws CtxException
    {
        _dataCount.incrementAndGet();
    }

    public void processedData()
        throws CtxException
    {
        _dataCount.decrementAndGet();
    }

    public boolean processedAll()
        throws CtxException
    {
        return (_dataCount.get() <= 0);
    }

    public Source source()
    {
        return _source;
    }

    public UUID channelID()
    {
        return _route.channelID();
    }

    public UUID requestID()
    {
        return _requestID;
    }

    public PData primary()
    {
        return _pdata;
    }

    public void transmit(PData[] resp)
        throws CtxException
    {
        Object send = _reader.transmitObject(resp,_route);
        if (send != null )
           _route.send(send);
    }

    public void close()
        throws CtxException
    {
        _route.close();
    }

    public String origin()
    {
        return "None";
    }

    public String referer()
    {
        return "None";
    }

    public boolean isKeepAlive()
    {
        return true;
    }
}

