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
 * File:                org.anon.smart.workbench.process.AlarmProcessor
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                28-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A processor for raising alarms, resolving etc
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.process;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.anon.smart.workbench.data.Alarm;
import org.anon.smart.workbench.data.WorkSpaceObject;

import org.anon.smart.smcore.data.SmartData;
import org.anon.smart.smcore.inbuilt.transition.SearchManager;

import org.anon.utilities.exception.CtxException;

public class AlarmProcessor
{
    public AlarmProcessor()
    {
    }

    public static Alarm raiseAlarm(WorkSpaceObject wso, int code, String message)
        throws CtxException
    {
        SmartData d = (SmartData)wso;
        Alarm alarm = new Alarm(d.smart___id(), code, message);
        return alarm;
    }

    public static List<Alarm> getAlarms(WorkSpaceObject wso)
        throws CtxException
    {
        SmartData d = (SmartData)wso;
        Map<String, Object> srch = new HashMap<String, Object>();
        srch.put("objectId", d.smart___id().toString());

        SearchManager mgr = new SearchManager();
        List result = new ArrayList();
        mgr.searchService("WorkbenchFlow", "Alarm", srch, result, 1, 1000, null, null);
        List<Alarm> alarms = new ArrayList<Alarm>();
        for (int i = 0; (i < result.size()); i++)
        {
            alarms.add((Alarm)result.get(i));
        }

        return alarms;
    }
}

