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
 * File:                org.anon.smart.smcore.anatomy.SMCoreDSParameterFinder
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                07-10-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A finder that sets up the correct group for objects in SM core
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.anatomy;

import org.anon.smart.d2cache.CacheableObject;
import org.anon.smart.d2cache.store.repository.datasource.metadata.DefaultMetadata.ParameterFinder;

import static org.anon.smart.base.utils.AnnotationUtils.*;

public class SMCoreDSParameterFinder implements ParameterFinder
{
    public SMCoreDSParameterFinder()
    {
    }

    public String getGroup(Class<? extends CacheableObject> datacls)
    {
        String ret = datacls.getSimpleName();
        try
        {
            ret = className(datacls);

            /*
             * if ((ret == null) && (datacls.equals(LinkedData.class)))
            {
            }*/

            //by default fall back on simplename
            if (ret == null)
                ret = datacls.getSimpleName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }
}

