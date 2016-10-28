/**
 * SMART - State Machine ARchiTecture
 *
 * Copyright (C) 2012 Individual contributors as indicated by
 * the @authors tag
 *
 * This file is a part of SMART.
 * it under the terms of the GNU General Public License as published by
 *
 * SMART is a free software: you can redistribute it and/or modify
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
 * File:                org.anon.smart.channels.tcp.TCPConfig
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                Dec 9, 2014
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * 
 *
 * ************************************************************
 * */
package org.anon.smart.channels.tcp;

import org.anon.smart.channels.shell.DataInstincts;
import org.anon.smart.channels.shell.ExternalConfig;
import org.anon.smart.channels.shell.SCFactory;
import org.anon.smart.channels.shell.SCType;
import org.anon.smart.channels.distill.Rectifier;

public class TCPConfig implements ExternalConfig
{
    private String _name;
    private int _port;
    private String _server;
    private TCPFormatter _formatter;
    private DataInstincts _instinct;
    private boolean _client = false;

    public TCPConfig(String nm, int port)
    {
        _name = nm;
        _server = "localhost";
        _port = port;
    }

    public TCPConfig(String nm, int port, String svr, TCPFormatter format)
    {
        _name = nm;
        _port = port;
        _server = svr;
        _formatter = format;
    }

    public void setClient() { _client = true; }
    public void setServer(String svr) { _server = svr; }

    public void setRectifierInstinct(Rectifier rectifier, TCPDataFactory fact) 
    { 
        if (!_client) 
            _instinct = new TCPDataInstinct(rectifier, fact); 
        else
            _instinct = new TCPClientDataInstinct(rectifier, fact);
    }

    @Override
    public SCType scType()
    {
        return SCType.external;
    }

    @Override
    public DataInstincts instinct()
    {
        return _instinct;
    }

    @Override
    public SCFactory creator()
    {
        if (!_client)
            return new TCPServerFactory();
        else
            return new TCPClientFactory();
    }

    @Override
    public String name()
    {
        return _name;
    }

    @Override
    public int port()
    {
        return _port;
    }

    @Override
    public String server()
    {
        return _server;
    }

}
