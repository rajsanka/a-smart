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
 * File:                org.anon.smart.smcore.test.channel.tcp.TestTCPChannel
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                27-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of test cases for tcp channel
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.test.channel.tcp;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;

import org.anon.smart.channels.distill.Rectifier;
import org.anon.smart.channels.SmartChannel;
import org.anon.smart.channels.tcp.TCPClientChannel;
import org.anon.smart.channels.shell.SCShell;
import org.anon.smart.channels.tcp.TCPConfig;
import org.anon.smart.channels.data.ContentData;
import org.anon.smart.channels.data.PData;

import org.anon.smart.smcore.test.CoreServerUtilities;
import org.anon.smart.smcore.test.TestClient;
import org.anon.smart.smcore.test.ResponseCollector;

import org.anon.smart.channels.test.tcp.TestTCPPData;
import org.anon.smart.channels.test.tcp.TestTCPDataFactory;

import org.anon.utilities.anatomy.CrossLinkApplication;

public class TestTCPChannel
{
    private SCShell _shell;

    protected PData createPData(InputStream istr, String uri)
        throws Exception
    {
        return new TestTCPPData(null, new ContentData(istr));
    }

    public void postTo(int port, String server, String post, boolean wait)
        throws Exception
    {
        _shell = new SCShell();
        String ret = "";
        ResponseCollector collect = new ResponseCollector(wait);
        Rectifier rr = new Rectifier();
        rr.addStep(collect);
        TCPConfig ccfg = new TCPConfig("Test", port);
        ccfg.setClient();
        ccfg.setServer(server);
        ccfg.setRectifierInstinct(rr, new TestTCPDataFactory());
        TCPClientChannel cchnl = (TCPClientChannel)_shell.addChannel(ccfg);
        cchnl.connect();
        ByteArrayInputStream istr = new ByteArrayInputStream(post.getBytes());
        //PData d = new TestPData(null, new ContentData(istr));
        PData d = createPData(istr, "");
        cchnl.post(new PData[] { d });
        if (wait)
        {
            collect.waitForResponse();
            ret = collect.getResponse();
            System.out.println("Got response: " + ret);
            cchnl.disconnect();
        }
    }

    //@Test
    public void testTestTCPChannel()
        throws Exception
    {
        int port = 9080;
        CoreServerUtilities utils = new CoreServerUtilities(port);
        utils.runServer("org.anon.smart.smcore.test.channel.tcp.RunSmartTCPServer");

        TestClient clnt = new TestClient(port, "localhost", "newtenant", "RegistrationFlow", "RegistrationFlow.soa");
        clnt.deployFromSampleJar();
        clnt.createTenant();

        postTo(9087, "localhost", "keyobject=FlowAdmin&keyvalue=RegistrationFlow&email=rsankarx@gmail.com&event=RegisterEvent&review=Reviewed&rating=1", true);

        utils.stopServer();
        //Thread.sleep(10000);
    }

    //@Test
    public void testKernelTCPChannel()
        throws Exception
    {
        /*byte data[] = new byte[12];
        data[0] = 0x00;
        data[1] = 0x01;
        data[2] = 0x00;
        data[3] = 0x00;
        data[4] = 0x00;
        data[5] = 0x06;
        data[6] = 0x11;
        data[7] = 0x03;
        data[8] = 0x00;
        data[9] = 0x6B;
        data[10] = 0x00;
        data[11] = 0x03;*/

        byte[] data = new byte[93];
        data[0] = 0x01;
        data[1] = 0x03;
        data[2] = 0x06;
        data[3] = 0x08;
        data[4] = 0x03;
        data[5] = 0x01;
        data[6] = 0x11;
        data[7] = 0x35;
        data[8] = 0x35;
        data[9] = (byte)0xE2;
        data[10] = (byte)0x83;
        data[11] = 0x01;
        data[12] = 0x04;
        data[13] = 0x18;
        data[14] = 0x08;
        data[15] = (byte)0xDE;
        data[16] = 0x00;
        data[17] = 0x00;
        data[18] = 0x00;
        data[19] = 0x00;
        data[20] = 0x00;
        data[21] = 0x00;
        data[22] = 0x00;
        data[23] = 0x00;
        data[24] = 0x00;
        data[25] = 0x00;
        data[26] = 0x00;
        data[27] = 0x00;
        data[28] = 0x00;
        data[29] = 0x00;
        data[30] = 0x00;
        data[31] = 0x00;
        data[32] = 0x00;
        data[33] = 0x00;
        data[34] = 0x00;
        data[35] = 0x00;
        data[36] = 0x00;
        data[37] = 0x00;
        data[38] = 0x38;
        data[39] = 0x14;
        data[40] = 0x01;
        data[41] = 0x04;
        data[42] = 0x30;
        data[43] = 0x00;
        data[44] = 0x00;
        data[45] = 0x00;
        data[46] = 0x00;
        data[47] = 0x00;
        data[48] = 0x00;
        data[49] = 0x00;
        data[50] = 0x00;
        data[51] = 0x00;
        data[52] = 0x00;
        data[53] = 0x00;
        data[54] = 0x00;
        data[55] = 0x00;
        data[56] = 0x00;
        data[57] = 0x00;
        data[58] = 0x00;
        data[59] = 0x00;
        data[60] = 0x00;
        data[61] = 0x00;
        data[62] = 0x00;
        data[63] = 0x00;
        data[64] = 0x00;
        data[65] = 0x00;
        data[66] = 0x00;
        data[67] = 0x00;
        data[68] = 0x00;
        data[69] = 0x00;
        data[70] = 0x00;
        data[71] = 0x00;
        data[72] = 0x00;
        data[73] = 0x00;
        data[74] = 0x00;
        data[75] = 0x00;
        data[76] = 0x00;
        data[77] = 0x00;
        data[78] = 0x00;
        data[79] = 0x00;
        data[80] = 0x00;
        data[81] = 0x00;
        data[82] = 0x00;
        data[83] = 0x00;
        data[84] = 0x00;
        data[85] = 0x00;
        data[86] = 0x00;
        data[87] = 0x00;
        data[88] = 0x00;
        data[89] = 0x00;
        data[90] = 0x00;
        data[91] = 0x42;
        data[92] = 0x2D;
        _shell = new SCShell();
        String ret = "";
        ResponseCollector collect = new ResponseCollector(false);
        Rectifier rr = new Rectifier();
        rr.addStep(collect);
        TCPConfig ccfg = new TCPConfig("Test", 9091);
        ccfg.setClient();
        //ccfg.setServer("128.199.135.204");
        ccfg.setServer("localhost");
        ccfg.setRectifierInstinct(rr, new TestTCPDataFactory());
        TCPClientChannel cchnl = (TCPClientChannel)_shell.addChannel(ccfg);
        cchnl.connect();
        ByteArrayInputStream istr = new ByteArrayInputStream(data);
        //PData d = new TestPData(null, new ContentData(istr));
        PData d = createPData(istr, "");
        cchnl.post(new PData[] { d });
        Thread.sleep(10000);
    }
}

