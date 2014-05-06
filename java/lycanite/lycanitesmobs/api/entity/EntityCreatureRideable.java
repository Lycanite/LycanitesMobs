package lycanite.lycanitesmobs.api.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCreatureRideable extends EntityCreatureTameable {
	
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
    		return this.mod.getConfig().speedMultipliers.get(this.getConfigName());
    	return super.getSpeedMultiplier();
    }
    
    
    // ==================================================
    //                       Update
    // ==================================================
    @Override
    public void onLivingUpdate() {
    	super.onLivingUpdate();
    	if(this.hasRiderTarget() && this.getRiderTarget() instanceof EntityLivingBase) {
    		EntityLivingBase riderLiving = (EntityLivingBase)this.getRiderTarget();
    		
    		// Run Mount Rider Effects:
    		this.riderEffects(riderLiving);
    		
    		// Protect Rider from Potion Effects:
    		for(Object possibleEffect : riderLiving.getActivePotionEffects()) {
    			if(possibleEffect instanceof PotionEffect) {
    				PotionEffect potionEffect = (PotionEffect)possibleEffect;
    				if(!this.isPotionApplicable(potionEffect))
    					riderLiving.removePotionEffect(potionEffect.getPotionID());
    			}
    		}
    	}
    	
    	if(this.hasRiderTarget()) {
    		// Player Rider Controls:
	    	if(this.getRiderTarget() instanceof EntityPlayer) {
	    		EntityPlayer player = (EntityPlayer)this.getRiderTarget();
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
    
    public void riderEffects(EntityLivingBase rider) {}

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {}
	
    
	// ==================================================
  	//                     Movement
  	// ==================================================
    @Override
    public boolean canBePushed() {
        return this.riddenByEntity == null;
    }
    
    @Override
    protected boolean isMovementBlocked() {
    	// This will disable AI, we don't want this though!
        //return this.hasRiderTarget() && this.isSaddled() ? true : false;
    	return false;
    }
    
    @Override
    public void updateRiderPosition() {
        if(this.hasRiderTarget()) {
            this.getRiderTarget().setPosition(this.posX, this.posY + this.getMountedYOffset() + this.getRiderTarget().getYOffset(), this.posZ);
        }
    }
    
    private void mountedByEntity(Entity entity) {
    	entity.rotationYaw = this.rotationYaw;
    	entity.rotationPitch = this.rotationPitch;
        if(!this.worldObj.isRemote)
        	entity.mountEntity(this);
    }
    
    // ========== Move with Heading ==========
    @Override
    public void moveEntityWithHeading(float moveStrafe, float moveForward) {
    	// Check if Mounted:
    	if(!this.isTamed() || !this.hasSaddle() || !this.hasRiderTarget() || !(this.getRiderTarget() instanceof EntityLivingBase) || !this.riderControl()) {
    		super.moveEntityWithHeading(moveStrafe, moveForward);
    		return;
    	}
    	
    	// Apply Rider Movement:
    	EntityLivingBase rider = (EntityLivingBase)this.getRiderTarget();
    	this.prevRotationYaw = this.rotationYaw = rider.rotationYaw;
    	this.rotationPitch = rider.rotationPitch * 0.5F;
    	this.setRotation(this.rotationYaw, this.rotationPitch);
    	this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
    	moveStrafe = ((EntityLivingBase)rider).moveStrafing * 0.5F;
    	moveForward = ((EntityLivingBase)rider).moveForward;
    	
    	// Jumping Controls:
    	if(!this.isMountJumping() && this.onGround) {
	    	if(this.getRiderTarget() instanceof EntityPlayer) {
	    		EntityPlayer player = (EntityPlayer)this.getRiderTarget();
	    		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
	    		if(playerExt != null && playerExt.isControlActive(ExtendedPlayer.CONTROL_ID.JUMP))
	    			this.startJumping();
	    	}
    	}
    	
    	// Jumping Behaviour:
    	if(this.getJumpPower() > 0.0F && !this.isMountJumping() && this.onGround) {
			this.motionY = this.getMountJumpHeight() * (double)this.getJumpPower();
            if(this.isPotionActive(Potion.jump))
                this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
            this.setMountJumping(true);
            this.isAirBorne = true;
            if(moveForward > 0.0F) {
                float f2 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
                float f3 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
                this.motionX += (double)(-0.4F * f2 * this.jumpPower);
                this.motionZ += (double)(0.4F * f3 * this.jumpPower);
            }
            if(!this.worldObj.isRemote)
            	this.playJumpSound();
            this.setJumpPower(0);
        }
        this.jumpMovementFactor = (float)(this.getAIMoveSpeed() * this.getGlideScale());
        
        // Apply Movement:
        if(!this.worldObj.isRemote) {
            this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
            super.moveEntityWithHeading(moveStrafe, moveForward);
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
        float f4 = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 4.0F;
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
    	return 0.5D;
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
    	if(this.canBeMounted(player) && !player.isSneaking() && !this.worldObj.isRemote && LycanitesMobs.config.getFeatureBool("MobMounting"))
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
            this.mountedByEntity(player);
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
    public boolean isOnSameTeam(EntityLivingBase target) {
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
    	if(this.getRiderTarget() != null)
    		return false;
    	
    	// Can Be Mounted By A Player:
    	if(this.isTamed() && entity instanceof EntityPlayer) {
    		EntityPlayer player = (EntityPlayer)entity;
    		if(player.getCommandSenderName().equalsIgnoreCase(this.getOwnerName()))
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
    	return this.inventory.getEquipmentStack("saddle") != null;
    }

	
	// ==================================================
  	//                    Immunities
  	// ==================================================
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        Entity entity = damageSource.getEntity();
        return this.riddenByEntity != null && this.riddenByEntity.equals(entity) ? false : super.attackEntityFrom(damageSource, damage);
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
    	this.playSound(AssetManager.getSound(this.mobInfo.name + "mount"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}
