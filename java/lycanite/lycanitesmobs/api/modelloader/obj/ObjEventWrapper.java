package lycanite.lycanitesmobs.api.modelloader.obj;

import net.minecraftforge.fml.common.eventhandler.Event;

import lycanite.lycanitesmobs.api.modelloader.obj.ObjEvent;

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
