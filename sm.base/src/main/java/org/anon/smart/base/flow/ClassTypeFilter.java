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
 * File:                org.anon.smart.base.flow.ClassTypeFilter
 * Author:              rsankar
 * Revision:            1.0
 * Date:                20-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A filter that gets a flow deployment that has the given type
 *
 * ************************************************************
 * */

package org.anon.smart.base.flow;

import org.anon.smart.deployment.DeploymentFilter;

import org.anon.utilities.exception.CtxException;

public class ClassTypeFilter implements DeploymentFilter<FlowDeployment>
{
    private String _name;

    public ClassTypeFilter(String name)
    {
        _name = name;
    }

    public boolean matches(FlowDeployment dep)
        throws CtxException
    {
        String cls = dep.classFor(_name);
        if (cls != null)
        {
            return dep.belongs(cls);
        }

        return false;
    }
}

