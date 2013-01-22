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
 * File:                org.anon.smart.channels.test.http.TestDistillation
 * Author:              rsankar
 * Revision:            1.0
 * Date:                11-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A testing for refinery
 *
 * ************************************************************
 * */

package org.anon.smart.channels.test.http;

import org.anon.smart.channels.distill.Distillation;
import org.anon.smart.channels.distill.Distillate;
import org.anon.smart.channels.data.PData;

import org.anon.utilities.exception.CtxException;

public class TestDistillation implements Distillation
{
    private boolean _respond;

    public TestDistillation(boolean resp)
    {
        _respond = resp;
    }

    public Distillate distill(Distillate prev)
        throws CtxException
    {
        System.out.println(prev.current());
        if (_respond)
        {
            PData pdata = (PData)prev.current();
            pdata.dscope().transmit(new PData[] { pdata });
        }
        return prev;
    }

    public Distillate condense(Distillate prev)
        throws CtxException
    {
        System.out.println(prev.current());
        return prev;
    }

    public boolean distillFrom(Distillate prev)
        throws CtxException
    {
        return true;
    }

    public boolean condenseFrom(Distillate prev)
        throws CtxException
    {
        return true;
    }
}

