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
 * File:                org.anon.smart.d2cache.store.repository.datasource.metadata.CompileSelect
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

import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.AttributeMetadata;

public class CompiledSelect extends AbstractSQL
{
    private String _alias;

    CompiledSelect(String tbl)
    {
        super(tbl, DataMetadata.sqltypes.selsql);
        _alias = tbl;
    }

    protected CompiledSelect(String tbl, DataMetadata.sqltypes type)
    {
        super(tbl, type);
        _alias = tbl;
    }

    protected CompiledSelect(CompiledSelect sel)
    {
        super(sel);
        _alias = sel._alias;
    }

    @Override
    public void addAttributeFragment(AttributeMetadata meta)
    {
        //run it as table.column so that the joins work correctly.
        _attributes.add(_alias + "." + meta.columnName());
        super.addAttributeFragment(meta);
    }

    @Override
    protected String prefix()
    {
        return "SELECT";
    }

    @Override
    protected String postfix()
    {
        return "FROM " + _table + " " + _alias;
    }
}

