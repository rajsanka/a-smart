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
 * File:                org.anon.smart.base.stt.asm.ASMFieldContext
 * Author:              rsankar
 * Revision:            1.0
 * Date:                28-12-2012
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A field context used by asm
 *
 * ************************************************************
 * */

package org.anon.smart.base.stt.asm;

import org.objectweb.asm.FieldVisitor;

import org.anon.smart.base.stt.FieldContext;
import org.anon.smart.base.stt.ClazzDescriptor;

public class ASMFieldContext extends FieldContext
{
    private FieldVisitor _visitor;

    public ASMFieldContext(ClazzDescriptor descriptor, String name, String signature, int access, String desc, Object val, FieldVisitor visit)
    {
        super(descriptor, name, signature, access, desc, val);
        _visitor = visit;
    }

    public FieldVisitor visitor() { return _visitor; }
}

