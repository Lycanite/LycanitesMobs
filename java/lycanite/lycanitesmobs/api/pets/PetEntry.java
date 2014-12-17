package lycanite.lycanitesmobs.api.pets;


import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PetEntry {
    /** The Pet Manager that this entry is added to, this can also be null. **/
    public PetManager petManager;
    /** The type of entry this is. This should really always be set if this entry is to be added to a manager. This should only be set when the entry is instantiated and then kept the same. **/
    private String type;
    /** This is set to false if this entry has been removed. Used by PetManagers to auto-remove finished entries. **/
    public boolean active = true;

    /** A timer used to count down to 0 for respawning. **/
    public int respawnTime;
    /** The amount of time until respawn. **/
    public int respawnTimeMax;
    /** Counts how many times this entry has summoned its entity. **/
    public int spawnCount = 0;
    /** If true, this entry and it's entity will be marked as temporary where once the entity is gone, it will not respawn. The minion type sets this to true. **/
    public boolean temporary = false;
    /** For temporary entities, this will set how the long the entity will last before it despawns. **/
    public int temporaryDuration = 5 * 20;

    /** The entity that this entry belongs to. **/
    public EntityLivingBase host;
    /** The class of the entity that this entry should spawn. **/
    public Class entityClass;
    /** The current entity instance that this entry is using. **/
    public Entity entity;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntry(String type, EntityLivingBase host, Class entityClass) {
        this.type = type;
        this.host = host;
        this.entityClass = entityClass;

        this.respawnTimeMax = 5 * 20;
        if("minion".equalsIgnoreCase(this.type))
            this.temporary = true;
	}


    // ==================================================
    //                       On Add
    // ==================================================
    /** Called when this entry is first added. A Pet Manager is passed if added to one, otherwise null. **/
    public void onAdd(PetManager petManager) {
        this.petManager = petManager;
    }


    // ==================================================
    //                       On Remove
    // ==================================================
    /** Called when this entry is finished and should be removed. Note: The PetManager will auto remove any inactive entries it might have. **/
    public void onRemove() {
        this.active = false;
    }
	
	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the PetManager, runs any logic for this entry. This is normally called from an entity update. **/
	public void onUpdate() {
		if(!this.active)
            return;
        if(!this.isActive()) {
            this.onRemove();
            return;
        }

        // Dead Check:
        if(!this.entity.isEntityAlive())
            this.entity = null;

        // No Entity:
        if(this.entity == null) {
            // Respawn:
            if(this.respawnTime-- <= 0)
                this.spawnEntity();
        }

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
            this.entity = (Entity)this.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {this.host.worldObj});
        } catch (Exception e) {
            LycanitesMobs.printWarning("", "[Pet Entry] A none Entity class was set in a PetEntry, only classes of Entity are valid!");
            e.printStackTrace();
        }
        if(this.entity == null)
            return;

        this.entity.setLocationAndAngles(this.host.posX, this.host.posY, this.host.posZ, this.host.rotationYaw, 0.0F);
        if(this.entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)this.entity;
            entityCreature.setMinion(true);

            float randomAngle = 45F + (45F * this.host.getRNG().nextFloat());
            if(this.host.getRNG().nextBoolean())
                randomAngle = -randomAngle;
            double[] spawnPos = entityCreature.getFacingPosition(this.host, -1, randomAngle);
            if(!entity.worldObj.isSideSolid((int)spawnPos[0], (int)spawnPos[1], (int)spawnPos[2], ForgeDirection.UP))
                entity.setLocationAndAngles((int)spawnPos[0], (int)spawnPos[1], (int)spawnPos[2], this.host.rotationYaw, 0.0F);
            else {
                spawnPos = entityCreature.getFacingPosition(this.host, -1, -randomAngle);
                if(entity.worldObj.isSideSolid((int) spawnPos[0], (int) spawnPos[1], (int) spawnPos[2], ForgeDirection.UP))
                    entity.setLocationAndAngles((int) spawnPos[0], (int) spawnPos[1], (int) spawnPos[2], this.host.rotationYaw, 0.0F);
            }

            if(this.temporary)
                entityCreature.setTemporary(this.temporaryDuration);
            if(entityCreature instanceof EntityCreatureTameable && this.host instanceof EntityPlayer) {
                ((EntityCreatureTameable)entityCreature).setPlayerOwner((EntityPlayer) this.host);
            }
        }
        this.respawnTime = this.respawnTimeMax;
        this.spawnCount++;
        this.onSpawnEntity(this.entity);
        this.host.worldObj.spawnEntityInWorld(entity);
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
        this.entity.setDead();
        this.entity = null;
    }

    /** Called when the entity for this entry is despawned. **/
    public void onDespawnEntity(Entity entity) {
        // This can be used on extensions of this class for NBT data, etc.
    }


    // ==================================================
    //                    Get Type
    // ==================================================
    /** Returns the type of this entry. This should always be accurate else PetManagers could have inactive entries stuck in their lists! **/
    public String getType() {
        return this.type;
    }
}
