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
 * File:                org.anon.smart.secure.channel.distill.sanitization.SecureSanitizeContext
 * Author:              rsankar
 * Revision:            1.0
 * Date:                11-06-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A context for searching
 *
 * ************************************************************
 * */

package org.anon.smart.secure.channel.distill.sanitization;

import org.anon.smart.secure.guards.Constants;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;

import org.anon.utilities.exception.CtxException;

public class SecureSanitizeContext implements Constants
{
    private String _eventName;
    private String _flowName;

    public SecureSanitizeContext(String eventname, String flowname)
        throws CtxException
    {
        _eventName = eventname;
        _flowName = flowname;
        threads().addToContextLocals(CURRENT_EVENT, _eventName);
        threads().addToContextLocals(CURRENT_FLOW, _flowName);
    }
}

