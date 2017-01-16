package lycanite.lycanitesmobs.swampmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.core.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.core.entity.ai.*;
import lycanite.lycanitesmobs.core.info.DropRate;
import lycanite.lycanitesmobs.core.info.MobInfo;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityEyewig extends EntityCreatureRideable {
	EntityAIAttackRanged rangedAttackAI;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEyewig(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 0;
        this.experience = 10;
        this.hasAttackSound = true;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        //this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(4, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("eyewigtreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(5, new EntityAIAttackMelee(this).setLongMemory(false).setMaxChaseDistance(4.0F));
        this.rangedAttackAI = new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(10).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false);
        this.tasks.addTask(6, rangedAttackAI);
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
        }
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.SPIDER_EYE), 0.9F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.FERMENTED_SPIDER_EYE), 0.2F));
        this.drops.add(new DropRate(new ItemStack(Items.STRING), 1).setMinAmount(2).setMaxAmount(5));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Rider Effects ==========
	public void riderEffects(EntityLivingBase rider) {
    	if(rider.isPotionActive(MobEffects.POISON))
    		rider.removePotionEffect(MobEffects.POISON);
    	if(rider.isPotionActive(MobEffects.BLINDNESS))
    		rider.removePotionEffect(MobEffects.BLINDNESS);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    EntityPoisonRay abilityProjectile = null;
    public void mountAbility(Entity rider) {
    	if(this.worldObj.isRemote)
    		return;
    	
    	if(this.getStamina() < this.getStaminaRecoveryMax() * 2)
    		return;

        if(this.hasAttackTarget())
            this.setAttackTarget(null);

    	// Update Laser:
    	if(this.abilityProjectile != null && this.abilityProjectile.isEntityAlive()) {
    		this.abilityProjectile.setTime(20);
    	}
    	else {
    		this.abilityProjectile = null;
    	}
    	
    	// Create New Laser:
    	if(this.abilityProjectile == null) {
	    	// Type:
    		if(this.getControllingPassenger() == null || !(this.getControllingPassenger() instanceof EntityLivingBase))
    			return;
    		
    		this.abilityProjectile = new EntityPoisonRay(this.worldObj, (EntityLivingBase)this.getControllingPassenger(), 25, 20, this);
    		this.abilityProjectile.setOffset(0, 0.5, 0);
	    	
	    	// Launch:
	        this.playSound(abilityProjectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.worldObj.spawnEntityInWorld(abilityProjectile);
    	}
    	
    	this.applyStaminaCost();
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) { return false; }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Effect:
        if(target instanceof EntityLivingBase) {
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, this.getEffectDuration(8), 0));
        }
        
        return true;
    }

    // ========== Ranged Attack ==========
    EntityPoisonRay projectile = null;
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Update Laser:
    	if(this.projectile != null && this.projectile.isEntityAlive()) {
    		this.projectile.setTime(20);
    	}
    	else {
    		this.projectile = null;
    	}
    	
    	// Create New Laser:
    	if(this.projectile == null) {
	    	// Type:
	    	this.projectile = new EntityPoisonRay(this.worldObj, this, 20, 10);
	    	
	    	// Launch:
	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.worldObj.spawnEntityInWorld(projectile);
    	}
    }
    
    
    // ==================================================
  	//                      Targets
  	// ==================================================
    @Override
    public boolean isAggressive() {
    	if(this.worldObj.isDaytime())
    		return this.testLightLevel() < 2;
    	else
    		return super.isAggressive();
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 10; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.POISON) return false;
        if(potionEffect.getPotion() == MobEffects.BLINDNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public float getFallResistance() {
    	return 10;
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.getItem() == ObjectManager.getItem("eyewigtreat");
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
}
