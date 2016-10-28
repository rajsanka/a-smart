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
 * File:                org.anon.smart.d2cache.store.repository.datasource.PersistableData
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration file for attributes of tables
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.io.Serializable;

import org.anon.smart.d2cache.CacheableObject;
import static org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager.*;

import org.anon.utilities.serialize.srdr.DirtyField;
import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;

public interface PersistableData extends CacheableObject, java.io.Serializable
{
    public Serializable smart___original();
    public boolean smart___isNew();
    public void smart___resetNew();
    public Object[] smart___keys();


    static <T extends CacheableObject> DataMetadata metadata(T obj)
        throws CtxException
    {
        return DataSchema.metadataFor(obj.getClass(), _globalName, _dbName);
    }

    static <T extends CacheableObject> SQLDescriptor updateSQL(T obj, Set<DirtyField> flds, boolean addkey)
        throws CtxException
    {
        return metadata(obj).updateSQL(flds, addkey);
    }

    static <T extends CacheableObject> String updateSQL(T obj, boolean addkey)
        throws CtxException
    {
        return metadata(obj).updateSQL(addkey);
    }

    static <T extends CacheableObject> String insertSQL(T obj)
        throws CtxException
    {
        return metadata(obj).insertSQL();
    }

    static <T extends CacheableObject> String deleteSQL(T obj)
        throws CtxException
    {
        return metadata(obj).deleteSQL();
    }

    static <T extends CacheableObject> Object[] insertArray(T obj)
        throws CtxException
    {
        return metadata(obj).toArray(DataMetadata.sqltypes.inssql, obj, false);
    }

    static <T extends CacheableObject> Object[] updateArray(T obj, boolean addkey)
        throws CtxException
    {
        return metadata(obj).toArray(DataMetadata.sqltypes.updkeysql, obj, addkey);
    }

    static <T extends CacheableObject> Object[] updateArray(T obj, SQLDescriptor desc, boolean addkey)
        throws CtxException
    {
        return metadata(obj).toArray(desc, obj, addkey);
    }

    static <T extends CacheableObject> boolean isModified(T obj, T orig)
        throws CtxException
    {
        Serializable original = (Serializable)orig;
        Serializable compare = (Serializable) obj;
        return !(serial().same(original, compare));
    }

    static <T extends CacheableObject> Set<DirtyField> computeChange(T obj, T orig)
        throws CtxException
    {
        Serializable original = (Serializable)orig;
        Serializable compare = (Serializable)obj;
        List<DirtyField> dfs = serial().oldwaydirtyFields(original, compare);
        Set<DirtyField> ret = new HashSet<>();
        ret.addAll(dfs);
        return ret;
    }

    static DataMetadata metadataFor(Class<? extends CacheableObject> cls)
        throws CtxException
    {
        return DataSchema.metadataFor(cls, _globalName, _dbName);
    }
}

