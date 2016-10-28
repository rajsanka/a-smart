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
 * File:                org.anon.smart.smcore.inbuilt.config.CCAvenueConfig
 * Author:              rsankar
 * Revision:            1.0
 * Date:                02-08-2014
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration for ccavenue payment gateway
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.inbuilt.config;

public final class CCAvenueConfig implements java.io.Serializable
{
    private final String ccavenueserver;
    private final String accessCode;
    private final String workingKey;
    private final String merchantId;

    public CCAvenueConfig()
    {
        ccavenueserver = "";
        accessCode = "";
        workingKey = "";
        merchantId = "";
    }

    public String getAccessCode()
    {
        return accessCode;
    }

    public String getWorkingKey()
    {
        return workingKey;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public String getCCAvenueServer()
    {
        return ccavenueserver;
    }
}

