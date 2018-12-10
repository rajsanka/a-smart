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
 * File:                org.anon.smart.workbench.data.WorkSpaceConfig
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration for the workspace
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.anon.utilities.exception.CtxException;

import static org.anon.smart.base.utils.AnnotationUtils.*;

public class WorkSpaceConfig implements java.io.Serializable
{
    private UUID configId;
    private String createdFromTemplate;
    private String workSpaceName;
    private String primaryWorkObjectFlow;       //The primary work object type
    private String primaryWorkObjectType;       //The primary work object type
    private Map<String, List<DataCriteria>> criteria;        //The data from all the criteria will be added to the wowrkspace
    private List<RuleConfig> rules;                 //will be a list of rule files
    private List<DataCompute> compute;          //the data will be passed through the compute to create a summary

    private transient Class primaryWorkObjectClazz;
    private transient List<Class> workObjectClazzez;

    public WorkSpaceConfig(WorkSpaceTemplate template, String nm)
        throws CtxException
    {
        configId = UUID.randomUUID();
        createdFromTemplate = template.getTemplateName();
        workSpaceName = template.getWorkSpaceName();
        if (nm != null)
            workSpaceName = nm;
        primaryWorkObjectFlow = template.getPrimaryWorkObjectFlow();
        primaryWorkObjectType = template.getPrimaryWorkObjectType();
        rules = template.getRules();
        compute = template.getCompute();

        Map<String, DataCriteria> c = template.getDefaultCriteria();
        criteria = new HashMap<String, List<DataCriteria>>();
        if (c != null)
        {
            for (String obj : c.keySet())
            {
                List<DataCriteria> lc = new ArrayList<DataCriteria>();
                lc.add(c.get(obj));
                criteria.put(obj, lc);
            }
        }
    }

    public WorkSpaceConfig(String nm, Class<? extends WorkSpaceObject> pwot)
        throws CtxException
    {
        configId = UUID.randomUUID();
        workSpaceName = nm;
        primaryWorkObjectClazz = pwot;
        primaryWorkObjectType = className(pwot);
        primaryWorkObjectFlow = flowFor(pwot);
        criteria = new HashMap<String, List<DataCriteria>>();
        rules = new ArrayList<RuleConfig>();
        compute = new ArrayList<DataCompute>();
    }

    public boolean matchesCriteria(WorkSpaceObject wso)
        throws CtxException
    {
        String type = className(wso.getClass());
        String flow = flowFor(wso.getClass());
        List<DataCriteria> lcriteria = criteria.get(flow + "." + type);
        for (DataCriteria c : lcriteria)
        {
            if (c.matches(wso)) return true;
        }

        return false;
    }

    public Set<String> getObjectTypes() { return criteria.keySet(); }
    public List<DataCriteria> getCriteria(String obj) { return criteria.get(obj); }
    public List<DataCriteria> getCriteria() { return criteria.get(primaryWorkObjectFlow + "." + primaryWorkObjectType); }
    public Class getPrimaryWorkObjectClazz() { return primaryWorkObjectClazz; }

    public Map<String, List<DataCriteria>> getAllCriteria() { return criteria; }

    public UUID getConfigId() { return configId; }
    public String getWorkSpaceName() { return workSpaceName; }
    public List<RuleConfig> getRules() { return rules; }
    public String getPrimaryWorkObjectFlow() { return primaryWorkObjectFlow; }
    public String getPrimaryWorkObjectType() { return primaryWorkObjectType; }
    public String getTemplateName() { return createdFromTemplate; }

    public boolean isPrimeObject(Object o)
        throws CtxException
    {
        if (o == null) return false;

        String flow = flowFor(o.getClass());
        String type = className(o.getClass());

        if ((flow == null) || (type == null))
            return false;

        return (flow.equals(primaryWorkObjectFlow) && type.equals(primaryWorkObjectType));
    }
}

