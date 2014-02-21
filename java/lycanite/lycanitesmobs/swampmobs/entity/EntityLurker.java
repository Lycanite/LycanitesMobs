package lycanite.lycanitesmobs.swampmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.desertmobs.entity.EntityJoust;
import lycanite.lycanitesmobs.desertmobs.entity.EntityJoustAlpha;
import lycanite.lycanitesmobs.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.entity.ai.EntityAIAvoid;
import lycanite.lycanitesmobs.entity.ai.EntityAIBeg;
import lycanite.lycanitesmobs.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.entity.ai.EntityAIFollowParent;
import lycanite.lycanitesmobs.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.entity.ai.EntityAIMate;
import lycanite.lycanitesmobs.entity.ai.EntityAIStealth;
import lycanite.lycanitesmobs.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetParent;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.entity.ai.EntityAIWatchClosest;
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

public class EntityLurker extends EntityCreatureTameable {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityLurker(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Lurker";
        this.mod = SwampMobs.instance;
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.hasAttackSound = true;
        
        this.eggName = "SwampEgg";
        
        this.setWidth = 0.8F;
        this.setHeight = 1.5F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, this.aiSit);
        this.tasks.addTask(1, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.tasks.addTask(3, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(4, new EntityAITempt(this).setItemID(Item.fermentedSpiderEye.itemID).setTemptDistanceMin(2.0D));
        this.tasks.addTask(5, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(6, new EntityAIMate(this));
        this.tasks.addTask(7, new EntityAIFollowParent(this));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
        if(ObjectManager.getMob("Joust") != null)
        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityJoust.class));
        if(ObjectManager.getMob("JoustAlpha") != null)
        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityJoustAlpha.class));
        this.targetTasks.addTask(0, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        
        // Drops:
        this.drops.add(new DropRate(Item.silk.itemID, 0.5F).setMinAmount(2).setMaxAmount(5));
        this.drops.add(new DropRate(Item.leather.itemID, 0.5F).setMaxAmount(2));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		if(this.isTamed())
			baseAttributes.put("maxHealth", 40D);
		else
			baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Lurker Blind Stalking:
        if(this.getAttackTarget() != null) {
        	if(this.getAttackTarget().isPotionActive(Potion.blindness.id))
        		this.setAvoidTarget(this.getAttackTarget());
        	else
        		this.setAvoidTarget(null);
        }
        else
        	this.setAvoidTarget(null);
        
        // Leap:
        if(this.onGround && !this.worldObj.isRemote && this.rand.nextInt(10) == 0) {
        	if(this.hasAttackTarget())
        		this.leap(6.0F, 0.4D, this.getAttackTarget());
        	else if(this.hasAvoidTarget())
        		this.leap(4.0F, 0.4D);
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
                ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.poison.id, effectSeconds * 20, 1));
                ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.blindness.id, (int)(effectSeconds * 20), 0));
            }
        }
        
        return true;
    }
    
    
    // ==================================================
   	//                     Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.worldObj.isRemote) return false;
    	else {
	    	if(this.hasAttackTarget()) {
	    		if(this.getAttackTarget() instanceof EntityPlayer) {
	    			EntityPlayer playerTarget = (EntityPlayer)this.getAttackTarget();
	    			ItemStack itemstack = playerTarget.inventory.getCurrentItem();
	    			if(itemstack != null && isTamingItem(itemstack))
	    				return false;
	    		}
	    		if(!this.getAttackTarget().isPotionActive(Potion.blindness.id))
	    			return false;
	    		if(this.getDistanceSqToEntity(this.getAttackTarget()) < (5.0D * 5.0D))
	    			return false;
	    	}
	    	else {
	    		if(this.isTamed())
	    			return false;
	    		if(!this.canFly() && !this.getNavigator().noPath())
	    			return false;
				if(this.canFly() && !this.flightNavigator.atTargetPosition())
					return false;
	    	}
	        return true;
        }
    }
    
    @Override
    public void startStealth() {
    	if(this.worldObj.isRemote) {
            String particle = "smoke";
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
            	this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    	super.startStealth();
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
    public int getBagSize() { return 5; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.blindness.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.itemID == Item.fermentedSpiderEye.itemID;
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
