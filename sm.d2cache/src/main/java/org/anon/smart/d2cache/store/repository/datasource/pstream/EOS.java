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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.EOS
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An end of stream indicator
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream;

import org.anon.smart.d2cache.CacheableObject;

import org.anon.utilities.exception.CtxException;

public class EOS implements CacheableObject, java.io.Serializable
{
    private int _recordsStreamed;
    private String _exception;

    public EOS(int count, String except) 
    {
        _recordsStreamed = count;
        _exception = except;
    }

    public int recordsStreamed() { return _recordsStreamed; }

    public String getException() { return _exception; }

    public String getType()
    {
        return "EOS";
    }

    public Object[] smart___keys()
    {
        return new Object[0];
    }

    public boolean smart___isNew()
    {
        return true;
    }

    public void smart___resetNew()
    {
    }

    public EOS smart___original()
    {
        return null;
    }

	public void smart___initOnLoad() 
        throws CtxException
    {
    }
}

