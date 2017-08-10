package com.lycanitesmobs.core.pets;


import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class PetEntry {
    /** The unique name for this entry, used when reading and writing NBT Data. If it is an empty string "" or null then NBT Data will not be stored. **/
    public String name;
    /** The Pet Manager that this entry is added to, this can also be null. **/
    public PetManager petManager;
    /** The ID given to this entry by the PetManager. **/
    public int petEntryID;
    /** The type of entry this is. This should really always be set if this entry is to be added to a manager. This should only be set when the entry is instantiated and then kept the same. **/
    private String type;
    /** This is set to false if this entry has been removed. Used by PetManagers to auto-remove finished temporary entries. **/
    public boolean active = true;

    /** A timer used to count down to 0 for respawning. **/
    public int respawnTime = 0;
    /** The amount of time until respawn. **/
    public int respawnTimeMax;
    /** True if the entity has died and must wait to respawn. **/
    public boolean isRespawning = false;
    /** Counts how many times this entry has summoned its entity. **/
    public int spawnCount = 0;
    /** If true, this entry and it's entity will be marked as temporary where once the entity is gone, it will not respawn. The minion type sets this to true. **/
    public boolean temporary = false;
    /** For temporary entities, this will set how the long the entity will last before it despawns. **/
    public int temporaryDuration = 5 * 20;
    /** True if this entry should keep its entity spawned/respawned. False if the entity should be removed and not spawned. This can be turned on and off (such as for familiars). **/
    public boolean spawningActive = true;

    /** The entity that this entry belongs to. **/
    public EntityLivingBase host;
    /** The summon set to use when spawning, etc. **/
    public SummonSet summonSet;
    /** The current entity instance that this entry is using. **/
    public Entity entity;
    /** The current entity NBT data. **/
    public NBTTagCompound entityNBT;
    /** Entity update tick, this counts up each tick as the entity is spawned and active and is paused when the entity is inactive. **/
    public int entityTick = 0;
    /** Entity Health **/
    public float entityHealth = 1;
    /** Entity Max Health **/
    public float entityMaxHealth = 1;

    /** The name to use for the entity. Leave empty/null "" for no name. **/
    public String entityName = "";
    /** The Subspecies ID to use for the entity. **/
    public int subspeciesID = 0;
    /** The size scale to use for the entity. **/
    public double entitySize = 1.0D;
    /** Coloring for this entity such as collar coloring. **/
    public String color = "000000";

    /** If true, a teleport has been requested to teleport the entity (if active) to the host entity. **/
    public boolean teleportEntity = false;

    /** If true, a release is pending where the player must confirm the release. This will not release the entity, instead the player must confirm that. Only used client side. **/
    public boolean releaseEntity = false;

    // ==================================================
    //                 Create from Entity
    // ==================================================
    /** Returns a new PetEntry based off the provided entity for the provided player. **/
    public static PetEntry createFromEntity(EntityPlayer player, EntityCreatureBase entity, String petType) {
        MobInfo mobInfo = entity.mobInfo;
        String entryName = petType + "-" + player.getName() + mobInfo.name + "-" + UUID.randomUUID().toString();
        PetEntry petEntry = new PetEntry(entryName, petType, player, mobInfo.name);
        if(entity.hasCustomName())
            petEntry.setEntityName(entity.getCustomNameTag());
        petEntry.setEntitySubspeciesID(entity.getSubspeciesIndex());
        petEntry.setEntitySize(entity.sizeScale);
        petEntry.setColor("000000");
        return petEntry;
    }

	
    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntry(String name, String type, EntityLivingBase host, String summonType) {
        this.name = name;
        this.type = type;
        this.host = host;

        ExtendedPlayer playerExt = null;
        if(host != null && host instanceof EntityPlayer)
            playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)host);
        this.summonSet = new SummonSet(playerExt);
        this.summonSet.summonableOnly = false;
        this.summonSet.setSummonType(summonType);

        this.respawnTimeMax = 5 * 60 * 20;
        if("minion".equalsIgnoreCase(this.type))
            this.temporary = true;
	}


    // ==================================================
    //                     Set Values
    // ==================================================
    public PetEntry setEntityName(String name) {
        this.entityName = name;
        return this;
    }

    public PetEntry setEntitySubspeciesID(int id) {
        this.subspeciesID = id;
        return this;
    }

    public PetEntry setEntitySize(double size) {
        this.entitySize = size;
        return this;
    }

    public PetEntry setColor(String color) {
        this.color = color;
        return this;
    }

    public PetEntry setOwner(EntityLivingBase owner) {
        this.host = owner;
        if(host != null && host instanceof EntityPlayer) {
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer) host);
            this.summonSet.playerExt = playerExt;
        }
        return this;
    }

    /** Used to set whether this PetEntry spawns mobs, this will also take a Spirit cost if Spirit is used (server side). use a direct spawningActive = true/false to avoid the Spirit cost. **/
    public PetEntry setSpawningActive(boolean spawningActive) {
        if(this.spawningActive == spawningActive)
            return this;
        if(!this.host.worldObj.isRemote) {
            if(!spawningActive)
                this.despawnEntity();
            else if(this.usesSpirit() && this.summonSet.playerExt != null) {
                if(this.summonSet.playerExt.spirit < this.getSpiritCost()) {
                    this.spawningActive = false;
                    return this;
                }
                this.summonSet.playerExt.spirit -= this.getSpiritCost();
                this.summonSet.playerExt.spiritReserved += this.getSpiritCost();
            }
        }
        this.spawningActive = spawningActive;
        return this;
    }


    // ==================================================
    //                     Get Values
    // ==================================================
    public MobInfo getMobInfo() {
        if(this.summonSet == null || "".equals(this.summonSet.summonType))
            return null;
        return MobInfo.getFromName(this.summonSet.summonType);
    }

    public float getHealth() {
        if(this.entity != null && this.entity instanceof EntityLivingBase)
            this.entityHealth = ((EntityLivingBase)this.entity).getHealth();
        return this.entityHealth;
    }

    public float getMaxHealth() {
        if(this.entity != null && this.entity instanceof EntityLivingBase)
            this.entityMaxHealth = ((EntityLivingBase)this.entity).getMaxHealth();
        return this.entityMaxHealth;
    }


    // ==================================================
    //                     Copy Entry
    // ==================================================
    /** Makes this entry copy all information from another entry, useful for updating entries. Does not copy over the owner, ID or entry name and will only copy the SummonSet's summon type. **/
    public void copy(PetEntry copyEntry) {
        this.setEntityName(copyEntry.entityName);
        this.setEntitySubspeciesID(copyEntry.subspeciesID);
        this.setEntitySize(copyEntry.entitySize);
        this.setColor(copyEntry.color);
        if(copyEntry.summonSet != null)
            this.summonSet.setSummonType(copyEntry.summonSet.summonType);
    }


    // ==================================================
    //                       Name
    // ==================================================
    public String getDisplayName() {
        String displayName = this.summonSet.getMobInfo().getTitle();
        if(!"".equals(this.entityName))
            displayName = this.entityName + " (" + displayName + ")";
        return displayName;
    }


    // ==================================================
    //                       On Add
    // ==================================================
    /** Called when this entry is first added. A Pet Manager is passed if added to one, otherwise null. **/
    public void onAdd(PetManager petManager, int petEntryID) {
        this.petManager = petManager;
        this.petEntryID = petEntryID;
    }


    // ==================================================
    //                       Remove
    // ==================================================
    /** Called when this entry is finished and should be removed. Note: The PetManager will auto remove any inactive entries it might have. **/
    public void remove() {
        this.setSpawningActive(false);
        this.active = false;
    }
	
	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the PetManager, runs any logic for this entry. This is normally called from an entity update. **/
	public void onUpdate(World world) {
        // Client Side Update:
        if(world.isRemote) {
            if(this.isRespawning && this.respawnTime > 0)
                this.respawnTime--;
            return;
        }

		if(!this.active)
            return;
        if(!this.isActive()) {
            this.remove();
            return;
        }

        // Active Spawning:
        if(this.spawningActive) {
            // Dead Check:
            if(this.entity != null && !this.entity.isEntityAlive()) {
                this.saveEntityNBT();
                this.entity = null;
                this.isRespawning = true;
                this.respawnTime = this.respawnTimeMax;
                if(this.summonSet.playerExt != null)
                    this.summonSet.playerExt.sendPetEntryToPlayer(this);
            }

            // No Entity:
            if(this.entity == null) {
                // Respawn:
                if(!this.isRespawning)
                    this.respawnTime = 0;
                if(this.respawnTime-- <= 0) {
                    this.spawnEntity();
                    this.isRespawning = false;
                }
            }

            // Entity Update:
            if(this.entity != null) {
                this.entityTick++;

                // Teleport Entity:
                if(this.teleportEntity) {
                    if(this.entity.worldObj != this.host.worldObj)
                        this.entity.changeDimension(this.host.worldObj.provider.getDimension());
                    this.entity.setPosition(this.host.posX, this.host.posY, this.host.posZ);
                }

                if(entity instanceof EntityLivingBase) {
                    EntityLivingBase entityLiving = (EntityLivingBase)this.entity;
                    if(this.entityTick % 20 == 0 && entityLiving.getHealth() < entityLiving.getMaxHealth())
                        entityLiving.setHealth(Math.min(entityLiving.getHealth() + 1, entityLiving.getMaxHealth()));
                    this.entityHealth = entityLiving.getHealth();
                    this.entityMaxHealth = entityLiving.getMaxHealth();
                }
            }
        }

        // Inactive Spawning:
        else {
            // Remove Entity If Spawned:
            if(this.entity != null) {
                this.saveEntityNBT();
                this.entity.setDead();
                this.entity = null;
            }

            // Count Down Respawn Timer If Active:
            if(this.respawnTime > 0)
                this.respawnTime--;
        }

        this.teleportEntity = false;
	}


    // ==================================================
    //                 On Behaviour Update
    // ==================================================
    /** Called when this entry's entity behaviour has been changed by the client. **/
    public void onBehaviourUpdate() {
        if(this.entity != null && this.entity instanceof EntityCreatureTameable)
            this.summonSet.applyBehaviour((EntityCreatureTameable)this.entity);
    }


    // ==================================================
    //                    Active Check
    // ==================================================
    /** Called every update, if this returns false this entry will call onRemove(). **/
    public boolean isActive() {
        if(this.entity == null && this.temporary && this.spawnCount > 0)
            return false;
        return true;
    }


    // ==================================================
    //                    Spawn Entity
    // ==================================================
    /** Spawns and sets this entry's entity if it isn't active already. **/
    public void spawnEntity() {
        if(this.entity != null || this.host == null)
            return;
        try {
            this.entity = (Entity)this.summonSet.getCreatureClass().getConstructor(new Class[] {World.class}).newInstance(new Object[] {this.host.worldObj});
        } catch (Exception e) {
            //LycanitesMobs.printWarning("", "[Pet Entry] A none Entity class was set in a PetEntry, only classes of Entity are valid!");
            //e.printStackTrace();
        }
        Entity spawnedEntity = this.entity;
        if(spawnedEntity == null)
            return;

        // Load NBT Data:
        this.loadEntityNBT();

        // Spawn Location:
        spawnedEntity.setLocationAndAngles(this.host.posX, this.host.posY, this.host.posZ, this.host.rotationYaw, 0.0F);

        if(spawnedEntity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)spawnedEntity;
            entityCreature.setMinion(true);
            entityCreature.setPetEntry(this);

            if(entityCreature instanceof EntityCreatureTameable) {
                EntityCreatureTameable entityTameable = (EntityCreatureTameable)entityCreature;
                this.summonSet.applyBehaviour(entityTameable);
            }

            // Better Spawn Location and Angle:
            float randomAngle = 45F + (45F * this.host.getRNG().nextFloat());
            if(this.host.getRNG().nextBoolean())
                randomAngle = -randomAngle;
            BlockPos spawnPos = entityCreature.getFacingPosition(this.host, -1, randomAngle);
            if(!spawnedEntity.worldObj.isSideSolid(spawnPos, EnumFacing.UP))
                spawnedEntity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), this.host.rotationYaw, 0.0F);
            else {
                spawnPos = entityCreature.getFacingPosition(this.host, -1, -randomAngle);
                if(spawnedEntity.worldObj.isSideSolid(spawnPos, EnumFacing.UP))
                    spawnedEntity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), this.host.rotationYaw, 0.0F);
            }

            // Temporary:
            if(this.temporary)
                entityCreature.setTemporary(this.temporaryDuration);

            // Entity Name and Appearance:
            if(this.entityName != null && !"".equals(this.entityName))
                entityCreature.setCustomNameTag(this.entityName);
            entityCreature.setSizeScale(this.entitySize);
            entityCreature.setSubspecies(this.subspeciesID, false);

            // Tamed Behaviour:
            if(entityCreature instanceof EntityCreatureTameable && this.host instanceof EntityPlayer) {
                EntityCreatureTameable entityTameable = (EntityCreatureTameable)entityCreature;
                entityTameable.setPlayerOwner((EntityPlayer)this.host);
                this.summonSet.applyBehaviour(entityTameable);
            }
        }

        this.spawnCount++;

        // Respawn with half health:
        if(spawnedEntity instanceof EntityLivingBase && this.isRespawning) {
            EntityLivingBase entityLiving = (EntityLivingBase)spawnedEntity;
            entityLiving.setHealth(entityLiving.getMaxHealth() / 2);
        }

        this.onSpawnEntity(spawnedEntity);
        this.host.worldObj.spawnEntityInWorld(spawnedEntity);
        this.entity = spawnedEntity;
    }

    /** Called when the entity for this entry is spawned just before it is added to the world. **/
    public void onSpawnEntity(Entity entity) {
        // This can be used on extensions of this class for NBT data, etc.
    }


    // ==================================================
    //                    Despawn Entity
    // ==================================================
    /** Despawns this entry's entity if it isn't already. This entry will still be active even if the entity is despawned so that it may be spawned again in the future. **/
    public void despawnEntity() {
        if(this.entity == null)
            return;
        this.onDespawnEntity(this.entity);
        this.saveEntityNBT();
        this.entity.setDead();
        this.entity = null;
    }

    /** Called when the entity for this entry is despawned. **/
    public void onDespawnEntity(Entity entity) {
        // This can be used on extensions of this class for NBT data, etc.
    }


    // ==================================================
    //                    Assign Entity
    // ==================================================
    /** Connects this PetEntry to the provided entity. If there is already an entity attached then the attached entity will be despawned. **/
    public void assignEntity(Entity entity) {
        if(this.entity != null)
            this.despawnEntity();
        this.setSpawningActive(true);
        this.entity = entity;

        if(this.entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase) this.entity;
            entityCreature.setMinion(true);
            entityCreature.setPetEntry(this);

            if(entityCreature instanceof EntityCreatureTameable) {
                EntityCreatureTameable entityTameable = (EntityCreatureTameable)entityCreature;
                this.summonSet.updateBehaviour(entityTameable);
            }

            if(this.temporary)
                entityCreature.setTemporary(this.temporaryDuration);

            if(this.entityName != null && !"".equals(this.entityName))
                entityCreature.setCustomNameTag(this.entityName);
            entityCreature.setSizeScale(this.entitySize);
            entityCreature.setSubspecies(this.subspeciesID, false);
        }

        this.spawnCount++;
        this.saveEntityNBT();
        this.onSpawnEntity(this.entity);
    }


    // ==================================================
    //                    Get Type
    // ==================================================
    /** Returns the type of this entry. This should always be accurate else PetManagers could have inactive entries stuck in their lists! **/
    public String getType() {
        return this.type;
    }


    // ==================================================
    //                    Spirit Cost
    // ==================================================
    /** Returns true if this PetEntry uses spirit to summon. **/
    public boolean usesSpirit() {
        return "pet".equals(this.getType()) || "mount".equals(this.getType());
    }

    /** Returns the spirit cost of this entity. **/
    public int getSpiritCost() {
        if(this.summonSet.playerExt == null)
            return 0;
        return this.summonSet.playerExt.spiritCharge * this.getMobInfo().summonCost;
    }


    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads pet entry from NBTTag. Should be called by PetManagers or other classes that store PetEntries and NBT Data for them. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        if(nbtTagCompound.hasKey("Active"))
            this.active = nbtTagCompound.getBoolean("Active");
        if(nbtTagCompound.hasKey("RespawnTime"))
            this.respawnTime = nbtTagCompound.getInteger("RespawnTime");
        if(nbtTagCompound.hasKey("Respawning"))
            this.isRespawning = nbtTagCompound.getBoolean("Respawning");
        if(nbtTagCompound.hasKey("SpawningActive"))
            this.spawningActive = nbtTagCompound.getBoolean("SpawningActive");

        this.summonSet.readFromNBT(nbtTagCompound);

        // Load Entity:
        if(nbtTagCompound.hasKey("EntityName"))
            this.setEntityName(nbtTagCompound.getString("EntityName"));
        if(nbtTagCompound.hasKey("SubspeciesID"))
            this.setEntitySubspeciesID(nbtTagCompound.getInteger("SubspeciesID"));
        if(nbtTagCompound.hasKey("EntitySize"))
            this.setEntitySize(nbtTagCompound.getDouble("EntitySize"));
        if(nbtTagCompound.hasKey("Color"))
            this.setColor(nbtTagCompound.getString("Color"));
        if(nbtTagCompound.hasKey("EntityNBT"))
            this.entityNBT = nbtTagCompound.getCompoundTag("EntityNBT");
    }

    // ========== Write ==========
    /** Writes pet entry to NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        if(this.name == null || "".equals(this.name))
            return;
        nbtTagCompound.setBoolean("Active", this.active);
        nbtTagCompound.setInteger("RespawnTime", this.respawnTime);
        nbtTagCompound.setBoolean("Respawning", this.isRespawning);
        nbtTagCompound.setBoolean("SpawningActive", this.spawningActive);

        nbtTagCompound.setInteger("ID", this.petEntryID);
        nbtTagCompound.setString("EntryName", this.name);
        nbtTagCompound.setString("Type", this.getType());
        this.summonSet.writeToNBT(nbtTagCompound);

        // Save Entity:
        if (this.usesSpirit()) {
            this.saveEntityNBT();
            nbtTagCompound.setString("EntityName", this.entityName);
            nbtTagCompound.setInteger("SubspeciesID", this.subspeciesID);
            nbtTagCompound.setDouble("EntitySize", this.entitySize);
            nbtTagCompound.setString("Color", this.color);
            nbtTagCompound.setTag("EntityNBT", this.entityNBT);
        }
    }

    // ========== Save Entity NBT ==========
    /** If this PetEntry currently has an active entity, this will save that entity's NBT data to this PetEntry's record of it. **/
    public void saveEntityNBT() {
        if(this.entityNBT == null)
                this.entityNBT = new NBTTagCompound();
        if(this.entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreatureBase = (EntityCreatureBase)this.entity;

            entityCreatureBase.inventory.writeToNBT(this.entityNBT);

            NBTTagCompound extTagCompound = new NBTTagCompound();
            entityCreatureBase.extraMobBehaviour.writeToNBT(extTagCompound);
            this.entityNBT.setTag("ExtraBehaviour", extTagCompound);

            if(this.entity instanceof EntityCreatureAgeable) {
                EntityCreatureAgeable entityCreatureAgeable = (EntityCreatureAgeable)this.entity;
                this.entityNBT.setInteger("Age", entityCreatureAgeable.getGrowingAge());
            }
        }
//        if(this.entity != null) {
//            if(this.entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).hasCustomNameTag())
//                this.entityName = ((EntityCreatureBase)entity).getCustomNameTag();
//            this.entity.writeToNBT(this.entityNBT);
//        }
    }

    // ========== Load Entity NBT ==========
    /** If this PetEntry is spawning a new entity, this will load any saved entity NBT data onto it. **/
    public void loadEntityNBT() {
        if(this.entity == null || this.entityNBT == null)
            return;
        if(this.entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreatureBase = (EntityCreatureBase)this.entity;

            entityCreatureBase.inventory.readFromNBT(this.entityNBT);

            if(this.entityNBT.hasKey("ExtraBehaviour"))
                entityCreatureBase.extraMobBehaviour.readFromNBT(this.entityNBT.getCompoundTag("ExtraBehaviour"));

            if(this.entity instanceof EntityCreatureAgeable) {
                EntityCreatureAgeable entityCreatureAgeable = (EntityCreatureAgeable)this.entity;
                if(this.entityNBT.hasKey("Age"))
                    entityCreatureAgeable.setGrowingAge(this.entityNBT.getInteger("Age"));
                else
                    entityCreatureAgeable.setGrowingAge(0);
            }
        }
//        this.entity.readFromNBT(this.entityNBT);
    }
}
