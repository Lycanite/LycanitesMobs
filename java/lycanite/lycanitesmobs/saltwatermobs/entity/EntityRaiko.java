package lycanite.lycanitesmobs.saltwatermobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ExtendedEntity;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupHunter;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityRaiko extends EntityCreatureBase implements IMob, IGroupHunter {
    public Entity pickupEntity;
    public EntityAIAttackMelee attackAI = new EntityAIAttackMelee(this).setLongMemory(false);
    public int waterTime = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRaiko(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 7;
        this.spawnsInDarkness = true;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.8F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, this.attackAI);
        this.tasks.addTask(8, new EntityAIWander(this).setPauseRate(0));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.42D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 48D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.feather), 1.0F).setMinAmount(3).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.bone), 0.75F).setMinAmount(1).setMaxAmount(3));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Entity Pickup Update:
        if(!this.worldObj.isRemote) {
	    	this.attackAI.setEnabled(!this.hasPickupEntity());
            if(!this.isInWater()) {
                this.waterTime = 0;

                // Random Dropping:
                if(this.hasPickupEntity()) {
                    ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
                    if(extendedEntity != null)
                        extendedEntity.setPickedUpByEntity(this);
                    if(this.ticksExisted % 100 == 0 && this.getRNG().nextBoolean()) {
                        this.dropPickupEntity();
                    }
                }
    	    	
    	    	// Random Swooping:
    	    	else if(this.hasAttackTarget() && this.getDistanceSqToEntity(this.getAttackTarget()) > 2 && this.getRNG().nextInt(20) == 0) {
    	    		if(this.posY - 1 > this.getAttackTarget().posY)
    	    			this.leap(1.0F, -1.0D, this.getAttackTarget());
    	    		else if(this.posY + 1 < this.getAttackTarget().posY)
    	    			this.leap(1.0F, 1.0D, this.getAttackTarget());
    	    	}
            }

            // Burst Out of Water:
            else {
                this.waterTime++;
                if(this.hasPickupEntity() || this.getAir() <= 40) {
	                if(this.waterTime >= (2 * 20)) {
	                    this.waterTime = 0;
	                    this.leap(0.5F, 2.0D);
	                }
                }
                else if(this.hasAttackTarget()) {
                	if(this.waterTime >= (16 * 20)) {
	                    this.waterTime = 4 * 20;
	                    this.leap(0.5F, 2.0D);
                	}
                }
                else if(this.waterTime >= (8 * 20)) {
                    this.waterTime = 4 * 20;
                    this.leap(0.5F, 2.0D);
            	}
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
    	
    	// Pickup:
        if(this.canPickupEntity(target)) {
        	this.pickupEntity(target);
        }
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canFly() { return true; }

    @Override
    public boolean canSwim() { return true; }
    
    @Override
    public void pickupEntity(Entity entity) {
    	super.pickupEntity(entity);
    	this.leap(1.0F, 2.0D);
    }

    @Override
    public void dropPickupEntity() {
    	// Drop Weight Effect:
        if(this.pickupEntity != null && this.pickupEntity instanceof EntityLivingBase) {
            if(ObjectManager.getPotionEffect("Weight") != null && ObjectManager.getPotionEffect("Weight").id < Potion.potionTypes.length)
                ((EntityLivingBase)this.pickupEntity).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("Weight").id, this.getEffectDuration(5), 1));
        }
    	super.dropPickupEntity();
    }
    
    @Override
    public double[] getPickupOffset(Entity entity) {
    	return new double[]{0, -1.0D, 0};
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
    	if(ObjectManager.getPotionEffect("Weight") != null)
        	if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Weight").id) return false;
        if(potionEffect.getPotionID() == Potion.blindness.id) return false;
        return super.isPotionApplicable(potionEffect);
    }
}
