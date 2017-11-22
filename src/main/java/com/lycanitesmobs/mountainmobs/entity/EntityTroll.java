package com.lycanitesmobs.mountainmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityTroll extends EntityCreatureTameable implements IMob {
	
	public boolean trollGreifing = true;
	
	// ========== Unique Entity Variables ==========
	public boolean stoneForm = false;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTroll(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 5;
        this.hasAttackSound = false;

        //this.canGrow = false;
        //this.babySpawnChance = 0.01D;
        
        this.trollGreifing = ConfigBase.getConfig(this.group, "general").getBool("Features", "Troll Griefing", this.trollGreifing, "Set to false to disable Troll block destruction.");
        
        this.setWidth = 1.5F;
        this.setHeight = 3.2F;
        this.solidCollision = true;
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
        this.tasks.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.5D).setRate(60).setRange(14.0F).setMinChaseDistance(5.0F));
        this.tasks.addTask(6, this.aiSit);
        this.tasks.addTask(7, new EntityAIFollowOwner(this).setStrayDistance(8).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 30D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 6D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.LOG), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.BONE), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.LEATHER), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.COAL), 1).setMinAmount(2).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("BoulderBlastCharge")), 0.5F).setMinAmount(1).setMaxAmount(1));
	}
    
    
    // ==================================================
    //                       Name
    // ==================================================
    public String getTextureName() {
    	if(this.stoneForm)
    		return super.getTextureName() + "_stone";
    	return super.getTextureName();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Daylight Stone Form:
        if(!this.stoneForm) {
        	if(this.isDaytime() && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
        		this.stoneForm = true;
        	}
        }
        else {
        	if(!this.isDaytime() || !this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
                this.stoneForm = false;
            }
        }
        
        // Destroy Blocks:
 		if(!this.getEntityWorld().isRemote)
 	        if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.trollGreifing) {
 		    	float distance = this.getAttackTarget().getDistanceToEntity(this);
 		    		if(distance <= this.width + 4.0F)
 		    			this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10, true);
 	        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.stoneForm) // Slower in stone form.
    		return 0.125F;
    	return 1.0F;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
        EntityProjectileBase projectile = new EntityBoulderBlast(this.getEntityWorld(), this);
        projectile.setProjectileScale(6f);
    	
    	// Y Offset:
    	projectile.posY -= this.height / 4;
    	
    	// Accuracy:
    	float accuracy = 3.0F * (this.getRNG().nextFloat() - 0.5F);
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX + accuracy;
        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
        double d2 = target.posZ - this.posZ + accuracy;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 6.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.getEntityWorld().spawnEntity(projectile);
        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
        if("Jarno".equals(this.getCustomNameTag()))
            return 0;

    	if(this.stoneForm) {
    		if(damageSrc.getTrueSource() != null) {
    			Item heldItem = null;
        		if(damageSrc.getTrueSource() instanceof EntityLivingBase) {
                    EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getTrueSource();
    	    		if(entityLiving.getHeldItem(EnumHand.MAIN_HAND) != null) {
    	    			heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND).getItem();
    	    		}
        		}
        		
        		if(ObjectLists.isPickaxe(heldItem))
    				return 2.0F;
        	}
    		return 0.25F;
    	}
    	
    	if(damageSrc.isFireDamage())
    		return 2.0F;
    	return 1.0F;
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
    
    @Override
    public boolean canBurn() { return !this.stoneForm; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
