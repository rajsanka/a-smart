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
 * File:                org.anon.smart.workbench.data.WorkSpaceTemplate
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                11-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A template for workspace
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.data;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class WorkSpaceTemplate implements java.io.Serializable
{

    private String templateName;
    private String workSpaceName;
    private String primaryWorkObjectFlow;       //The primary work object type
    private String primaryWorkObjectType;       //The primary work object type
    private List<RuleConfig> rules;                 //will be a list of rule files
    private List<DataCompute> compute;          //the data will be passed through the compute to create a summary
    private Map<String, DataCriteria> defaultCriteria;

    public WorkSpaceTemplate() { }

    public void setTemplateName(String nm) { templateName = nm; }
    public void setWorkSpaceName(String nm) { workSpaceName = nm; }
    public void setPrimaryWorkObjectFlow(String f) { primaryWorkObjectFlow = f; }
    public void setPrimaryWorkObjectType(String t) { primaryWorkObjectType = t; }
    public void setRules(List<RuleConfig> r) { rules = r; }
    public void addCriteria(String flow, String grp, DataCriteria c) 
    {
        if (defaultCriteria == null)
        {
            defaultCriteria = new HashMap<String, DataCriteria>();
        }

        defaultCriteria.put(flow + "." + grp, c);
    }

    public String getTemplateName() { return templateName; }
    public String getWorkSpaceName() { return workSpaceName; }
    public String getPrimaryWorkObjectFlow() { return primaryWorkObjectFlow; }
    public String getPrimaryWorkObjectType() { return primaryWorkObjectType; }
    public List<RuleConfig> getRules() { return rules; }
    public List<DataCompute> getCompute() { return compute; }
    public Map<String, DataCriteria> getDefaultCriteria() { return defaultCriteria; }
}

