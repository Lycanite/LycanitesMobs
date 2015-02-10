package lycanite.lycanitesmobs.arcticmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupFire;
import lycanite.lycanitesmobs.api.IGroupIce;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerThreats;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFrostweaver extends EntityCreatureTameable implements IMob, IGroupIce {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityFrostweaver(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = false;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(40).setRange(14.0F).setMinChaseDistance(5.0F).setChaseTime(-1));
        this.tasks.addTask(5, this.aiSit);
        this.tasks.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupFire.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        if(MobInfo.predatorsAttackAnimals) {
        	this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
        }
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.string), 1.0F).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.spider_eye), 0.5F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("FrostwebCharge")), 0.25F).setMaxAmount(3));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Particles:
        if(this.worldObj.isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.worldObj.spawnParticle("snowshovel", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityFrostweb projectile = new EntityFrostweb(this.worldObj, this);
        projectile.setProjectileScale(0.5f);
    	
    	// Y Offset:
    	projectile.posY -= this.height / 4;
    	
    	// Accuracy:
    	float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX + accuracy;
        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
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
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
        if(type.equals("ooze")) return false;
        return super.isDamageTypeApplicable(type);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.moveSlowdown.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.hunger.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public boolean webproof() { return true; }
}
