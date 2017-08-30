package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.HashMap;

public abstract class EntityCreatureAgeable extends EntityCreatureBase {
	
	// Size:
    private float scaledWidth = -1.0F;
    private float scaledHeight;
    
    // Targets:
    private EntityCreatureAgeable breedingTarget, breedingTargetPrev;
    public boolean hasBreedingTarget = false;
    
    // Growth:
    public int growthTime = -24000;
    public boolean canGrow = true;
    public double babySpawnChance = 0D;
    
    // Breeding:
    public int loveTime;
    private int loveTimeMax = 600;
    private int breedingTime;
    public int breedingCooldown = 6000;
    
    public boolean hasBeenFarmed = false;

    // Datawatcher:
    protected static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(EntityCreatureBase.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> LOVE = EntityDataManager.<Integer>createKey(EntityCreatureBase.class, DataSerializers.VARINT);
    
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public EntityCreatureAgeable(World world) {
		super(world);
	}
    
    // ========== Setup ==========
    public void setupMob() {
        if(this.babySpawnChance > 0D && this.rand.nextDouble() < this.babySpawnChance)
        	this.setGrowingAge(growthTime);
        super.setupMob();
    }
	
	// ========== Init ==========
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(AGE, (int)0);
        this.dataManager.register(LOVE, (int)0);
    }
    
    // ========== Name ==========
    @Override
    public String getAgeName() {
    	if(this.isChild())
    		return "Baby";
    	else
    		return "";
    }
	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	if(this.hasBeenFarmed)
    		return true;
    	return super.isPersistant();
    }
    
    public void setFarmed() {
    	this.hasBeenFarmed = true;
        if(this.timeUntilPortal > this.getPortalCooldown())
            this.timeUntilPortal = this.getPortalCooldown();
    }
    
    // ========== Get Random Subspecies ==========
    @Override
    public void getRandomSubspecies() {
    	if(this.isChild())
    		return;
    	super.getRandomSubspecies();
    }
	
	
	// ==================================================
  	//                       Update
  	// ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Growing:
        if(this.getEntityWorld().isRemote)
            this.setScaleForAge(this.isChild());
        else if(this.canGrow) {
            int age = this.getGrowingAge();
            if(age < 0) {
                ++age;
                this.setGrowingAge(age);
            }
            else if(age > 0) {
                --age;
                this.setGrowingAge(age);
            }
        }
        
        // Breeding:
        if(!this.canBreed())
            this.loveTime = 0;

        if(!this.getEntityWorld().isRemote)
        	this.dataManager.set(LOVE, this.loveTime);
        if(this.getEntityWorld().isRemote)
        	this.loveTime = this.getIntFromDataManager(LOVE);
        
        if(this.isInLove()) {
        	this.setFarmed();
            --this.loveTime;
            if(this.getEntityWorld().isRemote) {
	            EnumParticleTypes particle = EnumParticleTypes.HEART;
	            if(this.loveTime % 10 == 0) {
	                double d0 = this.rand.nextGaussian() * 0.02D;
	                double d1 = this.rand.nextGaussian() * 0.02D;
	                double d2 = this.rand.nextGaussian() * 0.02D;
	                this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
	            }
            }
        }
        else
            this.breedingTime = 0;
    }
    
    // ========== AI Update ==========
    @Override
    protected void updateAITasks() {
        if(!this.canBreed())
            this.loveTime = 0;
        super.updateAITasks();
    }
    
	
	// ==================================================
  	//                      Interact
  	// ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
    	
    	// Item Commands:
    	if(itemStack != null) {
    		
    		// Spawn Egg:
    		if(itemStack.getItem() == ObjectManager.getItem(this.group.getEggName()))
    			commands.put(CMD_PRIOR.ITEM_USE.id, "Spawn Baby");
    		
    		// Breeding Item:
    		if(this.isBreedingItem(itemStack) && this.canBreed() && !this.isInLove())
    			commands.put(CMD_PRIOR.ITEM_USE.id, "Breed");
    	}
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, EntityPlayer player, ItemStack itemStack) {
    	
    	// Spawn Baby:
    	if(command.equals("Spawn Baby") && !this.getEntityWorld().isRemote && ObjectManager.entityLists.containsKey(this.group.filename)) {
            ItemCustomSpawnEgg itemCustomSpawnEgg = (ItemCustomSpawnEgg)itemStack.getItem();
			 Class eggClass = ObjectManager.entityLists.get(this.group.filename).getClassFromID(itemCustomSpawnEgg.getEntityIdFromItem(itemStack));
			 if(eggClass != null && eggClass.isAssignableFrom(this.getClass())) {
				 EntityCreatureAgeable baby = this.createChild(this);
				 if(baby != null) {
					baby.setGrowingAge(baby.growthTime);
					baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
					baby.setFarmed();
					this.getEntityWorld().spawnEntity(baby);
					if(itemStack.hasDisplayName())
						baby.setCustomNameTag(itemStack.getDisplayName());
					this.consumePlayersItem(player, itemStack);
				 }
			 }
    	}
    	
    	// Breed:
    	if(command.equals("Breed")) {
    		if(this.breed())
    			this.consumePlayersItem(player, itemStack);
    	}
    	
    	super.performCommand(command, player, itemStack);
    }
	
	
	// ==================================================
  	//                        Age
  	// ==================================================
	public int getGrowingAge() {
        return this.getIntFromDataManager(AGE);
    }
	
	public void setGrowingAge(int age) {
		this.dataManager.set(AGE, age);
        this.setScaleForAge(this.isChild());
    }
	
	public void addGrowth(int growth) {
        int age = this.getGrowingAge();
        age += growth * 20;
        if (age > 0)
        	age = 0;
        this.setGrowingAge(age);
    }
	
	@Override
	public boolean isChild() {
        return this.getGrowingAge() < 0;
    }

	
	// ==================================================
  	//                        Size
  	// ==================================================
	public void setScaleForAge(boolean age) {
        this.setAgeScale(age ? 0.5F : 1.0F);
    }
	
	protected final void setAgeScale(float age) {
        super.setSize(this.scaledWidth * age, this.scaledHeight * age);
    }
	
	@Override
	protected void setSize(float width, float height) {
        boolean validWidth = this.scaledWidth > 0.0F;
        this.scaledWidth = width;
        this.scaledHeight = height;
        if(!validWidth)
            this.setAgeScale(1.0F);
        super.setSize(width, height);
    }

    /** When called, this reapplies the initial width and height this mob and then applies sizeScale. **/
    @Override
	public void updateSize() {
        this.setSize(this.setWidth, this.setHeight);
        this.setScaleForAge(this.isChild());
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    /** Can this entity by tempted (usually lured by an item) currently? **/
    public boolean canBeTempted() { return !this.isInLove(); }

	// ========== Targets ==========
	public EntityCreatureAgeable getBreedingTarget() { return this.breedingTarget; }
	public void setBreedingTarget(EntityCreatureAgeable target) { this.breedingTarget = target; }
	
    // ========== Create Child ==========
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partener) {
		return null;
	}
	
	// ========== Breeding Item ==========
	public boolean isBreedingItem(ItemStack itemStack) {
		return itemStack != null
				&& itemStack.getItem() == null
				&& itemStack.getItemDamage() == -1;
    }
	
	// ========== Valid Partner ==========
	public boolean canBreedWith(EntityCreatureAgeable partner) {
		if(partner == this) return false;
		if(partner.getClass() != this.getClass()) return false;
		return this.isInLove() && partner.isInLove();
	}
	
	// ========== Love Check ==========
	public boolean isInLove() {
		return this.loveTime > 0;
	}
	
	// ========== Breed ==========
	public boolean breed() {
		if(!this.canBreed())
			return false;
        this.loveTime = this.loveTimeMax;
        this.setAttackTarget(null);
        return true;
	}
	
	public boolean canBreed() {
        return this.getGrowingAge() == 0;
    }
	
	// ========== Procreate ==========
	public void procreate(EntityCreatureAgeable partner) {
		EntityCreatureAgeable baby = this.createChild(partner);

        if(baby != null) {
            this.finishBreeding();
            partner.finishBreeding();
            baby.setGrowingAge(baby.growthTime);
            Subspecies babySubspecies = this.mobInfo.getChildSubspecies(this, this.getSubspeciesIndex(), partner.getSubspecies());
            baby.setSubspecies(babySubspecies != null ? babySubspecies.index : 0, true);
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

            for(int i = 0; i < 7; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.getEntityWorld().spawnParticle(EnumParticleTypes.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
            }

            this.getEntityWorld().spawnEntity(baby);
        }
    }
	
	public void finishBreeding() {
        this.setGrowingAge(this.breedingCooldown);
        this.setBreedingTarget(null);
        this.loveTime = 0;
        this.breedingTime = 0;
	}
	
	
	// ==================================================
  	//                       NBT
  	// ==================================================
	// ========== Read ==========
    @Override
	public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
        super.readEntityFromNBT(nbtTagCompound);
        if(nbtTagCompound.hasKey("Age")) {
        	this.setGrowingAge(nbtTagCompound.getInteger("Age"));
        }
        else {
        	this.setGrowingAge(0);
        }
        
        if(nbtTagCompound.hasKey("InLove")) {
        	this.loveTime = nbtTagCompound.getInteger("InLove");
        }
        else {
        	this.loveTime = 0;
        }
        
        if(nbtTagCompound.hasKey("HasBeenFarmed")) {
        	if(nbtTagCompound.getBoolean("HasBeenFarmed")) {
        		this.setFarmed();
        	}
        }
    }
	
	// ========== Write ==========
    @Override
	public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("Age", this.getGrowingAge());
        nbtTagCompound.setInteger("InLove", this.loveTime);
        nbtTagCompound.setBoolean("HasBeenFarmed", this.hasBeenFarmed);
    }
}
