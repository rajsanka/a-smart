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
 * File:                org.anon.smart.smcore.stt.SeriesSTT
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                30-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A series data stereotype
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.stt;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.anon.smart.base.stt.annot.MethodExit;
import org.anon.smart.base.utils.AnnotationUtils;
import org.anon.smart.smcore.transition.TransitionContext;
import org.anon.smart.smcore.data.SeriesData;
import org.anon.smart.base.annot.KeyAnnotate;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;

import org.anon.utilities.exception.CtxException;

public class SeriesSTT implements SeriesData
{
    @KeyAnnotate(keys="___smart_id___")
    private UUID ___smart_id___;

    private long ___smart_createdOn___;
    private String ___smart_ownedBy___;

    public SeriesSTT()
    {
    }

    @MethodExit("constructor")
    private void seriesstt___init()
        throws CtxException
    {
        ___smart_id___ = UUID.randomUUID();
        ___smart_createdOn___ = System.currentTimeMillis();
        TransitionContext ctx = (TransitionContext)threads().threadContext();
        if (ctx != null)
            ctx.atomicity().includeNewSeries(this);
    }

    @Override
	public void smart___initOnLoad() 
        throws CtxException 
    {
    }

    public List<Object> smart___keys()
    {
        List<Object> keys = new ArrayList<Object>();
        keys.add(___smart_id___);
        return keys;
    }

    public void smart___addKey(Object k)
    {
    }

    public String smart___objectGroup()
        throws CtxException
    {
        return AnnotationUtils.objectName(this);
    }

    public UUID smart___id() { return ___smart_id___; }

    public boolean smart___isNew()
    {
        return true; //always config is always created not edited.
    }

    public void smart___setIsNew(boolean n)
    {
    }
}

