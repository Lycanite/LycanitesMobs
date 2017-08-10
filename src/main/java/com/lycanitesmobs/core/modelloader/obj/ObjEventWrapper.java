package com.lycanitesmobs.core.modelloader.obj;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ObjEventWrapper extends Event
{

    public ObjEvent objEvent;

    public ObjEventWrapper(ObjEvent e)
    {
        this.objEvent = e;
    }

    public boolean isCancelable()
    {
        return objEvent.canBeCancelled();
    }
}
