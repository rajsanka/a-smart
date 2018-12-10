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
 * File:                org.anon.smart.workbench.data.DataCriteria
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                04-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A criteria for data that belongs to the workspace
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.data;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class DataCriteria implements java.io.Serializable
{
    public class SingleCriteria implements java.io.Serializable
    {
        private String group;
        private String attribute;
        private String operator;
        private Object compare;
    }

    private List<SingleCriteria> criteria;

    public DataCriteria()
    {
        criteria = new ArrayList<SingleCriteria>();
    }

    public void addCriteria(String grp, String attr, String oper, Object val)
    {
        SingleCriteria sc = new SingleCriteria();
        sc.group = grp;
        sc.attribute = attr;
        sc.operator = oper;
        sc.compare = val;
        criteria.add(sc);
    }

    private boolean match(Object val, String operator, Object compare)
    {
        if ((val == null) || (compare == null))
            return false; //right now nulls not handled

        boolean ret = false;
        if (operator.equals("="))
        {
            ret = val.equals(compare);
        }
        else if (operator.equals("LIKE"))
        {
            String comp = compare.toString().replaceAll("\\*", "");
            ret = val.toString().contains(comp);
        }
        else if (operator.equals(">"))
        {

        }
        else if (operator.equals("<"))
        {

        }

        return ret;
    }

    public boolean matches(WorkSpaceObject wso)
        throws CtxException
    {
        //need to compute here. Can do a expression evaluation? Or we can do a javascript evaluation here?
        Class cls = wso.getClass();
        boolean ret = true;
        for (SingleCriteria c : criteria)
        {
            Object fval = reflect().getAnyFieldValue(cls, wso, c.attribute);
            ret = ret && match(fval, c.operator, c.compare);
            if (!ret) break;
        }
        return ret;
    }

    public Map<String, Object> getQuery()
    {
        //need to use queries that involve multiple objects
        Map<String, Object> query = new HashMap<String, Object>();
        for (SingleCriteria c : criteria)
        {
            query.put(c.attribute + " " + c.operator, c.compare);
        }

        return query;
    }
}

