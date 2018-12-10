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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.UpdateHandler
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An handler for insert/update query
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Future;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.pstream.UpdateFuture;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager.CacheObjectData;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager.ConnectionPoolEntity;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;

public class UpdateHandler implements UpdateFuture
{
    private List<Future<int[]>> _future;
    private ConnectionPoolEntity _entity;
    private Collection<CacheObjectData> _objs;
    private boolean _release;

    public UpdateHandler(ConnectionPoolEntity cpe, Collection<? extends CacheableObject> objs)
    {
        _future = new ArrayList<Future<int[]>>();
        _entity = cpe;
        //_objs = objs;
    }

    public UpdateHandler(ConnectionPoolEntity cpe, Collection<CacheObjectData> objs, boolean update) 
    {
        //this(cpe, objs);
        _future = new ArrayList<Future<int[]>>();
        _entity = cpe;
        //addFuture(future);
    }

    void addFuture(Future<int[]> future)
    {
        _future.add(future);
    }

    public void setRelease(boolean r)
    {
        _release = r;
    }

    public void release()
        throws CtxException
    {
        try
        {
            if ((_entity != null) && (_release))
            {
                //auto-commit is true.
                //_entity.getConnection().commit();
                _entity.closeConnection();
                _entity.pool().unlockone(_entity);
            }
            _entity = null;
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("UpdateHandler", "waitToComplete"));
        }
    }

    public int[] waitToComplete()
        throws CtxException
    {
        int[] ret = null;
        try
        {
            //wait for all futures to finish
            for (Future<int[]> future : _future)
                ret = future.get();
            //currently not required. Handled by other code.
            //_objs.parallelStream().forEach((CacheableObject d) -> d.smart___resetNew());
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("UpdateHandler", "waitToComplete"));
        }
        finally
        {
            //release();
        }

        return ret;
    }

    public void commit()
        throws CtxException
    {
        try
        {
            if (_entity != null) 
            {
                _entity.getConnection().commit();
            }
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("UpdateHandler", "commit"));
        }
        finally
        {
            release();
        }
    }

    public void rollback()
        throws CtxException
    {
        try
        {
            if (_entity != null) 
            {
                System.out.println("Calling rollback for: " + _entity + ":" + _objs);
                _entity.getConnection().rollback();
            }
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("UpdateHandler", "commit"));
        }
        finally
        {
            release();
        }
    }
}

