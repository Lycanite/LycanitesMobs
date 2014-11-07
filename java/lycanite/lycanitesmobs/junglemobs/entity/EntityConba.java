package lycanite.lycanitesmobs.junglemobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupHunter;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerThreats;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityConba extends EntityCreatureTameable implements IMob {
	EntityAIAttackRanged aiAttackRanged;
	EntityAIAttackMelee aiAttackMelee;
	EntityAIAvoid aiAvoid;
	public boolean vespidInfection = false;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConba(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.setWidth = 0.6F;
        this.setHeight = 0.9F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        
        this.aiAttackMelee = new EntityAIAttackMelee(this).setRate(10).setLongMemory(true).setEnabled(false);
        this.tasks.addTask(2, this.aiAttackMelee);
        
        this.aiAttackRanged = new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(30).setRange(16.0F).setMinChaseDistance(10.0F).setChaseTime(-1);
        this.tasks.addTask(2, this.aiAttackRanged);
        
        this.aiAvoid = new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.0D).setNearDistance(5.0D).setFarDistance(9.0D);
        this.tasks.addTask(3, this.aiAvoid);
        
        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupHunter.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupAlpha.class));
        this.targetTasks.addTask(5, new EntityAITargetAvoid(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.dye, 1, 3), 1).setMinAmount(1).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("PoopCharge")), 0.75F));
	}
    
    
    // ==================================================
    //                       Name
    // ==================================================
    public String getTextureName() {
    	if(this.vespidInfection)
    		return super.getTextureName() + "_infected";
    	return super.getTextureName();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Random Leaping:
        if(this.onGround && !this.worldObj.isRemote) {
        	if(this.hasAvoidTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(1.0F, 0.6D, this.getAttackTarget());
        	}
        	else {
        		if(this.rand.nextInt(50) == 0 && this.isMoving())
        			this.leap(1.0D, 0.6D);
        	}
        }
        
        // Infected AI:
        if(this.vespidInfection && !this.worldObj.isRemote) {
        	this.aiAttackMelee.setEnabled(true);
        	this.aiAttackRanged.setEnabled(false);
        }
        else {
        	this.aiAttackMelee.setEnabled(false);
        	this.aiAttackRanged.setEnabled(true);
        }
        
        // Infected Visuals
        if(this.worldObj.isRemote) {
        	this.vespidInfection = this.extraAnimation01();
        	if(this.vespidInfection) {
    	        for(int i = 0; i < 2; ++i) {
    	            this.worldObj.spawnParticle("witchMagic", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
    	        }
        	}
        }
    }
	
	// ========== AI Update ==========
	@Override
    public void updateAITasks() {
        // Avoid Attack Target:
        if(!this.worldObj.isRemote) {
	        if(this.getAttackTarget() != null && this.getAttackTarget() != this.getAvoidTarget())
	        	this.setAvoidTarget(this.getAttackTarget());
        }
		
        super.updateAITasks();
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Can Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(this.vespidInfection && (targetClass.isAssignableFrom(EntityVespid.class) || targetClass.isAssignableFrom(EntityVespidQueen.class)))
        	return false;
    	return super.canAttackClass(targetClass);
    }
    
    /*/ ========== Can Attack Entity ==========
    @Override
    public boolean canAttackEntity(EntityLivingBase target) {
    	if(this.getDistanceToEntity(target) < 8.0F)
    		return false;
        return super.canAttackEntity(target);
    }*/
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityPoop projectile = new EntityPoop(this.worldObj, this);
        projectile.setProjectileScale(2f);
    	
    	// Y Offset:
    	projectile.posY -= this.height / 4;
    	
    	// Accuracy:
    	float accuracy = 2.0F * (this.getRNG().nextFloat() - 0.5F);
    	
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
   	//                      Death
   	// ==================================================
    @Override
    public void onDeath(DamageSource damageSource) {
		if(!this.worldObj.isRemote && this.vespidInfection) {
			int j = 2 + this.rand.nextInt(5) + worldObj.difficultySetting.getDifficultyId() - 1;
            for(int k = 0; k < j; ++k) {
                float f = ((float)(k % 2) - 0.5F) * this.width / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * this.width / 4.0F;
                EntityVespid vespid = new EntityVespid(this.worldObj);
                vespid.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
                vespid.setSubspecies(this.getSubspeciesIndex(), true);
                vespid.setGrowingAge(vespid.growthTime);
                this.worldObj.spawnEntityInWorld(vespid);
                if(this.getAttackTarget() != null)
                	vespid.setRevengeTarget(this.getAttackTarget());
            }
		}
        super.onDeath(damageSource);
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }

    
    // ==================================================
    //                      Abilities
    // ==================================================
    // ========== Extra Animations ==========
    /** An additional animation boolean that is passed to all clients through the animation mask. **/
    public boolean extraAnimation01() {
    	if(!this.worldObj.isRemote)
    		return this.vespidInfection;
	    else
	    	return this.extraAnimation01;
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.moveSlowdown.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.confusion.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public float getFallResistance() {
    	return 100;
    }
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
    	super.readEntityFromNBT(nbtTagCompound);
        
        if(nbtTagCompound.hasKey("VespidInfection")) {
        	this.vespidInfection = nbtTagCompound.getBoolean("VespidInfection");
        }
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
    	nbtTagCompound.setBoolean("VespidInfection", this.vespidInfection);
    }
}
