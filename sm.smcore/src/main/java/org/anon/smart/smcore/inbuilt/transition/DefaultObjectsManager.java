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
 * File:                org.anon.smart.smcore.inbuilt.transition.DefaultObjectsManager
 * Author:              rsankar
 * Revision:            1.0
 * Date:                11-06-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A manager that allow creation of default objects
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.inbuilt.transition;

import java.util.List;
import java.util.ArrayList;

import org.anon.smart.base.tenant.TenantAdmin;
import org.anon.smart.base.tenant.SmartTenant;

import org.anon.utilities.exception.CtxException;

public class DefaultObjectsManager
{
    public static interface TenantDefaultCreator
    {
        public void addTenantObjects(TenantAdmin admin, SmartTenant tenant, ClassLoader ldr)
            throws CtxException;
    }

    public static interface InternalServices
    {
        public void setupContext(String svc, ClassLoader ldr)
            throws CtxException;
    }

    private static List<TenantDefaultCreator> CREATORS = new ArrayList<TenantDefaultCreator>();
    private static List<InternalServices> SERVICES = new ArrayList<InternalServices>();

    public static void addCreator(TenantDefaultCreator creator)
    {
        CREATORS.add(creator);
    }

    public static void addInternalServices(InternalServices svc)
    {
        SERVICES.add(svc);
    }

    public static void createDefaultObjects(TenantAdmin admin, SmartTenant tenant, ClassLoader ldr) 
        throws CtxException
    { 
        for (TenantDefaultCreator creator : CREATORS)
            creator.addTenantObjects(admin, tenant, ldr);
    }

    public static void setupInternalServiceContext(String s, ClassLoader ldr)
        throws CtxException
    {
        for (InternalServices svc : SERVICES)
            svc.setupContext(s, ldr);
    }

    private DefaultObjectsManager()
    {
    }
}

