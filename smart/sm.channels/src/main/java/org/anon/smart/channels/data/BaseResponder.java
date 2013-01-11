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
 * File:                org.anon.smart.channels.data.BaseResponder
 * Author:              rsankar
 * Revision:            1.0
 * Date:                11-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A responder for requests
 *
 * ************************************************************
 * */

package org.anon.smart.channels.data;

import java.util.Map;
import java.util.UUID;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;

import org.anon.utilities.exception.CtxException;

public class BaseResponder implements Responder
{
    private UUID _requestID;
    private Map<Object, Object> _responses;
    
    public BaseResponder(UUID requestID)
        throws CtxException
    {
        _requestID = requestID;
        _responses = anatomy().jvmEnv().mapFor(requestID.toString() + "-Responses");
    }

    public void addResponse(Object resp)
        throws CtxException
    {
        _responses.put(resp, resp);
    }

    public Object[] responses()
        throws CtxException
    {
        return _responses.values().toArray();
    }
}

