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
 * File:                org.anon.smart.workbench.inbuilt.transitions.WorkbenchEventHandler
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                07-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A handler for the workbench events
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.inbuilt.transitions;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.anon.smart.workbench.data.WorkSpace;
import org.anon.smart.workbench.data.WorkSpaceObject;
import org.anon.smart.workbench.data.WorkSpaceConfig;
import org.anon.smart.workbench.data.WorkSpaceTemplate;
import org.anon.smart.workbench.process.WorkSpaceProcessor;
import org.anon.smart.workbench.process.WorkSpaceTemplateProcessor;

import org.anon.smart.workbench.inbuilt.events.WorkSpaceEvent;
import org.anon.smart.workbench.inbuilt.events.GetWorkSpaceTemplates;
import org.anon.smart.workbench.inbuilt.events.CreateWorkSpaceFromTemplate;
import org.anon.smart.workbench.inbuilt.events.RefreshWorkSpace;
import org.anon.smart.workbench.inbuilt.events.SetupWorkSpace;
import org.anon.smart.workbench.inbuilt.responses.MyWorkSpaces;

import org.anon.smart.base.tenant.CrossLinkSmartTenant;
import org.anon.smart.base.tenant.shell.RuntimeShell;
import org.anon.smart.smcore.data.SmartData;
import org.anon.smart.smcore.data.SmartDataED;
import org.anon.smart.smcore.data.SmartPrimeData;
import org.anon.smart.smcore.data.datalinks.DataLinker;
import org.anon.smart.smcore.transition.TransitionContext;
import org.anon.smart.smcore.inbuilt.transition.SearchManager;

import org.anon.smart.workbench.inbuilt.responses.WSConfigData;
import org.anon.smart.workbench.inbuilt.responses.WorkSpaceMessage;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class WorkbenchEventHandler
{
    public WorkbenchEventHandler()
    {
    }

    public void getWorkSpaceTemplates(GetWorkSpaceTemplates get)
        throws CtxException
    {
        String dep = get.getFlow();
        Map<String, WorkSpaceTemplate> templates = WorkSpaceTemplateProcessor.getTemplatesFor(dep);
        WSConfigData ret = new WSConfigData();
        for (WorkSpaceTemplate temp : templates.values())
        {
            System.out.println("Adding Template from: " + temp);
            ret.addFromTemplate(temp);
        }
    }

    public void createWorkSpaceFromTemplate(CreateWorkSpaceFromTemplate template)
        throws CtxException
    {
        WorkSpaceConfig cfg = WorkSpaceTemplateProcessor.createWorkSpaceFromTemplate(template.getFlow(), template.getTemplateName(), template.getWorkSpaceName());
        WorkSpace ws = WorkSpaceProcessor.createWorkSpace(cfg);
        SetupWorkSpace refresh = new SetupWorkSpace(ws.getWorkSpaceName());
        WorkSpaceMessage msg = new WorkSpaceMessage(cfg.getWorkSpaceName(), cfg.getConfigId(), "Successfully created.");
    }

    public void refreshWorkSpace(WorkSpace ws, RefreshWorkSpace event)
        throws CtxException
    {
        //WorkSpace ws = event.getWorkspace();
        WorkSpaceProcessor.refreshWorkSpace(ws);
    }

    public void setupWorkSpace(SetupWorkSpace event)
        throws CtxException
    {
        WorkSpace ws = WorkSpaceProcessor.getWorkSpace(event.getWorkSpaceName());
        RefreshWorkSpace refresh = new RefreshWorkSpace(ws);
    }

    public void handleWorkSpaceEvent(WorkSpaceEvent evt)
        throws CtxException
    {
        try
        {
            WorkSpaceObject obj = WorkSpaceProcessor.getObject(evt.getObjectFlow(), evt.getObjectType(), evt.getObjectKey());
            WorkSpace[] ws = WorkSpaceProcessor.findAndAddToWorkSpace(obj);
            for (int i = 0; i < ws.length; i++)
            {
                System.out.println("Running rules for workspace: " + ws[i]);
                WorkSpaceProcessor.runWorkSpaceRules(ws[i], obj);
            }
        }
        catch (CtxException e)
        {
            e.printStackTrace();
        }
    }

    public void getMyWorkSpaces(String user)
        throws CtxException
    {
        //currently no public, private, just those I have created
        CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        RuntimeShell shell = (RuntimeShell)(tenant.runtimeShell());
        assertion().assertNotNull(shell, "WorkbenchEventHandler: Runtime Shell is NULL");
        SearchManager mgr = new SearchManager();

        Map<String, Object> query = new HashMap<String, Object>();
        query.put("___smart_ownedBy___", user);

        System.out.println("WorkbenchEventHandler: Searching workspace for: " + user);

        List result = new ArrayList();
        //assumption here is that the number of workspaces will not exceed 1000? We have to reevaluate this and work with pages otherwise
        mgr.searchService("WorkbenchFlow", "WorkSpace", query, result, 1, 1000, null, null);

        List cfgs = new ArrayList();
        mgr.searchService("WorkbenchFlow", "WorkSpaceConfig", query, cfgs, 1, 1000, null, null);

        Map<String, WorkSpaceConfig> processed = new HashMap<String, WorkSpaceConfig>();

        for (Object obj : cfgs)
        {
            WorkSpaceConfig cfg = (WorkSpaceConfig)obj;
            processed.put(cfg.getWorkSpaceName(), cfg);
        }

        MyWorkSpaces wspaces = new MyWorkSpaces();
        for (Object obj : result)
        {
            WorkSpace ws = (WorkSpace)obj;
            wspaces.addWorkSpace(ws, processed.get(ws.getWorkSpaceName()));
        }
    }
}

