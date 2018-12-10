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
 * File:                org.anon.smart.workbench.inbuilt.events.WorkSpaceEvent
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An internal event posted when workspaceobject is created or modified
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.inbuilt.events;

import java.util.List;

import org.anon.smart.smcore.data.SmartData;
import org.anon.smart.workbench.data.WorkSpaceObject;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.smart.base.utils.AnnotationUtils.*;

public class WorkSpaceEvent implements java.io.Serializable
{
    private String workSpaceObjectFlow;
    private String workSpaceObjectType;
    private Object workSpaceObjectKey;
    private boolean isCreated;
    private String fromState;
    private String toState;

    public WorkSpaceEvent(WorkSpaceObject obj, boolean isNew)
        throws CtxException
    {
        workSpaceObjectType = className(obj.getClass());
        workSpaceObjectFlow = flowFor(obj.getClass());

        Object o = obj;
        SmartData d = (SmartData)o;
        List<Object> keys = d.smart___keys();
        assertion().assertTrue((keys.size() > 0), "Cannot find keys for the object: " + obj);
        workSpaceObjectKey = keys.get(0);
        isCreated = isNew;
    }

    public WorkSpaceEvent(WorkSpaceObject obj, String from, String to)
        throws CtxException
    {
        this(obj, false);
        fromState = from;
        toState = to;
    }

    public String getObjectFlow() { return workSpaceObjectFlow; }
    public String getObjectType() { return workSpaceObjectType; }
    public Object getObjectKey() { return workSpaceObjectKey; }
    public boolean getIsCreated() { return isCreated; }
    public String getFromState() { return fromState; }
    public String getToState() { return toState; }

    public boolean sameWorkSpaceObject(WorkSpaceObject obj)
        throws CtxException
    {
        String type = className(obj.getClass());
        String flow = flowFor(obj.getClass());

        Object o = obj;
        SmartData d = (SmartData)o;
        List<Object> keys = d.smart___keys();
        assertion().assertTrue((keys.size() > 0), "Cannot find keys for the object: " + obj);
        Object key = keys.get(0);

        boolean ret = (workSpaceObjectType.equals(type) && workSpaceObjectFlow.equals(flow) && workSpaceObjectKey.equals(key));

        return ret;
    }
}

