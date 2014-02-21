package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityList {
    /** Provides a mapping between entity classes and a string */
    public Map stringToClassMapping = new HashMap();

    /** Provides a mapping between a string and an entity classes */
    public Map classToStringMapping = new HashMap();

    /** provides a mapping between an entityID and an Entity Class */
    public Map IDtoClassMapping = new HashMap();

    /** provides a mapping between an Entity Class and an entity ID */
    private Map classToIDMapping = new HashMap();

    /** Maps entity names to their numeric identifiers */
    private Map stringToIDMapping = new HashMap();

    /** This is a HashMap of the Creative Entity Eggs/Spawners. */
    public HashMap entityEggs = new LinkedHashMap();

    /**
     * adds a mapping between Entity classes and both a string representation and an ID
     */
    public void addMapping(Class par0Class, String par1Str, int par2) {
        stringToClassMapping.put(par1Str, par0Class);
        classToStringMapping.put(par0Class, par1Str);
        IDtoClassMapping.put(Integer.valueOf(par2), par0Class);
        classToIDMapping.put(par0Class, Integer.valueOf(par2));
        stringToIDMapping.put(par1Str, Integer.valueOf(par2));
    }

    /**
     * Adds a entity mapping with egg info.
     */
    public void addMapping(Class par0Class, String par1Str, int par2, int par3, int par4) {
        addMapping(par0Class, par1Str, par2);
        entityEggs.put(Integer.valueOf(par2), new EntityEggInfo(par2, par3, par4));
    }

    /**
     * Create a new instance of an entity in the world by using the entity name.
     */
    public Entity createEntityByName(String par0Str, World par1World) {
        Entity entity = null;

        try {
            Class oclass = (Class)stringToClassMapping.get(par0Str);

            if (oclass != null) {
                entity = (Entity)oclass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par1World});
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        return entity;
    }

    /**
     * create a new instance of an entity from NBT store
     */
    public Entity createEntityFromNBT(NBTTagCompound par0NBTTagCompound, World par1World) {
        Entity entity = null;

        if ("Minecart".equals(par0NBTTagCompound.getString("id"))) {
            switch (par0NBTTagCompound.getInteger("Type")) {
                case 0:
                    par0NBTTagCompound.setString("id", "MinecartRideable");
                    break;
                case 1:
                    par0NBTTagCompound.setString("id", "MinecartChest");
                    break;
                case 2:
                    par0NBTTagCompound.setString("id", "MinecartFurnace");
            }

            par0NBTTagCompound.removeTag("Type");
        }

        Class oclass = null;
        try {
            oclass = (Class)stringToClassMapping.get(par0NBTTagCompound.getString("id"));

            if (oclass != null) {
                entity = (Entity)oclass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par1World});
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity != null) {
            try {
                entity.readFromNBT(par0NBTTagCompound);
            }
            catch (Exception e) {
                FMLLog.log(Level.SEVERE, e,
                        "An Entity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                        par0NBTTagCompound.getString("id"), oclass.getName());
                entity = null;
            }
        }
        else {
            par1World.getWorldLogAgent().logWarning("Skipping Entity with id " + par0NBTTagCompound.getString("id"));
        }

        return entity;
    }

    /**
     * Create a new instance of an entity in the world by using an entity ID.
     */
    public Entity createEntityByID(int par0, World par1World) {
        Entity entity = null;

        try {
            Class oclass = getClassFromID(par0);

            if (oclass != null) {
                entity = (Entity)oclass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {par1World});
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity == null) {
            par1World.getWorldLogAgent().logWarning("Skipping Entity with id " + par0);
        }

        return entity;
    }

    /**
     * gets the entityID of a specific entity
     */
    public int getEntityID(Entity par0Entity) {
        Class oclass = par0Entity.getClass();
        return classToIDMapping.containsKey(oclass) ? ((Integer)classToIDMapping.get(oclass)).intValue() : 0;
    }

    /**
     * Return the class assigned to this entity ID.
     */
    public Class getClassFromID(int par0) {
        return (Class)IDtoClassMapping.get(Integer.valueOf(par0));
    }

    /**
     * Gets the string representation of a specific entity.
     */
    public String getEntityString(Entity par0Entity) {
        return (String)classToStringMapping.get(par0Entity.getClass());
    }

    /**
     * Finds the class using IDtoClassMapping and classToStringMapping
     */
    public String getStringFromID(int par0) {
        Class oclass = getClassFromID(par0);
        return oclass != null ? (String)classToStringMapping.get(oclass) : null;
    }
}
