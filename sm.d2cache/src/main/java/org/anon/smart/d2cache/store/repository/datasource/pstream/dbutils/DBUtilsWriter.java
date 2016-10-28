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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.DBUtilsWriter
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                19-07-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A writer into the database
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Stack;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.AsyncQueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.DataSchema;
import org.anon.smart.d2cache.store.repository.datasource.DSErrorCodes;
import org.anon.smart.d2cache.store.repository.datasource.SQLDescriptor;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.pstream.DataWriter;
import org.anon.smart.d2cache.store.repository.datasource.pstream.UpdateFuture;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager.ConnectionPoolEntity;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager.CacheObjectData;

import org.anon.utilities.exception.CtxException;
import org.anon.utilities.utils.Repeatable;
import org.anon.utilities.utils.RepeaterVariants;
import org.anon.utilities.serialize.srdr.DirtyField;
import org.anon.utilities.exception.StandardExceptionHandler;

import static org.anon.utilities.services.ServiceLocator.*;

public class DBUtilsWriter extends DBUtilsHandler implements DataWriter, DSErrorCodes
{
    //private String _dbname;
    private AsyncQueryRunner _runner;

    public DBUtilsWriter()
    {
        super();
    }

    public Repeatable repeatMe(RepeaterVariants rp)
        throws CtxException
    {
        PersistableManager.RepeatParams p = (PersistableManager.RepeatParams)rp;
        if (p.paramFor(0) instanceof ConnectionPoolEntity)
            return new DBUtilsWriter((ConnectionPoolEntity)(p.paramFor(0)));
        else
            return new DBUtilsWriter(p.paramFor(0).toString());
    }

    public DBUtilsWriter(String dbname)
        throws CtxException
    {
        super(dbname);
        //_dbname = dbname;
        _runner = new AsyncQueryRunner(ResourceManager.executionPool());
    }

    public DBUtilsWriter(ConnectionPoolEntity cpe)
        throws CtxException
    {
        super(cpe);
        _runner = new AsyncQueryRunner(ResourceManager.executionPool());
    }

    public <T extends CacheableObject> int update(CacheObjectData cd)
        throws CtxException
    {
        perf().checkpointHere("startupdate");
        T obj = (T)cd.getObject();
        Set<DirtyField> dirtyflds = PersistableData.computeChange(cd.getObject(), cd.getOriginal());
        perf().checkpointHere("searcheddirtyflds");
        ConnectionPoolEntity cpe = getCPE();
        try
        {
            QueryRunner run = new QueryRunner();
            Connection conn = cpe.getConnection();
            perf().checkpointHere("gotconnection");
            SQLDescriptor sql = PersistableData.updateSQL(obj, dirtyflds, true);
            Object[] params = PersistableData.updateArray(obj, sql, true);
            int cnt = run.update(conn, sql.sql(), params);
            //obj.smart___resetNew();
            //conn.commit();
            perf().checkpointHere("inserted");
            return cnt;
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DBUtilsWriter", "update"));
        }
        finally
        {
            releaseCPE(cpe);
        }

        return 0;
    }

    public <T extends CacheableObject> int insert(T obj)
        throws CtxException
    {
        //runs in sync, since this is a single object lookup
        perf().checkpointHere("startupdate");
        ConnectionPoolEntity cpe = getCPE();
        try
        {
            QueryRunner run = new QueryRunner();
            Connection conn = cpe.getConnection();
            perf().checkpointHere("gotconnection");
            String sql = PersistableData.insertSQL(obj);
            Object[] params = PersistableData.insertArray(obj);
            int cnt = run.update(conn, sql, params);
            //obj.smart___resetNew();
            //conn.commit();
            perf().checkpointHere("inserted");
            return cnt;
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DBUtilsWriter", "insert"));
        }
        finally
        {
            releaseCPE(cpe);
        }

        return 0;
    }

    public void createNotExists(String exists, String sql)
        throws CtxException
    {
        //runs in sync, since this is a single object lookup
        perf().checkpointHere("startupdate");
        ConnectionPoolEntity cpe = getCPE();
        try
        {
            QueryRunner run = new QueryRunner();
            Connection conn = cpe.getConnection();
            perf().checkpointHere("gotconnection");
            Object result = run.query(conn, exists, new ScalarHandler<String>());
            if ((result == null) || (result.toString().length() <= 0))
            {
                run.update(conn, sql);
            }

            //conn.commit();
            perf().checkpointHere("created");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            except().rt(e, this, new CtxException.Context("DBUtilsWriter", "insert"));
        }
        finally
        {
            releaseCPE(cpe);
        }
    }

    public <T extends CacheableObject> UpdateFuture insertAll(Collection<T> objs)
        throws CtxException
    {
        if ((objs == null) || (objs.size() <= 0))
            return null;

        perf().checkpointHere("startupdate");
        //TODO: shd we get multiple connections? Else this may be a problem?
        ConnectionPoolEntity cpe = getCPE();
        try
        {
            Connection conn = cpe.getConnection();
            perf().checkpointHere("gotconnection");
            //Cannot assume that all objects have the same sql. Hence will have to change this.
            //If we did it in parallel, is it possible that the child gets inserted before parent
            //and hence violates a foreign key?
            //First group and split the data by the actual class so we can do batch inserts on various 
            //objects correctly.
            Map<Class, List<T>> collected = objs.stream().collect(Collectors.groupingBy(CacheableObject::getClass));
            Stack<Class> seq = DataSchema.sequence(collected.keySet());
            UpdateHandler handler = new UpdateHandler(cpe, objs);
            //for (List<T> cobjs : collected.values())
            while (!seq.empty())
            {
                Class handle = seq.pop();
                List<T> cobjs = collected.get(handle);
                if (cobjs != null)
                {
                    T obj = cobjs.iterator().next();
                    String sql = PersistableData.insertSQL(obj);
                    Object[][] params = new Object[cobjs.size()][];
                    AtomicInteger count = new AtomicInteger(-1);
                    StandardExceptionHandler excepthandler = new StandardExceptionHandler(this, INVALID_UPDATE);
                    cobjs.parallelStream().forEach((T p) -> {
                        try
                        {
                            Object[] iparams = PersistableData.insertArray(p);
                            int ind = count.incrementAndGet();
                            params[ind] = iparams;
                        }
                        catch (Exception e)
                        {
                            excepthandler.handleException(e);
                        }
                    });
                    //need to hook to clean up, currently it has to be manually called :(
                    excepthandler.hasException();
                    System.out.println("Executing SQL: " + sql);
                    Future<int[]> future = _runner.batch(conn, sql, params);
                    handler.addFuture(future);
                    perf().checkpointHere("inserted");
                }
            }
            handler.setRelease(_release);
            return handler;
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DBUtilsWriter", "insertAll"));
        }
        finally
        {
            //releaseCPE(cpe);
        }

        return null;
    }

    public <T extends CacheableObject> UpdateFuture updateAll(Collection<CacheObjectData> objs)
        throws CtxException
    {
        //batch update will update all fields and not just dirty fields.
        //runs in sync, since this is a single object lookup
        if ((objs == null) || (objs.size() <= 0))
            return null;

        perf().checkpointHere("startupdate");
        ConnectionPoolEntity cpe = getCPE();
        try
        {
            Connection conn = cpe.getConnection();
            perf().checkpointHere("gotconnection");
            CacheObjectData cd = objs.iterator().next();
            T obj = (T)cd.getObject();
            String sql = PersistableData.updateSQL(obj, true);
            Object[][] params = new Object[objs.size()][];
            AtomicInteger count = new AtomicInteger(-1);
            StandardExceptionHandler handler = new StandardExceptionHandler(this, INVALID_UPDATE);
            objs.parallelStream().forEach((CacheObjectData pd) -> {
                try
                {
                    T p = (T)pd.getObject();
                    Object[] iparams = PersistableData.updateArray(p, true);
                    int ind = count.incrementAndGet();
                    params[ind] = iparams;
                }
                catch (Exception e)
                {
                    handler.handleException(e);
                }
            });
            //need to hook to clean up, currently it has to be manually called :(
            handler.hasException();
            Future<int[]> future = _runner.batch(conn, sql, params);
            perf().checkpointHere("updated");
            UpdateHandler handle = new UpdateHandler(future, cpe, objs);
            handle.setRelease(_release);
            return handle;
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DBUtilsWriter", "updateAll"));
        }
        finally
        {
            //releaseCPE(cpe);
        }

        return null;
    }

    /*
     * public <T extends CacheableObject> void flush(PersistableDataStream<T> stream)
        throws CtxException
    {
        try
        {
            //will do it in sync since this is already executing in a separate thread
            //batch may not be required, since we stream it one object at a time?
            T update = null;
            while ((update = stream.readNextData()) != null)
            {
                if (update.smart___isNew())
                    insert(update);
                else
                    update(update, null); //for now no original is present
            }
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DBUtilsWriter", "flush"));
        }
    }
    */
}

