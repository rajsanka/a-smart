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
 * File:                org.anon.smart.d2cache.store.repository.hbase.HBaseRecord
 * Author:              rsankar
 * Revision:            1.0
 * Date:                02-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A record for hbase
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.hbase;

import org.apache.hadoop.hbase.client.Put;

import org.anon.smart.d2cache.annot.CacheKeyAnnotate;
import org.anon.smart.d2cache.store.AbstractStoreRecord;

import static org.anon.utilities.services.ServiceLocator.*;
import org.anon.utilities.reflect.DataContext;
import org.anon.utilities.exception.CtxException;

public class HBaseRecord extends AbstractStoreRecord implements Constants
{
    private Put _putRecord;
    private String _table;
    private HBaseConnection _conn;
    private int _keyCount;

    public HBaseRecord(String group, Object primarykey, Object curr, HBaseConnection conn)
        throws CtxException
    {
        super(group, primarykey, curr);

        try
        {
            String cls = curr.getClass().getName();
            _table = _conn.getTableName(group);
            HBaseCRUD crud = conn.getCRUD();
            _putRecord = crud.newRecord(primarykey.toString(), SYNTHETIC_COL_FAMILY, CLASSNAME, cls);
            _keyCount = 0;
        }
        catch (Exception e)
        {
            except().rt(e, new CtxException.Context("HBaseRecord.init", "Exception"));
        }
    }

    public void append(DataContext ctx)
        throws CtxException
    {
        try
        {
            if (ctx.field() != null)
            {
                String key = _group + PART_SEPARATOR + ctx.fieldpath();
                Object fldval = ctx.fieldVal();
                if (fldval != null)
                {
                    _conn.getCRUD().addTo(_putRecord, DATA_COL_FAMILY, key, fldval);
                    if (ctx.field().isAnnotationPresent(CacheKeyAnnotate.class))
                    {
                        _conn.getCRUD().addTo(_putRecord, DATA_COL_FAMILY, KEY_NAME + _keyCount, fldval);
                        _keyCount++;
                    }
                }
            }
        }
        catch (Exception e)
        {
            except().rt(e, new CtxException.Context("HBaseRecord.append", "Exception"));
        }
    }

    Put putRecord() { return _putRecord; }
    String getTable() { return _table; }
}

