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
 * File:                org.anon.smart.d2cache.store.repository.datasource.metadata.AttributeMetadataImpl
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

package org.anon.smart.d2cache.store.repository.datasource.metadata;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.lang.reflect.Field;

import org.anon.smart.d2cache.store.repository.datasource.CompiledSQL;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class AttributeMetadataImpl implements AttributeMetadata
{
    private static final int INVALID_METADATA = 0x900001;

    private Field _fld;
    private String _name;
    private String _colName;
    private Class _colClazz;
    private boolean _isKey;
    private String _setterSQL;
    private boolean _storeJSON;

    private Set<AttributeMetadata> _relatedTo;

    private AttributeMetadata _relatedVia;
    private String _referenceColumnName;
    private String _joinSQL;
    private DataMetadata _refMetadata;
    private AbstractSQL _linkSQL;
    private AbstractSQL _subSQL;
    private String _fieldpath;

    private boolean _backwardReference;
    private Class _backReferenceClass;

    public AttributeMetadataImpl(String nm, String colnm, Class cls, Field fld) 
    {
        _name = nm;
        _colName = colnm;
        _colClazz = cls;
        _isKey = false;
        _setterSQL = colnm + " = ? ";
        _fld = fld;
        _relatedTo = new HashSet<AttributeMetadata>();
        _storeJSON = false;
        _backwardReference = false;
    }

    public AttributeMetadataImpl(String nm, Class cls, Field fld) 
    {
        this(nm, nm, cls, fld);
    }

    public AttributeMetadataImpl(String nm, Class cls, Field fld, String tbl, String refcol)
        throws CtxException
    {
        this(nm, nm, cls, fld, tbl, refcol);
    }

    public AttributeMetadataImpl(String nm, String colnm, Class cls, Field fld, String tbl, String refcol)
        throws CtxException
    {
        this(nm, colnm, cls, fld);
        subObjectOf(tbl, refcol);
    }

    public void setKey() { _isKey = true; }
    public void setStoreJSON(boolean json) { _storeJSON = json; }
    public boolean storeJSON() { return _storeJSON; }
    public void setFieldPath(String p) { _fieldpath = p; }

    public void subObjectOf(String tbl, String refcol)
        throws CtxException
    {
        //if (refcol == null)
         //   refcol = _colName;
        if (refcol != null)
        {
            _refMetadata = PersistableData.metadataFor(_colClazz);
            assertion().assertNotNull(_refMetadata,  this, "Please register the sub object before the root object.");

            String reftbl = _refMetadata.table();
            _referenceColumnName = refcol;
            AttributeMetadata refmeta = _refMetadata.metadataFor(refcol);
            if (refmeta == null)
            {
                //this is backward reference. The key of that table needs to be here
                _referenceColumnName = _refMetadata.key();
                refmeta = _refMetadata.metadataFor(_referenceColumnName);
                _colName = _name; //store in the column name the backward reference
                _setterSQL = _colName + " = ? ";
                _backwardReference = true;
                _backReferenceClass = refmeta.attributeType();
            }

            _joinSQL = tbl + "." + _colName + " = " +  reftbl + "." + _referenceColumnName;

            CompiledSelect select = (CompiledSelect)_refMetadata.compiled(DataMetadata.sqltypes.selsql);
            _subSQL = new CompiledSubSelect(select, tbl, _colName, _joinSQL);
            _linkSQL = new CompiledLinkSelect(select, tbl, _colName, _joinSQL);
        }
    }

    public boolean isKey() { return _isKey; }
    public String attributeName() { return _name; }
    public String columnName() { return _colName; }
    public Class attributeType() { return _colClazz; }
    public String getSQLFragment() { return _setterSQL; }
    public Field attributeField() { return _fld; }
    public String joinClause() { return _joinSQL; }
    public String fieldpath() { return _fieldpath; }
    public boolean isBackwardReference() { return _backwardReference; }
    public Class backReferenceType() { return _backReferenceClass; }

    public void addRelatedTo(AttributeMetadata meta) { _relatedTo.add(meta); }
    public Collection<AttributeMetadata> relatedTo() { return _relatedTo; }
    public void setRelatedVia(AttributeMetadata meta) { _relatedVia = meta; }
    public AttributeMetadata relatedVia() { return _relatedVia; }
    public String referenceColumnName() { return _referenceColumnName; }
    public DataMetadata associatedMetadata() { return _refMetadata; }

    public CompiledSQL compiledSQL(DataMetadata.sqltypes type)
    {
        switch (type)
        {
        case subsql:
            return _subSQL;
        case linksql:
        default:
            return _linkSQL;
        }
    }

    public String toString() 
    {
        return _name + ":" + _colName + ":" + _colClazz + ":" + _isKey;
    }
}

