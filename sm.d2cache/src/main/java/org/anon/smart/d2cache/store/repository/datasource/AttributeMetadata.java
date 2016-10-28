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
 * File:                org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata
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

import java.util.Map;
import java.util.Collection;
import java.lang.reflect.Field;

import org.anon.smart.d2cache.CacheableObject;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public interface AttributeMetadata
{
    public Field attributeField();
    public String attributeName();
    public String columnName();
    public Class attributeType();
    public String getSQLFragment();
    public boolean isKey();
    public boolean storeJSON();
    public DataMetadata associatedMetadata();
    public void setRelatedVia(AttributeMetadata meta);
    public AttributeMetadata relatedVia();
    //can be related to more than one?
    public void addRelatedTo(AttributeMetadata meta);
    public Collection<AttributeMetadata> relatedTo();
    public String fieldpath();

    //These are attributes information for sub objects
    public String referenceColumnName();
    public CompiledSQL compiledSQL(DataMetadata.sqltypes type);
    public String joinClause();

    default boolean canLazyLoad()
    {
        Class cls = attributeType();
        return (Collection.class.isAssignableFrom(cls) 
                || Map.class.isAssignableFrom(cls)
                || cls.isArray());
    }

    default AttributeMetadata referencedAttribute()
    {
        AttributeMetadata ret = null;
        if ((referenceColumnName() != null) && (referenceColumnName().length() > 0) && (associatedMetadata() != null))
        {
            ret = associatedMetadata().metadataFor(referenceColumnName());
        }

        return ret;
    }

    default boolean isSubObject()
    {
        //Maybe we shd only treat PersistableData, Collections and Maps as sub objects?
        return isAssociated() || isAggregation();

        /*
        return (!type().isPrimitiveClass(cls)
                && !type().isStandardJavaClass(cls));
                */
    }

    default CompiledSQL subSQL()
    {
        return compiledSQL(DataMetadata.sqltypes.subsql);
    }

    default CompiledSQL linkSQL()
    {
        return compiledSQL(DataMetadata.sqltypes.linksql);
    }

    default boolean isComposition()
    {
        //Associated means this is a single sub-object relation
        //relatedVia not null means that there exists another column
        //in the current object that is related and this has to be copied
        //over to the associated object.
        return ((isAssociated()) && (relatedVia() != null));
    }

    default boolean isAssociated()
    {
        Class cls = attributeType();
        return (type().isAssignable(cls, CacheableObject.class) && (!storeJSON()));
    }

    //this means that the attribute is present on the sub table
    //hence needs to not have a column here.
    default boolean isAggregation()
    {
        Class cls = attributeType();
        return ((type().isAssignable(cls, Collection.class) 
                || type().isAssignable(cls, Map.class)
                || cls.isArray()) && (!storeJSON()));
    }
}

