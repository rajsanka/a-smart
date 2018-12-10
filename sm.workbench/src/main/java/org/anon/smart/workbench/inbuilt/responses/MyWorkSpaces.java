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
 * File:                org.anon.smart.workbench.inbuilt.responses.MyWorkSpaces
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                13-01-2017
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of workspaces that the logged in user can work with
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.inbuilt.responses;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.anon.smart.workbench.data.DataCriteria;
import org.anon.smart.workbench.data.WorkSpace;
import org.anon.smart.workbench.data.WorkSpaceConfig;
import org.anon.smart.workbench.data.WorkSpaceSummary;

public class MyWorkSpaces implements java.io.Serializable
{
    public class WorkSpaceData
    {
        String name;
        String createdFrom;
        Map<String, List<DataCriteria>> criteria;
        int currentCount;
        int numberErrors;
        int numberAlarms;
    }

    private List<WorkSpaceData> workspaces;

    public MyWorkSpaces()
    {
        workspaces = new ArrayList<WorkSpaceData>();
    }

    public void addWorkSpace(WorkSpace space, WorkSpaceConfig cfg)
    {
        WorkSpaceData data = new WorkSpaceData();
        data.name = space.getWorkSpaceName();
        data.createdFrom = cfg.getTemplateName();
        data.criteria = cfg.getAllCriteria();

        WorkSpaceSummary summary = space.getSummary();
        data.currentCount = summary.currentObjects();
        data.numberErrors = summary.numberErrors();
        data.numberAlarms = summary.numberAlarms();
    }
}

