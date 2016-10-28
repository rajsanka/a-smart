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
 *
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.anon.smart.channels.test.tcp.TestTCPServer
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                26-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of test cases for TCP server
 *
 * ************************************************************
 * */

package org.anon.smart.channels.test.tcp;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import static org.junit.Assert.*;

import org.anon.smart.channels.distill.Rectifier;
import org.anon.smart.channels.SmartChannel;
import org.anon.smart.channels.tcp.TCPClientChannel;
import org.anon.smart.channels.shell.SCShell;
import org.anon.smart.channels.tcp.TCPConfig;
import org.anon.smart.channels.data.ContentData;
import org.anon.smart.channels.data.PData;

public class TestTCPServer
{
    @Test
    public void testTestTCPServer()
        throws Exception
    {
        Rectifier r = new Rectifier();
        r.addStep(new TestTCPDistillation(true));
        TCPConfig cfg = new TCPConfig("Test", 8084);
        cfg.setRectifierInstinct(r, new TestTCPDataFactory());
        SCShell shell = new SCShell();
        SmartChannel chnl = shell.addChannel(cfg);
        shell.startAllChannels();


        Rectifier rr = new Rectifier();
        rr.addStep(new TestTCPDistillation(false));
        TCPConfig ccfg = new TCPConfig("Test", 8084);
        ccfg.setClient();
        ccfg.setServer("localhost");
        ccfg.setRectifierInstinct(rr, new TestTCPDataFactory());
        TCPClientChannel cchnl = (TCPClientChannel)shell.addChannel(ccfg);
        cchnl.connect();
        String post = "This is a first post";
        ByteArrayInputStream istr = new ByteArrayInputStream(post.getBytes());
        PData d = new TestTCPPData(null, new ContentData(istr));
        cchnl.post(new PData[] { d });
        Thread.sleep(3000);
        cchnl.disconnect();

        shell.stopAllChannels();
    }
}

