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
 * File:                org.anon.smart.workbench.anatomy.WorkbenchModule
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A module for workbench
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.anatomy;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.anon.smart.base.flow.FlowConstants;
import org.anon.smart.base.tenant.SmartTenant;
import org.anon.smart.base.tenant.TenantsHosted;
import org.anon.smart.deployment.MacroDeployer;
import org.anon.smart.smcore.transition.plugin.PluginManager;
import org.anon.smart.workbench.stt.WorkbenchSTTService;
import org.anon.smart.workbench.plugin.WorkbenchPlugin;
import org.anon.smart.smcore.anatomy.SMCoreConfig;

import org.anon.utilities.anatomy.AModule;
import org.anon.utilities.anatomy.ModuleContext;
import org.anon.utilities.anatomy.StartConfig;
import org.anon.utilities.utils.Repeatable;
import org.anon.utilities.utils.RepeaterVariants;
import org.anon.utilities.exception.CtxException;

public class WorkbenchModule extends AModule implements FlowConstants
{
    public WorkbenchModule(AModule parent)
        throws CtxException
    {
        super(parent, new WorkbenchContext(), false);
    }

    protected void setup()
        throws CtxException
    {
        PluginManager.registerPlugin(new WorkbenchPlugin());
    }

    public Repeatable repeatMe(RepeaterVariants vars)
        throws CtxException
    {
        return new WorkbenchModule(_parent);
    }

    public void start(StartConfig cfg)
        throws CtxException
    {
        WorkbenchSTTService.initialize();
        if (!(cfg instanceof SMCoreConfig))
            return;

        WorkbenchContext mctx = (WorkbenchContext)_context;
        SMCoreConfig ccfg = (SMCoreConfig)cfg;

        if (ccfg.firstJVM())
        {
            //the smcore should have initialized before this, hence this shd just deploy and use??
            Map<String, String[]> features = MacroDeployer.deployFile(FLOW, "WorkbenchFlow.soa", null);
            SmartTenant powner = TenantsHosted.platformOwner();
            //enable before committing, else the space will not be present
            for (String flow : features.keySet())
                powner.deploymentShell().enableForMe(flow, features.get(flow), new HashMap<String, List<String>>(), true, true);

            System.out.println("STARTED WORKBENCH MODULE");
        }
    }

    public void stop()
        throws CtxException
    {
    }
}

