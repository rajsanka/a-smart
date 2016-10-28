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
 * File:                org.anon.smart.monitor.data.CounterMonitor
 * Author:              rsankar
 * Revision:            1.0
 * Date:                04-09-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A monitor that counts different occurrences
 *
 * ************************************************************
 * */

package org.anon.smart.monitor.data;

import java.util.List;
import java.util.ArrayList;

import org.anon.smart.base.annot.KeyAnnotate;
import org.anon.smart.monitor.stt.Constants;

import org.anon.utilities.utils.Repeatable;
import org.anon.utilities.utils.RepeaterVariants;
import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class CounterMonitor implements java.io.Serializable, Monitor, Constants
{
    protected DataLegend ___smart_legend___;
    @KeyAnnotate(keys="___smart_key___")
    protected String ___smart_key___;
    private String ___smart_group___;
    private int ___smart_counter___;
    private transient boolean isNew;

    private final String GLOBAL = "global";

    CounterMonitor()
    {
    }

    public CounterMonitor(String g, String evt)
        throws CtxException
    {
        assertion().assertNotNull(g, "Cannot create a monitor for null group");
        assertion().assertNotNull(evt, "Cannot create a monitor for null event");
        ___smart_group___ = g;
        ___smart_key___ = constructKey(g, evt, GLOBAL);
        ___smart_counter___ = 0;
        isNew = true;
        ___smart_legend___ = new DataLegend();
    }

    public CounterMonitor(String g, String evt, String b)
        throws CtxException
    {
        assertion().assertNotNull(g, "Cannot create a monitor for null group");
        assertion().assertNotNull(evt, "Cannot create a monitor for null event");
        ___smart_group___ = g;
        ___smart_key___ = constructKey(g, evt, b);
        ___smart_counter___ = 0;
        isNew = true;
        ___smart_legend___ = new DataLegend();
    }

    protected String constructKey(String group, String evt, String b)
    {
        return group + SEP + evt + SEP + b;
    }

    public void incrementCounter()
    {
        ___smart_counter___++;
    }

    public void monitorAction()
    {
        incrementCounter();
    }

    public List<Object> smart___keys()
        throws CtxException
    {
        List<Object> keys = new ArrayList<Object>();
        keys.add(___smart_legend___.getId());
        keys.add(___smart_key___);
        return keys;
    }

    public String smart___objectGroup()
        throws CtxException
    {
        return ___smart_group___;
    }

    public boolean smart___isNew()
        throws CtxException
    {
        return isNew;
    }

    @Override
    public void smart___initOnLoad() 
        throws CtxException 
    {
        isNew = false;
    }


    public String getKey(String g, String mevt, String[] parms)
        throws CtxException
    {
        return constructKey(g, mevt, GLOBAL);
    }

    public Repeatable repeatMe(RepeaterVariants vars)
        throws CtxException
    {
        MonitorParms parms = (MonitorParms)vars;
        return new CounterMonitor(parms.getGroup(), parms.getMonitorEvent());
    }

    public String toString()
    {
        return "Legend:" + ___smart_legend___ + ":Key:" + ___smart_key___ + ":Group:" + ___smart_group___ + ":counter:" + ___smart_counter___;
    }

    public void smart___setIsNew(boolean b)
    {
        isNew = b;
    }
}

