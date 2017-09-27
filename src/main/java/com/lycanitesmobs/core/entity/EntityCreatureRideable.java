package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityCreatureRideable extends EntityCreatureTameable {

    public Entity lastRiddenByEntity = null;

	// Jumping:
	public boolean mountJumping = false;
	public float jumpPower = 0.0F;

	public boolean abilityToggled = false;
	public boolean inventoryToggled = false;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public EntityCreatureRideable(World world) {
		super(world);
		this.hasJumpSound = true;
	}
	
	// ========== Init ==========
    @Override
    protected void entityInit() {
        super.entityInit();
    }
    
    // ========= Speed Multiplier ==========
    // Skip the difficulty scale when a mounted mob.
    public double getSpeedMultiplier() {
    	if(this.hasRiderTarget())
    		return this.mobInfo.multiplierSpeed;
    	return super.getSpeedMultiplier();
    }
    
    
    // ==================================================
    //                       Update
    // ==================================================
    @Override
    public void onLivingUpdate() {
    	super.onLivingUpdate();

        if(this.lastRiddenByEntity != this.getControllingPassenger()) {
            if(this.lastRiddenByEntity != null)
                this.onDismounted(this.lastRiddenByEntity);
            this.lastRiddenByEntity = this.getControllingPassenger();
        }

    	if(this.hasRiderTarget() && this.getControllingPassenger() instanceof EntityLivingBase) {
    		EntityLivingBase riderLiving = (EntityLivingBase)this.getControllingPassenger();
    		
    		// Run Mount Rider Effects:
    		this.riderEffects(riderLiving);
    		
    		// Protect Rider from Potion Effects:
    		for(Object possibleEffect : riderLiving.getActivePotionEffects()) {
    			if(possibleEffect instanceof PotionEffect) {
    				PotionEffect potionEffect = (PotionEffect)possibleEffect;
    				if(!this.isPotionApplicable(potionEffect))
    					riderLiving.removePotionEffect(potionEffect.getPotion());
    			}
    		}
    	}
    	
    	if(this.hasRiderTarget()) {
    		// Player Rider Controls:
	    	if(this.getControllingPassenger() instanceof EntityPlayer) {
	    		EntityPlayer player = (EntityPlayer)this.getControllingPassenger();
	    		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
	    		if(playerExt == null)
	    			return;
	    		
	    		// Mount Ability:
	    		if(playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY)) {
	    			this.mountAbility(player);
	    			this.abilityToggled = true;
	    		}
	    		else
	    			this.abilityToggled = false;
	    		
	    		// Player Inventory:
	    		if(playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY)) {
	    			if(!this.inventoryToggled)
	    				this.openGUI(player);
	    			this.inventoryToggled = true;
	    		}
	    		else
	    			this.inventoryToggled = false;
	    	}
    	}
    	else {
    		this.abilityToggled = false;
			this.inventoryToggled = false;
    	}
    }
    
    public void riderEffects(EntityLivingBase rider) {
        if(!rider.canBreatheUnderwater() && rider.isInWater())
            rider.setAir(300);
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {}

    public void onDismounted(Entity entity) {}
	
    
	// ==================================================
  	//                     Movement
  	// ==================================================
    @Override
    public boolean canBePushed() {
        if(this.getControllingPassenger() != null)
            return false;
        return super.canBePushed();
    }

    @Override
    public boolean canBeSteered() {
	    if(this.getEntityWorld().isRemote)
	        return true;
        Entity entity = this.getControllingPassenger();
        return entity == this.getOwner();
    }
    
    @Override
    protected boolean isMovementBlocked() {
    	// This will disable AI, we don't want this though!
        //return this.hasRiderTarget() && this.isSaddled() ? true : false;
    	return super.isMovementBlocked();
    }
    
    @Override
    public void updatePassenger(Entity passenger) {
        if(this.isPassenger(passenger)) {
            this.getControllingPassenger().setPosition(this.posX, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ);
        }
    }

    private void mount(Entity entity) {
    	entity.rotationYaw = this.rotationYaw;
    	entity.rotationPitch = this.rotationPitch;
        if(!this.getEntityWorld().isRemote)
        	entity.startRiding(this);
    }
    
    // ========== Move with Heading ==========
    @Override
    public void moveRelative(float strafe, float up, float forward, float friction) {
        // Check if Mounted:
        if (!this.isTamed() || !this.hasSaddle() || !this.hasRiderTarget() || !(this.getControllingPassenger() instanceof EntityLivingBase) || !this.riderControl()) {
            super.moveRelative(strafe, up, forward, friction);
            return;
        }
        this.moveMountedWithHeading(strafe, forward);
    }

    public void moveMountedWithHeading(float strafe, float forward) {
        // Apply Rider Movement:
        if(this.getControllingPassenger() instanceof EntityLivingBase) {
            EntityLivingBase rider = (EntityLivingBase) this.getControllingPassenger();
            this.prevRotationYaw = this.rotationYaw = rider.rotationYaw;
            this.rotationPitch = rider.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            strafe = rider.moveStrafing * 0.5F;
            forward = rider.moveForward;
        }

        // Swimming / Flying Controls:
        double flyMotion = 0;
        if(this.isInWater() || this.isInLava() || this.isFlying()) {
            if (this.getControllingPassenger() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) this.getControllingPassenger();
                ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.JUMP)) {
                    flyMotion = this.getSpeedMultiplier();
                }
                else if(player.rotationPitch > 0 && forward != 0.0F) {
                    flyMotion = this.getSpeedMultiplier() * -(player.rotationPitch / 90);
                }
                else {
                    flyMotion = 0;
                }
            }
        }

        else {
            // Jumping Controls:
            if (!this.isMountJumping() && this.onGround) {
                if (this.getControllingPassenger() instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) this.getControllingPassenger();
                    ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                    if (playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.JUMP)) {
                        this.startJumping();
                    }
                }
            }

            // Jumping Behaviour:
            if (this.getJumpPower() > 0.0F && !this.isMountJumping() && this.onGround && this.canPassengerSteer()) {
                this.motionY = this.getMountJumpHeight() * (double) this.getJumpPower();
                if (this.isPotionActive(MobEffects.JUMP_BOOST))
                    this.motionY += (double) ((float) (this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                this.setMountJumping(true);
                this.isAirBorne = true;
                if (forward > 0.0F) {
                    float f2 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
                    float f3 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
                    this.motionX += (double) (-0.4F * f2 * this.jumpPower);
                    this.motionZ += (double) (0.4F * f3 * this.jumpPower);
                }
                if (!this.getEntityWorld().isRemote)
                    this.playJumpSound();
                this.setJumpPower(0);
                net.minecraftforge.common.ForgeHooks.onLivingJump(this);
            }
            this.jumpMovementFactor = (float) (this.getAIMoveSpeed() * this.getGlideScale());
        }

        // Apply Movement:
        if(this.canPassengerSteer()) {
            this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            if(!this.useDirectNavigator()) {
                if(this.isFlying() && !this.isInWater() && !this.isInLava()) {
                    this.moveRelative(strafe, 0, forward, 0.1F);
                    this.move(MoverType.SELF, this.motionX, flyMotion, this.motionZ);
                    this.motionX *= 0.8999999761581421D;
                    this.motionY *= 0.8999999761581421D;
                    this.motionZ *= 0.8999999761581421D;
                }
                else if(this.isStrongSwimmer() && (this.isInWater() || this.isInLava())) {
                    this.moveRelative(strafe, 0, forward, 0.1F);
                    this.move(MoverType.SELF, this.motionX, flyMotion, this.motionZ);
                    this.motionX *= 0.8999999761581421D;
                    this.motionY *= 0.8999999761581421D;
                    this.motionZ *= 0.8999999761581421D;
                }
                else
                    super.moveRelative(strafe, 0, forward, 0.1F);
            }
            else
                this.directNavigator.flightMovement(strafe, forward);
        }

        // Clear Jumping:
        if(this.onGround) {
            this.setJumpPower(0);
            this.setMountJumping(false);
        }

        // Animate Limbs:
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
        if (f4 > 1.0F)
            f4 = 1.0F;
        this.limbSwingAmount += (f4 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    // ========== Jumping Start ==========
    public void startJumping() {
    	this.setJumpPower();
    }
    
    // ========== Jumping ==========
    public double getMountJumpHeight() {
    	return 0.75D;
    }
    
    public boolean isMountJumping() {
    	return this.mountJumping;
    }
    
    public void setMountJumping(boolean set) {
    	this.mountJumping = set;
    }

    // ========== Jump Power ==========
    public void setJumpPower(int power) {
    	if(power < 0)
    		power = 0;
    	if(power > 99)
    		power = 99;
    	if(power < 90)
            this.jumpPower = 1.0F * ((float)power / 89.0F);
    	else
        	this.jumpPower = 1.0F + (1.0F * ((float)(power - 89) / 10.0F));
    }
    
    public void setJumpPower() {
    	this.setJumpPower(89);
    }
    
    public float getJumpPower() {
    	return this.jumpPower;
    }
    
    // ========== Gliding ==========
    public double getGlideScale() {
    	return 0.1F;
    }
    
    // ========== Rider Control ==========
    public boolean riderControl() {
    	return true;
    }
	
    
	// ==================================================
  	//                     Interact
  	// ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
    	
    	// Mount:
        boolean mountingAllowed = MobInfo.mountingEnabled;
        if(mountingAllowed && this.isFlying())
            mountingAllowed = MobInfo.mountingFlightEnabled;
    	if(this.canBeMounted(player) && !player.isSneaking() && !this.getEntityWorld().isRemote && mountingAllowed)
    		commands.put(CMD_PRIOR.MAIN.id, "Mount");
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, EntityPlayer player, ItemStack itemStack) {
    	
    	// Mount:
    	if(command.equals("Mount")) {
    		this.playMountSound();
            this.clearMovement();
            this.setAttackTarget((EntityLivingBase)null);
            this.mount(player);
    	}
    	
    	super.performCommand(command, player, itemStack);
    }
    
    
    // ==================================================
    //                       Targets
    // ==================================================
    // ========== Teams ==========
    @Override
    public Team getTeam() {
        if(this.hasRiderTarget()) {
            EntityLivingBase rider = this.getRider();
            if(rider != null)
                return rider.getTeam();
        }
        return super.getTeam();
    }
    
    @Override
    public boolean isOnSameTeam(Entity target) {
        if(this.hasRiderTarget()) {
            EntityLivingBase rider = this.getRider();
            if(target == rider)
                return true;
            if(rider != null)
                return rider.isOnSameTeam(target);
        }
        return super.isOnSameTeam(target);
    }
    
    
    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public boolean canSit() { return false; }

    public boolean canBeMounted(Entity entity) {
    	if(this.getControllingPassenger() != null)
    		return false;
    	
    	// Can Be Mounted By A Player:
    	if(this.isTamed() && entity instanceof EntityPlayer) {
    		EntityPlayer player = (EntityPlayer)entity;
    		if(player == this.getOwner())
    			return this.hasSaddle() && !this.isChild();
    	}
    	
    	// Can Be Mounted By Mobs:
    	else if(!this.isTamed() && !(entity instanceof EntityPlayer)) {
    		return !this.isChild();
    	}
    	
    	return false;
    }
    
    
	// ==================================================
  	//                     Equipment
  	// ==================================================
    public boolean hasSaddle() {
    	ItemStack saddleStack = this.inventory.getEquipmentStack("saddle");
    	return saddleStack != null && !saddleStack.isEmpty();
    }

	
	// ==================================================
  	//                    Immunities
  	// ==================================================
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        Entity entity = damageSource.getTrueSource();
        return this.getControllingPassenger() != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(damageSource, damage);
    }
    
    @Override
    public float getFallResistance() {
    	return 2;
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Mount ==========
    public void playMountSound() {
    	this.playSound(AssetManager.getSound(this.mobInfo.name + "_mount"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}
