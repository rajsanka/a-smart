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
 * File:                org.anon.smart.workbench.renderer.WSUserPreferences
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                14-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * User preferences related to workspaces
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.renderer;

import java.util.List;

public class WSUserPreferences implements java.io.Serializable
{
    public class WSPreference implements java.io.Serializable
    {
        private String workspaceName;
        private String currentDisplay;
    }

    private String currentWorkSpace;
    private List<WSPreference> wsPreferences;

    public WSUserPreferences()
    {
    }

    public String getCurrentWorkSpace() { return currentWorkSpace; }

    public String getCurrentDisplay(String wsname)
    {
        return null;
        
    }
}

