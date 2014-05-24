package lycanite.lycanitesmobs.forestmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityTrent extends EntityCreatureTameable implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTrent(World par1World) {
        super(par1World);
        
        // Setup:
        this.mod = ForestMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 10;
        this.spawnsInDarkness = true;
        this.spawnsUnderground = false;
        this.hasAttackSound = true;
        this.spreadFire = true;
        
        this.eggName = "ForestEgg";
        
        this.setWidth = 2.9F;
        this.setHeight = 7.9F;
        this.setupMob();

        this.stepHeight = 2.0F;
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false).setRate(60));
        this.tasks.addTask(4, new EntityAIAttackMelee(this));
        this.tasks.addTask(5, this.aiSit);
        this.tasks.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityEnt.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setSightCheck(false));
        if(ObjectManager.getMob("Cinder") != null)
        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Cinder")));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 140D);
		baseAttributes.put("movementSpeed", 0.18D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.log, 1, 0), 1).setMaxAmount(32));
        this.drops.add(new DropRate(new ItemStack(Items.stick), 0.75F).setMaxAmount(16).setBurningDrop(new ItemStack(Items.coal)));
        this.drops.add(new DropRate(new ItemStack(Items.wheat_seeds), 0.1F).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.pumpkin_seeds), 0.05F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.melon_seeds), 0.05F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.emerald), 0.01F).setMaxAmount(1));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Water Healing:
        if(this.isInWater())
        	this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 3 * 20, 2));
        else if(this.worldObj.isRaining() && this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.posY, (int)this.posZ))
        	this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 3 * 20, 1));
    }
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityEnt.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Leech:
    	float leeching = this.getAttackDamage(damageScale) / 2;
    	this.heal(leeching);
    	
    	// Paralysis:
    	if(target instanceof EntityLivingBase && this.rand.nextFloat() >= 0.5F) {
    		if(ObjectManager.getPotionEffect("Paralysis") != null && ObjectManager.getPotionEffect("Paralysis").id < Potion.potionTypes.length)
    			((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("Paralysis").id, this.getEffectDuration(2), 0));
         }
        
        return true;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 4.0F;
    	if(damageSrc.getEntity() != null) {
    		if(damageSrc.getEntity() instanceof EntityPlayer) {
    			EntityPlayer entityPlayer = (EntityPlayer)damageSrc.getEntity();
	    		if(entityPlayer.getHeldItem() != null) {
	    			if(entityPlayer.getHeldItem().getItem() instanceof ItemAxe)
	    				return 4.0F;
	    		}
    		}
    		else if(damageSrc.getEntity() instanceof EntityLiving) {
	    		EntityLiving entityLiving = (EntityLiving)damageSrc.getEntity();
	    		if(entityLiving.getHeldItem() != null) {
	    			if(entityLiving.getHeldItem().getItem() instanceof ItemAxe)
	    				return 4.0F;
	    		}
    		}
    	}
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotionID() == Potion.moveSlowdown.id) return false;
        if(ObjectManager.getPotionEffect("Paralysis") != null)
        	if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Paralysis").id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
