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
 * File:                org.anon.smart.secure.transition.parms.UserPreferencesProbe
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                14-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A probe for user preferences
 *
 * ************************************************************
 * */

package org.anon.smart.secure.transition.parms;

import java.lang.reflect.Type;

import org.anon.smart.base.tenant.shell.RuntimeShell;
import org.anon.smart.secure.inbuilt.data.Session;
import org.anon.smart.secure.inbuilt.data.UserPreference;
import org.anon.smart.secure.session.SessionDirector;
import org.anon.smart.smcore.transition.atomicity.AtomicityConstants;

import org.anon.utilities.gconcurrent.execute.ParamType;
import org.anon.utilities.gconcurrent.execute.PDescriptor;
import org.anon.utilities.gconcurrent.execute.ProbeParms;
import org.anon.utilities.gconcurrent.execute.PProbe;
import org.anon.utilities.exception.CtxException;

import static org.anon.smart.base.utils.AnnotationUtils.*;
import static org.anon.utilities.services.ServiceLocator.*;

public class UserPreferencesProbe implements PProbe, AtomicityConstants
{
    public UserPreferencesProbe()
    {
    }

    private UserPreference getUserPreferences(String prefid)
        throws CtxException
    {
        Session sess = SessionDirector.currentSession();
        if (sess != null)
        {
            String userid = sess.getUserId();
            String key = UserPreference.constructKey(userid, prefid);
            RuntimeShell rshell = RuntimeShell.currentRuntimeShell();
            String group = className(UserPreference.class);
            String flow = flowFor(UserPreference.class);
            UserPreference preferences = (UserPreference)rshell.lookupFor(flow, group, key);
            return preferences;
        }

        return null;
    }

    public Object valueFor(Class cls, Type t, ProbeParms parms, PDescriptor desc)
        throws CtxException
    {
        if (desc != null)
        {
            String attr = desc.attribute();
            UserPreference prefs = getUserPreferences(attr);
            System.out.println("Getting the userpreferences: " + prefs + ":" + attr);
            if (prefs != null)
            {
                Object val = prefs.getPreference();
                if ((val != null) && type().isAssignable(val.getClass(), cls))
                    return val;
            }
        }
        return null;
    }

    public Object valueFor(Class cls, Type type, ProbeParms parms)
        throws CtxException
    {
        //if descriptor is not given then we do not know what to search.
        return null;
    }

    public Object valueFor(ProbeParms parms, Type type, PDescriptor desc)
        throws CtxException
    {
        return valueFor(null, type, parms, desc);
    }

    public void releaseValues(Object[] val)
        throws CtxException
    {
    }
}

