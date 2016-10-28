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
 * File:                org.anon.smart.smcore.channel.server.CustomTCPConfig
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                27-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration for tcp channels
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.channel.server;

import org.anon.smart.channels.tcp.TCPConfig;
import org.anon.smart.channels.tcp.TCPDataFactory;
import org.anon.smart.channels.distill.Rectifier;
import org.anon.smart.smcore.channel.distill.translation.CustomTranslationStage;
import org.anon.smart.smcore.channel.distill.sanitization.SanitizationStage;
import org.anon.smart.smcore.channel.distill.alteration.AlterationStage;
import org.anon.smart.smcore.channel.distill.storage.StorageStage;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.objservices.ConvertService.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;

public class CustomTCPConfig extends TCPConfig
{
    private String _tcpConfig;

    public CustomTCPConfig(String nm, int port, String translate, String configure, String parms)
        throws CtxException
    {
        super(nm, port);
        _tcpConfig = configure;
        Rectifier rectifier = new Rectifier();
        rectifier.addStep(new CustomTranslationStage(translate));
        rectifier.addStep(new SanitizationStage());
        rectifier.addStep(new AlterationStage());
        rectifier.addStep(new StorageStage());
        rectifier.setupErrorHandler(new EventErrorHandler());
        setRectifierInstinct(rectifier, new RawEventDataFactory(_tcpConfig, parms));
    }

    public String getTCPCoreConfig() { return _tcpConfig; }
}

