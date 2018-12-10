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
 * File:                org.anon.smart.workbench.data.WorkSpaceSummary
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                29-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A summary of the workspace
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.data;

import java.util.Map;
import java.util.HashMap;

public class WorkSpaceSummary implements java.io.Serializable
{
    private int currentCount;
    private int withErrors;
    private int withAlarms;
    private int numberPassedThrough;

    private Map<String, Integer> groupedCounts;

    public WorkSpaceSummary()
    {
        groupedCounts = new HashMap<String, Integer>();
        currentCount = 0;
        withErrors = 0;
        numberPassedThrough = 0;
    }

    public int numberAlarms() { return withAlarms; }
    public int numberErrors() { return withErrors; }
    public int currentObjects() { return currentCount; }
    public int totalObjects() { return numberPassedThrough; }

    void incrementObjectCount() { currentCount++; }
    void decrementObjectCount() { currentCount--; }

    void incrementErrorCount() { withErrors++; }
    void decrementErrorCount() { withErrors--; }

    void incrementAlarmCount() { withAlarms++; }
    void decrementAlarmCount() { withAlarms--; }

    void incrementPassThroughCount() { numberPassedThrough++; }

    void incrementGroupCount(String grp)
    {
        synchronized(groupedCounts) {
            Integer cnt = groupedCounts.get(grp);
            if (cnt == null) cnt = new Integer(-1);
            cnt++;
            groupedCounts.put(grp, cnt);
        }
    }

    void decrementGroupCount(String grp)
    {
        synchronized(groupedCounts) {
            Integer cnt = groupedCounts.get(grp);
            if (cnt != null) 
            {
                cnt--;
                groupedCounts.put(grp, cnt);
            }
        }
    }
}

