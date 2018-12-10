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
 * File:                org.anon.smart.workbench.process.WorkSpaceProcessor
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A processor for workbench related functions
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.process;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;

import org.anon.smart.base.tenant.CrossLinkSmartTenant;
import org.anon.smart.base.tenant.shell.RuntimeShell;
import org.anon.smart.smcore.data.SmartData;
import org.anon.smart.smcore.data.SmartDataED;
import org.anon.smart.smcore.data.SmartPrimeData;
import org.anon.smart.smcore.data.datalinks.DataLinker;
import org.anon.smart.smcore.transition.TransitionContext;
import org.anon.smart.smcore.inbuilt.transition.SearchManager;

import org.anon.smart.workbench.data.WorkSpace;
import org.anon.smart.workbench.data.WorkSpaceConfig;
import org.anon.smart.workbench.data.WorkSpaceTemplate;
import org.anon.smart.workbench.data.WorkSpaceObject;
import org.anon.smart.workbench.data.DataCriteria;
import org.anon.smart.workbench.data.RuleConfig;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;
import static org.anon.smart.base.utils.AnnotationUtils.*;

public class WorkSpaceProcessor
{
    public WorkSpaceProcessor()
    {
    }

    public static WorkSpaceObject getObject(String flow, String type, Object key)
        throws CtxException
    {
        CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        RuntimeShell shell = (RuntimeShell)(tenant.runtimeShell());
        WorkSpaceObject wso = (WorkSpaceObject)shell.lookupFor(flow, type, key);
        TransitionContext ctx = (TransitionContext)threads().threadContext();
        if (ctx != null)
        {
            SmartDataED ed = ctx.atomicity().includeData((SmartData)wso);
            Object o = ed.empirical();
            wso = (WorkSpaceObject)o;
        }
        return wso;
    }

    public static WorkSpace getWorkSpace(String wsName)
        throws CtxException
    {
        CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        RuntimeShell shell = (RuntimeShell)(tenant.runtimeShell());
        WorkSpace ws = (WorkSpace)shell.lookupFor("WorkbenchFlow", "WorkSpace", wsName);
        TransitionContext ctx = (TransitionContext)threads().threadContext();
        if (ctx != null)
        {
            SmartDataED ed = ctx.atomicity().includeData((SmartData)ws);
            Object o = ed.empirical();
            ws = (WorkSpace)o;
        }
        return ws;
    }


    public static WorkSpace[] findAndAddToWorkSpace(WorkSpaceObject obj)
        throws CtxException
    {
        CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        RuntimeShell shell = (RuntimeShell)(tenant.runtimeShell());
        assertion().assertNotNull(shell, "WorkSpaceProcessor: Runtime Shell is NULL");
        SearchManager mgr = new SearchManager();

        Object val = obj; //assumption is that a workspaceobject is also a smartdata object.
        SmartPrimeData data = (SmartPrimeData)val;
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("primaryWorkObjectType", className(data.getClass()));
        query.put("primaryWorkObjectFlow", flowFor(data.getClass()));

        System.out.println("WorkbenchProcessor: Searching workspace for: " + className(data.getClass()) + ":" + flowFor(data.getClass()));

        List<WorkSpace> ret = new ArrayList<WorkSpace>();
        List result = new ArrayList();
        //assumption here is that the number of workspaces will not exceed 1000? We have to reevaluate this and work with pages otherwise
        //TODO: change operation to be a bulk operation
        mgr.searchService("WorkbenchFlow", "WorkSpaceConfig", query, result, 1, 1000, null, null);
        for (Object o : result)
        {
            WorkSpaceConfig cfg = (WorkSpaceConfig)o;
            boolean[] add = addRemoveWorkSpace(cfg, obj, null);
            WorkSpace ws = null;
            System.out.println("addremove: " + add[0] + ":" + add[1] + ":" + add[2]);
            if (add[0] || add[1] || add[2])
            {
                ws = (WorkSpace)shell.lookupFor("WorkbenchFlow", "WorkSpace", cfg.getWorkSpaceName());
                if (add[0]) ws.incrementObjectCount();
                if (add[1]) ws.decrementObjectCount();
            }
            if (add[2]) ret.add(ws);
        }

        System.out.println("Returing: " + ret);
        return ret.toArray(new WorkSpace[0]);
    }

    private static boolean[] addRemoveWorkSpace(WorkSpaceConfig cfg, WorkSpaceObject obj, WorkSpace ws)
        throws CtxException
    {
        boolean[] ret = new boolean[] { false, false, false };
        if (cfg.matchesCriteria(obj))
        {
            System.out.println("WorkbenchProcessor: matches criteria: Adding to workspace: " + cfg.getWorkSpaceName());
            ret[0] = obj.smart___addToWorkSpace(cfg.getWorkSpaceName());
            if (ret[0] && (ws != null)) ws.incrementObjectCount();
            ret[2] = true;
        }
        else
        {
            ret[1] = obj.smart___removeFromWorkSpace(cfg.getWorkSpaceName());
            if (ret[1] && (ws != null)) ws.decrementObjectCount();
            System.out.println("WorkbenchProcessor: does not match criteria: Removing from workspace: " + cfg.getWorkSpaceName() + ":" + ret[1]);
        }

        return ret;
    }

    public static WorkSpace createWorkSpace(WorkSpaceConfig cfg)
        throws CtxException
    {
        CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        RuntimeShell shell = (RuntimeShell)(tenant.runtimeShell());
        assertion().assertNotNull(shell, "WorkSpaceProcessor: Runtime Shell is NULL");

        WorkSpace exist = (WorkSpace)shell.lookupFor("WorkbenchFlow", "WorkSpace", cfg.getWorkSpaceName());
        assertion().assertTrue((exist == null), "Workspace with " + cfg.getWorkSpaceName() + " already exists.");

        WorkSpace space = new WorkSpace(cfg.getWorkSpaceName(), cfg.getConfigId());
        return space;
    }

    private static void addObjectsToSession(String key, List<DataCriteria> criteria, KieSession session)
        throws CtxException
    {
        SearchManager mgr = new SearchManager();
        String[] split = key.split("\\.");
        String flow = split[0];
        String type = split[1];
        for (DataCriteria c : criteria)
        {
            Map<String, Object> query = c.getQuery();
            List result = new ArrayList();
            System.out.println("Searching for: " + flow + ":" + type + ":" + key + ":" + query);
            mgr.searchService(flow, type, query, result, 1, 1000, null, null);
            for (Object o : result)
            {
                session.insert(o);

                //may not be the most optimal way.
                DataLinker linker = new DataLinker();
                List<SmartData> related = linker.getLinkedObjects((SmartData)o);
                for (SmartData d : related) session.insert(d);
            }
        }
    }

    private static void addOtherObjectsToSession(WorkSpaceConfig cfg, KieSession session)
        throws CtxException
    {
        //add the other objects to the rules session
        Set<String> types = cfg.getObjectTypes();
        String pkey = cfg.getPrimaryWorkObjectFlow() + "." + cfg.getPrimaryWorkObjectType();
        for (String t : types)
        {
            //primary type is already added.
            if (t.equals(pkey))
                continue;

            List<DataCriteria> criteria = cfg.getCriteria(t);
            addObjectsToSession(t, criteria, session);
        }
    }

    public static void refreshWorkSpace(WorkSpace space)
        throws CtxException
    {
        CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        RuntimeShell shell = (RuntimeShell)(tenant.runtimeShell());
        assertion().assertNotNull(shell, "WorkSpaceProcessor: Runtime Shell is NULL");
        assertion().assertNotNull(space, "WorkSpaceProcessor: WorkSpace is NULL");

        //Assumption is that the config is not changed.
        final WorkSpaceConfig cfg = (WorkSpaceConfig)shell.lookupFor("WorkbenchFlow", "WorkSpaceConfig", space.getConfigId());

        SearchManager mgr = new SearchManager();
        //update objects to be with workspace name from all data criteria
        //TODO: need to change the d2cache and subsequently all txns so that this can be executed as a single sql statement
        KieSession session = getSessionFor(space, cfg);
        List<DataCriteria> criteria = cfg.getCriteria();
        for (DataCriteria c : criteria)
        {
            Map<String, Object> query = c.getQuery();
            
            List result = new ArrayList();
            String flow = cfg.getPrimaryWorkObjectFlow();
            String type = cfg.getPrimaryWorkObjectType();
            //again limited to 1000 objects, this needs to be changed.
            mgr.searchService(flow, type, query, result, 1, 1000, null, null);
            for (Object o : result)
            {
                WorkSpaceObject obj = (WorkSpaceObject)o;
                obj.smart___addToWorkSpace(cfg.getWorkSpaceName());
                obj.smart___clearAllErrors(space.getWorkSpaceName());
                System.out.println("Adding object " + obj + " to session: " + session);
                session.insert(obj);
            }
        }

        addOtherObjectsToSession(cfg, session);

        try
        {
            System.out.println("CreateWorkSpace: Firing rules.");
            session.fireAllRules();
        }
        catch (Exception e)
        {
            except().rt(e, new CtxException.Context(WorkSpaceProcessor.class.getName(), "refreshWorkSpace"));
        }
    }

    private static WorkSpaceConfig getConfigFor(WorkSpace space)
        throws CtxException
    {
        CrossLinkSmartTenant tenant = CrossLinkSmartTenant.currentTenant();
        RuntimeShell shell = (RuntimeShell)(tenant.runtimeShell());
        WorkSpaceConfig cfg = (WorkSpaceConfig) shell.lookupFor("WorkbenchFlow", "WorkSpaceConfig", space.getConfigId());
        return cfg;
    }

    private static void modifyAfterRuleFired(WorkSpace ws, WorkSpaceConfig cfg, List<RuleConfig> rules, String rName, List<Object> objs)
    {
       String state = null;
       for (RuleConfig rc : rules)
       {
           state = rc.getChangedState(rName);
           if (state != null) break;
       }

       System.out.println( "Changing state once event is fired for:  " + objs + ":" + rName + ":" + state);
       if (state == null) return;

       for (Object o : objs)
       {
           try
           {
               if (cfg.isPrimeObject(o))
               {
                   SmartData sd = (SmartData)o;
                   sd.smart___transition(state);
                   addRemoveWorkSpace(cfg, (WorkSpaceObject)o, ws);
               }
           }
           catch (Exception e)
           {
               //need to add errors to object?
               WorkSpaceObject wso = (WorkSpaceObject)o;
               wso.smart___addWSError(cfg.getWorkSpaceName(), 120001, e.getMessage());
               e.printStackTrace();
           }
       }
    }

    private static KieSession getSessionFor(WorkSpace space, WorkSpaceConfig cfg1)
        throws CtxException
    {
        try
        {
            final WorkSpaceConfig cfg = (cfg1 == null) ? getConfigFor(space) : cfg1;
            final WorkSpace ws = space;
            final List<RuleConfig> rules = cfg.getRules();
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            for ( int i = 0; i < rules.size(); i++ ) 
            {
                String ruleFile = rules.get(i).getFile();
                System.out.println( "getSessionFor: Loading file: " + ruleFile );
                kbuilder.add( ResourceFactory.newClassPathResource( ruleFile, WorkSpace.class ), ResourceType.DRL );
            }

            assertion().assertTrue(!kbuilder.hasErrors(), "The rules files have errors, cannot continue. " + kbuilder.getErrors());
            Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
            kbase.addKnowledgePackages( pkgs );
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

            ksession.addEventListener( new DefaultAgendaEventListener() {
                   public void afterMatchFired(AfterMatchFiredEvent event) {
                       super.afterMatchFired( event );
                       String rName = event.getMatch().getRule().getName();
                       WorkSpaceProcessor.modifyAfterRuleFired(ws, cfg, rules, rName, event.getMatch().getObjects());
                   }
            });     
            ksession.addEventListener( new DebugRuleRuntimeEventListener() );     
            ksession.addEventListener( new DebugAgendaEventListener() );     

            return ksession;
        }
        catch (Exception e)
        {
            except().rt(e, new CtxException.Context(WorkSpaceProcessor.class.getName(), "getSessionFor"));
        }

        return null;
    }

    //We run assuming that the workspaceobject has a set of linked object which are the only ones that will get affected by
    //the change? Or should we add all the objects?
    public static void runWorkSpaceRules(WorkSpace space, WorkSpaceObject obj)
        throws CtxException
    {
        try
        {
            Object o = obj;
            WorkSpaceConfig cfg = getConfigFor(space);
            obj.smart___clearAllErrors(space.getWorkSpaceName());
            KieSession session = getSessionFor(space, cfg);

            //TODO: how to handle removed objects?
            session.insert(obj);
            session.insert(space);

            DataLinker linker = new DataLinker();
            List<SmartData> related = linker.getLinkedObjects((SmartData)o);
            for (SmartData d : related) session.insert(d);

            addOtherObjectsToSession(cfg, session);

            session.fireAllRules();
        }
        catch (Exception e)
        {
            except().rt(e, new CtxException.Context(WorkSpaceProcessor.class.getName(), "runWorkSpaceRules"));
        }
    }
}

