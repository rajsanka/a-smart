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
 * File:                org.anon.smart.workbench.anatomy.WorkbenchContext
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A context for workbench
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.anatomy;

import org.anon.smart.base.loader.SmartLoader;
import org.anon.smart.base.loader.LoaderVars;
import org.anon.smart.base.dspace.DSpaceAuthor;
import org.anon.smart.base.anatomy.SmartModuleContext;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;

import org.anon.utilities.anatomy.ModuleContext;
import org.anon.utilities.anatomy.StartConfig;
import org.anon.utilities.anatomy.JVMEnvironment;
import org.anon.utilities.exception.CtxException;

public class WorkbenchContext implements SmartModuleContext
{
    public WorkbenchContext()
    {
    }

    public void setup(StartConfig cfg) throws CtxException {
    }

    public JVMEnvironment vmEnvironment()
        throws CtxException
    {
        return null; //use default
    }

    public ClassLoader smartLoader(ClassLoader ldr, String name)
        throws CtxException
    {
        return null; //don't override, just use whatever is there.
    }

    public DSpaceAuthor spaceAuthor()
        throws CtxException
    {
        return null;
    }

    public String[] getEnableFlows()
    {
        //return new String[] {  "WorkbenchFlow" };
        return new String[] { };
    }
}

