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
 * File:                org.anon.smart.d2cache.store.repository.datasource.metadata.AbstractSQL
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
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

import org.anon.smart.d2cache.store.repository.datasource.CompiledSQL;
import org.anon.smart.d2cache.store.repository.datasource.SQLDescriptor;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;

import org.anon.utilities.serialize.srdr.DirtyField;

public abstract class AbstractSQL implements CompiledSQL
{
    protected String _table;
    protected List<String> _attributes;
    protected List<String> _attributePaths;
    protected List<Field> _attributeFlds;
    protected int _count;
    protected DataMetadata.sqltypes _sqlType;

    protected List<CompiledSQL> _subSQLs;

    protected AbstractSQL(String tbl, DataMetadata.sqltypes type) 
    {
        _attributes = new ArrayList<String>();
        _attributePaths = new ArrayList<String>();
        _attributeFlds = new ArrayList<Field>();
        _subSQLs = new ArrayList<CompiledSQL>();
        _table = tbl;
        _count = 0;
        _sqlType = type;
    }

    public void changeTable(String tbl)
    {
        _table = tbl;
    }

    protected AbstractSQL(AbstractSQL sql)
    {
        _table = sql._table;
        _attributes = new ArrayList<String>();
        _attributePaths = new ArrayList<String>();
        _attributeFlds = new ArrayList<Field>();
        _subSQLs = new ArrayList<CompiledSQL>();
        _count = 0;
        _sqlType = sql._sqlType;
        appendCompiled(sql);
    }

    public void appendCompiled(AbstractSQL sql)
    {
        _attributes.addAll(sql._attributes);
        _attributePaths.addAll(sql._attributePaths);
        _attributeFlds.addAll(sql._attributeFlds);
        _count = sql._count;
    }

    public void addAttributeFragment(AttributeMetadata meta)
    {
        if ((meta.relatedVia() == null)  || (meta.isBackwardReference()))
        { 
            _attributeFlds.add(meta.attributeField()); 
            //System.out.println(this.getClass().getName() + ":" + _table + ": Adding path: " + meta + ":" + meta.fieldpath());
            _attributePaths.add(meta.fieldpath()); 
        }
        //if the attributemetadata is a sub object then add the compiled sql
        if (meta.associatedMetadata() != null)
        {
            DataMetadata associated = meta.associatedMetadata();
            CompiledSQL sql = associated.compiled(_sqlType);
            if (sql != null) 
            {
                _subSQLs.add(sql);
            }
        }
    }

    protected void addCompiledSubSQL(CompiledSQL sql)
    {
        _subSQLs.add(sql);
    }

    protected abstract String prefix();
    protected abstract String postfix();

    protected String listToString(String sep)
    {
        String add = "";
        String ret = "";
        for (int i = 0; i < _count; i++)
        {
            ret += add + _attributes.get(i);
            add = sep;
        }

        return ret;
    }

    protected SQLDescriptor listToString(String sep, Set<DirtyField> dflds)
    {
        SQLDescriptor sdesc = new SQLDescriptor();
        List<String> desc = new ArrayList<String>();
        List<Field> flds = new ArrayList<Field>();
        for (DirtyField df : dflds)
        {
            //Right now uses only the top level fields, have to see how to
            //propagate it to the child level fields.
            int ind = _attributeFlds.indexOf(df.getField());
            flds.add(df.getField());
            desc.add(_attributes.get(ind));
        }

        String add = "";
        String ret = "";
        for (int i = 0; i < desc.size(); i++)
        {
            ret += add + desc.get(i);
            add = sep;
        }
        sdesc.setSQL(ret);
        sdesc.setFields(flds.toArray(new Field[0]));
        return sdesc;
    }

    public String sql()
    {
        _count = _attributes.size();
        return prefix() + " " + listToString(",") + " " + postfix();
    }

    public SQLDescriptor sql(Set<DirtyField> flds)
    {
        SQLDescriptor ret = listToString(",", flds);
        String sql = prefix() + " " + ret.sql() + " " + postfix();
        ret.setSQL(sql);
        return ret;
    }

    public Field[] paramSequence()
    {
        return _attributeFlds.toArray(new Field[0]);
    }

    public String[] paramPathSequence()
    {
        //System.out.println("returning paths: " + _attributePaths);
        return _attributePaths.toArray(new String[0]);
    }

    public CompiledSQL[] subSQLs()
    {
        return _subSQLs.toArray(new CompiledSQL[0]);
    }

    @Override
    public String sql(CompiledSQL p)
    {
        return ""; //default is nothing
    }
}

