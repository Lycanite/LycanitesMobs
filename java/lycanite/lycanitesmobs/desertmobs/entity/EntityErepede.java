package lycanite.lycanitesmobs.desertmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIBeg;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIPlayerControl;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityErepede extends EntityCreatureRideable implements IGroupPredator {
	
	int difficultyUpdate = -1;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityErepede(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.hasAttackSound = false;
        
        this.eggName = "DesertEgg";
        
        this.setWidth = 0.8F;
        this.setHeight = 1.5F;
        this.attackTime = 10;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
        this.isMobWhenNotTamed = true;
        
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(4, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("erepedetreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(40).setRange(14.0F).setMinChaseDistance(6.0F).setChaseTime(-1));
        this.tasks.addTask(6, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(MobInfo.predatorsAttackAnimals) {
	        if(ObjectManager.getMob("Joust") != null)
	        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityJoust.class).setPackHuntingScale(1, 3));
	        if(ObjectManager.getMob("JoustAlpha") != null)
	        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityJoustAlpha.class).setPackHuntingScale(1, 1));
        }
        
        this.targetTasks.addTask(0, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		if(this.isTamed())
			baseAttributes.put("maxHealth", 60D);
		else
			baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 0.25D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 0D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.flint), 1).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Blocks.iron_ore), 0.5F).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("MudshotCharge")), 0.25F));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }
    
    public void riderEffects(EntityLivingBase rider) {
    	if(rider.isPotionActive(Potion.weakness))
    		rider.removePotionEffect(Potion.weakness.id);
    	if(rider.isPotionActive(Potion.hunger))
    		rider.removePotionEffect(Potion.hunger.id);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    public float getSpeedMod() {
    	if(this.hasRiderTarget())
	    	if(this.worldObj.getBlock((int)this.posX, (int)this.boundingBox.minY - 1, (int)this.posZ).getMaterial() == Material.sand
	    		|| (this.worldObj.getBlock((int)this.posX, (int)this.boundingBox.minY - 1, (int)this.posZ).getMaterial() == Material.air
	    		&& this.worldObj.getBlock((int)this.posX, (int)this.boundingBox.minY - 2, (int)this.posZ).getMaterial() == Material.sand))
	    		return 1.8F;
    	return 1.0F;
    }
    
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {
    	if(this.worldObj.isRemote)
    		return;
    	
    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
    	
    	if(rider instanceof EntityPlayer) {
    		EntityPlayer player = (EntityPlayer)rider;
	    	EntityMudshot projectile = new EntityMudshot(this.worldObj, player);
	    	this.worldObj.spawnEntityInWorld(projectile);
	    	this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	    	this.setJustAttacked();
    	}
    	
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 5;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 5 * 20;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityMudshot projectile = new EntityMudshot(this.worldObj, this);
        projectile.setProjectileScale(2f);
    	
    	// Y Offset:
    	projectile.posY -= this.height / 4;
    	
    	// Accuracy:
    	float accuracy = 2.0F * (this.getRNG().nextFloat() - 0.5F);
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX + accuracy;
        double d1 = target.posY - (target.height * 0.25D) - projectile.posY + accuracy;
        double d2 = target.posZ - this.posZ + accuracy;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 6.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(projectile);
        
        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 15; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
    	if(type.equals("cactus")) return false;
    	return super.isDamageTypeApplicable(type);
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.hunger.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.weakness.id) return false;
        return super.isPotionApplicable(par1PotionEffect);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityErepede(this.worldObj);
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack par1ItemStack) {
		return false;
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemStack) {
        return itemStack.getItem() == ObjectManager.getItem("erepedetreat");
    }
    
    @Override
    public void setTamed(boolean setTamed) {
    	if(setTamed)
    		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
    	else
    		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
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
