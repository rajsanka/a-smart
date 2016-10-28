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
 * File:                org.anon.smart.d2cache.store.repository.datasource.metadata.SchemaProcessor
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                30-09-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A processor to setup schema
 *
 * ************************************************************
 * */

package org.anon.smart.d2cache.store.repository.datasource.metadata;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.PersistableData;

import org.anon.utilities.loader.PostProcessor;
import org.anon.utilities.crosslink.CrossLinkAny;

import static org.anon.utilities.services.ServiceLocator.*;

public class SchemaProcessor implements PostProcessor
{
    public SchemaProcessor()
    {
    }

    public void process(ClassLoader ldr, Class cls)
    {
        try
        {
            if (cls.getName().startsWith("org.anon.smart.d2cache"))
                return;

            CrossLinkAny cl = new CrossLinkAny("org.anon.smart.d2cache.store.repository.datasource.PersistableData", ldr);
            cl.invoke("metadataFor", new Object[] { cls });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

