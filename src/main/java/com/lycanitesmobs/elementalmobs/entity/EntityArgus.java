package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.MobDrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityArgus extends EntityCreatureTameable implements IMob, IFusable {

	private int teleportTime = 60;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityArgus(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.8F;
        this.setupMob();

        this.stepHeight = 1.0F;

        this.justAttackedTime = 40;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
		this.tasks.addTask(1, new EntityAIFollowFuse(this).setLostDistance(16));
        this.tasks.addTask(2, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setRate(20).setLongMemory(true));
        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(8).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
		this.targetTasks.addTask(7, new EntityAITargetFuse(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new MobDrop(new ItemStack(Items.ENDER_PEARL), 0.5F).setMaxAmount(2));
        this.drops.add(new MobDrop(new ItemStack(Blocks.OBSIDIAN), 0.5F).setMaxAmount(2));
	}

    // ========== Set Size ==========
    @Override
    public void setSize(float width, float height) {
        if(this.getSubspeciesIndex() == 3) {
            super.setSize(width * 2, height * 2);
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public double getRenderScale() {
        if(this.getSubspeciesIndex() == 3) {
            return this.sizeScale * 2;
        }
        return this.sizeScale;
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Random Target Teleporting:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget()) {
	        if(this.teleportTime-- <= 0) {
	        	this.teleportTime = 20 + this.getRNG().nextInt(20);
        		this.playJumpSound();
        		BlockPos teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().width - 3D, 0);
        		if(this.canTeleportTo(this.getEntityWorld(), teleportPosition)
        		&& this.canTeleportTo(this.getEntityWorld(), new BlockPos(teleportPosition.getX(), teleportPosition.getY() + 1, teleportPosition.getZ())))
                    this.setPosition(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
        		else if(this.canTeleportTo(this.getEntityWorld(), teleportPosition)
                && this.canTeleportTo(this.getEntityWorld(), teleportPosition))
                    this.setPosition(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ);
	        }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }

    public boolean canTeleportTo(World world, BlockPos pos) {
        IBlockState blockState = this.getEntityWorld().getBlockState(pos);
        if(blockState.getBlock() == null)
            return false;
        if(blockState.isNormalCube())
            return false;
        if(this.getSubspeciesIndex() >= 3)
            return true;
        if(this.testLightLevel(pos) > 1)
            return false;
        return true;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Effects:
        if(target instanceof EntityLivingBase) {
			if(ObjectManager.getPotionEffect("instability") != null)
				((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("instability"), this.getEffectDuration(10), 0));
		}
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(ObjectManager.getPotionEffect("instability") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("instability")) return false;
        return super.isPotionApplicable(potionEffect);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }


	// ==================================================
	//                      Fusion
	// ==================================================
	protected IFusable fusionTarget;

	@Override
	public IFusable getFusionTarget() {
		return this.fusionTarget;
	}

	@Override
	public void setFusionTarget(IFusable fusionTarget) {
		this.fusionTarget = fusionTarget;
	}

	@Override
	public Class getFusionClass(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return EntityGrue.class;
		}
		if(fusable instanceof EntityGeonach) {
			return EntityTremor.class;
		}
		if(fusable instanceof EntityDjinn) {
			return EntityWraith.class;
		}
		return null;
	}
}
