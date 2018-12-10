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
 * File:                org.anon.smart.secure.inbuilt.data.UserPreference
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                14-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of preferences for the given user
 *
 * ************************************************************
 * */

package org.anon.smart.secure.inbuilt.data;

import java.util.Map;
import java.util.HashMap;

public class UserPreference implements java.io.Serializable
{
    private String key;
    private String userId;
    private String preferenceId;
    private Object preference;

    public UserPreference(String id, String prefid)
    {
        userId = id;
        preferenceId = prefid;
        key = constructKey(userId, preferenceId);
    }

    public void setPreference(Object pref) { preference = pref; }

    public static String constructKey(String uid, String pid)
    {
        return uid + "-" + pid;
    }

    public Object getPreference()
    {
        return preference;
    }

    public String getKey() { return key; }
    public String getUserId() { return userId; }
    public String getPreferenceId() { return preferenceId; }

}

