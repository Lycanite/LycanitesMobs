package com.lycanitesmobs.core.modelloader.obj;

import net.minecraft.entity.Entity;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.vecmath.Vector4f;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class ObjModel extends Model
{

    public List<ObjObject> objObjects;

    protected String filename;

    public Entity entity;
    
    ObjModel() {
        objObjects = new ArrayList<ObjObject>();
    }
    
    public ObjModel(String classpathElem) {
        this();
        this.filename = classpathElem;
        if(filename.contains("/"))
            setID(filename.substring(filename.lastIndexOf("/")+1));
        else
            setID(filename);
    }
    
    protected byte[] read(InputStream resource) throws IOException {
        int i;
        byte[] buffer = new byte[65565];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((i = resource.read(buffer, 0, buffer.length)) != -1)
        {
            out.write(buffer,0,i);
        }
        out.flush();
        out.close();
        return out.toByteArray();
    }

    public void renderGroup(ObjObject group) {
        this.renderGroup(group, new Vector4f(1, 1, 1, 1));
    }

    public void renderGroup(ObjObject group, Vector4f color) {
        if(fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_GROUP).setData(group, group)))
            this.renderGroupImpl(group, color);
        fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_GROUP).setData(group, group));
    }
    
    public void renderGroups(String groupsName) {
        if(fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_GROUPS).setData(groupsName)))
            this.renderGroupsImpl(groupsName);
        fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_GROUPS).setData(groupsName));
    }
    
    public void render() {
        if(fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_ALL)))
            this.renderImpl();
        fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_ALL));
    }
    
    protected abstract void renderGroupsImpl(String groupsName);
    
    protected abstract void renderGroupImpl(ObjObject objGroup, Vector4f color);

    protected abstract void renderImpl();
    
    public abstract boolean fireEvent(ObjEvent event);
}
