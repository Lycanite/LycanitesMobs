package lycanite.lycanitesmobs.api.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISit;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCreatureTameable extends EntityCreatureAgeable implements EntityOwnable {
	
	// Stats:
	public float hunger = this.getCreatureHungerMax();
	public float stamina = this.getStaminaMax();
	public float staminaRecovery = 0.5F;
	public boolean hasCollarColor = false;
	public boolean isMobWhenNotTamed = true;
	
	// AI:
	public EntityAISit aiSit = new EntityAISit(this);
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public EntityCreatureTameable(World world) {
		super(world);
		this.setTamed(false);
	}
	
	// ========== Init ==========
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(WATCHER_ID.TAMED.id, (byte)0);
        this.dataWatcher.addObject(WATCHER_ID.OWNER.id, "");
        this.dataWatcher.addObject(WATCHER_ID.HEALTH.id, new Float(this.getHealth()));
        this.dataWatcher.addObject(WATCHER_ID.HUNGER.id, new Float(this.getCreatureHungerMax()));
        this.dataWatcher.addObject(WATCHER_ID.STAMINA.id, new Float(this.getStaminaMax()));
    }
    
    // ========== Name ==========
    @Override
    public String getEntityName() {
    	if(!this.isTamed() || !LycanitesMobs.config.getFeatureBool("OwnerTags"))
    		return super.getEntityName();
    	
    	String ownerName = this.getOwnerName();
    	String ownerSuffix = "'s ";
    	if(ownerName.substring(ownerName.length() - 1) == "s" || (ownerName.substring(ownerName.length() - 1) == "S"))
    			ownerSuffix = "' ";
    	String ownedName = ownerName + ownerSuffix + this.getFullName();
    	
    	if(this.hasCustomNameTag())
    		return super.getEntityName() + " (" + ownedName + ")";
    	else
    		return ownedName;
    }
    
    
    // ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Despawning ==========
    @Override
    protected boolean canDespawn() {
    	if(this.isTamed()) return false;
        return super.canDespawn();
    }
    
    @Override
    public boolean despawnCheck() {
        if(this.worldObj.isRemote)
        	return false;
    	if(this.isTamed())
    		return false;
        return super.despawnCheck();
    }
    
    @Override
    public boolean isPersistant() {
    	return this.isTamed() || super.isPersistant();
    }
    
    
    // ==================================================
  	//                      Movement
  	// ==================================================
    // ========== Can leash ==========
    @Override
    public boolean canLeash(EntityPlayer player) {
	    if(this.isTamed() && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()))
	        return true;
	    return super.canLeash(player);
    }
    
    // ========== Test Leash ==========
    @Override
    public void testLeash(float distance) {
    	if(this.isSitting() && distance > 10.0F)
    		this.clearLeashed(true, true);
    	else
    		super.testLeash(distance);
    }
    
    
    // ==================================================
    //                       Update
    // ==================================================
    @Override
    public void onLivingUpdate() {
    	super.onLivingUpdate();
    	this.staminaUpdate();
    }
    
    public void staminaUpdate() {
    	if(this.worldObj.isRemote)
    		return;
    	if(this.stamina < this.getStaminaMax() && this.staminaRecovery >= this.getStaminaRecoveryMax() / 2)
    		this.setStamina(Math.min(this.stamina + this.staminaRecovery, this.getStaminaMax()));
    	if(this.staminaRecovery < this.getStaminaRecoveryMax())
    		this.staminaRecovery = Math.min(this.staminaRecovery + (this.getStaminaRecoveryMax() / this.getStaminaRecoveryWarmup()), this.getStaminaRecoveryMax());
    }
    
    @Override
    protected void updateAITick() {
        this.dataWatcher.updateObject(WATCHER_ID.HEALTH.id, Float.valueOf(this.getHealth()));
    }
    
    
    // ==================================================
    //                       Interact
    // ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
		
		// Open GUI:
		if(this.isTamed() && player.isSneaking() && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote)
			commands.put(CMD_PRIOR.IMPORTANT.id, "GUI");
    	
    	// Server Item Commands:
    	if(itemStack != null && !this.worldObj.isRemote) {
    		
    		// Taming:
    		if(!this.isTamed() && isTamingItem(itemStack) && LycanitesMobs.config.getFeatureBool("MobTaming"))
    			commands.put(CMD_PRIOR.IMPORTANT.id, "Tame");
    		
    		// Feeding:
    		if(this.isTamed() && this.isHealingItem(itemStack) && this.dataWatcher.getWatchableObjectFloat(WATCHER_ID.HEALTH.id) < this.getMaxHealth())
                commands.put(CMD_PRIOR.ITEM_USE.id, "Feed");
    		
    		// Coloring:
    		if(this.isTamed() && this.hasCollarColor && itemStack.itemID == Item.dyePowder.itemID && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()))
    			commands.put(CMD_PRIOR.ITEM_USE.id, "Color");
    		
    		// Equipment:
    		if(this.isTamed() && !this.isChild() && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName())) {
	    		String equipSlot = this.inventory.getSlotForEquipment(itemStack);
	    		if(equipSlot != null && (this.inventory.getEquipmentStack(equipSlot) == null || this.inventory.getEquipmentStack(equipSlot).itemID != itemStack.itemID))
	    			commands.put(CMD_PRIOR.EQUIPPING.id, "Equip Item");
    		}
    	}
		
		// Sit:
		if(this.isTamed() && this.canSit() && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote)
			commands.put(CMD_PRIOR.MAIN.id, "Sit");
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, EntityPlayer player, ItemStack itemStack) {
    	
    	// Open GUI:
    	if(command.equals("GUI")) {
    		this.openGUI(player);
    	}
    	
    	// Tame:
    	if(command.equals("Tame")) {
    		this.tame(player);
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Feed:
    	if(command.equals("Feed")) {
    		int healAmount = 4;
    		if(itemStack.getItem() instanceof ItemFood) {
    			ItemFood itemFood = (ItemFood)Item.itemsList[itemStack.itemID];
    			healAmount = itemFood.getHealAmount();
    		}
    		this.heal((float)healAmount);
            this.playEatSound();
            if(this.worldObj.isRemote) {
                String particle = "heart";
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                for(int i = 0; i < 25; i++)
                	this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
            }
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Color:
    	if(command.equals("Color")) {
    		int colorID = BlockColored.getBlockFromDye(itemStack.getItemDamage());
            if(colorID != this.getCollarColor()) {
                this.setCollarColor(colorID);
        		this.consumePlayersItem(player, itemStack);
            }
    	}
    	
    	// Equip Armor:
    	if(command.equals("Equip Item")) {
    		ItemStack equippedItem = this.inventory.getEquipmentStack(this.inventory.getSlotForEquipment(itemStack));
    		if(equippedItem != null)
    			this.dropItem(equippedItem);
    		ItemStack equipStack = itemStack.copy();
    		equipStack.stackSize = 1;
    		this.inventory.setEquipmentStack(equipStack.copy());
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Sit:
    	if(command.equals("Sit")) {
    		this.playTameSound();
            this.setAttackTarget((EntityLivingBase)null);
            this.clearMovement();
        	this.aiSit.setSitting(!this.isSitting());
            this.isJumping = false;
    	}
    	
    	super.performCommand(command, player, itemStack);
    }
    
    // ========== Can Name Tag ==========
    @Override
    public boolean canNameTag(EntityPlayer player) {
    	if(!this.isTamed())
    		return super.canNameTag(player);
    	else if(this.isTamed() && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()))
    		return super.canNameTag(player);
    	return false;
    }
    
    
    // ==================================================
    //                       Targets
    // ==================================================
    // ========== Teams ==========
    @Override
    public Team getTeam() {
        if(this.isTamed()) {
            EntityLivingBase owner = this.getOwner();
            if(owner != null)
                return owner.getTeam();
        }
        return super.getTeam();
    }
    
    @Override
    public boolean isOnSameTeam(EntityLivingBase target) {
        if(this.isTamed()) {
            EntityLivingBase owner = this.getOwner();
            if(target == owner)
                return true;
            if(owner != null)
                return owner.isOnSameTeam(target);
        }
        return super.isOnSameTeam(target);
    }
    
    // XXX Unknown
    public boolean func_142018_a(EntityLivingBase targetA, EntityLivingBase targetB) {
        return true;
    }
    
    // ==================================================
    //                       Attacks
    // ==================================================
    // ========== Can Attack ==========
	@Override
	public boolean canAttackClass(Class targetClass) {
		return super.canAttackClass(targetClass);
	}
	
    // ========== Attacked From ==========
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damage) {
        if (this.isEntityInvulnerable())
            return false;
        else {
            Entity entity = damageSrc.getEntity();
            this.aiSit.setSitting(false);

            if(entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            	damage = (damage + 1.0F) / 2.0F;

            return super.attackEntityFrom(damageSrc, damage);
        }
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    public boolean isTamed() {
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id) & 4) != 0;
    }
    
    public void setTamed(boolean setTamed) {
        byte tamed = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id);
        if(setTamed)
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamed | 4)));
        else
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamed & -5)));
        this.setAlwaysRenderNameTag(setTamed);
    }
    
    public boolean isTamingItem(ItemStack itemstack) {
        return false;
    }
    
    // ========== Tame Entity ==========
    public boolean tame(EntityPlayer player) {
    	if(!this.worldObj.isRemote)
            if(this.rand.nextInt(3) == 0) {
                this.setTamed(true);
                this.clearMovement();
                this.setAttackTarget((EntityLivingBase)null);
                this.aiSit.setSitting(true);
                this.setOwner(player.getCommandSenderName());
                this.playTameSound();
            }
    	else
    		this.playTameEffect(this.isTamed());
    	return this.isTamed();
    }
    
    
    // ==================================================
    //                       Owner
    // ==================================================
    @Override
    public String getOwnerName() {
        return this.dataWatcher.getWatchableObjectString(WATCHER_ID.OWNER.id);
    }
    
    public void setOwner(String playername) {
        this.dataWatcher.updateObject(WATCHER_ID.OWNER.id, playername);
    }
    
    @Override
    public EntityLivingBase getOwner() {
        return this.worldObj.getPlayerEntityByName(this.getOwnerName());
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(byte status) {
        if(status == 7)
            this.playTameEffect(true);
        else if(status == 6)
            this.playTameEffect(false);
        else
            super.handleHealthUpdate(status);
    }
    
    // ========== Feeding Food ==========
    public boolean isHealingItem(ItemStack testStack) {
    	return false;
    }
    
    
    // ==================================================
    //                       Sitting
    // ==================================================
    public EntityAISit getSitAI() {
        return this.aiSit;
    }
    
    public boolean isSitting() {
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id) & 1) != 0;
    }

    public void setSitting(boolean setSitting) {
        byte sitting = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id);
        if(setSitting)
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(sitting | 1)));
        else
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(sitting & -2)));
    }
    
    
    // ==================================================
    //                       Hunger
    // ==================================================
    public float getCreatureHunger() {
    	if(this.worldObj == null)
    		return this.getCreatureHungerMax();
    	if(!this.worldObj.isRemote)
    		return this.hunger;
    	else
    		return this.dataWatcher.getWatchableObjectFloat(WATCHER_ID.HUNGER.id);
    }
    
    public void setCreatureHunger(float setHunger) {
    	this.hunger = setHunger;
    }
    
    public float getCreatureHungerMax() {
    	return 20;
    }
    
    
    // ==================================================
    //                       Stamina
    // ==================================================
    public float getStamina() {
    	if(this.worldObj != null && this.worldObj.isRemote)
    		this.stamina = this.dataWatcher.getWatchableObjectFloat(WATCHER_ID.STAMINA.id);
    	return this.stamina;
    }
    
    public void setStamina(float setStamina) {
    	this.stamina = setStamina;
    	if(this.worldObj != null && !this.worldObj.isRemote) {
    		this.dataWatcher.updateObject(WATCHER_ID.STAMINA.id, setStamina);
    	}
    }
    
    public float getStaminaMax() {
    	return 100;
    }
    
    public float getStaminaRecoveryMax() {
    	return 0.5F;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 10 * 20;
    }
    
    public float getStaminaCost() {
    	return 1;
    }
    
    public void applyStaminaCost() {
    	float newStamina = this.getStamina() - this.getStaminaCost();
    	if(newStamina < 0)
    		newStamina = 0;
    	this.setStamina(newStamina);
    	this.staminaRecovery = 0;
    }
    
    // ========== GUI Feedback ==========
    public float getStaminaPercent() {
    	return this.getStamina() / this.getStaminaMax();
    }
    
    // "energy" = Usual blue-orange bar. "toggle" = Solid purple bar for on and off.
    public String getStaminaType() {
    	return "energy";
    }
    
    
    // ==================================================
    //                    Collar Color
    // ==================================================
    public int getCollarColor() {
        return this.dataWatcher.getWatchableObjectByte(WATCHER_ID.COLOR.id) & 15;
    }
    
    public void setCollarColor(int color) {
        this.dataWatcher.updateObject(WATCHER_ID.COLOR.id, Byte.valueOf((byte)(color & 15)));
    }
    
    
    // ==================================================
    //                      Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
 	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
    	EntityCreatureAgeable spawnedBaby = super.createChild(baby);
    	String ownerName = this.getOwnerName();
    	if(ownerName != null && ownerName.trim().length() > 0 && spawnedBaby != null && spawnedBaby instanceof EntityCreatureTameable) {
    		EntityCreatureTameable tamedBaby = (EntityCreatureTameable)spawnedBaby;
    		tamedBaby.setOwner(ownerName);
    		tamedBaby.setTamed(true);
    	}
    	return spawnedBaby;
 	}
    
    
    // ==================================================
    //                     Abilities
    // ==================================================
    /** Returns whether or not this mob is hostile towards players, changes if a mob is tamed, etc too. **/
    @Override
    public boolean isHostile() {
    	if(this.isMobWhenNotTamed)
			return !this.isTamed();
    	return super.isHostile();
    }
    
    // =========== Movement ==========
    public boolean canBeTempted() { return !this.isTamed(); }
    public boolean canSit() { return true; }
    
    
    // ==================================================
    //                       Client
    // ==================================================
    protected void playTameEffect(boolean success) {
        String particle = "heart";
        if(!success)
        	particle = "smoke";

        for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    }
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
        super.readEntityFromNBT(nbtTagCompound);
        if(nbtTagCompound.hasKey("Owner")) {
	        String owner = nbtTagCompound.getString("Owner");
	        if(owner.length() > 0) {
	            this.setOwner(owner);
	            this.setTamed(true);
	        }
        }
        else {
        	this.setOwner("");
            this.setTamed(false);
        }
        
        if(nbtTagCompound.hasKey("Sitting")) {
	        this.aiSit.setSitting(nbtTagCompound.getBoolean("Sitting"));
	        this.setSitting(nbtTagCompound.getBoolean("Sitting"));
        }
        
        if(nbtTagCompound.hasKey("Hunger")) {
        	this.setCreatureHunger(nbtTagCompound.getFloat("Hunger"));
        }
        else {
        	this.setCreatureHunger(this.getCreatureHungerMax());
        }
        
        if(nbtTagCompound.hasKey("Stamina")) {
        	this.setStamina(nbtTagCompound.getFloat("Stamina"));
        }
    }
    
    // ========== Write ==========
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
        if(this.getOwnerName() == null) {
        	nbtTagCompound.setString("Owner", "");
        }
        else {
        	nbtTagCompound.setString("Owner", this.getOwnerName());
        }
        nbtTagCompound.setBoolean("Sitting", this.isSitting());
        nbtTagCompound.setFloat("Hunger", this.getCreatureHunger());
        nbtTagCompound.setFloat("Stamina", this.getStamina());
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    @Override
    protected String getLivingSound() {
    	String sound = "Say";
    	if(this.isTamed() && this.getHealth() < this.getMaxHealth())
    		sound = "Beg";
    	return AssetManager.getSound(entityName + sound);
    }
    
    // ========== Tame ==========
    public void playTameSound() {
    	this.playSound(AssetManager.getSound(entityName + "Tame"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
    
    // ========== Eat ==========
    public void playEatSound() {
    	this.playSound(AssetManager.getSound(entityName + "Eat"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}
