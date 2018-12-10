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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.DBTypeService
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A type converter for SQL
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils;

import java.util.Map;
import java.util.HashMap;
import java.sql.ResultSet;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class DBTypeService
{
    private static final DBTypeService INSTANCE = new DBTypeService();

    private static Map<Class, String> _getterCalls = new HashMap<Class, String>();
    private static Map<Class, String> _setterCalls = new HashMap<Class, String>();

    static
    {
        //TODO: add more here.
        _getterCalls.put(String.class, "getString");
        _getterCalls.put(Integer.class, "getInt");
        _getterCalls.put(Double.class, "getDouble");
        _getterCalls.put(int.class, "getInt");
        _getterCalls.put(double.class, "getDouble");

        _setterCalls.put(String.class, "setString");
        _setterCalls.put(Integer.class, "setInt");
        _setterCalls.put(Double.class, "setDouble");
        _setterCalls.put(int.class, "setInt");
        _setterCalls.put(double.class, "setDouble");
    }

    private DBTypeService() 
    {
    }

    public static <T> T readData(ResultSet rs, String colnm, Class type)
        throws CtxException
    {
        Object ret = null;
        try
        {
            String mthdname = _getterCalls.get(type);
            if (mthdname != null)
            {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandle handle = lookup.findVirtual(ResultSet.class, mthdname, MethodType.methodType(type, String.class));
                ret = handle.invoke(rs, colnm);
            }
            else
            {
                ret = rs.getObject(colnm);
            }
        }
        catch (Throwable e)
        {
            except().rt(e, INSTANCE, new CtxException.Context("DBTypeService", "readData"));
        }

        //System.out.println("Got data: " + ret);
        return (T)ret;
    }
}

