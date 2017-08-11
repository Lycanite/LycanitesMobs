package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.ai.EntityAISit;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.UUID;

public class EntityCreatureTameable extends EntityCreatureAgeable implements IEntityOwnable {
	
	// Stats:
	public float hunger = this.getCreatureHungerMax();
	public float stamina = this.getStaminaMax();
	public float staminaRecovery = 0.5F;
	public boolean isMobWhenNotTamed = true;
	public float sittingGuardRange = 16F;

    // Owner:
    public UUID ownerUUID;
	
	// AI:
	public EntityAISit aiSit;

    // Datawatcher:
    protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(EntityCreatureBase.class, DataSerializers.BYTE);
    protected static final DataParameter<String> OWNER = EntityDataManager.<String>createKey(EntityCreatureBase.class, DataSerializers.STRING);
    protected static final DataParameter<Float> HUNGER = EntityDataManager.<Float>createKey(EntityCreatureBase.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> STAMINA = EntityDataManager.<Float>createKey(EntityCreatureBase.class, DataSerializers.FLOAT);
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
        this.dataManager.register(TAMED, (byte)0);
        this.dataManager.register(OWNER, "");
        this.dataManager.register(HUNGER, new Float(this.getCreatureHungerMax()));
        this.dataManager.register(STAMINA, new Float(this.getStaminaMax()));
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.aiSit = new EntityAISit(this);
    }
    
    // ========== Name ==========
    @Override
    public String getName() {
    	if(!this.isTamed() || !MobInfo.ownerTags)
    		return super.getName();
    	
    	String ownerName = this.getOwnerName();
    	String ownerSuffix = "'s ";
        if(ownerName != null && ownerName.length() > 0) {
            if ("s".equals(ownerName.substring(ownerName.length() - 1)) || "S".equals(ownerName.substring(ownerName.length() - 1)))
                ownerSuffix = "' ";
        }
    	String ownedName = ownerName + ownerSuffix + this.getFullName();
    	
    	if(this.hasCustomName())
    		return super.getName();// + " (" + ownedName + ")";
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
        if(this.getEntityWorld().isRemote)
        	return false;
        if(this.getPetEntry() != null && this.getPetEntry().entity != this && this.getPetEntry().entity != null)
            return true;
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
    /** Applies the subspecies health multiplier for this mob. **/
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
    		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(tamedHealth);
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
    public boolean canBeLeashedTo(EntityPlayer player) {
	    if(this.isTamed() && player.getName().equalsIgnoreCase(this.getOwnerName()))
	        return true;
	    return super.canBeLeashedTo(player);
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
    	if(this.getEntityWorld().isRemote)
    		return;
    	if(this.stamina < this.getStaminaMax() && this.staminaRecovery >= this.getStaminaRecoveryMax() / 2)
    		this.setStamina(Math.min(this.stamina + this.staminaRecovery, this.getStaminaMax()));
    	if(this.staminaRecovery < this.getStaminaRecoveryMax())
    		this.staminaRecovery = Math.min(this.staminaRecovery + (this.getStaminaRecoveryMax() / this.getStaminaRecoveryWarmup()), this.getStaminaRecoveryMax());
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
		if(!this.getEntityWorld().isRemote && this.isTamed() && (itemStack == null || player.isSneaking()) && player.getName().equalsIgnoreCase(this.getOwnerName()))
			commands.put(CMD_PRIOR.MAIN.id, "GUI");
    	
    	// Server Item Commands:
    	if(!this.getEntityWorld().isRemote && itemStack != null && !player.isSneaking()) {
    		
    		// Taming:
    		if(!this.isTamed() && isTamingItem(itemStack) && MobInfo.tamingEnabled)
    			commands.put(CMD_PRIOR.IMPORTANT.id, "Tame");
    		
    		// Feeding:
    		if(this.isTamed() && this.isHealingItem(itemStack) && this.getHealth() < this.getMaxHealth())
                commands.put(CMD_PRIOR.ITEM_USE.id, "Feed");
    		
    		// Equipment:
    		if(this.isTamed() && !this.isChild() && this.canEquip() && player.getName().equalsIgnoreCase(this.getOwnerName())) {
	    		String equipSlot = this.inventory.getSlotForEquipment(itemStack);
	    		if(equipSlot != null && (this.inventory.getEquipmentStack(equipSlot) == null || this.inventory.getEquipmentStack(equipSlot).getItem() != itemStack.getItem()))
	    			commands.put(CMD_PRIOR.EQUIPPING.id, "Equip Item");
    		}
    	}
		
		// Sit:
		//if(this.isTamed() && this.canSit() && player.getName().equalsIgnoreCase(this.getOwnerName()) && !this.getEntityWorld().isRemote)
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
    			healAmount = itemFood.getHealAmount(itemStack);
    		}
    		this.heal((float)healAmount);
            this.playEatSound();
            if(this.getEntityWorld().isRemote) {
                EnumParticleTypes particle = EnumParticleTypes.HEART;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                double d2 = this.rand.nextGaussian() * 0.02D;
                for(int i = 0; i < 25; i++)
                	this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
            }
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Equip Armor:
    	if(command.equals("Equip Item")) {
    		ItemStack equippedItem = this.inventory.getEquipmentStack(this.inventory.getSlotForEquipment(itemStack));
    		if(equippedItem != null)
    			this.dropItem(equippedItem);
    		ItemStack equipStack = itemStack.copy();
    		equipStack.setCount(1);
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
    	else if(this.isTamed() && player.getName().equalsIgnoreCase(this.getOwnerName()))
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

        // Update Pet Entry Summon Set:
        if(this.petEntry != null && this.petEntry.summonSet != null) {
            this.petEntry.summonSet.updateBehaviour(this);
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
            Entity owner = this.getOwner();
            if(owner != null)
                return owner.getTeam();
        }
        return super.getTeam();
    }
    
    @Override
    public boolean isOnSameTeam(Entity target) {
        if(this.isTamed()) {
            if(target == this.getOwner())
                return true;
            if(target instanceof EntityCreatureTameable) {
            	EntityCreatureTameable tamedTarget = (EntityCreatureTameable)target;
            	if(tamedTarget.isTamed() && (!this.getEntityWorld().getMinecraftServer().isPVPEnabled()) || !this.isPVP() || tamedTarget.getOwner() == this.getOwner())
            		return true;
            }
            if(this.getOwner() != null)
                return this.getOwner().isOnSameTeam(target);
        }
        return super.isOnSameTeam(target);
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
            if(!this.getEntityWorld().isRemote) {
                boolean canPVP = this.getEntityWorld().getMinecraftServer().isPVPEnabled() && this.isPVP();
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

    // ========= Get Damage Source ==========
    /**
     * Returns the damage source to be used by this mob when dealing damage.
     * @param nestedDamageSource This can be null or can be a passed entity damage source for all kinds of use, mainly for minion damage sources.
     * @return
     */
    @Override
    public DamageSource getDamageSource(EntityDamageSource nestedDamageSource) {
        if(this.isTamed() && this.getOwner() != null) {
            if(nestedDamageSource == null)
                nestedDamageSource = new EntityDamageSource("mob", this);
            return new MinionEntityDamageSource(nestedDamageSource, this.getOwner());
        }
        return super.getDamageSource(nestedDamageSource);
    }
	
    // ========== Attacked From ==========
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damage) {
        if (this.isEntityInvulnerable(damageSrc))
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
        try {
            return (this.getByteFromDataManager(TAMED) & TAMED_ID.IS_TAMED.id) != 0;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public void setTamed(boolean setTamed) {
        byte tamed = this.getByteFromDataManager(TAMED);
        if(setTamed) {
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamed | TAMED_ID.IS_TAMED.id)));
            this.spawnEventType = "";
        }
        else {
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamed - (tamed & TAMED_ID.IS_TAMED.id))));
        }
        this.setAlwaysRenderNameTag(setTamed);
        this.applyTamedHealthMultiplier();
    }
    
    public boolean isTamingItem(ItemStack itemstack) {
        return false;
    }
    
    // ========== Tame Entity ==========
    public boolean tame(EntityPlayer player) {
    	if(!this.getEntityWorld().isRemote && this.getSubspeciesIndex() < 3)
            if(this.rand.nextInt(3) == 0) {
                this.setPlayerOwner(player);
                this.unsetTemporary();
                String tameMessage = I18n.translateToLocal("message.pet.tamed");
                tameMessage = tameMessage.replace("%creature%", this.getSpeciesName());
        		player.sendMessage(new TextComponentString(tameMessage));
        		this.playTameEffect(this.isTamed());
                player.addStat(ObjectManager.getAchievement(this.mobInfo.name + ".tame"), 1);
                if(this.timeUntilPortal > this.getPortalCooldown())
                    this.timeUntilPortal = this.getPortalCooldown();
            }
            else {
            	String tameFailedMessage = I18n.translateToLocal("message.pet.tamefail");
            	tameFailedMessage = tameFailedMessage.replace("%creature%", this.getSpeciesName());
        		player.sendMessage(new TextComponentString(tameFailedMessage));
        		this.playTameEffect(this.isTamed());
            }
    	return this.isTamed();
    }
    
    public void setPlayerOwner(EntityPlayer player) {
        this.setPlayerOwner();
        this.setOwnerId(player.getUniqueID());
        this.setOwner(player.getName());
    }

    public void setPlayerOwner(UUID playerUUID, String playerName) {
        this.setPlayerOwner();
        this.setOwnerId(playerUUID);
        this.setOwner(playerName);
    }

    public void setPlayerOwner() {
        this.setTamed(true);
        this.clearMovement();
        this.setAttackTarget((EntityLivingBase) null);
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
        try {
            return this.getStringFromDataManager(OWNER);
        }
        catch(Exception e) {
            return "";
        }
    }
    
    public void setOwner(String owner) {
        this.dataManager.set(OWNER, owner);
    }

    public UUID getOwnerId() {
        return this.ownerUUID;
    }

    public void setOwnerId(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
    
    @Override
    public Entity getOwner() {
        if(this.getOwnerId() == null)
            return null;
        return this.getEntityWorld().getPlayerEntityByUUID(this.getOwnerId());
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte status) {
        if(status == 7)
            this.playTameEffect(true);
        else if(status == 6)
            this.playTameEffect(false);
        else
            super.handleStatusUpdate(status);
    }
    
    // ========== Feeding Food ==========
    public boolean isHealingItem(ItemStack testStack) {
    	return false;
    }
    
    
    // ==================================================
    //                    Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return false; }
    public byte behaviourBitMask() { return this.getByteFromDataManager(TAMED); }
    
    // ========== Sitting ==========
    public boolean isSitting() {
    	if(!this.isTamed())
    		return false;
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.MOVE_SIT.id) != 0;
    }

    public void setSitting(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set) {
            this.dataManager.set(TAMED, Byte.valueOf((byte) (tamedStatus | TAMED_ID.MOVE_SIT.id)));
            this.setHome((int)this.posX, (int)this.posY, (int)this.posZ, this.sittingGuardRange);
        }
        else {
            this.dataManager.set(TAMED, Byte.valueOf((byte) (tamedStatus - (tamedStatus & TAMED_ID.MOVE_SIT.id))));
            this.detachHome();
        }
    }
    
    // ========== Following ==========
    public boolean isFollowing() {
    	if(!this.isTamed())
    		return false;
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.MOVE_FOLLOW.id) != 0;
    }

    public void setFollowing(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set)
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TAMED_ID.MOVE_FOLLOW.id)));
        else
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.MOVE_FOLLOW.id))));
    }
    
    // ========== Passiveness ==========
    public boolean isPassive() {
    	if(!this.isTamed())
    		return false;
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.STANCE_PASSIVE.id) != 0;
    }

    public void setPassive(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set) {
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TAMED_ID.STANCE_PASSIVE.id)));
            this.setAttackTarget(null);
            this.setStealth(0);
        }
        else
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.STANCE_PASSIVE.id))));
    }
    
    // ========== Agressiveness ==========
    public boolean isAggressive() {
    	if(!this.isTamed())
    		return super.isAggressive();
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.STANCE_AGGRESSIVE.id) != 0;
    }

    public void setAggressive(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set)
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TAMED_ID.STANCE_AGGRESSIVE.id)));
        else
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.STANCE_AGGRESSIVE.id))));
    }
    
    // ========== PvP ==========
    public boolean isPVP() {
        return (this.getByteFromDataManager(TAMED) & TAMED_ID.PVP.id) != 0;
    }

    public void setPVP(boolean set) {
    	if(!this.petControlsEnabled())
    		set = false;
        byte tamedStatus = this.getByteFromDataManager(TAMED);
        if(set)
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus | TAMED_ID.PVP.id)));
        else
            this.dataManager.set(TAMED, Byte.valueOf((byte)(tamedStatus - (tamedStatus & TAMED_ID.PVP.id))));
    }
    
    
    // ==================================================
    //                       Hunger
    // ==================================================
    public float getCreatureHunger() {
    	if(this.getEntityWorld() == null)
    		return this.getCreatureHungerMax();
    	if(!this.getEntityWorld().isRemote)
    		return this.hunger;
    	else {
            try {
                return this.getFloatFromDataManager(HUNGER);
            } catch (Exception e) {
                return 0;
            }
        }
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
    	if(this.getEntityWorld() != null && this.getEntityWorld().isRemote) {
            try {
                this.stamina = this.getFloatFromDataManager(STAMINA);
            } catch (Exception e) {}
        }
    	return this.stamina;
    }
    
    public void setStamina(float setStamina) {
    	this.stamina = setStamina;
    	if(this.getEntityWorld() != null && !this.getEntityWorld().isRemote) {
    		this.dataManager.set(STAMINA, setStamina);
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
        EnumParticleTypes particle = EnumParticleTypes.HEART;
        if(!success)
        	particle = EnumParticleTypes.SMOKE_NORMAL;

        for(int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
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
    	return this.isTamed() && player.getName().equalsIgnoreCase(this.getOwnerName());
    }


    // ========== Boss Health Bar ==========
    public boolean showBossInfo() {
        if(this.isTamed())
            return false;
        return super.showBossInfo();
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

        if(nbtTagCompound.hasKey("OwnerUUID")) {
            String uuidString = nbtTagCompound.getString("OwnerUUID");
            if(!"".equals(uuidString))
                this.setOwnerId(UUID.fromString(uuidString));
            else
                this.setOwnerId(null);
        }
        else {
            this.ownerUUID = null;
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
        if(this.getOwnerId() == null) {
            nbtTagCompound.setString("OwnerUUID", "");
        }
        else {
            nbtTagCompound.setString("OwnerUUID", this.getOwnerId().toString());
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
    /** Get number of ticks, at least during which the living entity will be silent. **/
    @Override
    public int getTalkInterval() {
        if(this.isTamed())
            return 600;
        return super.getTalkInterval();
    }
    @Override
    protected SoundEvent getAmbientSound() {
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
