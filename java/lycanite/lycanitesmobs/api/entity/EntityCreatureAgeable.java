package lycanite.lycanitesmobs.api.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
        this.dataWatcher.addObject(WATCHER_ID.AGE.id, (int)0);
        this.dataWatcher.addObject(WATCHER_ID.LOVE.id, (int)0);
    }
    
    // ========== Name ==========
    @Override
    public String getAgeName() {
    	if(this.isChild())
    		return "";
    	else
    		return "";
    }
	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	return this.hasBeenFarmed;
    }
    
    public void setFarmed() {
    	this.hasBeenFarmed = true;
    }
	
	
	// ==================================================
  	//                       Update
  	// ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Growing:
        if(this.worldObj.isRemote)
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
        if(this.getGrowingAge() != 0)
        	this.setFarmed();
        
        // Breeding:
        if(!this.canBreed())
            this.loveTime = 0;

        if(!this.worldObj.isRemote)
        	this.dataWatcher.updateObject(WATCHER_ID.LOVE.id, (int)this.loveTime);
        if(this.worldObj.isRemote)
        	this.loveTime = this.dataWatcher.getWatchableObjectInt(WATCHER_ID.LOVE.id);
        
        if(this.isInLove()) {
        	this.setFarmed();
            --this.loveTime;
            if(this.worldObj.isRemote) {
	            String particle = "heart";
	            if(this.loveTime % 10 == 0) {
	                double d0 = this.rand.nextGaussian() * 0.02D;
	                double d1 = this.rand.nextGaussian() * 0.02D;
	                double d2 = this.rand.nextGaussian() * 0.02D;
	                this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
	            }
            }
        }
        else
            this.breedingTime = 0;
    }
    
    // ========== AI Update ==========
    protected void updateAITick() {
        if(!this.canBreed())
            this.loveTime = 0;
        super.updateAITick();
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
    		if(itemStack.itemID == ObjectManager.getItem(this.eggName).itemID)
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
    	if(command.equals("Spawn Baby") && !this.worldObj.isRemote && ObjectManager.entityLists.containsKey(this.mod.getDomain())) {
			 Class eggClass = ObjectManager.entityLists.get(this.mod.getDomain()).getClassFromID(itemStack.getItemDamage());
			 if(eggClass != null && eggClass.isAssignableFrom(this.getClass())) {
				 EntityCreatureAgeable baby = this.createChild(this);
				 if(baby != null) {
					baby.setGrowingAge(baby.growthTime);
					baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
					this.worldObj.spawnEntityInWorld(baby);
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
        return this.dataWatcher.getWatchableObjectInt(WATCHER_ID.AGE.id);
    }
	
	public void setGrowingAge(int age) {
        this.dataWatcher.updateObject(WATCHER_ID.AGE.id, (int)age);
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
        this.setScale(age ? 0.5F : 1.0F);
    }
	
	protected final void setScale(float age) {
        super.setSize(this.scaledWidth * age, this.scaledHeight * age);
    }
	
	protected final void setSize(float width, float height) {
        boolean validWidth = this.scaledWidth > 0.0F;
        this.scaledWidth = width;
        this.scaledHeight = height;
        if(!validWidth)
            this.setScale(1.0F);
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
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
				&& itemStack.itemID == -1
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
            baby.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

            for(int i = 0; i < 7; ++i) {
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle("heart", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
            }

            this.worldObj.spawnEntityInWorld(baby);
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
        	nbtTagCompound.setBoolean("HasBeenFarmed", this.hasBeenFarmed);
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
