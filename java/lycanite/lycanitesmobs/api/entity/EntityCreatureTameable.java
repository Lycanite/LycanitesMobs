package lycanite.lycanitesmobs.api.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISit;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCreatureTameable extends EntityCreatureAgeable implements IEntityOwnable {
	
	// Stats:
	public float hunger = this.getCreatureHungerMax();
	public float stamina = this.getStaminaMax();
	public float staminaRecovery = 0.5F;
	public boolean isMobWhenNotTamed = true;
	public float sittingGuardRange = 16F;
	
	// AI:
	public EntityAISit aiSit = new EntityAISit(this);
	
    /** Used for the TAMED WATCHER_ID, this holds a series of booleans that describe the tamed status as well as instructed behaviour. **/
	public static enum TAMED_ID {
		IS_TAMED((byte)1), MOVE_SIT((byte)2), MOVE_FOLLOW((byte)4),
		STANCE_PASSIVE((byte)8), STANCE_AGGRESSIVE((byte)16), PVP((byte)32);
		public final byte id;
	    private TAMED_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
	
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
    public String getCommandSenderName() {
    	if(!this.isTamed() || !MobInfo.ownerTags)
    		return super.getCommandSenderName();
    	
    	String ownerName = this.getOwnerName();
    	String ownerSuffix = "'s ";
    	if("s".equals(ownerName.substring(ownerName.length() - 1)) || "S".equals(ownerName.substring(ownerName.length() - 1)))
    			ownerSuffix = "' ";
    	String ownedName = ownerName + ownerSuffix + this.getFullName();
    	
    	if(this.hasCustomNameTag())
    		return super.getCommandSenderName() + " (" + ownedName + ")";
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
    	if(this.isTamed() && !this.isTemporary)
    		return false;
        return super.despawnCheck();
    }
    
    @Override
    public boolean isPersistant() {
    	return this.isTamed() || super.isPersistant();
    }
    
    
	// ==================================================
    //                      Stats
    // ==================================================
    /** Applies the subspecies health multipler for this mob. **/
    @Override
    public void applySubspeciesHealthMultiplier() {
    	if(this.isTamed())
    		return;
    	super.applySubspeciesHealthMultiplier();
    }
    
    /** Applies the tamed health multipler for this mob. This should override subspecies. **/
    public void applyTamedHealthMultiplier() {
    	double tamedHealth = this.getBaseHealth();
    	if(this.isTamed()) {
    		tamedHealth *= 3;
    		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(tamedHealth);
	    	if(this.getHealth() > tamedHealth)
	    		this.setHealth((float)tamedHealth);
    	}
    	else {
    		this.applySubspeciesHealthMultiplier();
    	}
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
		if(!this.worldObj.isRemote && this.isTamed() && (itemStack == null || player.isSneaking()) && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()))
			commands.put(CMD_PRIOR.MAIN.id, "GUI");
    	
    	// Server Item Commands:
    	if(!this.worldObj.isRemote && itemStack != null && !player.isSneaking()) {
    		
    		// Taming:
    		if(!this.isTamed() && isTamingItem(itemStack) && MobInfo.tamingEnabled)
    			commands.put(CMD_PRIOR.IMPORTANT.id, "Tame");
    		
    		// Feeding:
    		if(this.isTamed() && this.isHealingItem(itemStack) && this.dataWatcher.getWatchableObjectFloat(WATCHER_ID.HEALTH.id) < this.getMaxHealth())
                commands.put(CMD_PRIOR.ITEM_USE.id, "Feed");
    		
    		// Equipment:
    		if(this.isTamed() && !this.isChild() && !this.isMinion() && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName())) {
	    		String equipSlot = this.inventory.getSlotForEquipment(itemStack);
	    		if(equipSlot != null && (this.inventory.getEquipmentStack(equipSlot) == null || this.inventory.getEquipmentStack(equipSlot).getItem() != itemStack.getItem()))
	    			commands.put(CMD_PRIOR.EQUIPPING.id, "Equip Item");
    		}
    	}
		
		// Sit:
		//if(this.isTamed() && this.canSit() && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()) && !this.worldObj.isRemote)
			//commands.put(CMD_PRIOR.MAIN.id, "Sit");
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, EntityPlayer player, ItemStack itemStack) {
    	
    	// Open GUI:
    	if(command.equals("GUI")) {
    		this.playTameSound();
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
    			ItemFood itemFood = (ItemFood)itemStack.getItem();
    			healAmount = itemFood.func_150905_g(itemStack); // getHealAmount() + itemStack arg.
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
        	this.setSitting(!this.isSitting());
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
    
    // ========== Perform GUI Command ==========
    @Override
    public void performGUICommand(EntityPlayer player, byte guiCommandID) {
    	if(!this.petControlsEnabled())
    		return;
    	if(player != this.getOwner())
    		return;
    	
    	if(guiCommandID == GUI_COMMAND_ID.SITTING.id) {
    		this.setSitting(!this.isSitting());
    		this.playTameSound();
    	}
    	
    	if(guiCommandID == GUI_COMMAND_ID.FOLLOWING.id) {
    		this.setFollowing(!this.isFollowing());
    		this.playTameSound();
    	}
    	
    	if(guiCommandID == GUI_COMMAND_ID.PASSIVE.id) {
    		this.setPassive(!this.isPassive());
    		this.playTameSound();
    	}
    	
    	if(guiCommandID == GUI_COMMAND_ID.STANCE.id) {
    		this.setAggressive(!this.isAggressive());
    		this.playTameSound();
    	}
    	
    	if(guiCommandID == GUI_COMMAND_ID.PVP.id) {
    		this.setPVP(!this.isPVP());
    		this.playTameSound();
    	}
    	
    	super.performGUICommand(player, guiCommandID);
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
            if(target == this.getOwner())
                return true;
            if(target instanceof EntityCreatureTameable) {
            	EntityCreatureTameable tamedTarget = (EntityCreatureTameable)target;
            	if(tamedTarget.isTamed() && (!MinecraftServer.getServer().isPVPEnabled()) || !this.isPVP() || tamedTarget.getOwner() == this.getOwner())
            		return true;
            }
            if(this.getOwner() != null)
                return this.getOwner().isOnSameTeam(target);
        }
        return super.isOnSameTeam(target);
    }
    
    // Unknown
    public boolean func_142018_a(EntityLivingBase targetA, EntityLivingBase targetB) {
        return true;
    }
    
    // ==================================================
    //                       Attacks
    // ==================================================
    // ========== Can Attack ==========
	@Override
	public boolean canAttackClass(Class targetClass) {
		if(this.isPassive())
			return false;
		return super.canAttackClass(targetClass);
	}
	
	@Override
	public boolean canAttackEntity(EntityLivingBase targetEntity) {
		if(this.isPassive())
			return false;
		if(this.isTamed()) {
            if(this.getOwner() == targetEntity)
                return false;
            if(!this.worldObj.isRemote) {
                boolean canPVP = MinecraftServer.getServer().isPVPEnabled() && this.isPVP();
                if(targetEntity instanceof EntityPlayer && !canPVP)
                    return false;
                if(targetEntity instanceof EntityCreatureTameable) {
                    EntityCreatureTameable targetTameable = (EntityCreatureTameable)targetEntity;
                    if(targetTameable.isTamed()) {
                        if(!canPVP)
                            return false;
                        if(targetTameable.getOwner() == this.getOwner())
                            return false;
                    }
                }
            }
        }
		return super.canAttackEntity(targetEntity);
	}
	
    // ========== Attacked From ==========
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damage) {
        if (this.isEntityInvulnerable())
            return false;
        else {
            if(!this.isPassive())
            	this.setSitting(false);

            Entity entity = damageSrc.getSourceOfDamage();
            if(entity instanceof EntityThrowable)
            	entity = ((EntityThrowable)entity).getThrower();
            
            if(this.isTamed() && this.getOwner() == entity)
            	return false;

            return super.attackEntityFrom(damageSrc, damage);
        }
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    public boolean isTamed() {
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id) & TAMED_ID.IS_TAMED.id) != 0;
    }
    
    public void setTamed(boolean setTamed) {
        byte tamed = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id);
        if(setTamed) {
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamed | TAMED_ID.IS_TAMED.id)));
            this.spawnEventType = "";
        }
        else {
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamed - (tamed & TAMED_ID.IS_TAMED.id))));
        }
        this.setAlwaysRenderNameTag(setTamed);
        this.applyTamedHealthMultiplier();
    }
    
    public boolean isTamingItem(ItemStack itemstack) {
        return false;
    }
    
    // ========== Tame Entity ==========
    public boolean tame(EntityPlayer player) {
    	if(!this.worldObj.isRemote)
            if(this.rand.nextInt(3) == 0) {
                this.setPlayerOwner(player);
                String tameMessage = StatCollector.translateToLocal("message.pet.tamed");
                tameMessage = tameMessage.replace("%creature%", this.getSpeciesName());
        		player.addChatMessage(new ChatComponentText(tameMessage));
        		this.playTameEffect(this.isTamed());
            }
            else {
            	String tameFailedMessage = StatCollector.translateToLocal("message.pet.tamefail");
            	tameFailedMessage = tameFailedMessage.replace("%creature%", this.getSpeciesName());
        		player.addChatMessage(new ChatComponentText(tameFailedMessage));
        		this.playTameEffect(this.isTamed());
            }
    	return this.isTamed();
    }
    
    public void setPlayerOwner(EntityPlayer player) {
    	this.setTamed(true);
        this.clearMovement();
        this.setAttackTarget((EntityLivingBase)null);
        this.setOwner(player.getCommandSenderName());
        this.setSitting(false);
        this.setFollowing(true);
        this.setPassive(false);
        this.setAggressive(false);
        this.setPVP(true);
        this.playTameSound();
    }
    
    
    // ==================================================
    //                       Owner
    // ==================================================
    public String getOwnerName() {
    	return this.dataWatcher.getWatchableObjectString(WATCHER_ID.OWNER.id);
    }
    @Override
    public String func_152113_b() { //getOwnerName
    	return this.getOwnerName();
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
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return false; }
    
    // ========== Sitting ==========
    public boolean isSitting() {
    	if(!this.isTamed())
    		return false;
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id) & TAMED_ID.MOVE_SIT.id) != 0;
    }

    public void setSitting(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id);
        if(set) {
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus | TAMED_ID.MOVE_SIT.id)));
            this.setHome((int)this.posX, (int)this.posY, (int)this.posZ, this.sittingGuardRange);
        }
        else {
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.MOVE_SIT.id))));
            this.detachHome();
        }
    }
    
    // ========== Following ==========
    public boolean isFollowing() {
    	if(!this.isTamed())
    		return false;
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id) & TAMED_ID.MOVE_FOLLOW.id) != 0;
    }

    public void setFollowing(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id);
        if(set)
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus | TAMED_ID.MOVE_FOLLOW.id)));
        else
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.MOVE_FOLLOW.id))));
    }
    
    // ========== Passiveness ==========
    public boolean isPassive() {
    	if(!this.isTamed())
    		return false;
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id) & TAMED_ID.STANCE_PASSIVE.id) != 0;
    }

    public void setPassive(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id);
        if(set) {
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus | TAMED_ID.STANCE_PASSIVE.id)));
            this.setAttackTarget(null);
            this.setStealth(0);
        }
        else
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.STANCE_PASSIVE.id))));
    }
    
    // ========== Agressiveness ==========
    public boolean isAggressive() {
    	if(!this.isTamed())
    		return super.isAggressive();
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id) & TAMED_ID.STANCE_AGGRESSIVE.id) != 0;
    }

    public void setAggressive(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id);
        if(set)
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus | TAMED_ID.STANCE_AGGRESSIVE.id)));
        else
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.STANCE_AGGRESSIVE.id))));
    }
    
    // ========== PvP ==========
    public boolean isPVP() {
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id) & TAMED_ID.PVP.id) != 0;
    }

    public void setPVP(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TAMED.id);
        if(set)
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus | TAMED_ID.PVP.id)));
        else
            this.dataWatcher.updateObject(WATCHER_ID.TAMED.id, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.PVP.id))));
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
    
    /** Overrides the vanilla method when check for EnumCreatureType.monster, it will return true if this mob is hostile and false if it is not regardless of this creature's actual EnumCreatureType. Takes tameable mobs into account too. **/
    @Override
	public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
    	if(forSpawnCount && this.isTamed()) // Tamed creatures should no longer take up the mob spawn count.
    		return false;
        return super.isCreatureType(type, forSpawnCount);
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
    //                       Visuals
    // ==================================================
    // ========== Coloring ==========
    /**
     * Returns true if this mob can be dyed different colors. Usually for wool and collars.
     * @param player The player to check for when coloring, this is to stop players from dying other players pets. If provided with null it should return if this creature can be dyed in general.
     */
    @Override
    public boolean canBeColored(EntityPlayer player) {
    	if(player == null) return true;
    	return this.isTamed() && player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName());
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
	        this.setSitting(nbtTagCompound.getBoolean("Sitting"));
        }
        else {
        	this.setSitting(false);
        }
        
        if(nbtTagCompound.hasKey("Following")) {
	        this.setFollowing(nbtTagCompound.getBoolean("Following"));
        }
        else {
        	this.setFollowing(true);
        }
        
        if(nbtTagCompound.hasKey("Passive")) {
	        this.setPassive(nbtTagCompound.getBoolean("Passive"));
        }
        else {
        	this.setPassive(false);
        }
        
        if(nbtTagCompound.hasKey("Aggressive")) {
	        this.setAggressive(nbtTagCompound.getBoolean("Aggressive"));
        }
        else {
        	this.setAggressive(false);
        }
        
        if(nbtTagCompound.hasKey("PVP")) {
	        this.setPVP(nbtTagCompound.getBoolean("PVP"));
        }
        else {
        	this.setPVP(true);
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
        nbtTagCompound.setBoolean("Following", this.isFollowing());
        nbtTagCompound.setBoolean("Passive", this.isPassive());
        nbtTagCompound.setBoolean("Aggressive", this.isAggressive());
        nbtTagCompound.setBoolean("PVP", this.isPVP());
        nbtTagCompound.setFloat("Hunger", this.getCreatureHunger());
        nbtTagCompound.setFloat("Stamina", this.getStamina());
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    @Override
    protected String getLivingSound() {
    	String sound = "_say";
    	if(this.isTamed() && this.getHealth() < this.getMaxHealth())
    		sound = "_beg";
    	return AssetManager.getSound(this.mobInfo.name + sound);
    }
    
    // ========== Tame ==========
    public void playTameSound() {
    	this.playSound(AssetManager.getSound(this.mobInfo.name + "_tame"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
    
    // ========== Eat ==========
    public void playEatSound() {
    	this.playSound(AssetManager.getSound(this.mobInfo.name + "_eat"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}
