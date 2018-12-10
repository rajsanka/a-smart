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
 * File:                org.anon.smart.workbench.renderer.DataDisplay
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                13-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A display configuration for WSObject
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.renderer;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.anon.utilities.crosslink.CrossLinkAny;
import org.anon.utilities.verify.VerifiableObject;
import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class DataDisplay implements java.io.Serializable, VerifiableObject
{
    public static class AttributeDisplay implements java.io.Serializable
    {
        private String attribute;
        private String label;
        private String renderAs;
        private String operator;

        public String getAttribute() { return attribute; }
        public String getLabel() { return label; }
        public String getRenderAs() { return renderAs; }
        public String getOperator() { return operator; }
    }

    public static class Display implements java.io.Serializable
    {
        private String type;
        private String transform;
        private List<AttributeDisplay> details; 

        private transient Class transformer;
        private transient String transformMethod;

        public DataDisplayManager.displaytypes getType() { return DataDisplayManager.displaytypes.valueOf(type); }
        public List<AttributeDisplay> getDetails() { return details; }

        public Object transformObject(Object o)
            throws CtxException
        {
            CrossLinkAny cl = new CrossLinkAny(transformer);
            return cl.invoke(transformMethod, o);
        }
    }

    private String name;
    private List<Display> display;

    private transient Class myDataClazz;
    private transient Map<DataDisplayManager.displaytypes, Display> mappedDisplay;

    public DataDisplay()
    {
    }

    public boolean isVerified()
    {
        return true;
    }

    public boolean verify()
        throws CtxException
    {
        return true;
    }

    public String getName() { return name; }
    public Display getDisplay(DataDisplayManager.displaytypes type) { return mappedDisplay.get(type); }

    public void setup(Class dataclazz)
        throws CtxException
    {
        mappedDisplay = new HashMap<DataDisplayManager.displaytypes, Display>();
        assertion().assertNotNull(display, "No displays defined for: " + name);
        myDataClazz = dataclazz;
        for (Display disp : display)
        {
            String[] transform = disp.transform.split("\\.");
            assertion().assertTrue((transform.length >= 2), "Transform has to be of the format class.method");
            String method = transform[transform.length - 1];
            String clazz = transform[0];
            for (int i = 1; i < (transform.length - 1); i++)
            {
                clazz += "." + transform[i];
            }

            CrossLinkAny any = new CrossLinkAny(clazz);
            disp.transformer = any.linkType();
            disp.transformMethod = method;
            mappedDisplay.put(disp.getType(), disp);
        }
    }
}

