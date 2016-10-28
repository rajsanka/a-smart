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


import org.junit.Test;

import static org.junit.Assert.*;

import org.anon.smart.d2cache.store.repository.datasource.DataSchema;
import org.anon.smart.d2cache.store.repository.datasource.DataMetadata;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;
import org.anon.smart.d2cache.store.repository.datasource.pstream.UpdateFuture;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceConfig;
import org.anon.smart.d2cache.store.repository.datasource.resources.ResourceManager;
import org.anon.smart.d2cache.store.repository.datasource.metadata.DataMetadataImpl;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableManager;
import org.anon.smart.d2cache.store.repository.datasource.pstream.PersistableDataStream;
import org.anon.smart.d2cache.store.repository.datasource.pstream.SelectLiteQuery;

public class TestPersistable
{
    public void setupMetadata()
        throws Exception
    {
        ResourceConfig cfg = new ResourceConfig("default", "jdbc:mysql://localhost:3306/smarttest", 
                "smarttest", "smarttest", "smarttest", "com.mysql.jdbc.Driver");
        ResourceManager.registerConnectionPool(cfg);
        
        DataMetadataImpl meta = new DataMetadataImpl(SimplePersistableData.class, true);
        meta.setTable("tt_simple");
        meta.addKey("persist1", String.class);
        meta.addAttribute("persist2", int.class);
        meta.addAttribute("another", String.class);

        DataSchema.registerFor("SimplePersistableData", SimplePersistableData.class, meta);

        DataMetadataImpl rmeta = new DataMetadataImpl(RelatedPersistableData.class, true);
        rmeta.setTable("tt_related");
        rmeta.addKey("relatedId", int.class);
        rmeta.addAttribute("relatedStr", String.class);
        rmeta.addAttribute("complexId", int.class);
        
        DataSchema.registerFor("RelatedPersistableData", RelatedPersistableData.class, rmeta);

        DataMetadataImpl ameta = new DataMetadataImpl(AnotherRelatedData.class, true);
        ameta.setTable("tt_another");
        ameta.addKey("anotherId", int.class);
        ameta.addAttribute("anotherstr", String.class);
        ameta.addAttribute("complexId", int.class);
        
        DataSchema.registerFor("AnotherRelatedData", AnotherRelatedData.class, ameta);

        DataMetadataImpl cmeta = new DataMetadataImpl(ComplexPersistableData.class, true);
        cmeta.setTable("tt_complex");
        cmeta.addKey("complexId", int.class);
        cmeta.addAttribute("complexData", String.class);
        cmeta.addAttribute("relatedData", "complexId", RelatedPersistableData.class, "complexId");
        cmeta.addAttribute("another", "complexId", AnotherRelatedData.class, "complexId");

        DataSchema.registerFor("ComplexPersistableData", ComplexPersistableData.class, cmeta);

        DataMetadataImpl c1meta = new DataMetadataImpl(ComplexData.class, true);
        c1meta.setTable("tt_simple_complex");
        c1meta.addKey("cpersist1", String.class);
        c1meta.addAttribute("cpersist2", int.class);
        c1meta.addAttribute("cpersist3", String.class);
        c1meta.addAttribute("linked", "cpersist3", SimplePersistableData.class, "another");

        DataSchema.registerFor("ComplexData", ComplexData.class, c1meta);
    }

    private PersistableManager getStream()
        throws Exception
    {
        return new PersistableManager("default", "test");
    }

    @Test
    public void testPersistence()
        throws Exception
    {
        setupMetadata();
        try (PersistableManager mgr = getStream()) 
        {
            for (int i = 0; i < 200; i++)
            {
                SimplePersistableData d = new SimplePersistableData("key" + 1000 + i);

                mgr.append(d, null, true, d.smart___keys());
            }

            UpdateFuture[] futures = mgr.flush();
            futures[0].waitToComplete();

            mgr.select(SimplePersistableData.class)
                .where()
                .get("persist1").like("key*")
                //.get("persist2").eq(10)
                .search()
                .stream()
                .forEach((SimplePersistableData d) -> 
                { 
                    System.out.println(d);
                });

            //test re-iterating the same data.
            PersistableDataStream<SimplePersistableData> stream = mgr.select(SimplePersistableData.class)
                .where()
                .get("persist1").like("key*")
                .search();
            stream.stream().forEach( (SimplePersistableData d) -> {
                System.out.println("Primary: " + d);
            });

            stream.stream().forEach( (SimplePersistableData d) -> {
                System.out.println("Secondary: " + d);
            });

            //mgr.done(data);
            //mgr.done(data1);
        }
    }

    @Test
    public void testUpdatePersistence()
        throws Exception
    {
        setupMetadata();
        try (PersistableManager mgr = getStream()) 
        {
            for (int i = 0; i < 200; i++)
            {
                SimplePersistableData d = new SimplePersistableData(2000 + i);
                mgr.append(d, null, true, d.smart___keys());
            }

            UpdateFuture[] futures = mgr.flush(); //inserts.
            futures[0].waitToComplete();

            for (int i = 0; i < 2; i++)
            {
                SimplePersistableData d = mgr.get(SimplePersistableData.class, "key1" + (2000 + i));
                d.modify(i);
                mgr.append(d, d.smart___original(), false, d.smart___keys());
            }

            futures = mgr.flush(); //updates
            futures[1].waitToComplete();

            /*
            mgr.select(SimplePersistableData.class)
                .set("another").value("another")
                //.set("persist2").expr() need a syntax for this?
                .where()
                .get("persist2").gt(2050)
                .update();
                */

            mgr.select(SimplePersistableData.class)
                .where()
                .get("persist1").like("key*")
                //.get("persist2").eq(10)
                .search()
                .stream()
                .forEach((SimplePersistableData d) -> 
                { 
                    System.out.println(d);
                });

            //mgr.done(data);
            //mgr.done(data1);
        }
    }

    @Test
    public void testComplex()
        throws Exception
    {
        //test for a 1:n relation declared using the Parent --contains--> List<Children>
        //Children are related to an already declared field within the parent.
        //
        setupMetadata();
        try (PersistableManager mgr = getStream()) 
        {
            for (int i = 0; i < 200; i++)
            {
                ComplexPersistableData cd = new ComplexPersistableData(i);
                mgr.append(cd, null, true, cd.smart___keys());
            }

            UpdateFuture[] futures = mgr.flush(); //inserts.
            futures[0].waitToComplete();

            //should retrieve both the complexdata and the relateddata
            //a fully resolved class
            PersistableDataStream<ComplexPersistableData> cstream = 
                mgr.select(ComplexPersistableData.class)
                .where()
                .get("complexId").gt("0")
                .search();

            cstream.stream()
                .forEach((ComplexPersistableData d) -> 
                { 
                    System.out.println(d);
                });

            //this should only select the top header fields and not
            //the related values
            cstream = mgr.selectRoot(ComplexPersistableData.class)
                    .where()
                    .get("complexId").gt(0)
                    .search();

            cstream.stream()
                .forEach( (ComplexPersistableData d) -> {
                    System.out.println(d);
                });

            //Should only select the related persistabledata
            //for 2001 ComplexPersistableData that match the given
            //criteria
            //PersistableDataStream<RelatedPersistableData> stream = 
            mgr.<RelatedPersistableData>selectLite(ComplexPersistableData.class, "relatedData", 201)
                .where()
                .get("relatedId").gt(2020)
                .search()
                .stream()
                .forEach( (RelatedPersistableData d) -> {
                    System.out.println(d);
                });

            //we should be able to select based on the parent's criteria
            //for subobjects
            SelectLiteQuery.LiteConditions query = (SelectLiteQuery.LiteConditions)
                mgr.selectLite(ComplexPersistableData.class, "another", 201)
                .where();

            query.parent().get("complexData").like("complex*")
                .search()
                .stream()
                .forEach( (Object d) -> System.out.println(d));

            /*
            //should select both complex and related data.
            mgr.selectView(ComplexPersistableData.class, RelatedPersistableData.class)
                .where()
                .get("complexId").gt(2010)
                .search()
                .stream()
                .forEach((ComplexPersistableData d) -> {
                    System.out.println(d);
                });
            */
        }
    }

    /*
    @Test
    public void testCustom()
        throws Exception
    {
        try (PersistableManager mgr = getStream())
        {
            List<CustomPersistableData> data = mgr.select()
                .where()
                .get("Persist1").eq("key*")
                .search();
        }
    }
    */

    @Test
    public void testComplex1()
        throws Exception
    {
        try (PersistableManager mgr = getStream())
        {
            SimplePersistableData sdata = new SimplePersistableData(500000);
            mgr.append(sdata, null, true, sdata.smart___keys());

            ComplexData cdata = new ComplexData(); 
            mgr.append(cdata, null, true, cdata.smart___keys());

            mgr.flush();

            mgr.select(ComplexData.class)
                .where()
                .get("cpersist1").like("ComplexD*")
                .get("cpersist2").eq("100")
                .search()
                .stream()
                .forEach( (ComplexData d) -> {
                    System.out.println(d);
                    try
                    {
                        SimplePersistableData sdata1 = d.getLinked();
                        sdata1.modify(57000);
                        mgr.append(sdata1, sdata1.smart___original(), false, sdata1.smart___keys());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                });

            mgr.flush();
        }
    }
}

