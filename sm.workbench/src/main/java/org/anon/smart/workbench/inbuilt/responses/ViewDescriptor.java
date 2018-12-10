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
 * File:                org.anon.smart.workbench.inbuilt.responses.ViewDescriptor
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                14-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A descriptor returned for a given view
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.inbuilt.responses;

import java.util.List;
import java.util.ArrayList;

import org.anon.smart.workbench.renderer.DataDisplay;

public class ViewDescriptor implements java.io.Serializable
{
    class AttributeDescriptor implements java.io.Serializable
    {
        private String attribute;
        private String label;
        private String renderAs;

        AttributeDescriptor(DataDisplay.AttributeDisplay disp)
        {
            attribute = disp.getAttribute();
            label = disp.getLabel();
            renderAs = disp.getRenderAs();
        }
    }

    List<AttributeDescriptor> attributes;

    public ViewDescriptor(DataDisplay.Display display)
    {
        attributes = new ArrayList<AttributeDescriptor>();
        List<DataDisplay.AttributeDisplay> attrs = display.getDetails();
        for (int i = 0; i < attrs.size(); i++)
        {
            AttributeDescriptor desc = new AttributeDescriptor(attrs.get(i));
            attributes.add(desc);
        }
    }
}

