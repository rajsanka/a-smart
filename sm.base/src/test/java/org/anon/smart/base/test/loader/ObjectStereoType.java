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
 * File:                org.anon.smart.base.test.loader.ObjectStereoType
 * Author:              rsankar
 * Revision:            1.0
 * Date:                31-12-2012
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A stereotype to test multiple types
 *
 * ************************************************************
 * */

package org.anon.smart.base.test.loader;

import org.anon.smart.base.stt.annot.MethodExit;

import org.anon.utilities.fsm.FiniteState;
import org.anon.utilities.fsm.StateEntity;
import org.anon.utilities.exception.CtxException;

public class ObjectStereoType implements StateEntity
{
    private String __object__type__;
    private FiniteState __current__state__;


    public ObjectStereoType()
    {
    }

    @MethodExit("constructor")
    private void initializeObject()
    {
        __object__type__ = "New";
    }

    public String utilities___stateEntityType()
    {
        return __object__type__;
    }

    public void utilities___setCurrentState(FiniteState state)
    {
        __current__state__ = state;
    }

    public FiniteState utilities___currentState()
    {
        return __current__state__;
    }

    public StateEntity utilities___parent()
        throws CtxException
    {
        return null;
    }

    public StateEntity[] utilities___children(String setype)
        throws CtxException
    {
        return null;
    }
}

