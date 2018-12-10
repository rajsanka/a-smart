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
 * File:                org.anon.smart.workbench.stt.tl.WorkSpaceObjectTL
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A template for workbench objects
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.stt.tl;

import org.anon.smart.base.stt.tl.BaseTL;
import org.anon.smart.workbench.stt.Constants;
import org.anon.smart.workbench.annot.WorkSpaceObjectAnnotate;

import org.anon.utilities.exception.CtxException;

public class WorkSpaceObjectTL extends BaseTL implements Constants
{
    public WorkSpaceObjectTL()
    {
    }

    public String[] getTypes()
        throws CtxException
    {
        return new String[] { WORKSPACEOBJECT };
    }


    @Override
    public Class[] getAnnotations(String name)
        throws CtxException
    {
        //assumption is that this is an extra with others, hence base class annotates is
        //already present.
        return new Class[] { WorkSpaceObjectAnnotate.class };
    }

    public static WorkSpaceObjectTL defaultFor(String clsname, String type, String flow, String[] parms)
    {
        WorkSpaceObjectTL ret = new WorkSpaceObjectTL();
        return ret;
    }

    @Override
    public String[] getExtras()
        throws CtxException
    {
        return new String[0];
    }
}

