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

import org.anon.smart.d2cache.store.repository.datasource.PersistableData;

/*
import javax.persistence.NamedQuery;

@NamedQuery(
        name="CustomPersistableData",
        query="SELECT a.persist1, a.persist2, a.another, b.cpersist1, b.cpersist2 FROM tt_simple a, tt_complex b where a.persist1 = b.linksimple"
        )
        */
public abstract class CustomPersistableData implements PersistableData
{
    //@Column(table="tt_simple", name="persist1")
    private String persist1;

    //@Column(table="tt_simple", name="persist2")
    private int persist2;

    //@Column(table="tt_simple", name="another")
    private String another;

    //@Column(table="tt_complex", name="cpersist1")
    private String cpersist1;

    //@Column(table="tt_complex", name="cpersist2")
    private int cpersist2;

    //@Column(table="tt_complex", name="cpersist3")
    private String srch1;

    public CustomPersistableData() 
    {
        srch1 = "somthing*";
    }
}

