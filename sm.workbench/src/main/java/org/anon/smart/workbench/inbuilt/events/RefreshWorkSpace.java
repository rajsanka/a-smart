/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.anon.smart.workbench.inbuilt.events.RefreshWorkSpace
 * Author:              rsankarx
 * Revision:            1.0
 * Date:                23-11-2016
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * An internal refresh workspace event that adds the current objects to the workspace and runs the rules
 *
 * ************************************************************
 * */

package org.anon.smart.workbench.inbuilt.events;

import org.anon.smart.workbench.data.WorkSpace;

public class RefreshWorkSpace implements java.io.Serializable
{
    private WorkSpace workSpace;

    public RefreshWorkSpace(WorkSpace ws)
    {
        workSpace = ws;
    }

    public WorkSpace getWorkspace() { return workSpace; }
}

