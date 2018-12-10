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
 * File:                org.anon.smart.d2cache.store.repository.datasource.DataMetadata
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
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.Field;
import java.io.ByteArrayOutputStream;

import com.google.gson.Gson;  

import org.anon.smart.d2cache.CacheableObject;

import org.anon.utilities.crosslink.CrossLinkAny;
import org.anon.utilities.exception.CtxException;
import org.anon.utilities.serialize.srdr.DirtyField;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;
import static org.anon.utilities.objservices.ConvertService.*;

public interface DataMetadata extends DSErrorCodes
{
    public static enum sqltypes
    {
        inssql, selsql, delsql, updsql, lookupsql, updkeysql, subsql, linksql;
    }

    public Class<? extends CacheableObject> clazz();
    public String table();
    public String key();
    public Map<String, AttributeMetadata> attributes();
    public AttributeMetadata metadataFor(String attribute);
    public AttributeMetadata columnMetadataFor(String column);
    public CompiledSQL compiled(sqltypes type);
    public AttributeMetadata[] subAttributes();
    public void createTable(String gname, String dbName)
        throws CtxException;
    public DataMetadata replicateMe(String gname);

    default AttributeMetadata metadataForKey()
    {
        System.out.println("Got metadatafor: " + this.key() + ":" + clazz().getName());
        return metadataFor(this.key());
    }

    default Object keyValueFor(Object o)
        throws CtxException
    {
        try
        {
            AttributeMetadata meta = metadataForKey();
            meta.attributeField().setAccessible(true);
            return meta.attributeField().get(o);
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DataMetadata", "keyValueFor"));
        }

        return null;
    }

    default String keySQLFragment()
    {
        return metadataFor(this.key()).getSQLFragment();
    }

    default String updateSQL(boolean addkey)
    {
        //by default gives back all the fields.
        String ret = null;
        if (addkey)
            ret = this.compiled(sqltypes.updkeysql).sql();
        else
            ret = this.compiled(sqltypes.updsql).sql();

        return ret;
    }

    default String insertSQL()
    {
        return this.compiled(sqltypes.inssql).sql();
    }

    default String selectSQL()
    {
        return this.compiled(sqltypes.selsql).sql();
    }

    default String deleteSQL()
    {
        return this.compiled(sqltypes.delsql).sql();
    }

    default String lookupSQL()
    {
        return this.compiled(sqltypes.lookupsql).sql();
    }

    default SQLDescriptor updateSQL(Set<DirtyField> flds, boolean addkey)
        throws CtxException
    {
        SQLDescriptor desc = null;
        if (addkey)
            desc = this.compiled(sqltypes.updkeysql).sql(flds);
        else
            desc = this.compiled(sqltypes.updsql).sql(flds);
        return desc;
    }

    default Object[] toArray(SQLDescriptor desc, Object obj, boolean addkey)
        throws CtxException
    {
        return toArray(desc.fields(), null, obj, addkey);
    }

    default Object[] toArray(sqltypes type, Object obj, boolean addkey)
        throws CtxException
    {
        CompiledSQL sql = this.compiled(type);
        Field[] sequence = sql.paramSequence();
        String[] path = sql.paramPathSequence();
        return toArray(sequence, path, obj, addkey);
    }

    default void changeData(AttributeMetadata lmeta, Field fld, Object obj, Object val)
        throws CtxException
    {
        try
        {
            fld.setAccessible(true);
            Object rval = fld.get(obj);
            if (rval instanceof CacheableObject)
            {
                lmeta.attributeField().setAccessible(true);
                lmeta.attributeField().set(rval, val);
            }
            else if (rval instanceof Collection)
            {
                Collection coll = (Collection)rval;
                for (Object o : coll)
                {
                    lmeta.attributeField().setAccessible(true);
                    lmeta.attributeField().set(o, val);
                }
            }
            else if (rval instanceof Map)
            {
                Map map = (Map)rval;
                for (Object o : map.values())
                {
                    lmeta.attributeField().setAccessible(true);
                    lmeta.attributeField().set(o, val);
                }
            }
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DataMetadata", "ChangeData"));
        }
    }

    default void associateRequiredData(AttributeMetadata rel, Object val, Object obj)
        throws CtxException
    {
        DataMetadata rmeta = rel.associatedMetadata();
        assertion().assertNotNull(rmeta, obj, "Cannot find the related metadata for attribute " + rel);
        String rcolName = rel.referenceColumnName();
        AttributeMetadata lmeta = rmeta.columnMetadataFor(rcolName);
        assertion().assertNotNull(lmeta, obj, "Cannot find the related metadata for attribute " + rcolName + ":" + rmeta.table() + ":" + val + ":" + obj);

        changeData(lmeta, rel.attributeField(), obj, val);
    }

    default Object getParamValue(Field fld, String path, Object obj)
        throws CtxException
    {
        try
        {
            if (obj == null)
                return obj;

            //System.out.println("Looking for: " + fld + ":" + path + ":" + obj);
            Object val = null;
            if ((path != null) && (path.length() > 0))
                val = reflect().getAnyFieldValueWithPath(obj.getClass(), obj, path);
            else
                val = fld.get(obj);
            String attribute = fld.getName();
            AttributeMetadata ameta = this.metadataFor(attribute);
            if ((val != null) && (ameta.relatedTo() != null) && (ameta.relatedTo().size() > 0) && (!ameta.isBackwardReference()))
            {
                //setup all the related to the current value.
                Collection<AttributeMetadata> related = ameta.relatedTo();
                for (AttributeMetadata rel : related)
                {
                    associateRequiredData(rel, val, obj);
                }
            }

            //System.out.println("Parameter Value for: " + obj + ":" + val + ":" + ameta.isSubObject() + ":" + ameta.storeJSON() + ":" + ameta.isBackwardReference() + ":" + ameta.relatedVia());

            if ((val != null) && ameta.isSubObject() && (ameta.relatedVia() == null) && (!ameta.storeJSON()))
            {
                //means we are inside a sub object whose reference is stored in a column
                //which is not handled by any other column.
                DataMetadata meta = ameta.associatedMetadata();
                assertion().assertNotNull(meta, obj, "Cannot find the related metadata for attribute " + attribute);
                String colName = ameta.referenceColumnName();
                AttributeMetadata lmeta = meta.columnMetadataFor(colName);
                assertion().assertNotNull(lmeta, obj, "Cannot find the related metadata for attribute " + attribute + ":" + colName);

                lmeta.attributeField().setAccessible(true);
                //System.out.println("getParamValue: " + val + ":" + (val instanceof CacheableObject) + ":" + colName);
                if (val instanceof CacheableObject)
                {
                    val = lmeta.attributeField().get(val);
                    //System.out.println("getParamValue: read data: " + val + ":");
                }
                else if (val instanceof Collection)
                {
                    //assumption in this case is that all objects have the same relation
                    Collection coll = (Collection)val;
                    if (coll.size() > 0)
                    {
                        val = lmeta.attributeField().get(coll.iterator().next());
                    }
                }
                else if (val instanceof Map)
                {
                    Map map = (Map)val;
                    if (map.size() > 0)
                    {
                        val = lmeta.attributeField().get(map.values().iterator().next());
                    }
                }
            }

            if ((val != null) && ameta.storeJSON())
            {
                //ByteArrayOutputStream ostr = new ByteArrayOutputStream();
                //convert().writeObject(val, ostr, translator.json);
                //val = ostr.toString();
                //ostr.close();
                //if the field type is not the same as the value type.
                String clsname = val.getClass().getName();
                Gson gson = new Gson();
                val = gson.toJson(val);
                val = clsname + ":::" + val; 
            }

            if (!type().checkPrimitive(ameta.attributeType()) || convert().canConvertFromString(ameta.attributeType()))
            {
                //convert to string
                val = convert().objectToString(val);
            }
            //if (val != null)
                //System.out.println("for: " + ameta.attributeType() + ":" + val + ":" + fld.getName() + ":" + val.getClass());

            return val;
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DataMetadata", "getParamValue"));
        }

        return null;
    }

    default Object[] toArray(Field[] sequence, String[] path, Object obj, boolean addkey)
        throws CtxException
    {
        try
        {
            List<Object> ret = new ArrayList<Object>();
            for (int i = 0; i < sequence.length; i++)
            {
                //can't use mthod handles since these are private fields
                //:((( very bad.
                String p = null;
                if ((path != null) && (path.length > i))
                    p = path[i];
                sequence[i].setAccessible(true);
                //System.out.println("got paths: " + path + ":" + i + ":" + p + ":" + sequence[i].getName());
                ret.add(getParamValue(sequence[i], p, obj));
            }

            if (addkey)
            {
                AttributeMetadata keymeta = metadataForKey();
                Field fld = keymeta.attributeField();
                fld.setAccessible(true);
                ret.add(getParamValue(fld, null, obj));
            }

            return ret.toArray();
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DataMetadata", "toArray"));
        }

        return null;
    }

    default String[] linkedSQLs()
    {
        AttributeMetadata[] subattrs = this.subAttributes();
        //no sub attributes found
        if ((subattrs == null) || (subattrs.length <= 0))
            return new String[0];

        String[] ret = new String[subattrs.length];
        for (int i = 0; i < subattrs.length; i++)
            ret[i] = subattrs[i].linkSQL().sql();

        return ret;
    }

    default Class[] related()
    {
        AttributeMetadata[] subattrs = this.subAttributes();
        //no sub attributes found
        if ((subattrs == null) || (subattrs.length <= 0))
            return new Class[0];

        Class[] ret = new Class[subattrs.length];
        for (int i = 0; i < subattrs.length; i++)
        {
            ret[i] = subattrs[i].attributeType();
        }

        return ret;
    }

    static boolean shouldExpandIntoParent(String cls)
    {
        boolean expandintoparent = false;
        try
        {
            CrossLinkAny clt = new CrossLinkAny(cls);
            expandintoparent = ((Boolean)clt.invoke("smart___expandIntoParent"));
        }
        catch (Exception e)
        {
            //ignore if there is a problem in the method
        }

        return expandintoparent;
    }
}

