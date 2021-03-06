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
 * File:                org.anon.smart.smcore.data.SeriesDataED
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                30-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of data that is a insert only data
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.data;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.anon.smart.atomicity.TruthData;
import org.anon.smart.atomicity.EmpiricalData;
import org.anon.smart.smcore.transition.TransitionContext;
import org.anon.smart.smcore.transition.atomicity.AtomicityConstants;

import static org.anon.smart.base.utils.AnnotationUtils.*;
import static org.anon.utilities.services.ServiceLocator.*;
import static org.anon.utilities.objservices.ObjectServiceLocator.*;

import org.anon.utilities.exception.CtxException;

public class SeriesDataED implements EmpiricalData, TruthData, AtomicityConstants
{
    private SeriesData _data;
    private SeriesData _empirical;
    private boolean _isNew;

    public SeriesDataED(SeriesData d)
        throws CtxException
    {
        assertion().assertNotNull(d, "Cannot add a null data to transaction.");
        _data = d;
        _empirical = (SeriesData)serial().clone(d);
        _isNew = true;
    }


    public void setNew()
    {
        _isNew = true;
    }

    public TruthData truth()
        throws CtxException
    {
        return this;
    }

    public boolean isErrorData()
    {
        return false;
    }

    public String dataType()
        throws CtxException
    {
        return SERIES;
    }

    public List<String> tags()
        throws CtxException
    {
        List<String> lst = new ArrayList<String>();
        lst.add(objectName(_data));
        return lst;
    }

    public UUID truthID()
    {
        return _data.smart___id();
    }

    public boolean start(UUID txnid)
        throws CtxException
    {
        TransitionContext ctx = (TransitionContext)threads().threadContext();
        ctx.transaction().startTransaction(_data);
        return true;
    }

    public boolean simulate(UUID txnid, EmpiricalData edata)
        throws CtxException
    {
        System.out.println("simulating series: " + edata);
        TransitionContext ctx = (TransitionContext)threads().threadContext();
        assertion().assertNotNull(ctx, "The object is not in an transition context to be accepted.");
        ctx.transaction().addToTransaction(_data);
        return true;
    }

    public boolean accept(UUID txnid, EmpiricalData edata)
        throws CtxException
    {
        return true;
    }

    public void discard(UUID txnid, EmpiricalData edata)
        throws CtxException
    {
        return;
    }

    public boolean end(UUID txnid)
        throws CtxException
    {
        return true;
    }

    public void recordEmpiricalData(UUID txnid, EmpiricalData edata)
        throws CtxException
    {
        //does nothing. There is no way the same truth config is used across. it just creates
        //new config objects.
    }

    //cannot be modified. But to prevent original data be changed from what it was previously, data is cloned before being sent out.
    public SeriesData empirical() { return _empirical; }
}

