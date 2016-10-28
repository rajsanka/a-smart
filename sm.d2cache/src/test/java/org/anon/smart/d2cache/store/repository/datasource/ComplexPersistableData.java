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

import java.util.List;
import java.util.ArrayList;

import org.anon.smart.d2cache.annot.PrimeKeyAnnotate;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
 
public class ComplexPersistableData implements PersistableData
{
    @PrimeKeyAnnotate
    private int complexId;
    private String complexData;

    //default takes the same name in both the name and referencedColumnName
    private List<RelatedPersistableData> relatedData;

    private List<AnotherRelatedData> another;

    private transient boolean _isNew = true;
    private transient ComplexPersistableData _original;

    public ComplexPersistableData(int ind)
    {
        complexId = 10 + ind;
        complexData = "complex" + ind;
        relatedData = new ArrayList<RelatedPersistableData>();
        another = new ArrayList<AnotherRelatedData>();
        for (int i = 0; i < 10; i++)
        {
            RelatedPersistableData rdata = new RelatedPersistableData((complexId * 10) + i);
            relatedData.add(rdata);
        }

        for (int i = 0; i < 10; i++)
        {
            AnotherRelatedData adata = new AnotherRelatedData((complexId * 10) + i);
            another.add(adata);
        }
    }

    public String getType()
    {
        return this.getClass().getSimpleName();
    }

    public Object[] smart___keys()
    {
        return new Object[] { complexId };
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
        _original = (ComplexPersistableData)orig;
    }

    public ComplexPersistableData smart___original()
    {
        return _original;
    }

    public void smart___initOnLoad()
    {
    }

    public String toString()
    {
        return ":" + complexId + ":" + complexData + ":" + relatedData + ":" + another;
    }
}

