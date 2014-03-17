package lycanite.lycanitesmobs.demonmobs.entity;

import java.util.HashMap;
import java.util.List;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupHunter;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIMate;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIPlayerControl;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRiderAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRiderRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityPinky extends EntityCreatureRideable implements IAnimals, IGroupAnimal, IGroupAlpha, IGroupPredator, IGroupHunter {
	
	EntityAIPlayerControl playerControlAI;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityPinky(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Pinky";
        this.mod = DemonMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.experience = 5;
        this.hasAttackSound = true;
        this.spreadFire = true;
        
        this.spawnsOnPeaceful = DemonMobs.config.getFeatureBool("PinkiesOnPeaceful");
        this.despawnNaturally = DemonMobs.config.getFeatureBool("DespawnPinkiesNaturally");
        this.eggName = "DemonEgg";
        
        this.setWidth = 0.9F;
        this.setHeight = 1.95F;
        this.setupMob();
        
        this.attackTime = 10;
        this.stepHeight = 1.0F;
        
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIMate(this));
        this.tasks.addTask(2, new EntityAITempt(this).setItemID(ObjectManager.getItem("HellfireCharge").itemID).setTemptDistanceMin(4.0D));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setTargetClass(EntityPigZombie.class).setSpeed(1.5D).setRate(10).setDamage(8.0D).scaleRange(2.0D));
        this.tasks.addTask(4, new EntityAIAttackMelee(this).setSpeed(1.5D).setRate(10));
        this.playerControlAI = new EntityAIPlayerControl(this);
        this.tasks.addTask(5, playerControlAI);
        this.tasks.addTask(7, new EntityAIWander(this).setSpeed(1.0D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        
        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityCow.class).setTameTargetting(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPig.class).setTameTargetting(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntitySheep.class).setTameTargetting(true));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityPigZombie.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class));
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		if(this.isTamed())
			baseAttributes.put("maxHealth", 80D);
		else
			baseAttributes.put("maxHealth", 40D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.25D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 3D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(ObjectManager.getItem("PinkyMeatRaw").itemID, 1).setBurningItem(ObjectManager.getItem("PinkyMeatCooked").itemID, -1).setMinAmount(1).setMaxAmount(3));
	}
	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	return this.worldObj.provider.dimensionId != -1 || super.isPersistant();
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
    	if(rider.isPotionActive(Potion.wither))
    		rider.removePotionEffect(Potion.wither.id);
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
    	if(this.worldObj.isRemote)
    		return;
    	
    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
    	
    	// Withering Roar:
    	double distance = 4.0D;
    	List<EntityLivingBase> possibleTargets = this.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(distance, distance, distance), null);
    	if(!possibleTargets.isEmpty()) {
    		for(EntityLivingBase possibleTarget : possibleTargets) {
    			if(possibleTarget != null && possibleTarget.isEntityAlive()
    					&& possibleTarget != this && possibleTarget != this.getRider()
    					&& !this.isOnSameTeam(possibleTarget)) {
    				boolean doDamage = true;
				    if(this.getRider() instanceof EntityPlayer) {
				    	if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
				    		doDamage = false;
				    	}
				    }
    				if(doDamage) {
    					possibleTarget.addPotionEffect(new PotionEffect(Potion.wither.id, 10 * 20, 0));
    				}
    			}
    		}
    	}
    	this.playAttackSound();
    	this.setJustAttacked();
    	
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 20;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 5 * 20;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }
	
	
    // ==================================================
    //                     Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 1.0D;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
        if(!super.meleeAttack(target, damageScale))
        	return false;
        
    	// Breed:
    	if(target instanceof EntityCow || target instanceof EntityPig || target instanceof EntitySheep)
    		this.breed();
    	
        return true;
    }
    
    
    // ==================================================
   	//                     Abilities
   	// ==================================================
    public boolean canBeTempted() {
    	return this.isChild();
    }
    
    
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
        if(par1PotionEffect.getPotionID() == Potion.wither.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityPinky(this.worldObj);
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack par1ItemStack) {
        return false; // Breeding is triggered by attacking specific mobs instead!
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.itemID == ObjectManager.getItem("HellfireCharge").itemID && this.isChild();
    }
    
    @Override
    public void setTamed(boolean setTamed) {
    	if(setTamed)
    		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(80.0D);
    	else
    		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(40.0D);
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
