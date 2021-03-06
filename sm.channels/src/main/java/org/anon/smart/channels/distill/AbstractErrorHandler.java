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
 * File:                org.anon.smart.channels.distill.AbstractErrorHandler
 * Author:              rsankar
 * Revision:            1.0
 * Date:                22-01-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An abstract implementation that can be used
 *
 * ************************************************************
 * */

package org.anon.smart.channels.distill;

import org.anon.smart.channels.data.PData;
import org.anon.smart.channels.data.DScope;
import org.anon.smart.channels.data.Responder;

public abstract class AbstractErrorHandler implements ErrorHandler
{
    public AbstractErrorHandler()
    {
    }

    protected abstract Isotope[] createResponses(Rectifier rectifier, Throwable t, Distillate start);

    public void handleError(Rectifier rectifier, Throwable t, Distillate start)
    {
        try
        {
            PData d = (PData)start.current();
            Isotope[] responses = createResponses(rectifier, t, start);
            if (responses != null)
            {
                PData[] resp = new PData[responses.length];
                for (int i = 0; (i < responses.length); i++)
                {
                    Distillate respstart = new Distillate(responses[i]);
                    Distillate dist = rectifier.condense(respstart);
                    resp[i] = (PData)dist.current();
                }
                d.dscope().transmit(resp);
            }
        }
        catch (Exception e)
        {
            //TODO: need to log
            e.printStackTrace();
        }
    }
}

