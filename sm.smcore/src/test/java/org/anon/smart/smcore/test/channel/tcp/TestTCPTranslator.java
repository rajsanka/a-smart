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
 * File:                org.anon.smart.smcore.test.channel.tcp.TestTCPTranslator
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                30-09-2015
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A translator for testing tcp channels
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.test.channel.tcp;

import java.io.OutputStream;

import org.anon.smart.smcore.channel.distill.translation.FormTranslator;

public class TestTCPTranslator extends FormTranslator
{
    public TestTCPTranslator()
    {
    }

    public void writeStream(Object resp, OutputStream ostr)
        throws Exception
    {
        //standard for now.
        System.out.println("Response is: " + resp + ":" + resp.getClass());
        String constant = "testresponse";
        ostr.write(constant.getBytes());
    }
}

