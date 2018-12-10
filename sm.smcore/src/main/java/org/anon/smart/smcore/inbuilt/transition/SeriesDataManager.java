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
 * File:                org.anon.smart.smcore.inbuilt.transition.SeriesDataManager
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                30-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A manager for series data
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.inbuilt.transition;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.anon.smart.d2cache.ListParams;
import org.anon.smart.base.flow.FlowConstants;
import org.anon.smart.base.tenant.TenantConstants;
import org.anon.smart.base.tenant.CrossLinkSmartTenant;
import org.anon.smart.base.tenant.shell.CrossLinkDeploymentShell;
import org.anon.smart.base.tenant.shell.RuntimeShell;
import org.anon.smart.smcore.events.SmartEvent;
import org.anon.smart.smcore.inbuilt.events.CheckExistence;
import org.anon.smart.smcore.inbuilt.events.GetListings;
import org.anon.smart.smcore.inbuilt.events.ListAllEvent;
import org.anon.smart.smcore.inbuilt.events.LookupEvent;
import org.anon.smart.smcore.inbuilt.events.GetSeriesEvent;
import org.anon.smart.smcore.inbuilt.responses.CheckExistenceResponse;
import org.anon.smart.smcore.inbuilt.responses.ListAllResponse;
import org.anon.smart.smcore.inbuilt.responses.LookupResponse;
import org.anon.smart.smcore.inbuilt.responses.SearchResponse;
import org.anon.smart.smcore.transition.TransitionContext;
import org.anon.smart.smcore.data.SeriesData;
import org.anon.smart.smcore.data.SeriesDataED;
import org.anon.utilities.exception.CtxException;


import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;


public class SeriesDataManager
{
    public SeriesDataManager()
    {
    }

    public void getSeries(GetSeriesEvent evt)
        throws CtxException
    {
        //TODO:
    }

    public boolean seriesDataFor(String flow, String group, Map<String, Object> query, List result, Integer pn, Integer ps, String sortby, Boolean asc)
        throws CtxException
    {
		if ((query != null) && (query.size() > 0))
        {
            System.out.println("Getting Series Data with this queryMap:" + query);
            CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
            RuntimeShell shell = (RuntimeShell)(tenant.runtimeShell());
            assertion().assertNotNull(shell, "SeriesDataManager: Runtime Shell is NULL");
            assertion().assertNotNull(result, "Need to initialize the result with an empty list.");
            CrossLinkDeploymentShell dShell = new CrossLinkDeploymentShell(tenant.deploymentShell());
            Class clz = dShell.seriesClass(flow, group);
            System.out.println("Retrieved: " + group + ":" + flow + ":" + clz);
            assertion().assertNotNull(clz, "Cannot find deployment for: " + group + ":" + flow);
            List<Object> searchResult = new ArrayList<Object>();
            if(clz != null)
            {
                int pagenum = -1;
                if (pn != null) pagenum = pn.intValue();
                int pagesize = -1;
                if (ps != null) pagesize = ps.intValue();
                boolean a = true;
                if (asc != null) a = asc.booleanValue();
                System.out.println("SeriesDataManager:result type is "+clz.getName() );
                searchResult = shell.searchFor(dShell.deploymentFor(flow).deployedName(),
                                                clz, query, Integer.MAX_VALUE, pagenum, pagesize, sortby, a);
            
                if (searchResult != null)
                {
                    System.out.println("ResultSet size:"+searchResult.size());
                    for(Object res : searchResult)
                    {
                        SeriesDataED ed = new SeriesDataED((SeriesData)res);
                        //do not add into the transaction. This is not an updatable object
                        result.add(ed.empirical());
                    }
                }
            }
        }

        return false;
    }
}

