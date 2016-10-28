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
 * File:                org.anon.smart.d2cache.store.repository.mysql.MySQLTransaction
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A transaction for mysql
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.mysql;

import java.util.UUID;
import java.util.Collection;

import org.anon.smart.d2cache.store.StoreRecord;
import org.anon.smart.d2cache.store.StoreConnection;
import org.anon.smart.d2cache.store.AbstractStoreTransaction;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager;
import org.anon.smart.d2cache.store.repository.datasource.pstream.UpdateFuture;

import org.anon.utilities.exception.CtxException;

public class MySQLTransaction extends AbstractStoreTransaction
{
    private UUID _transactionId;
    private PersistableManager _manager;
    private UpdateFuture[] _futures;
    private MySQLConnection _connect;

    public MySQLTransaction(UUID txid, StoreConnection connect, PersistableManager mgr)
    {
        super(txid, connect);
        _transactionId = txid;
        _connect = (MySQLConnection)connect;
        _manager = mgr;
    }

    protected StoreRecord createNewRecord(String group, Object primarykey, Object curr, Object orig, boolean isnew)
        throws CtxException
    {
        System.out.println("Adding record for: " + primarykey + ":"  + curr + ":" + orig + ":" + isnew);
    	return new MySQLRecord(group, primarykey, curr, orig, isnew, _manager, _connect);
    }

	@Override
	public StoreRecord addRecord(String group, Object primarykey, Object curr, Object orig, Object relatedKey, boolean isnew) 
        throws CtxException 
    {
        return null;
	}

    public void commit()
        throws CtxException
    {
        _futures = _manager.flush();
        for (int i = 0; (_futures != null) && (i < _futures.length); i++)
        {
            if (_futures[i] != null) _futures[i].waitToComplete();
        }

    }

    public void rollback()
        throws CtxException
    {
        //don't do anything here.
    }

    public boolean shouldStore(String storeIn)
    {
        return ((storeIn == null) || (storeIn.length() <= 0) || (storeIn.indexOf("repository") >= 0));
    }
}

