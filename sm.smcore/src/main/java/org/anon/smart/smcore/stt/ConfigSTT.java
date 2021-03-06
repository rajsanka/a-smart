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
 * File:                org.anon.smart.smcore.stt.ConfigSTT
 * Author:              rsankar
 * Revision:            1.0
 * Date:                05-05-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A stereotype for config objects
 *
 * ************************************************************
 * */

package org.anon.smart.smcore.stt;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.anon.smart.smcore.data.DataLegend;
import org.anon.smart.base.stt.annot.MethodExit;
import org.anon.smart.base.utils.AnnotationUtils;
import org.anon.smart.smcore.transition.TransitionContext;
import org.anon.smart.smcore.data.ConfigData;
import org.anon.smart.base.annot.KeyAnnotate;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;

import org.anon.utilities.exception.CtxException;

public class ConfigSTT implements ConfigData
{
    @KeyAnnotate(keys="___smart_config_key___")
    private String ___smart_config_key___;

    private List<Object> ___smart_keys___;
    //private UUID ___smart_config_id___;
    private DataLegend ___smart_legend___;

    public ConfigSTT()
    {
    }

    @MethodExit("constructor")
    private void configstt___init()
        throws CtxException
    {
        //___smart_config_id___ = UUID.randomUUID();
        ___smart_legend___ = new DataLegend();
        TransitionContext ctx = (TransitionContext)threads().threadContext();
        if (ctx != null)
            ctx.atomicity().includeNewConfig(this);
    }

    @Override
	public void smart___initOnLoad() 
        throws CtxException 
    {
    }

    public List<Object> smart___keys()
    {
        List<Object> keys = new ArrayList<Object>();
        keys.add(___smart_legend___.id());
        for (int i = 0; (___smart_keys___ != null) && (i < ___smart_keys___.size()); i++)
            keys.add(___smart_keys___.get(i));
        return keys;
    }

    public void smart___addKey(Object k)
    {
        if (___smart_keys___ == null)
            ___smart_keys___ = new ArrayList<Object>();
        ___smart_keys___.add(k);
        ___smart_config_key___ = k.toString(); //in mysql limited keys
    }

    public String smart___objectGroup()
        throws CtxException
    {
        return AnnotationUtils.objectName(this);
    }

    //public UUID smart___id() { return ___smart_id___; }
    public UUID smart___id() { return ___smart_legend___.id(); }

    public boolean smart___isNew()
    {
        return true; //always config is always created not edited.
    }

    public void smart___setIsNew(boolean n)
    {
    }
}

