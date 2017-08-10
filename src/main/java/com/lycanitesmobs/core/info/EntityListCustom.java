package com.lycanitesmobs.core.info;

import com.google.common.collect.Maps;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class EntityListCustom {
    /** provides a mapping between an entityID and an Entity Class */
    public Map<String, Class> IDtoClassMapping = new HashMap<String, Class>();

    /** provides a mapping between an Entity Class and an entity ID */
    private Map<Class, String> classToIDMapping = new HashMap<Class, String>();

    /** This is a HashMap of the Creative Entity Eggs/Spawners. */
    public Map<String, EntityEggInfo> entityEggs = Maps.<String, EntityEggInfo>newLinkedHashMap();

    /**
     * adds a mapping between Entity classes and both a string representation and an ID
     */
    public void addMapping(Class entityClass, String entityID) {
        this.IDtoClassMapping.put(entityID, entityClass);
        this.classToIDMapping.put(entityClass, entityID);
    }

    public void addMapping(Class entityClass, String entityID, int baseColor, int spotColor) {
        this.addMapping(entityClass, entityID);
        this.entityEggs.put(entityID, new EntityEggInfo(entityID, baseColor, spotColor));
    }

    /**
     * create a new instance of an entity from NBT store
     */
    public Entity createEntityFromNBT(NBTTagCompound nbtTagCompound, World world) {
        Entity entity = null;

        if ("Minecart".equals(nbtTagCompound.getString("id"))) {
            switch (nbtTagCompound.getInteger("Type")) {
                case 0:
                    nbtTagCompound.setString("id", "MinecartRideable");
                    break;
                case 1:
                    nbtTagCompound.setString("id", "MinecartChest");
                    break;
                case 2:
                    nbtTagCompound.setString("id", "MinecartFurnace");
            }

            nbtTagCompound.removeTag("Type");
        }

        this.createEntityByID(nbtTagCompound.getString("id"), world);

        return entity;
    }

    /**
     * Create a new instance of an entity in the world by using an entity ID.
     */
    public Entity createEntityByID(String entityID, World world) {
        Entity entity = null;

        try {
            Class entityClass = this.getClassFromID(entityID);

            if (entityClass != null) {
                entity = (Entity)entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity == null) {
            LycanitesMobs.printWarning("","Unable to spawn entity with the id " + entityID);
        }

        return entity;
    }

    /**
     * gets the entityID of a specific entity
     */
    public String getEntityID(Entity entity) {
        Class entityClass = entity.getClass();
        return classToIDMapping.containsKey(entityClass) ? classToIDMapping.get(entityClass) : null;
    }

    /**
     * Return the class assigned to this entity ID.
     */
    public Class getClassFromID(String entityID) {
        return this.IDtoClassMapping.get(entityID);
    }
    
    public static class EntityEggInfo {
        /** The entityID of the spawned mob */
        public final String spawnedID;
        /** Base color of the egg */
        public final int primaryColor;
        /** Color of the egg spots */
        public final int secondaryColor;

        public EntityEggInfo(String entityID, int primaryColor, int secondaryColor) {
            this.spawnedID = entityID;
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
        }
    }
}
