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
 * File:                org.anon.smart.smcore.anatomy.SMCorePrintStream
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                05-12-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A print stream that overrides the system outs to append appropriate fields
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.anatomy;

import java.util.Date;
import java.io.PrintStream;

import org.anon.smart.smcore.transition.TransitionContext;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;

public class SMCorePrintStream  extends PrintStream
{
    public SMCorePrintStream()
    {
        super(System.out);
    }

    public void println(String msg)
    {
        String newmsg = "";
        try
        {
            Date now = new Date();
            String nowstr = convert().dateToString(now, null);
            newmsg = nowstr + ":";
            Thread thrd = Thread.currentThread();
            newmsg += thrd.getName() + ":";
        }
        catch (Exception e)
        {
            newmsg += "Unknown Date : Unknown Context: ";
        }

        newmsg += msg;
        super.println(newmsg);
    }

}

