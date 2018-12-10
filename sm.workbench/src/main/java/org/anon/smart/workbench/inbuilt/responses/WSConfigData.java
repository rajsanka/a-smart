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
 * File:                org.anon.smart.workbench.inbuilt.responses.WSConfigData
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of templates for the given flow
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.inbuilt.responses;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.anon.smart.workbench.data.DataCriteria;
import org.anon.smart.workbench.data.WorkSpaceConfig;
import org.anon.smart.workbench.data.WorkSpaceTemplate;
import org.anon.smart.workbench.data.RuleConfig;

public class WSConfigData implements java.io.Serializable
{
    static class ConfigDetails implements java.io.Serializable
    {
        private UUID configId;
        private String templateName;
        private String workspaceName;
        private String primaryWorkObjectFlow;
        private String primaryWorkObjectType;
        private List<RuleConfig> rules;
        private Map<String, List<DataCriteria>> criteria;
    }

    private List<ConfigDetails> config;

    public WSConfigData()
    {
        config = new ArrayList<ConfigDetails>();
    }

    public void addFromTemplate(WorkSpaceTemplate template)
    {
        ConfigDetails det = new ConfigDetails();
        det.templateName = template.getTemplateName();
        det.workspaceName = template.getWorkSpaceName();
        det.primaryWorkObjectFlow = template.getPrimaryWorkObjectFlow();
        det.primaryWorkObjectType = template.getPrimaryWorkObjectType();
        det.rules = template.getRules();
        det.criteria = new HashMap<String, List<DataCriteria>>();
        Map<String, DataCriteria> crit = template.getDefaultCriteria();
        for (String obj : crit.keySet())
        {
            List<DataCriteria> lst = new ArrayList<DataCriteria>();
            lst.add(crit.get(obj));
            det.criteria.put(obj, lst);
        }
        config.add(det);
        System.out.println("Adding template: " + det);
    }

    public void addFromConfig(WorkSpaceConfig cfg)
    {
        ConfigDetails det = new ConfigDetails();
        det.configId = cfg.getConfigId();
        det.templateName = cfg.getTemplateName();
        det.workspaceName = cfg.getWorkSpaceName();
        det.primaryWorkObjectFlow = cfg.getPrimaryWorkObjectFlow();
        det.primaryWorkObjectType = cfg.getPrimaryWorkObjectType();
        det.rules = cfg.getRules();
        config.add(det);
    }
}

