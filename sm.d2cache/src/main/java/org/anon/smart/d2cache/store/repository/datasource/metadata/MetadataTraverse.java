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
 * File:                org.anon.smart.d2cache.store.repository.datasource.metadata.MetadataTraverse
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                27-09-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A traversal of class for creating default metadata
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.metadata;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.annot.PrimeKeyAnnotate;
import org.anon.smart.d2cache.annot.CacheKeyAnnotate;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;

import org.anon.utilities.reflect.CVisitor;
import org.anon.utilities.reflect.DataContext;

import org.anon.utilities.reflect.ClassTraversal;
import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class MetadataTraverse implements CVisitor
{
    private DataMetadataImpl _metadata;
    private String _name;
    private String _sql;
    private transient String _addSQL;
    private Class _currentClass;
    private Map<String, Class> _related;
    private String _keyName;

    private static final Map<Class, String> TYPESMAP;
    private static final Map<Class, Class> inProgress;

    static {
        TYPESMAP = new HashMap<Class, String>();
        TYPESMAP.put(String.class, "VARCHAR");
        TYPESMAP.put(int.class, "INTEGER");
        TYPESMAP.put(Integer.class, "INTEGER");
        TYPESMAP.put(long.class, "BIGINT");
        TYPESMAP.put(Long.class, "BIGINT");
        TYPESMAP.put(double.class, "DOUBLE(20, 6)");
        TYPESMAP.put(Double.class, "DOUBLE(20, 6)");
        TYPESMAP.put(float.class, "FLOAT(10, 2)");
        TYPESMAP.put(Float.class, "FLOAT(10, 2)");
        TYPESMAP.put(boolean.class, "BOOLEAN");
        TYPESMAP.put(Boolean.class, "BOOLEAN");
        TYPESMAP.put(short.class, "INTEGER");
        TYPESMAP.put(Short.class, "INTEGER");
        inProgress = new ConcurrentHashMap<Class, Class>();
    }

    public MetadataTraverse(String name, Class<? extends CacheableObject> cls)
        throws CtxException
    {
        _name = name;
        _metadata = new DataMetadataImpl(cls);
        String tablename = _name + "__" + cls.getSimpleName();
        tablename = tablename.replaceAll("\\-", "_");
        _metadata.setTable(tablename); //tenant__classname
        //_sql = "CREATE TABLE " + _metadata.table() + "( ";
        _sql = "(";
        _addSQL = "";
        _currentClass = cls;
        _related = new HashMap<String, Class>();
        inProgress.put(cls, cls);
    }

    public Set keySet(DataContext ctx)
    {
        return null;
    }

    public int collectionSize(DataContext ctx)
    {
        return 0;
    }

    private String getCreateAttributeFragment(String name, Class t, boolean key, boolean unique)
    {
        //default to string??
        String coltype = TYPESMAP.get(t);
        if (coltype == null)
            coltype = TYPESMAP.get(String.class);

        if (coltype.equals("VARCHAR") && key)
            coltype += "(255)";
        else if (coltype.equals("VARCHAR"))
            coltype += "(1024)";

        String ret =  _addSQL + " " + name + " " + coltype;
        if (key) 
            ret += " NOT NULL PRIMARY KEY";
        else if (unique) 
            ret += " NOT NULL UNIQUE ";

        _addSQL = ",";

        //System.out.println("Returning: " + ret);
        return ret;
    }

    private Object handleObject(Field fld, Class t, String name, String path)
        throws CtxException
    {
        //System.out.println("handleObject for attribute: " + name + ":" + t + ":" + type().isAssignable(t, CacheableObject.class));
        if (type().isAssignable(t, CacheableObject.class))
        {
            //System.out.println("Adding metadata for related: " + name + ":" + t);
            PersistableData.metadataFor(t);
            //assumption is that the keyname used in this is the same name used in the sub object for relation
            //System.out.println("Adding related: " + name + ":" + t);
            _related.put(name, t);
        }
        else
        {
            boolean expandintoparent = DataMetadata.shouldExpandIntoParent(t.getName());
            if (expandintoparent)
            {
                //don't do anything, let the next come??
                return ""; //so that we can traverse further.
            }
            else
            {
                //add it as a json expand
                _metadata.addAttribute(name, t, true, path);
                String attr = getCreateAttributeFragment(name, t, false, false);
                _sql += attr;
            }
        }
        return null;
    }

    public Object visit(DataContext ctx)
        throws CtxException
    {
        //we can just setup the metadata for this class?
        Field fld = ctx.field();
        if (fld != null)
        {
            //System.out.println("Field: " + fld + ":" + fld.getName() + ":" + ctx.before() + ":" + fld.getType() + ":" + ctx.traversingClazz() + ":" + inProgress);
            int mod = fld.getModifiers();
            String name = fld.getName();
            Class t = fld.getType();
            if ((!t.equals(_currentClass))  && (!inProgress.containsKey(t)) && (!Modifier.isTransient(mod)) && (!Modifier.isStatic(mod)))
            {
                if (!type().checkPrimitive(t) && (!type().checkStandard(t)) && (!type().isAssignable(t, Collection.class)))
                {
                    //ensure metadata is present for the class
                    return handleObject(fld, t, name, ctx.fieldpath());
                }
                else if (type().isAssignable(t, Collection.class) || type().isAssignable(t, Map.class))
                {
                    //it is a collection, then add the relation of the generic type.
                    //assumed it is declared.
                    Type type = fld.getGenericType();
                    if ((type instanceof ParameterizedType)) 
                    {
                        ParameterizedType pt = (ParameterizedType) type;
                        Type at = pt.getActualTypeArguments()[0];
                        Class send = null;
                        if (at instanceof ParameterizedType) 
                            send = (Class) ((ParameterizedType) at).getRawType();
                        else
                            send = (Class)at;

                        if (!send.equals(_currentClass) && (!type().checkPrimitive(send)) && (!type().checkStandard(send)))
                        {
                            return handleObject(fld, send, name, ctx.fieldpath());
                        }
                        else if (!send.equals(_currentClass))
                        {
                            //if it is an list of primitive types store it as json
                            _metadata.addAttribute(name, t, true, ctx.fieldpath());
                            String attr = getCreateAttributeFragment(name, t, false, false);
                            _sql += attr;
                        }
                    }
                }
                else if (type().checkPrimitive(t) || type().checkStandard(t))
                {
                    if (hasAnnotation(fld, PrimeKeyAnnotate.class))
                    {
                        //System.out.println("Adding " + fld.getName() + " as key");
                        _metadata.addKey(name, t);
                        _sql += getCreateAttributeFragment(name, t, true, false);
                        _keyName = name;
                    }
                    else
                    {
                        _metadata.addAttribute(name, t, false, ctx.fieldpath());
                        String attr = getCreateAttributeFragment(name, t, false, hasAnnotation(fld, CacheKeyAnnotate.class));
                        _sql += attr;
                    }
                }

                return "";
            }

            return null;
        }
        else
        {
            return "";
        }
    }

    private boolean hasAnnotation(Field fld, Class<? extends Annotation> annot)
        throws CtxException
    {
        boolean ret = fld.isAnnotationPresent(annot);
        if (!ret)
        {
            Annotation[] annots = fld.getAnnotations();
            //System.out.println("Annotations present on field: " + fld.getName() + ":" + annots);
            for (int i = 0; (!ret) && (annots != null) && (i < annots.length); i++)
            {
                Class cls = annots[i].annotationType();
                ret = cls.isAnnotationPresent(annot);
            }
        }
        return ret;
    }

    private void setupRelatedAttributes()
        throws CtxException
    {
        for (String related : _related.keySet())
        {
            System.out.println("Adding submeta data for: " + related + ":" + _keyName + ":" + _related.get(related));
            _metadata.addAttribute(related, _keyName, _related.get(related), _keyName);
            AttributeMetadata meta = _metadata.metadataFor(related);
            if (meta.isBackwardReference())
            {
                String attr = getCreateAttributeFragment(meta.columnName(), meta.backReferenceType(), false, false);
                _sql += attr;
            }
        }
    }

    private void done()
        throws CtxException
    {
        setupRelatedAttributes();
        _sql += ")";
        _metadata.setCreateSQL(getExistsSQL(), _sql);
        inProgress.remove(_currentClass);
    }

    public static MetadataTraverse createMetadata(String name, Class<? extends CacheableObject> cls)
        throws CtxException
    {
        System.out.println("MetadataTraversal creating metadata for: " + cls);
        MetadataTraverse traverse = new MetadataTraverse(name, cls);
        ClassTraversal traversal = new ClassTraversal(cls, traverse);
        traversal.traverse(false);
        traverse.done();
        System.out.println("MetadataTraversal Create SQL is: " + traverse._sql);
        return traverse;
    }

    public DataMetadata getMetadata()
    {
        return _metadata;
    }

    public String getCreateSQL()
    {
        return _sql;
    }

    public String getExistsSQL()
    {
        return "SHOW TABLES LIKE ";
    }
}

