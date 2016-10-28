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
 * File:                org.anon.smart.smcore.channel.distill.translation.ModBusTranslator
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                25-02-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A translator for Mod bus related package
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.channel.distill.translation;

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import org.anon.smart.smcore.channel.distill.ChannelConstants;

import org.anon.utilities.exception.CtxException;

import static org.anon.utilities.services.ServiceLocator.*;


public class ModBusTranslator
{
    public ModBusTranslator()
    {
    }

    private void writeToBuffer(byte[] bytes, int start, int len, StringBuffer buff)
    {
        for (int i = start; i < len; i++)
        {
            buff.append(" ");
            buff.append(String.format("0x%02X", bytes[i]));
        }
    }

    public Map<String, Object> readStream(InputStream in)
        throws Exception
    {
        StringBuffer buff = new StringBuffer();
        Map<String, Object> ret = new HashMap<String, Object>();
        byte MBAP[] = new byte[7];
        int read = in.read(MBAP, 0, 7);
        assertion().assertTrue((read == 7), "Not the expected format");
        int sz = ((MBAP[5] & 0xFF) << 8) + (MBAP[4] & 0xFF);
        int fncode = MBAP[6] & 0xFF;
        writeToBuffer(MBAP, 0, 7, buff);
        byte data[] = new byte[1024];
        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        int readlen = 1; //Unique id is already read
        System.out.println("size to Read: " + sz);
        while (readlen < sz)
        {
            read = in.read(data, 0, 1024);
            System.out.println("Read: " + read + ":" + sz + ":" + readlen);
            if (read > 0) 
            {
                ostr.write(data, 0, read);
                //write a formatted output into some log.
                writeToBuffer(data, 0, read, buff);
                readlen += read;
            }
        }

        ret.put("Data", ostr.toByteArray());
        System.out.println("ModBus Bytes: " + buff.toString());
        return ret;
    }

    public void writeStream(Object resp, OutputStream ostr)
        throws CtxException
    {
        //do nothing. Unless we know what to do.
        System.out.println("Response is: " + resp + ":" + resp.getClass());
    }
}

