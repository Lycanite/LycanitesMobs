package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

public class EntityCacodemon extends EntityCreatureRideable implements IGroupDemon {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCacodemon(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = false;
        
        this.setWidth = 1.9F;
        this.setHeight = 1.9F;
        
        this.justAttackedTime = 20;
        this.setupMob();

        this.stepHeight = 1.0F;
        this.hitAreaWidthScale = 1.5F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIMate(this));
        this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(5, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("cacodemontreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(6, new EntityAIAttackRanged(this).setSpeed(0.25D).setRate(80).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
        this.tasks.addTask(7, new EntityAIFollowParent(this));
        this.tasks.addTask(8, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityGhast.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 40D);
		baseAttributes.put("attackDamage", 0D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.GHAST_TEAR), 0.25F).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.GUNPOWDER), 0.5F).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.BLAZE_POWDER), 0.5F).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demoniclightningcharge")), 0.75F));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("soulstonedemonic")), 1).setMinAmount(1).setSubspecies(3));
	}

    // ========== Size ==========
    @Override
    public void setSize(float width, float height) {
        if(this.getSubspeciesIndex() == 3) {
            super.setSize(width * 2, height * 2);
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public double getRenderScale() {
        if(this.getSubspeciesIndex() == 3) {
            return this.sizeScale * 2;
        }
        return this.sizeScale;
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        if(!this.getEntityWorld().isRemote && this.getSubspeciesIndex() == 3 && this.hasAttackTarget() && this.ticksExisted % 20 == 0) {
            this.allyUpdate();
        }

        super.onLivingUpdate();
    }

    @Override
    public void riderEffects(EntityLivingBase rider) {
        if(rider.isPotionActive(MobEffects.WITHER))
            rider.removePotionEffect(MobEffects.WITHER);
        if(rider.isBurning())
            rider.extinguish();
    }

    // ========== Spawn Minions ==========
    public void allyUpdate() {
        if(this.getEntityWorld().isRemote)
            return;

        // Spawn Minions:
        if(this.nearbyCreatureCount(EntityNetherSoul.class, 64D) < 10) {
            float random = this.rand.nextFloat();
            if(random <= 0.1F)
                this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
        }
    }

    public void spawnAlly(double x, double y, double z) {
        EntityLivingBase minion = new EntityNetherSoul(this.getEntityWorld());
        minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
        if(minion instanceof EntityCreatureBase) {
            EntityCreatureBase minionCreature = (EntityCreatureBase)minion;
            minionCreature.setMinion(true);
            minionCreature.setMasterTarget(this);
        }
        this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null)
            minion.setRevengeTarget(this.getAttackTarget());
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    public boolean isFlying() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return false; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
        if(targetClass.isAssignableFrom(EntityTrite.class) || targetClass.isAssignableFrom(EntityAstaroth.class) || targetClass.isAssignableFrom(EntityAsmodeus.class) || targetClass.isAssignableFrom(EntityNetherSoul.class))
            return false;
        return super.canAttackClass(targetClass);
    }

	// ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityDemonicBlast projectile = new EntityDemonicBlast(this.getEntityWorld(), this);
        projectile.setProjectileScale(2f);
    	
    	// Y Offset:
    	projectile.posY -= this.height * 0.5D;
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX;
        double d1 = target.posY - projectile.posY;
        double d2 = target.posZ - this.posZ;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.1F;
        float velocity = 0.5F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 0.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.getEntityWorld().spawnEntity(projectile);

        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isDamageEntityApplicable(Entity entity) {
    	if(entity instanceof EntityCacodemon)
    		return false;
    	return super.isDamageEntityApplicable(entity);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.WITHER) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.getItem() == ObjectManager.getItem("cacodemontreat");
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }


    // ==================================================
    //                      Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
    }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.abilityToggled)
            return;

        if(this.hasPickupEntity()) {
            this.dropPickupEntity();
            return;
        }

        if(this.getStamina() < this.getStaminaCost())
            return;

        if(rider instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)rider;
            EntityDemonicBlast projectile = new EntityDemonicBlast(this.getEntityWorld(), player);
            this.getEntityWorld().spawnEntity(projectile);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.setJustAttacked();
        }

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 10;
    }

    public int getStaminaRecoveryWarmup() {
        return 2 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }


    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness() {
        if(justAttacked())
            return 1.0F;
        else
            return super.getBrightness();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        if(justAttacked())
            return 15728880;
        else
            return super.getBrightnessForRender();
    }
}
