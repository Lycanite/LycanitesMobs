package com.lycanitesmobs.freshwatermobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityIoray extends EntityCreatureRideable implements IMob, IGroupPredator {

	EntityAIWander wanderAI;
    EntityAIAttackRanged rangedAttackAI;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityIoray(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 1;
        this.experience = 8;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0D;
        this.canGrow = true;
        
        this.setWidth = 2.8F;
        this.setHeight = 1.8F;
        this.setupMob();

        // Stats:
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIStayByWater(this));
        this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(3, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("ioraytreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(5, new EntityAIAttackMelee(this).setLongMemory(false).setMaxChaseDistance(4.0F));
        this.rangedAttackAI = new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(10).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false);
        this.tasks.addTask(6, rangedAttackAI);
        this.wanderAI = new EntityAIWander(this);
        this.tasks.addTask(6, wanderAI.setPauseRate(60));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntitySquid.class));
        }
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 80D);
		baseAttributes.put("movementSpeed", 0.36D);
		baseAttributes.put("knockbackResistance", 0.8D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.FISH), 1F).setBurningDrop(new ItemStack(Items.COOKED_FISH)).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.FISH, 1, 3), 0.5F).setBurningDrop(new ItemStack(Items.COOKED_FISH, 1, 3)).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.PRISMARINE_SHARD, 1), 1F).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.PRISMARINE_CRYSTALS, 1), 0.75F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.DIAMOND, 1), 0.05F).setMaxAmount(1));
        this.drops.add(new DropRate(new ItemStack(Items.DYE, 1, 4), 1).setMinAmount(2).setMaxAmount(4));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("lifedraincharge")), 0.5F).setMaxAmount(1));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, (5 * 20) + 5, 1));
        if(rider.isPotionActive(ObjectManager.getPotionEffect("paralysis")))
            rider.removePotionEffect(ObjectManager.getPotionEffect("paralysis"));
        if(rider.isPotionActive(ObjectManager.getPotionEffect("penetration")))
            rider.removePotionEffect(ObjectManager.getPotionEffect("penetration"));
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(block == Blocks.FLOWING_WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }
	
	// Swimming:
	@Override
	public boolean isStrongSwimmer() {
		return true;
	}
	
	// Walking:
	@Override
	public boolean canWalk() {
		return false;
	}

    // ========== Mounted Offset ==========
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.25D;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(ObjectManager.getPotionEffect("penetration") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("penetration")) return false;
        if(ObjectManager.getPotionEffect("paralysis") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("paralysis")) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }

    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    EntityWaterJet abilityProjectile = null;
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.getStamina() < this.getStaminaRecoveryMax() * 2)
            return;

        if(this.hasAttackTarget())
            this.setAttackTarget(null);

        // Update Laser:
        if(this.abilityProjectile != null && this.abilityProjectile.isEntityAlive()) {
            this.abilityProjectile.setTime(20);
        }
        else {
            this.abilityProjectile = null;
        }

        // Create New Laser:
        if(this.abilityProjectile == null) {
            // Type:
            if(this.getControllingPassenger() == null || !(this.getControllingPassenger() instanceof EntityLivingBase))
                return;

            this.abilityProjectile = new EntityWaterJet(this.getEntityWorld(), (EntityLivingBase)this.getControllingPassenger(), 25, 20, this);
            this.abilityProjectile.setOffset(0, 0.5, 0);

            // Launch:
            this.playSound(abilityProjectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().spawnEntity(abilityProjectile);
        }

        this.applyStaminaCost();
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) { return false; }

    // Dismount:
    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity != null && entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 5 * 20, 1));
        }
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityWaterJet projectile = null;
    @Override
    public void rangedAttack(Entity target, float range) {
        // Update Laser:
        if(this.projectile != null && this.projectile.isEntityAlive()) {
            this.projectile.setTime(20);
        }
        else {
            this.projectile = null;
        }

        // Create New Laser:
        if(this.projectile == null) {
            // Type:
            this.projectile = new EntityWaterJet(this.getEntityWorld(), this, 20, 10);

            // Launch:
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().spawnEntity(projectile);
        }
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 10; }


    // ==================================================
    //                      Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntityIoray(this.getEntityWorld());
    }


    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemStack) {
        if(itemStack == null)
            return false;
        return itemStack.getItem() == ObjectManager.getItem("ioraytreat");
    }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("cookedmeat", testStack) || ObjectLists.inItemList("cookedfish", testStack);
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    @Override
    public boolean petControlsEnabled() { return false; }
}
