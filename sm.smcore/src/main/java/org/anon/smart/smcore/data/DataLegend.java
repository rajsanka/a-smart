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
 * File:                org.anon.smart.smcore.data.DataLegend
 * Author:              rsankar
 * Revision:            1.0
 * Date:                15-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A legend attached to the data used by smart
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.data;

import java.util.UUID;

public class DataLegend implements java.io.Serializable
{
    private UUID ___smart_id___;
    private String ___smart_group___;

    private long ___smart_createdOn___;
    private long ___smart_lastModifiedOn___;
    private String ___smart_ownedBy___;
    private String ___smart_lastModifiedBy___;

    public DataLegend()
    {
        ___smart_id___ = UUID.randomUUID();
        ___smart_createdOn___ = System.nanoTime();
        ___smart_lastModifiedOn___ = System.nanoTime();
        //TODO: pickup from thread for user
    }

    public UUID id() { return ___smart_id___; }
    public String group() { return ___smart_group___; }
    public String ownedBy() { return ___smart_ownedBy___; }
    public void setOwnedBy(String owner) { ___smart_ownedBy___ = owner; }
    public String lastModifiedBy() { return ___smart_lastModifiedBy___; }

    public void stampData()
    {
        ___smart_lastModifiedOn___ = System.nanoTime();
        //TODO: pickup from thread for user
    }

    public void setGroup(String grp) { ___smart_group___ = grp; }

    public static boolean smart___expandIntoParent() { return true; }
}

