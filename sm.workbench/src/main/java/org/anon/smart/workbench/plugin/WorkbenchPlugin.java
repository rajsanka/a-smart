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
 * File:                org.anon.smart.workbench.plugin.WorkbenchPlugin
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A plugin for workbench functions
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.anon.smart.base.dspace.DSpaceService;
import org.anon.smart.base.tenant.shell.RuntimeShell;
import org.anon.smart.smcore.data.SmartData;
import org.anon.smart.smcore.data.SmartPrimeData;
import org.anon.smart.smcore.events.SmartEvent;
import org.anon.smart.smcore.transition.TransitionContext;
import org.anon.smart.smcore.transition.plugin.BasicPlugin;
import org.anon.smart.workbench.stt.Constants;
import org.anon.smart.d2cache.D2CacheTransaction;
import org.anon.smart.workbench.data.WorkSpaceObject;
import org.anon.smart.workbench.inbuilt.events.WorkSpaceEvent;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;

import org.anon.utilities.exception.CtxException;

public class WorkbenchPlugin extends BasicPlugin implements Constants
{
    public WorkbenchPlugin()
    {
    }

    @Override
    public void eventProcessed(SmartEvent evt)
        throws CtxException
    {
        System.out.println("WorkbenchPlugin: eventProcessed: " + evt + ":");
    }

    private void createInternalEvent(SmartData data, String from, String to)
        throws CtxException
    {
        if (data instanceof WorkSpaceObject)
        {
            if (threads().threadContext() instanceof TransitionContext)
            {
                TransitionContext ctx = (TransitionContext)threads().threadContext();
                Object evt = ctx.event();
                boolean create = (!evt.getClass().equals(WorkSpaceEvent.class));
                if (!create)
                {
                    create = !(((WorkSpaceEvent)evt).sameWorkSpaceObject((WorkSpaceObject)data));
                }
                if (create)
                {
                    WorkSpaceObject wsdata = (WorkSpaceObject)data;
                    if (!wsdata.smart___hasAlarms())
                    {
                        WorkSpaceEvent wsevt = new WorkSpaceEvent((WorkSpaceObject)data, true);
                    }
                }
            }
        }
    }

    @Override
    public void objectCreated(SmartData data)
        throws CtxException
    {
        System.out.println("WorkbenchPlugin: SmartData: " + data + ":");
        createInternalEvent(data, null, null);
    }

    @Override
    public void objectModified(SmartData data)
        throws CtxException
    {
        createInternalEvent(data, null, null);
    }

    @Override
    public void primeObjectCreated(SmartPrimeData pdata)
        throws CtxException
    {
        System.out.println("WorkbenchPlugin: PrimeCreated: " + pdata + ":");
        createInternalEvent(pdata, null, null);
    }

    @Override
    public void stateTransitioned(SmartData data, String from, String to)
        throws CtxException
    {
        System.out.println("WorkbenchPlugin: Object transitioned: " + data + ":" + from + ":" + to);
        createInternalEvent(data, from, to);
    }
}

