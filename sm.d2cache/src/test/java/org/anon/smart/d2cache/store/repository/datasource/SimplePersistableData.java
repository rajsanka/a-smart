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

public class SimplePersistableData implements PersistableData 
{
    @PrimeKeyAnnotate
    private String persist1;

    private int persist2;

    private String another;

    private transient boolean _isNew = true;
    private transient SimplePersistableData _original;

    public SimplePersistableData() 
    {
        persist1 = "key1";
        persist2 = 10;
        another = "another";
    }

    public SimplePersistableData(int i) 
    {
        persist1 = "key1" + i;
        persist2 = 10 + i;
        another = "another" + i;
    }

    public SimplePersistableData(String k)
    {
        persist1 = k;
    }

    public void modify(int cnt)
    {
        _original = new SimplePersistableData();
        _original.persist1 = persist1;
        _original.persist2 = persist2;
        _original.another = another;
        persist2 = persist2 + 10 + cnt;
        another = another + "modified" + cnt;
    }

    public SimplePersistableData(boolean srch)
    {
        another = "mod*";
    }

    public void changesomething()
    {
        persist2 = 100;
        another = "modified";
    }

    public String getAnother() { return another; }

    public String getType()
    {
        return this.getClass().getSimpleName();
    }

    public Object[] smart___keys()
    {
        return new Object[] { persist1 };
    }

    public void smart___resetNew()
    {
        _isNew = false;
    }

    public boolean smart___isNew()
    {
        return _isNew;
    }

    public void saveOriginal(PersistableData orig)
    {
        _original = (SimplePersistableData)orig;
    }

    public SimplePersistableData smart___original()
    {
        return _original;
    }

    public void smart___initOnLoad()
    {
    }

    public String toString()
    {
        return ":" + persist1 + ":" + persist2 + ":" + another;
    }
}

