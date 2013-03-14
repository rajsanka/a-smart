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
 * File:                org.anon.smart.smcore.stt.STTService
 * Author:              rsankar
 * Revision:            1.0
 * Date:                15-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A service which registers the appropriate stt and annotations
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.stt;

import org.anon.smart.base.stt.STTRegister;

import org.anon.utilities.exception.CtxException;

public class STTService
{
    private STTService()
    {
    }

    public static void initialize()
        throws CtxException
    {
        STTRegister.registerSTT("PrimeData", "org.anon.smart.smcore.stt.SmartPrimeDataSTT");
        STTRegister.registerSTT("Data", "org.anon.smart.smcore.stt.SmartDataSTT");
        STTRegister.registerSTT("Event", "org.anon.smart.smcore.stt.EventSTT");
    }
}

