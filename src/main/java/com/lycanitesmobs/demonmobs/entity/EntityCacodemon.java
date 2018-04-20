package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.elementalmobs.entity.EntityWraith;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCacodemon extends EntityCreatureRideable implements IGroupDemon {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCacodemon(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
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
        this.tasks.addTask(3, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("cacodemontreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(4, new EntityAIAttackRanged(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
        this.tasks.addTask(5, this.aiSit);
        this.tasks.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(7, new EntityAIFollowParent(this));
        this.tasks.addTask(8, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(4, new EntityAITargetOwnerThreats(this));
        this.targetTasks.addTask(4, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityGhast.class));
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
        if(CreatureManager.getInstance().getCreature("wraith").enabled) {
            if (this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("wraith").entityClass, 64D) < 10) {
                float random = this.rand.nextFloat();
                if (random <= 0.1F)
                    this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
            }
        }
    }

    public void spawnAlly(double x, double y, double z) {
        EntityWraith minion = new EntityWraith(this.getEntityWorld());
        minion.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
		minion.setMinion(true);
		minion.setMasterTarget(this);
        this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null) {
            minion.setRevengeTarget(this.getAttackTarget());
        }
        minion.chargeAttack();
        minion.setSizeScale(this.sizeScale);
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    public boolean isFlying() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
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
        if(targetClass.isAssignableFrom(EntityTrite.class) || targetClass.isAssignableFrom(EntityAstaroth.class) || targetClass.isAssignableFrom(EntityAsmodeus.class))
            return false;
        if(CreatureManager.getInstance().getCreature("wraith") != null) {
            if (targetClass.isAssignableFrom(CreatureManager.getInstance().getCreature("wraith").entityClass))
                return false;
        }
        return super.canAttackClass(targetClass);
    }

	// ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntityDemonicBlast.class, target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
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
