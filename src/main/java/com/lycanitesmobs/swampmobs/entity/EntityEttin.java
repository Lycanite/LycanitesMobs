package com.lycanitesmobs.swampmobs.entity;

import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityEttin extends EntityCreatureAgeable implements IMob {
	public boolean ettinGreifing = true;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEttin(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.ettinGreifing = ConfigBase.getConfig(this.creatureInfo.group, "general").getBool("Features", "Ettin Griefing", this.ettinGreifing, "Set to false to disable Ettin block destruction.");
        this.solidCollision = true;
        this.setupMob();
        
        // Stats:
        this.attackPhaseMax = 2;
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
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
    	// Destroy Blocks:
		if(!this.getEntityWorld().isRemote)
	        if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.ettinGreifing) {
		    	float distance = this.getAttackTarget().getDistanceToEntity(this);
		    		if(distance <= this.width + 4.0F)
		    			this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10, true);
	        }
        
        super.onLivingUpdate();
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	boolean success = super.meleeAttack(target, damageScale);
    	if(success)
    		this.nextAttackPhase();
    	return success;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.POISON) return false;
        if(potionEffect.getPotion() == MobEffects.BLINDNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityEttin(this.getEntityWorld());
	}
}
