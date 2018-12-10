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
 * File:                org.anon.smart.workbench.renderer.DataDisplayManager
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                13-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A manager for the display
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.renderer;

import java.util.Map;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.anon.smart.workbench.data.WorkSpaceObject;

import org.anon.utilities.config.Format;
import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;

public class DataDisplayManager
{
    public static enum displaytypes { list, viewdetail, editdetail, search, chart };
    public static enum attributerenderer { text, date, address };

    private static final DataDisplayManager MANAGER = new DataDisplayManager();

    public static DataDisplay.Display getDisplayFor(Class<? extends WorkSpaceObject> cls, displaytypes type)
        throws CtxException
    {
        return MANAGER.displayFor(cls, type);
    }

    private Map<Class, DataDisplay> displays = new ConcurrentHashMap<Class, DataDisplay>();

    private DataDisplayManager()
    {
    }

    private void readDisplayConfig(Class<? extends WorkSpaceObject> cls)
        throws CtxException
    {
        String name = cls.getName();
        name = name.replaceAll("\\.", "\\/");
        name += ".display";
        System.out.println("Trying to read display for: " + name);

        InputStream istr = this.getClass().getClassLoader().getResourceAsStream(name);
        assertion().assertNotNull(istr, "Cannot find display details for: " + cls + ". Please create " + name);
        Format fmt = config().readYMLConfig(istr);
        Map values = fmt.allValues();
        DataDisplay display = convert().mapToVerifiedObject(DataDisplay.class, values);
        display.setup(cls);
        displays.put(cls, display);
    }

    private DataDisplay.Display displayFor(Class<? extends WorkSpaceObject> cls, displaytypes type)
        throws CtxException
    {
        if (!displays.containsKey(cls))
        {
            readDisplayConfig(cls);
        }

        DataDisplay ddisplay = displays.get(cls);
        assertion().assertNotNull(ddisplay, "No display configured for " + cls);
        DataDisplay.Display display = ddisplay.getDisplay(type);
        assertion().assertNotNull(display, "No display configured for " + type + " for: " + cls);
        return display;
    }

}

