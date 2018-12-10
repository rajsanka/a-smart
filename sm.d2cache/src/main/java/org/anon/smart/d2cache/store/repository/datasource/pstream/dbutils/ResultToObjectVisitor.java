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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.ResultToObjectVisitor
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A visitor that creates an object from the resultset
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.io.ByteArrayInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.DSErrorCodes;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream;

import org.anon.utilities.crosslink.CrossLinkAny;
import org.anon.utilities.exception.CtxException;
import org.anon.utilities.reflect.CVisitor;
import org.anon.utilities.reflect.DataContext;

import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;
import static org.anon.utilities.objservices.ConvertService.*;

public class ResultToObjectVisitor implements CVisitor, DSErrorCodes
{
    //Creates an object from the current row in the result set.
    //If the current row is after the end, this will cause the
    //resultset exception
    private ResultSet _resultSet;
    private Object _created;
    private DataMetadata _metadata;
    private Map<String, PersistableDataStream> _subStreams;

    public ResultToObjectVisitor(ResultSet rs, DataMetadata meta)
    {
        this(rs, meta, null);
    }
    
    public ResultToObjectVisitor(ResultSet rs, DataMetadata meta, Map<String, PersistableDataStream> linked)
    {
        _resultSet = rs;
        _metadata = meta;
        _subStreams = linked;
    }

    public Set keySet(DataContext ctx)
    {
        return null;
    }

    public int collectionSize(DataContext ctx)
    {
        return 0; //anything that is a collection needs to have a different sql stmt
    }

    protected Object convertValue(Object jval, Class traversing)
        throws CtxException
    {
        //override this function to do preconversion before checking to return.
        return jval;
    }

    protected Object createObject(DataContext ctx)
        throws CtxException
    {
        //Override this to create derived classes
        Object val = reflect().silentcreate(ctx.traversingClazz());
        return val;
    }

    private Object subObjectFrom(Set<CacheableObject> vals, Class clazz)
        throws CtxException
    {
        if (vals == null)
            return null;

        if (type().isAssignable(vals.getClass(), clazz))
            return vals; //if set, return as is.

        if (type().isAssignable(clazz, List.class))
        {
            //if it is a list
            List lst = new ArrayList();
            lst.addAll(vals);
            return lst;
        }

        if (vals.size() > 0)
        {
            Object val = vals.iterator().next();
            if (type().isAssignable(val.getClass(), clazz))
                return val;
        }

        //TODO: handle maps
        return vals;
    }

    public Object visit(DataContext ctx)
        throws CtxException
    {
        //this will only create the object for the top level
        //data. The rest has to have a new sql statement to be
        //executed.
        Object val = ctx.fieldVal();
        Field fld = ctx.field();
        String name = null;
        Object jval = null;
        if (fld != null) //is a field
        {
            int mod = fld.getModifiers();
            if (Modifier.isTransient(mod) || Modifier.isStatic(mod))
                return null;

            name = fld.getName();
            AttributeMetadata ameta = _metadata.metadataFor(name);
            if (ameta == null)
            {
                //check if it is that we need to store data into parent? if yes, then create a new object and return
                boolean expand = DataMetadata.shouldExpandIntoParent(fld.getType().getName());
                if (expand)
                {
                    val = reflect().silentcreate(fld.getType());
                    return val;
                }
            }
            assertion().assertNotNull(ameta, this, INVALID_ATTRIBUTE, "Cannot find a column for: " + name + ":" + val + ":" + ctx.traversingClazz() + ":" + ctx.fieldpath());
            String col = ameta.columnName();

            jval = DBTypeService.readData(_resultSet, col, ctx.traversingClazz());
            if (jval == null)
                return jval;

            jval = convertValue(jval, ctx.traversingClazz());
            val = jval;
 

            if ((jval != null) && (jval instanceof String) && (convert().canConvertFromString(fld.getType())))
                jval = convert().stringToClass(jval.toString(), fld.getType());

            if ((jval != null) && type().isAssignable(jval.getClass(), fld.getType()))
                return jval;

            if (ameta.storeJSON())
            {
                Object ret = null;
                if (jval != null)
                {
                    //ByteArrayInputStream istr = new ByteArrayInputStream(jval.toString().getBytes());
                    //ret = convert().readObject(istr, ameta.attributeType(), translator.json);
                    //TODO: fix this;
                    //System.out.println("Converting json for: " + jval.toString() + ":" + ":" + ameta.attributeType() + ":" + ameta.attributeField().getName());
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Type t = ameta.attributeField().getGenericType();
                    Class cls = ameta.attributeField().getType();
                    String[] vals = jval.toString().split(":::");
                    String clsname = cls.getName();
                    Object oneval = jval;
                    if (vals.length >= 2)
                    {
                        clsname = vals[0];
                        oneval = vals[1];
                    }

                    if (!clsname.equals(cls.getName()) && (!type().isAssignable(cls, Collection.class)) && (!type().isAssignable(cls, Map.class)))
                    {
                        CrossLinkAny any = new CrossLinkAny(vals[0]);
                        t = any.linkType();
                    }
                    //System.out.println("Converting: " + jval + ":" + t + ":" + ameta.attributeType() + ":" + ameta.attributeField().getName() + ":" + clsname + ":" + oneval);
                    ret = gson.fromJson(oneval.toString(), t);
                    ctx.modify(ret);
                }

                return null; //modify directly. Return null so it is not traversed further
            }
        }

        if ((fld != null) && (name != null) && (_subStreams != null) && (_subStreams.containsKey(name)))
        {
            try
            {
                //System.out.println("Retrieving data from substream for: " + name);
                PersistableDataStream astream = _subStreams.get(name);
                //System.out.println("Retrieving data from substream for: " + name + ":" + astream);
                //jval has the related column read from this resultset
                Set<CacheableObject> vals = astream.readRelatedData(jval);
                //System.out.println("Retrieving data for: " + name + ":" + vals);
                val = subObjectFrom(vals, fld.getType());
                //return null so we do not traverse into sub objects? But does this mean we cannot
                //go sub objects of sub? It will get set when that object is created from the
                //Persistence stream. So should not be a problem.
                ctx.modify(val);
            }
            catch (CtxException e)
            {
                e.printStackTrace();
            }
            return null;
        }
        else if (ctx.before() && (name != null))
        {
            //this means that we do not want to retrieve the sub values?
            //should we create dummy objects? Currently no.
            return null;
        }

        //if (ctx.before())
        {
            //starting
            if (val == null) val = createObject(ctx);
            if (_created == null) _created = val;
            //return val;
        }

        return val;
    }

    public <T extends CacheableObject> T createdObject() { return (T) _created; }
}

