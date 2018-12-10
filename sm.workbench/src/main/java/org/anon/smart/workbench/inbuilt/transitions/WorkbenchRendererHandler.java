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
 * File:                org.anon.smart.workbench.inbuilt.transitions.WorkbenchRendererHandler
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                14-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A handler to handle renderer events
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.inbuilt.transitions;

import org.anon.smart.workbench.data.WorkSpaceObject;
import org.anon.smart.workbench.renderer.DataDisplay;
import org.anon.smart.workbench.renderer.DataDisplayManager;
import org.anon.smart.workbench.inbuilt.events.GetViewDescriptor;
import org.anon.smart.workbench.inbuilt.responses.ViewDescriptor;

import org.anon.smart.base.tenant.CrossLinkSmartTenant;
import org.anon.smart.base.tenant.shell.CrossLinkDeploymentShell;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class WorkbenchRendererHandler
{
    public WorkbenchRendererHandler()
    {
    }

    public void getViewDescriptor(GetViewDescriptor get)
        throws CtxException
    {
        DataDisplayManager.displaytypes dtype = DataDisplayManager.displaytypes.valueOf(get.getViewType());
        CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        CrossLinkDeploymentShell dshell = tenant.deploymentShell();

        Class<? extends WorkSpaceObject> wsClazz = (Class<? extends WorkSpaceObject>)dshell.dataClass(get.getFlow(), get.getGroup());
        if (wsClazz == null)
            wsClazz = (Class<? extends WorkSpaceObject>)dshell.seriesClass(get.getFlow(), get.getGroup());

        assertion().assertNotNull(wsClazz, "Cannot find the deployment for " + get.getFlow() + ":" + get.getGroup());
        DataDisplay.Display display = DataDisplayManager.getDisplayFor(wsClazz, dtype);
        ViewDescriptor descriptor = new ViewDescriptor(display);
    }
}

