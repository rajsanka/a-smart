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
 * File:                org.anon.smart.base.stt.tl.TemplateReader
 * Author:              rsankar
 * Revision:            1.0
 * Date:                30-12-2012
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A template configuration reader for a class
 *
 * ************************************************************
 * */

package org.anon.smart.base.stt.tl;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;
import static org.anon.utilities.services.ServiceLocator.*;
import org.anon.utilities.utils.VMSingleton;
import org.anon.utilities.exception.CtxException;

public class TemplateReader extends VMSingleton
{
    private static final String TEMPATEREADERNAME = "org.anon.smart.base.stt.tl.TemplateReader";

    private static TemplateReader SINGLE_INSTANCE = null;

    private static void setSingleInstance(Object obj)
    {
        if (SINGLE_INSTANCE == null)
            SINGLE_INSTANCE = (TemplateReader)obj;
    }

    private static Object getSingleInstance()
    {
        return SINGLE_INSTANCE;
    }

    private Map<String, Class<? extends BaseTL>> __type__mapping__ = new HashMap<String, Class<? extends BaseTL>>();
    private List<SearchTemplate> __search__templates__ = new ArrayList<SearchTemplate>();

    public TemplateReader()
    {
        super();
        //in the order it will be searched
        __search__templates__.add(new ClassTemplate());
    }

    public static void registerTemplate(String name, Class<? extends BaseTL> type)
        throws CtxException
    {
        TemplateReader reader = getTemplateReader();
        reader.__type__mapping__.put(name, type);
    }

    public static BaseTL[] readConfig(Map values)
        throws CtxException
    {
        TemplateReader reader = getTemplateReader();
        List<BaseTL> ret = new ArrayList<BaseTL>();
        String[] types = BaseTL.getTypes(values);
        for (int i = 0; i < types.length; i++)
        {
            Class<? extends BaseTL> cls = reader.__type__mapping__.get(types[i]);
            if (cls != null)
            {
                BaseTL tl = (BaseTL)convert().mapToObject(cls, values);
                ret.add(tl);
            }
        }

        return ret.toArray(new BaseTL[0]);
    }

    public static BaseTL[] readTemplate(String cls, ClassLoader ldr)
        throws CtxException
    {
        TemplateReader reader = getTemplateReader();
        return reader.readTemplateFor(cls, ldr);
    }

    public static boolean shouldTemplatize(String cls, ClassLoader ldr)
        throws CtxException
    {
        TemplateReader reader = getTemplateReader();
        return reader.templateFound(cls, ldr);
    }

    private static TemplateReader getTemplateReader()
        throws CtxException
    {
        return (TemplateReader)getInstance(TEMPATEREADERNAME);
    }

    private BaseTL[] readTemplateFor(String cls, ClassLoader ldr)
        throws CtxException
    {
        for (int i = 0; i < __search__templates__.size(); i++)
        {
            SearchTemplate temp = __search__templates__.get(i);
            SearchTemplate stemp = (SearchTemplate)temp.repeatMe(null);
            BaseTL[] templates = stemp.searchFor(cls, ldr);
            if (templates != null)
                return templates;
        }

        return null;
    }

    private boolean templateFound(String cls, ClassLoader ldr)
    {
        for (int i = 0; i < __search__templates__.size(); i++)
        {
            SearchTemplate temp = __search__templates__.get(i);
            if (temp.templateFound(cls, ldr))
                return true;
        }

        return false;
    }
}

