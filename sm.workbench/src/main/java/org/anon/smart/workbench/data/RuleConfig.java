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
 * File:                org.anon.smart.workbench.data.RuleConfig
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                23-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration when the rules are run
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.data;

import java.util.List;
import java.util.ArrayList;

public class RuleConfig implements java.io.Serializable
{
    static class StateChange implements java.io.Serializable
    {
        private String rule;
        private String state;
    }

    private String name;
    private String file;
    private List<StateChange> changestate;

    public RuleConfig(String nm, String f)
    {
        name = nm;
        file = f;
        changestate = new ArrayList<StateChange>();
    }

    public void addStateChange(String r, String s)
    {
        StateChange c = new StateChange();
        c.rule = r;
        c.state = s;
        changestate.add(c);
    }

    public String getFile() { return file; }
    public String getChangedState(String rule) 
    { 
        for (StateChange c : changestate)
        {
            if (c.rule.equals(rule))
                return c.state; 
        }

        return null;
    }
}

