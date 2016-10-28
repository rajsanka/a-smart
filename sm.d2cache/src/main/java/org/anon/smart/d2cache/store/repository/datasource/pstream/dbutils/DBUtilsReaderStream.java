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
 * File:                org.anon.smart.d2cache.store.repository.datasource.DBUtilsReaderStream
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A stream that reads the data 
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils;

import java.util.concurrent.Future;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager.ConnectionPoolEntity;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class DBUtilsReaderStream<T extends CacheableObject> extends PersistableDataStream<T>
{
    private Future<Integer> _executionStatus;
    private ConnectionPoolEntity _connection;
    private PersistableDataHandler _handler;
    private boolean _release;

    public DBUtilsReaderStream(Class<T> cls) 
        throws CtxException
    {
        super(cls);
    }

    void setFuture(Future<Integer> f)
    {
        _executionStatus = f;
    }

    void setRelease(boolean r)
    {
        _release = r;
    }

    void setHandler(PersistableDataHandler handler)
    {
        _handler = handler;
    }

    void setConnection(ConnectionPoolEntity cpe)
    {
        _connection = cpe;
    }

    @Override
    public void closeStream()
        throws CtxException
    {
        super.closeStream();
        if (_release) _connection.pool().unlockone(_connection);
    }

    public void waitCompletion()
        throws CtxException
    {
        try
        {
            _executionStatus.get();
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DBUtilsReaderStream", "waitComplete"));
        }
    }
}

