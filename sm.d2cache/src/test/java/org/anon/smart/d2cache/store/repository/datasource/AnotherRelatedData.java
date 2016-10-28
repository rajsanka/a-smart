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

import org.anon.smart.d2cache.annot.PrimeKeyAnnotate;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;

public class AnotherRelatedData implements PersistableData
{
    @PrimeKeyAnnotate
    private int anotherId;
    private String anotherstr;
    private int complexId;

    private transient boolean _isNew = true;
    private transient AnotherRelatedData _original;

    public AnotherRelatedData(int i) 
    {
        anotherId = i + 10;
        anotherstr = "another: " + i;
    }

    public String getType()
    {
        return this.getClass().getSimpleName();
    }

    public Object[] smart___keys()
    {
        return new Object[] { anotherId };
    }

    public void smart___resetNew()
    {
        _isNew = false;
    }

    public boolean smart___isNew()
    {
        return _isNew;
    }

    public void saveOriginal(AnotherRelatedData orig)
    {
        _original = (AnotherRelatedData)orig;
    }

    public AnotherRelatedData smart___original()
    {
        return _original;
    }

    public void smart___initOnLoad()
    {
    }

    public String toString()
    {
        return ":" + anotherId + ":" + anotherstr;
    }
}

