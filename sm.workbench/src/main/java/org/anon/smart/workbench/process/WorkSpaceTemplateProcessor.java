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
 * File:                org.anon.smart.workbench.process.WorkSpaceTemplateProcessor
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                16-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A processor for workspace templates
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.process;

import java.net.URL;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;

import org.anon.smart.workbench.data.DataCriteria;
import org.anon.smart.workbench.data.WorkSpaceConfig;
import org.anon.smart.workbench.data.WorkSpaceTemplate;
import org.anon.smart.workbench.data.RuleConfig;

import org.anon.smart.base.tenant.CrossLinkSmartTenant;
import org.anon.smart.base.tenant.shell.CrossLinkDeploymentShell;

import org.anon.utilities.config.Format;
import org.anon.utilities.verify.VerifiableObject;
import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;

public class WorkSpaceTemplateProcessor
{
    private static class DeploymentTemplates
    {
        String deployment;
        Map<String, WorkSpaceTemplate> templates;
    }
    private static Map<String, DeploymentTemplates> TEMPLATES = new ConcurrentHashMap<String, DeploymentTemplates>();

    private static class WSCriteria
    {
        private String object;
        private String flow;
        private String attribute;
        private String operator;
        private Object value;
    }

    private static class WSStateChanged
    {
        private String rule;
        private String state;
    }

    private static class WSRuleConfig
    {
        private String name;
        private String file;
        private List<WSStateChanged> changestate;

        RuleConfig createConfig(String f)
        {
            RuleConfig c = new RuleConfig(name, f);
            if (changestate != null)
            {
                for (WSStateChanged sc : changestate)
                {
                    c.addStateChange(sc.rule, sc.state);
                }
            }
            return c;
        }
    }

    private static class WSConfig implements VerifiableObject
    {
        private String name;
        private String workspaceName;
        private String primeObjectFlow;
        private String primeObject;
        private List<WSRuleConfig> rules;
        private List<WSCriteria> defaultCriteria;

        public boolean isVerified()
        {
            return true;
        }

        public boolean verify()
            throws CtxException
        {
            return true;
        }
    }

    public WorkSpaceTemplateProcessor()
    {
    }

    private static DeploymentTemplates loadTemplates(String dep)
        throws CtxException
    {
        try
        {
            DeploymentTemplates dtemplates = TEMPLATES.get(dep);
            if (dtemplates != null)
                return dtemplates; //already loaded.

            CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
            CrossLinkDeploymentShell shell = tenant.deploymentShell();
            List<String> templates = shell.getWorkspaces(dep);
            dtemplates = new DeploymentTemplates();
            dtemplates.deployment = dep;
            dtemplates.templates = new ConcurrentHashMap<String, WorkSpaceTemplate>();
            for (int i = 0; (templates != null) && (i < templates.size()); i++)
            {
                String templatefilename = templates.get(i);
                String templatefile = templatefilename.replaceAll("\\.", "\\/");
                templatefile += ".soa";
                System.out.println("Got Template as: " + templatefilename + " Looking for: " + templatefile);
                InputStream str = WorkSpaceTemplateProcessor.class.getClassLoader().getResourceAsStream(templatefile);
                if (str == null)
                {
                    System.out.println("ERROR!!! Cannot find the resource " + templatefile);
                    continue;
                }
                Format fmt = config().readYMLConfig(str);
                Map values = fmt.allValues();
                WSConfig config = convert().mapToVerifiedObject(WSConfig.class, values);
                WorkSpaceTemplate template = new WorkSpaceTemplate();
                template.setTemplateName(config.name);
                template.setWorkSpaceName(config.workspaceName);
                template.setPrimaryWorkObjectFlow(config.primeObjectFlow);
                template.setPrimaryWorkObjectType(config.primeObject);
                List<RuleConfig> rules = new ArrayList<RuleConfig>();
                List<WSRuleConfig> srchpkgs = config.rules;
                for (int k = 0; (srchpkgs != null) && (k < srchpkgs.size()); k++)
                {
                    WSRuleConfig wsrc = srchpkgs.get(k);
                    String pkg = wsrc.file;
                    pkg = pkg.replaceAll("\\.", "\\/");
                    pkg += ".drl"; //assume everything is a drl file for now.
                    URL url = WorkSpaceTemplateProcessor.class.getClassLoader().getResource(pkg);
                    if (url == null)
                    {
                        System.out.println("ERROR!!! Cannot find the resource " + pkg);
                        continue;
                    }

                    RuleConfig rc = wsrc.createConfig(pkg);
                    rules.add(rc);
                }
                template.setRules(rules);

                for (int k = 0; k < config.defaultCriteria.size(); k++)
                {
                    WSCriteria c = config.defaultCriteria.get(k);
                    DataCriteria criteria = new DataCriteria();
                    criteria.addCriteria(c.object, c.attribute, c.operator, c.value);
                    template.addCriteria(c.flow, c.object, criteria) ;
                }

                dtemplates.templates.put(config.name, template);
            }
            TEMPLATES.put(dep, dtemplates);
            return dtemplates;
        }
        catch (Exception e)
        {
            except().rt(e, new CtxException.Context("", ""));
        }

        return null;
    }

    public static WorkSpaceConfig createWorkSpaceFromTemplate(String dep, String template, String wsname)
        throws CtxException
    {
        DeploymentTemplates dtemplates = loadTemplates(dep);
        WorkSpaceTemplate wstemplate = dtemplates.templates.get(template);
        WorkSpaceConfig cfg = new WorkSpaceConfig(wstemplate, wsname);
        return cfg;
    }

    public static Map<String, WorkSpaceTemplate> getTemplatesFor(String dep)
        throws CtxException
    {
        DeploymentTemplates dtemplates = loadTemplates(dep);
        return dtemplates.templates;
    }
}

