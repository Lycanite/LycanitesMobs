package com.lycanitesmobs.core.modelloader.obj;


public abstract class Model
{

    private String id;

    public abstract void render();
    
    public abstract void renderGroups(String s);
    
    public void setID(String id)
    {
        this.id = id;
    }
    
    public String getID()
    {
        return id;
    }
}
