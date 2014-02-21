package lycanite.lycanitesmobs.forestmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.entity.ai.EntityAIMoveVillage;
import lycanite.lycanitesmobs.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityEnt extends EntityCreatureAgeable implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEnt(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Ent";
        this.mod = ForestMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.spawnsUnderground = false;
        this.hasAttackSound = true;
        this.spreadFire = true;
        
        this.eggName = "ForestEgg";
        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.8F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(4, new EntityAIAttackMelee(this));
        this.tasks.addTask(6, new EntityAIMoveVillage(this));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpClasses(EntityTrent.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setSightCheck(false));
        if(ObjectManager.getMob("Cinder") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Cinder")));
        
        // Drops:
        this.drops.add(new DropRate(Block.wood.blockID, 0, 1).setMaxAmount(6));
        this.drops.add(new DropRate(Item.stick.itemID, 0.5F).setMaxAmount(6).setBurningItem(Item.coal.itemID, 1));
        this.drops.add(new DropRate(Item.seeds.itemID, 0.1F).setMaxAmount(3));
        this.drops.add(new DropRate(Item.pumpkinSeeds.itemID, 0.05F).setMaxAmount(1));
        this.drops.add(new DropRate(Item.melonSeeds.itemID, 0.05F).setMaxAmount(1));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 25D);
		baseAttributes.put("movementSpeed", 0.18D);
		baseAttributes.put("knockbackResistance", 0.5D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
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
    	if(targetClass.isAssignableFrom(EntityTrent.class))
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
        
        return true;
    }
    
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 2.0F;
    	if(damageSrc.getEntity() != null) {
    		if(damageSrc.getEntity() instanceof EntityPlayer) {
    			EntityPlayer entityPlayer = (EntityPlayer)damageSrc.getEntity();
	    		if(entityPlayer.getHeldItem() != null) {
	    			if(entityPlayer.getHeldItem().getItem() instanceof ItemAxe)
	    				return 2.0F;
	    		}
    		}
    		else if(damageSrc.getEntity() instanceof EntityLiving) {
	    		EntityLiving entityLiving = (EntityLiving)damageSrc.getEntity();
	    		if(entityLiving.getHeldItem() != null) {
	    			if(entityLiving.getHeldItem().getItem() instanceof ItemAxe)
	    				return 2.0F;
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
        if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Paralysis").id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityEnt(this.worldObj);
	}
}
