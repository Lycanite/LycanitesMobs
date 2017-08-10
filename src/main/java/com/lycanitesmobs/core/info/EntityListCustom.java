package com.lycanitesmobs.core.info;

import com.google.common.collect.Maps;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class EntityListCustom {
    /** provides a mapping between an entityID and an Entity Class */
    public Map<ResourceLocation, Class> IDtoClassMapping = new HashMap<ResourceLocation, Class>();

    /** provides a mapping between an Entity Class and an entity ID */
    private Map<Class, ResourceLocation> classToIDMapping = new HashMap<Class, ResourceLocation>();

    /** This is a HashMap of the Creative Entity Eggs/Spawners. */
    public Map<ResourceLocation, EntityEggInfo> entityEggs = Maps.<ResourceLocation, EntityEggInfo>newLinkedHashMap();

    /**
     * adds a mapping between Entity classes and both a string representation and an ID
     */
    public void addMapping(Class entityClass, ResourceLocation entityID) {
        this.IDtoClassMapping.put(entityID, entityClass);
        this.classToIDMapping.put(entityClass, entityID);
    }

    public void addMapping(Class entityClass, ResourceLocation entityID, int baseColor, int spotColor) {
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

        this.createEntityByID(new ResourceLocation(nbtTagCompound.getString("id")), world);

        return entity;
    }

    /**
     * Create a new instance of an entity in the world by using an entity ID.
     */
    public Entity createEntityByID(ResourceLocation entityID, World world) {
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
    public ResourceLocation getEntityID(Entity entity) {
        Class entityClass = entity.getClass();
        return classToIDMapping.containsKey(entityClass) ? classToIDMapping.get(entityClass) : null;
    }

    /**
     * Return the class assigned to this entity ID.
     */
    public Class getClassFromID(ResourceLocation entityID) {
        return this.IDtoClassMapping.get(entityID);
    }
    
    public static class EntityEggInfo {
        /** The entityID of the spawned mob */
        public final ResourceLocation spawnedID;
        /** Base color of the egg */
        public final int primaryColor;
        /** Color of the egg spots */
        public final int secondaryColor;

        public EntityEggInfo(ResourceLocation entityID, int primaryColor, int secondaryColor) {
            this.spawnedID = entityID;
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
        }
    }
}
