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
 * File:                org.anon.smart.secure.inbuilt.responses.PermittedFeatures
 * Author:              rsankar
 * Revision:            1.0
 * Date:                05-08-2014
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A response with permitted features
 *
 * ************************************************************
 * */

package org.anon.smart.secure.inbuilt.responses;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.anon.smart.secure.access.Access;
import org.anon.smart.secure.access.ALOrganizer;
import org.anon.smart.secure.inbuilt.data.SmartRole;

public class PermittedFeatures implements java.io.Serializable
{
    private Map<String, String> features;
    private boolean allPermitted = false;

    public PermittedFeatures()
    {
        features = new HashMap<String, String>();
    }

    public void addPermittedFrom(SmartRole role)
    {
        if (role == null)
            return;

        allPermitted = role.allAllowed();
        List<SmartRole.PermittedAccess> pa = role.permittedAccess();
        if (pa == null)
            return;

        for (SmartRole.PermittedAccess a : pa)
        {
            String p = a.getPermitted();
            Access acc = Access.valueOf(a.getAccess());
            boolean add = !(features.containsKey(p));
            if (!add)
            {
                Access check = Access.valueOf(features.get(p));
                add = (ALOrganizer.implies(acc, check)); //if the current perm implies the one present, then put
            }

            if (add) features.put(p, a.getAccess());
        }
    }
}

