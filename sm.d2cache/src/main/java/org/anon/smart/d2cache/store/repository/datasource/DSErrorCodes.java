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
 * File:                org.anon.smart.d2cache.store.repository.datasource.DSErrorCodes
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

public interface DSErrorCodes
{
    public static final int USE_CLASSLOADER = 0x801;
    public static final int INVALID_DBNAME = 0x802;
    public static final int END_REACHED = 0x803;
    public static final int INVALID_DATA = 0x804;
    public static final int DUPLICATE_ATTRIBUTE = 0x805;
    public static final int INVALID_UPDATE = 0x806;
    public static final int INVALID_ATTRIBUTE = 0x807;
    public static final int ERROR_READING = 0x808;
    public static final int INVALID_SETUP = 0x809;
}

