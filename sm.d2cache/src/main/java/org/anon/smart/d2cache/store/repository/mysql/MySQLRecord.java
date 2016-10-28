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
 * File:                org.anon.smart.d2cache.store.repository.mysql.MySQLRecord
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A record that needs to be created into a table in mysql
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.mysql;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.AbstractStoreRecord;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager;

import org.anon.utilities.reflect.DataContext;
import org.anon.utilities.exception.CtxException;

public class MySQLRecord extends AbstractStoreRecord
{
    private CacheableObject _object;
    private CacheableObject _original;
    private Object _key;
    private String _group;

    public MySQLRecord(String group, Object primary, Object curr, Object orig, boolean isnew, PersistableManager mgr, MySQLConnection connect)
        throws CtxException
    {
        super(group, primary, curr, orig);
        _object = (CacheableObject)curr;
        _original = (CacheableObject)orig;
        _key = primary;
        _group = group;
        System.out.println("Appending: " + primary + ":" + curr + ":" + orig);
        mgr.append(_object, _original, isnew, new Object[] { _key });
    }

    public void append(DataContext ctx, boolean update)
        throws CtxException
    {
        //don't do anything here.
    }
}

