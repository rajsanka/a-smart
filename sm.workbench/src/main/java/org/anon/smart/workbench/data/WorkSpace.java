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
 * File:                org.anon.smart.workbench.data.WorkSpace
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A workspace to which data is associated and rules run
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.data;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.anon.smart.smcore.data.SmartData;

import org.anon.utilities.exception.CtxException;

public class WorkSpace implements java.io.Serializable
{
    private String name;
    private UUID configId;
    private WorkSpaceSummary summary;

    private Map<String, Object> computedValues;

    public WorkSpace(String nm, UUID cfgid)
    {
        name = nm;
        configId = cfgid;
        summary = new WorkSpaceSummary();
        computedValues = new HashMap<String, Object>();
    }

    public UUID getConfigId() { return configId; }
    public String getWorkSpaceName() { return name; }

    public void raiseAlarm(Object obj, int code, String msg)
        throws CtxException
    {
        WorkSpaceObject wso = (WorkSpaceObject)obj;
        wso.smart___raiseAlarm(code, msg);
        incrementAlarmCount();
    }

    public void addError(Object obj, int code, String err)
        throws CtxException
    {
        WorkSpaceObject wso = (WorkSpaceObject)obj;
        wso.smart___addWSError(name, code, err);
        incrementErrorCount();
    }

    public void transition(Object obj, String state)
        throws CtxException
    {
        SmartData sd = (SmartData)obj;
        sd.smart___transition(state);
    }

    public void clearErrors(Object obj)
        throws CtxException
    {
        WorkSpaceObject wso = (WorkSpaceObject)obj;
        wso.smart___clearAllErrors(name);
        decrementErrorCount();
    }

    public void clearHasAlarms(Object obj)
    {
        WorkSpaceObject wso = (WorkSpaceObject)obj;
        wso.smart___clearHasAlarms();
    }

    public void incrementObjectCount() { summary.incrementObjectCount(); }
    public void decrementObjectCount() { summary.decrementObjectCount(); }
    public void incrementErrorCount() { summary.incrementErrorCount(); }
    public void decrementErrorCount() { summary.decrementErrorCount(); }
    public void incrementAlarmCount() { summary.incrementAlarmCount(); }
    public void decrementAlarmCount() { summary.decrementAlarmCount(); }

    public void incrementPassThroughCount() { summary.incrementPassThroughCount(); }

    public void incrementGroupCount(String grp) { summary.incrementGroupCount(grp); }
    public void decrementGroupCount(String grp) { summary.incrementGroupCount(grp); }

    public void setComputedValue(String k, Object val)
    {
        computedValues.put(k, val);
    }

    public WorkSpaceSummary getSummary() { return summary; }
}

