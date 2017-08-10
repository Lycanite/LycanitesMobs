package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityAstaroth extends EntityCreatureBase implements IMob, IGroupDemon {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAstaroth(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 2;
        this.experience = 15;
        this.hasAttackSound = false;
        
        this.setWidth = 3.5F;
        this.setHeight = 2.0F;
        this.solidCollision = false;
        this.setupMob();
        this.hitAreaScale = 1.5F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(5).setRange(40.0F).setMinChaseDistance(16.0F).setChaseTime(-1));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpClasses(EntityTrite.class));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 40D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 40D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.IRON_INGOT), 1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.REDSTONE), 1).setMinAmount(3).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("devilstarcharge")), 0.5F));
	}


    // ==================================================
    //                      Update
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Asmodeus Master:
        if(this.updateTick % 20 == 0) {
            if (this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityAsmodeus && ((EntityCreatureBase)this.getMasterTarget()).getBattlePhase() > 0) {
                EntityHellShield projectile = new EntityHellShield(this.worldObj, this);
                projectile.setProjectileScale(3f);
                projectile.posY -= this.height * 0.35D;
                double dX = this.getMasterTarget().posX - this.posX;
                double dY = this.getMasterTarget().posY + (this.getMasterTarget().height * 0.75D) - projectile.posY;
                double dZ = this.getMasterTarget().posZ - this.posZ;
                double distance = MathHelper.sqrt_double(dX * dX + dZ * dZ) * 0.1F;
                float velocity = 0.8F;
                projectile.setThrowableHeading(dX, dY + distance, dZ, velocity, 0.0F);
                this.worldObj.spawnEntityInWorld(projectile);
            }
        }
    }
    
    
	// ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityTrite.class) || targetClass.isAssignableFrom(EntityCacodemon.class) || targetClass.isAssignableFrom(EntityAsmodeus.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityDevilstar projectile = new EntityDevilstar(this.worldObj, this);
        projectile.setProjectileScale(1f);
    	
    	// Y Offset:
    	projectile.posY -= this.height * 0.35D;
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX;
        double d1 = target.posY - (target.height * 0.25D) - projectile.posY;
        double d2 = target.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.1F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 0.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(projectile);

        super.rangedAttack(target, range);
    }
	
	
	// ==================================================
   	//                      Death
   	// ==================================================
	@Override
	public void onDeath(DamageSource par1DamageSource) {
        if(!this.worldObj.isRemote && MobInfo.getFromName("trite").mobEnabled) {
            int j = 2 + this.rand.nextInt(5) + worldObj.getDifficulty().getDifficultyId() - 1;
            for(int k = 0; k < j; ++k) {
                float f = ((float)(k % 2) - 0.5F) * this.width / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * this.width / 4.0F;
                EntityTrite trite = new EntityTrite(this.worldObj);
                trite.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
                trite.setMinion(true);
                trite.setSubspecies(this.getSubspeciesIndex(), true);
                this.worldObj.spawnEntityInWorld(trite);
                if(this.getAttackTarget() != null)
                	trite.setRevengeTarget(this.getAttackTarget());
            }
        }
        super.onDeath(par1DamageSource);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.WITHER) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
}
