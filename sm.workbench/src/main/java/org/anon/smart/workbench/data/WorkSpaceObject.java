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
 * File:                org.anon.smart.workbench.data.WorkSpaceObject
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An interface to implement if it is a workspace object
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.data;

import java.util.List;

import org.anon.utilities.exception.CtxException;

public interface WorkSpaceObject
{
    public String[] smart___getWorkSpaces();
    public boolean smart___addToWorkSpace(String nm);
    public boolean smart___removeFromWorkSpace(String nm);
    public boolean smart___belongsTo(String nm);

    public void smart___addWSError(String wsname, int code, String error);
    public List<WorkSpaceError> smart___getWSErrors(String wsname);
    public boolean smart___hasErrors(String wsname);
    public void smart___clearAllErrors(String wsname);

    public void smart___raiseAlarm(int code, String msg)
        throws CtxException;

    public List<Alarm> smart___alarms()
        throws CtxException;

    public boolean smart___hasAlarms();
    public void smart___clearHasAlarms();
}

