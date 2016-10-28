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
 * File:                org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.DBUtilsReader
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

package org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils;

import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.lang.reflect.Field;
import java.util.concurrent.Future;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.AsyncQueryRunner;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.pstream.DataReader;
import org.anon.smart.d2cache.store.repository.datasource.pstream.DataReader.QueryData;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager.ConnectionPoolEntity;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager;

import org.anon.utilities.exception.CtxException;
import org.anon.utilities.utils.Repeatable;
import org.anon.utilities.utils.RepeaterVariants;

import static org.anon.utilities.services.ServiceLocator.*;

public class DBUtilsReader extends DBUtilsHandler implements DataReader
{
    //private String _dbname;
    private AsyncQueryRunner _runner;

    public DBUtilsReader()
    {
        super();
    }

    public Repeatable repeatMe(RepeaterVariants rp)
        throws CtxException
    {
        PersistableManager.RepeatParams p = (PersistableManager.RepeatParams)rp;
        if (p.paramFor(0) instanceof ConnectionPoolEntity)
            return new DBUtilsReader((ConnectionPoolEntity)(p.paramFor(0)));
        else
            return new DBUtilsReader(p.paramFor(0).toString());
    }

    public DBUtilsReader(String dbname)
        throws CtxException
    {
        super(dbname);
        //_dbname = dbname;
        _runner = new AsyncQueryRunner(ResourceManager.executionPool());
    }

    public DBUtilsReader(ConnectionPoolEntity cpe)
        throws CtxException
    {
        super(cpe);
        //_dbname = dbname;
        _runner = new AsyncQueryRunner(ResourceManager.executionPool());
    }

    public <T extends CacheableObject> T lookup(Class<T> datacls, Object key)
        throws CtxException
    {
        //runs in sync, since this is a single object lookup
        perf().startHere("startlookup");
        ConnectionPoolEntity cpe = getCPE();
        try
        {
            QueryRunner run = new QueryRunner();
            Connection conn = cpe.getConnection();
            perf().checkpointHere("gotconnection");
            LookupHandler<T> lh = new LookupHandler(datacls);
            String sql = lh.lookupSQL();
            System.out.println("Executing: " + sql + ":" + key);
            T data = run.query(conn, sql, lh, key);
            perf().checkpointHere("ranquery");
            return data;
        }
        catch (Exception e)
        {
            except().rt(e, this, new CtxException.Context("DBUtilsReader", "lookup"));
        }
        finally
        {
            releaseCPE(cpe);
        }

        return null;
    }

    public <T extends CacheableObject> T lookup(QueryData<T> query, Object key)
        throws CtxException
    {
        //oh bother. Just use the async one and waittocomplete instead.
        return null;
    }

    public <T extends CacheableObject> PersistableDataStream<T> queryDB(Class<T> datacls, String sql, Object[] params)
        throws CtxException
    {
        return queryDB(datacls, sql, null, null, params);
    }

    private <T extends CacheableObject> PersistableDataStream<T> queryDB(Class<T> datacls, String sql, 
            Map<String, PersistableDataStream> sub, Field mapped, Object[] params)
        throws CtxException
    {
        perf().startHere("startquerydb");
        PersistableDataHandler<T> handler = new PersistableDataHandler<T>(datacls, sub, mapped);
        DBUtilsReaderStream<T> stream = handler.getStream();
        perf().checkpointHere("createdstream");
        try
        {
            //TODO: test if it is reqd that this object stays in scope?
            ConnectionPoolEntity cpe = getCPE();
            stream.setConnection(cpe);
            Connection connect = cpe.getConnection();
            perf().checkpointHere("connected");
            if ((params != null) && (params.length > 0))
                System.out.println("Executing: " + sql + ":" + params[0]);
            Future<Integer> future = _runner.query(connect, sql, handler, params);
            stream.setFuture(future);
            stream.setRelease(_release);
            perf().checkpointHere("scheduled");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            except().rt(e, this, new CtxException.Context("DBUtilsReader", "queryDB"));
        }

        return stream;
    }

    public <T extends CacheableObject> PersistableDataStream<T> queryDB(QueryData<T> query, Object ... params)
        throws CtxException
    {
        //first run the sub queries so that we can set it into the base query handler
        Map<String, PersistableDataStream> subs = new HashMap<String, PersistableDataStream>();
        for (QueryData q : query.links())
        {
            PersistableDataStream linked = queryDB(q, params);
            subs.put(q.attribute(), linked);
        }

        PersistableDataStream<T> ret = queryDB(query.dataClass(), query.sql(), subs, query.mappedAttribute(), params);
        return ret;
    }
}

