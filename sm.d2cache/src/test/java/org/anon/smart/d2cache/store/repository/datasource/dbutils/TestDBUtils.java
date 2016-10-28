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

package org.anon.smart.d2cache.store.repository.datasource.dbutils;

import java.util.List;
import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;

import org.anon.smart.d2cache.store.repository.datasource.DataSchema;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceConfig;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager;
import org.anon.smart.d2cache.store.repository.datasource.metadata.DataMetadataImpl;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream;
import org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.DBUtilsReader;
import org.anon.smart.d2cache.store.repository.datasource.pstream.dbutils.DBUtilsWriter;
import org.anon.smart.d2cache.store.repository.datasource.pstream.UpdateFuture;

import org.anon.smart.d2cache.store.repository.datasource.SimplePersistableData;

import static org.anon.utilities.services.ServiceLocator.*;

public class TestDBUtils
{
    public TestDBUtils() 
    {
    }

    private void setupMetadata()
        throws Exception
    {
        ResourceConfig cfg = new ResourceConfig("default", "jdbc:mysql://localhost:3306/smarttest", 
                "smarttest", "smarttest", "smarttest", "com.mysql.jdbc.Driver");
        ResourceManager.registerConnectionPool(cfg);
        
        DataMetadataImpl meta = new DataMetadataImpl(SimplePersistableData.class);
        meta.setTable("tt_simple");
        meta.addKey("persist1", String.class);
        meta.addAttribute("persist2", int.class);
        meta.addAttribute("another", String.class);

        DataSchema.registerFor(SimplePersistableData.class, meta);
    }

    @Test
    public void testSimple()
        throws Exception
    {
        setupMetadata();
        DBUtilsReader rdr = new DBUtilsReader("default");
        DataMetadata meta = PersistableData.metadataFor(SimplePersistableData.class);
        String sql = meta.selectSQL() + " WHERE " + meta.metadataFor("persist2").getSQLFragment();
        readSQL(rdr, sql, 10);
        sql = meta.selectSQL();
        readSQL(rdr, sql);
        sql = meta.lookupSQL();
        readSQL(rdr, sql, "key20");

        perf().startHere("starttest");
        SimplePersistableData data = rdr.lookup(SimplePersistableData.class, "key21");
        //perf().dumpHere();
        System.out.println("Retrieved data: " + data);
    }

    private void readSQL(DBUtilsReader rdr, String sql, Object ... parms)
        throws Exception
    {
        perf().startHere("readSQL");
        PersistableDataStream<SimplePersistableData> stream = rdr.queryDB(SimplePersistableData.class, sql, parms);

        SimplePersistableData d = null;
        while ((d = stream.readNextData()) != null)
        {
            System.out.println("Read: " + d);
        }
        //perf().dumpHere();
    }

    @Test
    public void testInsert()
        throws Exception
    {
        setupMetadata();
        DBUtilsWriter write = new DBUtilsWriter("default");
        perf().startHere("insertOne");
        SimplePersistableData d = new SimplePersistableData(40);
        int inserted = write.insert(d);
        //perf().dumpHere();
        assertTrue(inserted == 1);

        List<SimplePersistableData> simplelst = new ArrayList<SimplePersistableData>();
        for (int i = 0; i < 100; i++)
        {
            simplelst.add(new SimplePersistableData(50 + i));
        }

        perf().startHere("insertOne");
        UpdateFuture future = write.insertAll(simplelst);
        int[] arr = future.waitToComplete();
        //perf().dumpHere();
        assertTrue(arr.length == 100);
    }
}

