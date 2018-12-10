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
 * File:                org.anon.smart.workbench.inbuilt.events.GetViewDescriptor
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                14-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An event to retriever to get the view descriptor for a given type
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.inbuilt.events;

public class GetViewDescriptor implements java.io.Serializable
{
    private String flow;
    private String group;
    private String viewType;

    public GetViewDescriptor()
    {
    }

    public String getFlow() { return flow; }
    public String getGroup() { return group; }
    public String getViewType() { return viewType; }
}

