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
 * File:                org.anon.smart.d2cache.store.repository.datasource.metadata.DataMetadataImpl
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

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.CompiledSQL;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.DSErrorCodes;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class DataMetadataImpl implements DataMetadata, DSErrorCodes
{
    private Class<? extends CacheableObject> _dataclazz;
    private Map<String, AttributeMetadata> _attributes;
    private Map<String, AttributeMetadata> _columns;
    private Map<String, AttributeMetadata> _relatedColumns;
    private String _table;
    private String _primary;
    private Map<DataMetadata.sqltypes, AbstractSQL> _compiled;
    private Map<String, AttributeMetadata> _subAttributes;
    private String _existsSQL;
    private String _createSQL;
    private boolean _created = false;
    private Map<String, DataMetadata> _versions;

    public DataMetadataImpl(DataMetadataImpl meta, String table)
    {
        _dataclazz = meta._dataclazz;
        _attributes = meta._attributes; //we can use the same reference
        _columns = meta._columns;
        _relatedColumns = meta._relatedColumns;
        _table = table;
        _primary = meta._primary;
        _compiled = meta._compiled;
        _subAttributes = meta._subAttributes;
        _existsSQL = meta._existsSQL;
        _createSQL = meta._createSQL;
        _created = false;
    }

    public DataMetadataImpl(Class<? extends CacheableObject> datacls) 
    {
        _dataclazz = datacls;
        _attributes = new HashMap<String, AttributeMetadata>();
        _columns = new HashMap<String, AttributeMetadata>();
        _compiled = new HashMap<DataMetadata.sqltypes, AbstractSQL>();
        _relatedColumns = new HashMap<String, AttributeMetadata>();
        _subAttributes = new HashMap<String, AttributeMetadata>();
        _versions = new ConcurrentHashMap<String, DataMetadata>();
    }

    public DataMetadataImpl(Class<? extends CacheableObject> datacls, boolean created)
    {
        this(datacls);
        _created = created;
    }

    public void setTable(String nm) 
    { 
        _table = nm; 
        _compiled.put(DataMetadata.sqltypes.inssql, new CompiledInsert(_table));
        _compiled.put(DataMetadata.sqltypes.selsql, new CompiledSelect(_table));
        _compiled.put(DataMetadata.sqltypes.delsql, new CompiledDelete(_table));
        _compiled.put(DataMetadata.sqltypes.updsql, new CompiledUpdate(_table));
        _compiled.put(DataMetadata.sqltypes.lookupsql, new CompiledLookup(_table));
        _compiled.put(DataMetadata.sqltypes.updkeysql, new CompiledUpdateWithKey(_table));
        _versions = new ConcurrentHashMap<String, DataMetadata>();
    }

    public DataMetadata replicateMe(String gname)
    {
        String table = constructTableName(gname);
        if (_versions.containsKey(table))
        {
            return _versions.get(table);
        }

        DataMetadata meta = new DataMetadataImpl(this, table);
        _versions.put(table, meta);
        return meta;
    }

    private String constructTableName(String gname)
    {
        String tablename = DefaultMetadata.getTableNameFor(gname, _dataclazz);
        if (tablename == null)
        {
            String group = DefaultMetadata.getGroupFor(_dataclazz);
            tablename = gname + "__" + group;
            tablename = tablename.replaceAll("\\-", "_");
            //System.out.println("For: " + gname + ":" + _dataclazz.getName() + ": Returning: " + tablename);
        }
        return tablename;
    }

    public void setCreateSQL(String exists, String create)
    {
        _createSQL = create;
        _existsSQL = exists;
    }

    public void createTable(String gname, String dbname)
        throws CtxException
    {
        //need to reuse from MetadataTraversal.
        if (!_created)
        {
            String tablename = constructTableName(gname);
            _table = tablename;
            String create = "CREATE TABLE " + _table + _createSQL;
            String exist = _existsSQL + "'" + _table + "'";
            _created = DefaultMetadata.createTable(exist, create, gname, dbname);
        }
    }

    private void storeAttribute(String nm, String colnm, Class cls, boolean key, String refcol, boolean json, String path) 
        throws CtxException
    {
        assertion().assertTrue((!_attributes.containsKey(nm)), this, "Cannot not have the same attribute twice " + nm + ":" + path + ":" + cls.getName());
        Field fld = reflect().getAnyField(_dataclazz, nm);
        if ((path != null) && (path.length() > 0))
            fld = reflect().getAnyFieldWithPath(_dataclazz, path);
        assertion().assertNotNull(fld, this, "Cannot find the field: " + nm + " in " + cls.getName());
        AttributeMetadataImpl meta = new AttributeMetadataImpl(nm, colnm, cls, 
                fld, _table, refcol);
        meta.setFieldPath(path);
        meta.setStoreJSON(json);
        if (key)
        {
            meta.setKey();
            _primary = nm;
        }
        _attributes.put(nm, meta);
        AttributeMetadata exist = _columns.get(colnm);
        if (meta.isBackwardReference())
            exist = null; //backward reference, col is same as
        if (exist == null)
        {
            _columns.put(colnm, meta);
        }
        else
        {
            if (exist.isAggregation() && !meta.isAggregation())
            {
                //if the previous was an aggregation, then add it as a 
                //column now.
                exist.setRelatedVia(meta);
                _columns.put(colnm, meta);
                meta.addRelatedTo(exist);
            }
            else
            {
                meta.setRelatedVia(exist);
                exist.addRelatedTo(meta);
            }
        }

        if (meta.isSubObject()) _relatedColumns.put(meta.columnName(), meta);
        //This is a sub field, hence add it.
        if (meta.associatedMetadata() != null)
        {
            _subAttributes.put(nm, meta);
        }
        //System.out.println("Stored attribute as: " + nm + ":" + colnm + ":" + _subAttributes.get(nm) + ":" + exist + ":" + meta + ":" + json + ":" + path + ":" + _dataclazz);
        _compiled.values().stream().forEach((AbstractSQL sql) -> sql.addAttributeFragment(meta));
    }

    public void addAttribute(String nm, String colnm, Class cls)
        throws CtxException
    {
        storeAttribute(nm, colnm, cls, false, null, false, null);
    }

    public void addAttribute(String nm, String colnm, Class cls, String refcol)
        throws CtxException
    {
        storeAttribute(nm, colnm, cls, false, refcol, false, null);
    }

    public void addAttribute(String nm, Class cls, String refcol)
        throws CtxException
    {
        storeAttribute(nm, nm, cls, false, refcol, false, null);
    }

    public void addAttribute(String nm, Class cls)
        throws CtxException
    {
        storeAttribute(nm, nm, cls, false, null, false, null);
    }

    public void addAttribute(String nm, Class cls, boolean json)
        throws CtxException
    {
        storeAttribute(nm, nm, cls, false, null, json, null);
    }

    public void addAttribute(String nm, Class cls, boolean json, String path)
        throws CtxException
    {
        storeAttribute(nm, nm, cls, false, null, json, path);
    }

    public void addKey(String nm, String colnm, Class cls)
        throws CtxException
    {
        storeAttribute(nm, colnm, cls, true, null, false, null);
    }

    public void addKey(String nm, Class cls)
        throws CtxException
    {
        storeAttribute(nm, nm, cls, true, null, false, null);
    }

    public Class<? extends CacheableObject> clazz() { return _dataclazz; }
    public String table() { return _table; }
    public String key() { return _primary; }
    public Map<String, AttributeMetadata> attributes() { return _attributes; }
    public AttributeMetadata metadataFor(String attribute) { return _attributes.get(attribute); }
    public AttributeMetadata columnMetadataFor(String col) { return _columns.get(col); }
    public AttributeMetadata[] subAttributes() { return _subAttributes.values().toArray(new AttributeMetadata[0] ); }

    public CompiledSQL compiled(DataMetadata.sqltypes type) 
    { 
        CompiledSQL ret = _compiled.get(type); 
        ret.changeTable(table());
        return ret;
    }
}

