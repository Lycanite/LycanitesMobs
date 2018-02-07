package com.lycanitesmobs.mountainmobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityJabberwock extends EntityCreatureTameable implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityJabberwock(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if(this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
        }
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this));
        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(8).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIMoveVillage(this));
        this.tasks.addTask(7, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Random Lunging:
        if(this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(6.0F, 0.1D, this.getAttackTarget());
        	}
        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Damage Effect:
        if(target instanceof EntityLivingBase) {
    		if(ObjectManager.getPotionEffect("weight") != null)
    			((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("weight"), this.getEffectDuration(5), 0));
        }
        return true;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.MINING_FATIGUE) return false;
        if(ObjectManager.getPotionEffect("weight") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("weight")) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public float getFallResistance() {
    	return 50;
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityJabberwock(this.getEntityWorld());
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                       Visuals
    // ==================================================
    /** Returns this creature's main texture. Also checks for for subspecies. **/
    public ResourceLocation getTexture() {
        if(!"Rudolph".equals(this.getCustomNameTag()))
            return super.getTexture();

        String textureName = this.getTextureName() + "_rudolph";
        if(AssetManager.getTexture(textureName) == null)
            AssetManager.addTexture(textureName, this.creatureInfo.group, "textures/entity/" + textureName.toLowerCase() + ".png");
        return AssetManager.getTexture(textureName);
    }
}
