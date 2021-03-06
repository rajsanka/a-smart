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
 * File:                org.anon.smart.d2cache.store.repository.datasource.metadata.CompiledLinkSelect
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

import org.anon.smart.d2cache.store.repository.datasource.CompiledSQL;

public class CompiledLinkSelect extends CompiledSelect
{
    private String _parentTable;
    private String _parentColumn;
    private String _joinClause;

    public CompiledLinkSelect(CompiledSelect sel, String ptbl, String pcol, String join)
    {
        super(sel);
        _parentTable = ptbl;
        _parentColumn = pcol;
        _joinClause = join;
    }

    @Override
    public String sql()
    {
        _count = _attributes.size();
        String pre = super.prefix();
        String post = " FROM " + _table + " , " + _parentTable + 
            " WHERE " + _joinClause;
        return pre + " " + listToString(",") + " " + post;
    }

    @Override
    public String sql(CompiledSQL p)
    {
        CompiledLinkSelect parent = (CompiledLinkSelect)p;
        //third level sub select. Do we want to make it infinites levels???
        _count = _attributes.size();
        String pre = super.prefix();
        String post = " FROM " + _table + " , " + _parentTable + " , " + parent._parentTable + 
            " WHERE " + _joinClause + " AND " + parent._joinClause;
        return pre + " " + listToString(",") + " " + post;
    }
}

