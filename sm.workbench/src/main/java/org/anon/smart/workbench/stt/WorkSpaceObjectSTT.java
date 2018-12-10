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
 * File:                org.anon.smart.workbench.stt.WorkSpaceObjectSTT
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A stereotype for workspace objects
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.stt;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.anon.smart.workbench.data.Alarm;
import org.anon.smart.workbench.data.WorkSpaceError;
import org.anon.smart.workbench.data.WorkSpaceObject;
import org.anon.smart.workbench.process.AlarmProcessor;

import org.anon.utilities.exception.CtxException;

public class WorkSpaceObjectSTT implements WorkSpaceObject
{
    //stores a list of workspace names as tags. All objects belonging to the workspace
    //is tagged with it's name
    private List<String> ___smart_workspace___;

    //a list of workspaces this object belonged to. 
    private List<String> ___smart_belongedTo___;

    private Map<String, List<WorkSpaceError>> ___smart_wserrors___;

    private boolean ___smart_hasAlarms___;

    public WorkSpaceObjectSTT()
    {
    }

    public String[] smart___getWorkSpaces()
    {
        return ___smart_workspace___.toArray(new String[0]);
    }

    public boolean smart___addToWorkSpace(String nm)
    {
        if (___smart_workspace___ == null)
            ___smart_workspace___ = new ArrayList<String>();

        if (!___smart_workspace___.contains(nm))
        {
            ___smart_workspace___.add(nm);
            return true;
        }

        return false;
    }

    public boolean smart___removeFromWorkSpace(String nm)
    {
        if (___smart_workspace___ == null)
            ___smart_workspace___ = new ArrayList<String>();

        if (___smart_belongedTo___ == null)
            ___smart_belongedTo___ = new ArrayList<String>();

        if (___smart_workspace___.contains(nm))
        {
            ___smart_workspace___.remove(nm);
            ___smart_belongedTo___.add(nm);
            return true;
        }

        return false;
    }

    public boolean smart___belongsTo(String nm)
    {
        if (___smart_workspace___ == null)
            ___smart_workspace___ = new ArrayList<String>();

        return ___smart_workspace___.contains(nm);
    }

    public void smart___addWSError(String wsname, int code, String error)
    {
        if (___smart_wserrors___ == null)
            ___smart_wserrors___ = new HashMap<String, List<WorkSpaceError>>();

        List<WorkSpaceError> errs = ___smart_wserrors___.get(wsname);
        if (errs == null)
            errs = new ArrayList<WorkSpaceError>();

        WorkSpaceError err = new WorkSpaceError(code, error);
        errs.add(err);
        ___smart_wserrors___.put(wsname, errs);
    }

    public List<WorkSpaceError> smart___getWSErrors(String wsname)
    {
        if (___smart_wserrors___ != null)
            return ___smart_wserrors___.get(wsname);

        return null;
    }

    public boolean smart___hasErrors(String wsname)
    {
        if (___smart_wserrors___ != null)
            return ___smart_wserrors___.containsKey(wsname);

        return false;
    }

    public void smart___clearAllErrors(String wsname)
    {
        if (___smart_wserrors___ != null)
        {
            ___smart_wserrors___.remove(wsname);
        }
    }

    public void smart___raiseAlarm(int code, String message)
        throws CtxException
    {
        System.out.println("Raising alarm: " + code);
        Alarm a = AlarmProcessor.raiseAlarm(this, code, message);
        ___smart_hasAlarms___ = (a != null);
    }

    public List<Alarm> smart___alarms()
        throws CtxException
    {
        return AlarmProcessor.getAlarms(this);
    }

    public boolean smart___hasAlarms()
    {
        return ___smart_hasAlarms___;
    }

    public void smart___clearHasAlarms()
    {
        ___smart_hasAlarms___ = false;
    }
}

