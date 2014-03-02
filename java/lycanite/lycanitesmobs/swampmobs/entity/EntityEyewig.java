package lycanite.lycanitesmobs.swampmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIPlayerControl;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityEyewig extends EntityCreatureRideable {
	EntityAIAttackRanged rangedAttackAI;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEyewig(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Eyewig";
        this.mod = SwampMobs.instance;
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.experience = 10;
        this.spawnsInDarkness = true;
        this.hasAttackSound = true;
        
        this.eggName = "SwampEgg";

        this.rangedDamage = new int[] {2, 3, 4};
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(4, new EntityAITempt(this).setItemID(ObjectManager.getItem("PoisonGland").itemID).setTemptDistanceMin(4.0D));
        this.rangedAttackAI = new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(10).setStaminaTime(100).setRange(14.0F).setMinChaseDistance(4.0F).setChaseTime(-1);
        this.tasks.addTask(5, rangedAttackAI);
        this.tasks.addTask(6, new EntityAIAttackMelee(this).setLongMemory(false).setMaxChaseDistance(14.0F));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
        
        // Drops:
        this.drops.add(new DropRate(Item.spiderEye.itemID, 0.9F).setMaxAmount(2));
        this.drops.add(new DropRate(Item.fermentedSpiderEye.itemID, 0.2F));
        this.drops.add(new DropRate(Item.silk.itemID, 1).setMinAmount(2).setMaxAmount(5));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Rider Effects ==========
	public void riderEffects(EntityLivingBase rider) {
    	if(rider.isPotionActive(Potion.poison))
    		rider.removePotionEffect(Potion.poison.id);
    	if(rider.isPotionActive(Potion.blindness))
    		rider.removePotionEffect(Potion.blindness.id);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    public float getSpeedMod() {
    	if(this.isInWater())
    		return 8.0F;
    	return 1.0F;
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
    		if(this.getRiderTarget() == null || !(this.getRiderTarget() instanceof EntityLivingBase))
    			return;
    		
    		this.abilityProjectile = new EntityPoisonRay(this.worldObj, (EntityLivingBase)this.getRiderTarget(), 20, 10, this);
    		this.abilityProjectile.setOffset(0, 0.5, 0);
	    	
	    	// Damage:
	    	abilityProjectile.setDamage(rangedDamage[0]);
	        if(worldObj.difficultySetting == 2) abilityProjectile.setDamage(rangedDamage[1]);
	        else if(worldObj.difficultySetting > 2) abilityProjectile.setDamage(rangedDamage[2]);
	        
	        // Launch:
	        this.playSound(abilityProjectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.worldObj.spawnEntityInWorld(abilityProjectile);
    	}
    	
    	this.applyStaminaCost();
    }
    
    
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
            byte effectSeconds = 8;
            if(this.worldObj.difficultySetting > 1)
                if (this.worldObj.difficultySetting == 2)
                	effectSeconds = 12;
                else if (this.worldObj.difficultySetting == 3)
                	effectSeconds = 16;
            if(target instanceof EntityPlayer)
            	effectSeconds /= 2;
            if(effectSeconds > 0) {
                ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.blindness.id, (int)(effectSeconds * 20), 0));
            }
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
	    	
	    	// Damage:
	        projectile.setDamage(rangedDamage[0]);
	        if(worldObj.difficultySetting == 2) projectile.setDamage(rangedDamage[1]);
	        else if(worldObj.difficultySetting > 2) projectile.setDamage(rangedDamage[2]);
	        
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
    		return true;
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
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.blindness.id) return false;
        return super.isPotionApplicable(par1PotionEffect);
    }
    
    @Override
    public float getFallResistance() {
    	return 5;
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.itemID == ObjectManager.getItem("PoisonGland").itemID;
    }
    
    @Override
    public void setTamed(boolean setTamed) {
    	if(setTamed)
    		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(40.0D);
    	else
    		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20.0D);
    	super.setTamed(setTamed);
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
