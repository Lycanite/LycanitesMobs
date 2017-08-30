package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityRahovart extends EntityCreatureBase implements IMob, IGroupDemon {

    public List<EntityPlayer> playerTargets = new ArrayList<EntityPlayer>();
    public int hellfireEnergy = 0;
    public List<EntityHellfireOrb> hellfireOrbs = new ArrayList<EntityHellfireOrb>();

    // Data Manager:
    protected static final DataParameter<Integer> HELLFIRE_ENERGY = EntityDataManager.<Integer>createKey(EntityCreatureBase.class, DataSerializers.VARINT);

    // First Phase:
    public List<EntityBelph> hellfireBelphMinions = new ArrayList<EntityBelph>();

    // Second Phase:
    public List<EntityBehemoth> hellfireBehemothMinions = new ArrayList<EntityBehemoth>();
    public int hellfireWallTime = 0;
    public int hellfireWallTimeMax = 20 * 20;
    public boolean hellfireWallClockwise = false;
    public EntityHellfireBarrier hellfireWallLeft;
    public EntityHellfireBarrier hellfireWallRight;

    // Third Phase:
    public List<EntityHellfireBarrier> hellfireBarriers = new ArrayList<EntityHellfireBarrier>();
    public int hellfireBarrierHealth = 100;

    public float damageTakenThisSec = 0;
    public float healthLastTick = -1;


    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRahovart(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 2;
        this.experience = 1000;
        this.hasAttackSound = false;
        this.justAttackedTime = 40;
        
        this.setWidth = 7F;
        this.setHeight = 25F;
        this.solidCollision = true;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaWidthScale = 2F;

        // Boss:
        this.boss = true;
        this.damageMax = 25;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        //this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(60).setRange(32).setMinChaseDistance(0F).setChaseTime(-1));
        //this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityBelph.class));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityBehemoth.class));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityNetherSoul.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
        baseAttributes.put("maxHealth", 5000D);
        baseAttributes.put("movementSpeed", 0.32D);
        baseAttributes.put("knockbackResistance", 1D);
        baseAttributes.put("followRange", 40D);
        baseAttributes.put("attackDamage", 18D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.BLAZE_POWDER), 1F).setMinAmount(20).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(Items.BLAZE_ROD), 1F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.DIAMOND), 1F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.NETHER_STAR), 1F).setMinAmount(1).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("doomfirecharge")), 1F).setMinAmount(20).setMaxAmount(100));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("hellfirecharge")), 1F).setMinAmount(10).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("soulstonedemonic")), 1F).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("demonstone")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("demonstonebrick")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("demonstonetile")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("demoncrystal")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("nethersoulsigil")), 1F).setMinAmount(1).setMaxAmount(3));
	}

    // ========== Init ==========
    /** Initiates the entity setting all the values to be watched by the data manager. **/
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(EntityRahovart.HELLFIRE_ENERGY, this.hellfireEnergy);
    }

    // ========== Rendering Distance ==========
    /** Returns a larger bounding box for rendering this large entity. **/
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().expand(10, 50, 10).offset(0, 25, 0);
    }


    // ==================================================
    //                      Positions
    // ==================================================
    // ========== Arena Center ==========
    /** Sets the central arena point for this mob to use. **/
    public void setArenaCenter(BlockPos pos) {
        super.setArenaCenter(pos);
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        // Enforce Damage Limit:
        if(this.healthLastTick < 0)
            this.healthLastTick = this.getHealth();
        if(this.healthLastTick - this.getHealth() > 50)
            this.setHealth(this.healthLastTick);
        this.healthLastTick = this.getHealth();
        if(!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {
            this.damageTakenThisSec = 0;
        }

        super.onLivingUpdate();

        // Look At Target:
        if(this.hasAttackTarget() && !this.getEntityWorld().isRemote) {
            this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 30.0F, 30.0F);
        }

        // Arena Snapping:
        if(this.hasArenaCenter()) {
            BlockPos arenaPos = this.getArenaCenter();
            double arenaY = this.posY;
            if (this.getEntityWorld().isAirBlock(arenaPos))
                arenaY = arenaPos.getY();
            else if (this.getEntityWorld().isAirBlock(arenaPos.add(0, 1, 0)))
                arenaY = arenaPos.add(0, 1, 0).getY();

            if (this.posX != arenaPos.getX() || this.posY != arenaY || this.posZ != arenaPos.getZ())
                this.setPosition(arenaPos.getX(), arenaY, arenaPos.getZ());
        }

        // Sync Hellfire Energy:
        if(!this.getEntityWorld().isRemote)
            this.dataManager.set(HELLFIRE_ENERGY, this.hellfireEnergy);
        else
            this.hellfireEnergy = this.dataManager.get(HELLFIRE_ENERGY);

        // Hellfire Update:
        updateHellfireOrbs(this, this.updateTick, 5, this.hellfireEnergy, 10, this.hellfireOrbs);

        // Update Phases:
        if(!this.getEntityWorld().isRemote)
            this.updatePhases();

        // Player Targets and No Player Healing:
        if(!this.getEntityWorld().isRemote && this.updateTick % 200 == 0) {
            this.playerTargets = this.getNearbyEntities(EntityPlayer.class, null, 64);
        }
        if(!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {
            if (this.playerTargets.size() == 0)
                this.heal(50);
        }

        // Passive Attacks:
        if(!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {

            // Random Projectiles:
            for(int i = 0; i < 3; i++) {
                EntityProjectileBase projectile = new EntityHellfireball(this.getEntityWorld(), this);
                projectile.setProjectileScale(8f);
                projectile.setThrowableHeading((this.getRNG().nextFloat()) - 0.5F, this.getRNG().nextFloat(), (this.getRNG().nextFloat()) - 0.5F, 1.2F, 3.0F);
                this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                this.getEntityWorld().spawnEntity(projectile);
            }

            // Flying Player Nether Soul Attack:
            for(EntityPlayer target : this.playerTargets) {
                if(target.capabilities.isCreativeMode || target.isSpectator())
                    continue;
                this.rangedAttack(target, 1F);
                if(target.posY > this.posY + MobInfo.bossAntiFlight) {
                    for(int i = 0; i < 3; i++) {
                        EntityNetherSoul minion = new EntityNetherSoul(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        minion.setAttackTarget(target);
                        minion.setMasterTarget(null); // Clear master target so that these minions don't break phase 3 barriers.
                    }
                }
            }

            // Primary Target
            if(this.hasAttackTarget()) {
                this.rangedAttack(this.getAttackTarget(), 1F);
            }
        }
    }

    // ========== Phases Update ==========
    public void updatePhases() {

        // ===== First Phase - Hellfire Wave =====
        if(this.getBattlePhase() == 0) {
            // Clean Up:
            if(!this.hellfireBehemothMinions.isEmpty()) {
                for (EntityBehemoth minion : this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBehemothMinions = new ArrayList<EntityBehemoth>();
            }
            this.hellfireWallTime = 0;
            this.hellfireBarrierCleanup();

            // Hellfire Minion Update - Every Second:
            if(this.updateTick % 20 == 0) {
                for (EntityBelph minion : this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
                    if (minion.isDead) {
                        this.onMinionDeath(minion);
                        continue;
                    }
                    minion.hellfireEnergy += 5; // Charged after 20 secs.
                    if (minion.hellfireEnergy >= 100) {
                        this.hellfireEnergy += 20;
                        this.onMinionDeath(minion);
                        this.getEntityWorld().createExplosion(minion, minion.posX, minion.posY, minion.posZ, 1, false);
                        minion.hellfireEnergy = 0;
                        minion.setDead();
                        continue;
                    }
                }
            }

            // Hellfire Charged:
            if(this.hellfireEnergy >= 100) {
                this.hellfireEnergy = 0;
                double angle = this.getRNG().nextFloat() * 360;
                if(this.hasAttackTarget()) {
                    double deltaX = this.getAttackTarget().posX - this.posX;
                    double deltaZ = this.getAttackTarget().posZ - this.posZ;
                    angle = Math.atan2(deltaZ, deltaX) * 180 / Math.PI;
                }
                this.hellfireWaveAttack(angle);
            }

            // Every 5 Secs:
            if(this.updateTick % 100 == 0) {
                int summonAmount = this.getRNG().nextInt(4); // 0-3 Hellfire Belphs
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        EntityBelph minion = new EntityBelph(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        this.hellfireBelphMinions.add(minion);
                    }
            }
        }

        // ===== Second Phase - Hellfire Wall =====
        if(this.getBattlePhase() == 1) {
            // Clean Up:
            if(!this.hellfireBelphMinions.isEmpty()) {
                for (EntityBelph minion : this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBelphMinions = new ArrayList<EntityBelph>();
            }
            this.hellfireBarrierCleanup();

            // Hellfire Minion Update - Every Second:
            if(this.hellfireWallTime <= 0 && this.updateTick % 20 == 0) {
                for (EntityBehemoth minion : this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
                    if (minion.isDead) {
                        this.onMinionDeath(minion);
                        continue;
                    }
                    minion.hellfireEnergy += 5; // Charged after 20 secs.
                    if (minion.hellfireEnergy >= 100) {
                        this.hellfireEnergy += 20;
                        this.onMinionDeath(minion);
                        this.getEntityWorld().createExplosion(minion, minion.posX, minion.posY, minion.posZ, 1, false);
                        minion.hellfireEnergy = 0;
                        minion.setDead();
                        continue;
                    }
                }
            }

            // Hellfire Charged:
            if(this.hellfireEnergy >= 100) {
                this.hellfireEnergy = 0;
                this.hellfireWallAttack(this.rotationYaw);
            }

            // Hellfire Wall:
            if(this.hellfireWallTime > 0) {
                this.hellfireWallUpdate();
                this.hellfireWallTime--;
            }

            // Every 20 Secs:
            if(this.updateTick % 400 == 0) {
                int summonAmount = 2; // 2 Hellfire Behemoth
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        EntityBehemoth minion = new EntityBehemoth(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        this.hellfireBehemothMinions.add(minion);
                    }
            }

            // Every 10 Secs:
            if(this.updateTick % 200 == 0) {
                int summonAmount = this.getRNG().nextInt(4) - 1; // 0-2 Belphs with 50% fail chance.
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityBelph minion = new EntityBelph(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
            }
        }

        // ===== Third Phase - Hellfire Barrier =====
        if(this.getBattlePhase() >= 2) {
            // Clean Up:
            if(!this.hellfireBelphMinions.isEmpty()) {
                for (EntityBelph minion : this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBelphMinions = new ArrayList<EntityBelph>();
            }
            if(!this.hellfireBehemothMinions.isEmpty()) {
                for (EntityBehemoth minion : this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
                    minion.hellfireEnergy = 0;
                }
                this.hellfireBehemothMinions = new ArrayList<EntityBehemoth>();
            }
            this.hellfireWallTime = 0;

            // Hellfire Energy - Every Second:
            if(this.updateTick % 20 == 0) {
                if (this.hellfireEnergy < 100)
                    this.hellfireEnergy += 5;
            }

            // Hellfire Charged:
            if(this.hellfireEnergy >= 100 && this.hellfireBarriers.size() < 20) {
                this.hellfireEnergy = 0;
                this.hellfireBarrierAttack(360F * this.getRNG().nextFloat());
            }

            // Hellfire Barriers:
            if(this.hellfireBarriers.size() > 0)
                this.hellfireBarrierUpdate();

            // Every 10 Secs:
            if(this.updateTick % 200 == 0) {
                int summonAmount = this.getRNG().nextInt(2); // 0-1 Hellfire Behemoth
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                    for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                        EntityBehemoth minion = new EntityBehemoth(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                        this.hellfireBehemothMinions.add(minion);
                    }
            }

            // Every 20 Secs:
            if(this.updateTick % 400 == 0) {
                int summonAmount = this.getRNG().nextInt(4); // 0-3 Belphs
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityBelph minion = new EntityBelph(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
                summonAmount = this.getRNG().nextInt(3); // 0-2 Nether Souls
                summonAmount *= this.playerTargets.size();
                if(summonAmount > 0)
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityNetherSoul minion = new EntityNetherSoul(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
            }
        }

        if(this.hellfireWallTime <= 0)
            this.hellfireWallCleanup();
    }

    // ========== Minion Death ==========
    @Override
    public void onMinionDeath(EntityLivingBase minion) {
        if(minion instanceof EntityBelph && this.hellfireBelphMinions.contains(minion)) {
            this.hellfireBelphMinions.remove(minion);
            return;
        }
        if(minion instanceof EntityBehemoth && this.hellfireBehemothMinions.contains(minion)) {
            this.hellfireBehemothMinions.remove(minion);
            return;
        }
        if(this.hellfireBarriers.size() > 0) {
            if(minion instanceof EntityBehemoth)
                this.hellfireBarrierHealth -= 100;
            else
                this.hellfireBarrierHealth -= 50;
        }
    }


    // ==================================================
    //                  Battle Phases
    // ==================================================
    @Override
    public void updateBattlePhase() {
        double healthNormal = this.getHealth() / this.getMaxHealth();
        if(healthNormal <= 0.2D) {
            this.setBattlePhase(2);
            return;
        }
        if(healthNormal <= 0.6D) {
            this.setBattlePhase(1);
            return;
        }
        this.setBattlePhase(0);
    }


    // ==================================================
    //                     Hellfire
    // ==================================================
    public static void updateHellfireOrbs(EntityLivingBase entity, long orbTick, int hellfireOrbMax, int hellfireOrbEnergy, float orbSize, List<EntityHellfireOrb> hellfireOrbs) {
        if(!entity.getEntityWorld().isRemote)
            return;

        int hellfireChargeCount = Math.round((float)Math.min(hellfireOrbEnergy, 100) / (100F / hellfireOrbMax));
        int hellfireOrbRotationTime = 5 * 20;
        double hellfireOrbAngle = 360 * ((float)(orbTick % hellfireOrbRotationTime) / hellfireOrbRotationTime);
        double hellfireOrbAngleOffset = 360.0D / hellfireOrbMax;

        // Add Required Orbs:
        while(hellfireOrbs.size() < hellfireChargeCount) {
            EntityHellfireOrb hellfireOrb = new EntityHellfireOrb(entity.getEntityWorld(), entity);
            hellfireOrb.clientOnly = true;
            hellfireOrbs.add(hellfireOrb);
            entity.getEntityWorld().spawnEntity(hellfireOrb);
            hellfireOrb.setProjectileScale(orbSize);
        }

        // Remove Excess Orbs:
        while(hellfireOrbs.size() > hellfireChargeCount) {
            hellfireOrbs.get(hellfireOrbs.size() - 1).setDead();
            hellfireOrbs.remove(hellfireOrbs.size() - 1);
        }

        // Update Orbs:
        for(int i = 0; i < hellfireOrbs.size(); i++) {
            EntityHellfireOrb hellfireOrb = hellfireOrbs.get(i);
            double rotationRadians = Math.toRadians((hellfireOrbAngle + (hellfireOrbAngleOffset * i)) % 360);
            double x = (entity.width * 1.25D) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
            double z = (entity.width * 1.25D) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
            hellfireOrb.posX = entity.posX - x;
            hellfireOrb.posY = entity.posY + (entity.height * 0.75F);
            hellfireOrb.posZ = entity.posZ - z;
            hellfireOrb.setPosition(entity.posX - x, entity.posY + (entity.height * 0.75F), entity.posZ - z);
            hellfireOrb.projectileLife = 5;
        }
    }
	
	
	// ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityBelph.class))
    		return false;
        return super.canAttackClass(targetClass);
    }

    public boolean canAttackEntity(EntityLivingBase targetEntity) {
        if(targetEntity instanceof IGroupDemon || targetEntity instanceof IGroupFire) {
            if(targetEntity instanceof EntityCreatureTameable)
                return ((EntityCreatureTameable)targetEntity).getOwner() instanceof EntityPlayer;
            else
                return false;
        }
        return super.canAttackEntity(targetEntity);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityHellfireball projectile = new EntityHellfireball(this.getEntityWorld(), this);
        projectile.setProjectileScale(8f);
        if(!(target instanceof EntityPlayer))
            projectile.setBaseDamage(20);
    	
    	// Y Offset:
    	projectile.posY -= this.height * 0.25F;
    	
    	// Accuracy:
    	float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX + accuracy;
        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
        double d2 = target.posZ - this.posZ + accuracy;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 6.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.getEntityWorld().spawnEntity(projectile);
        super.rangedAttack(target, range);
    }

    // ========== Hellfire Wave ==========
    public void hellfireWaveAttack(double angle) {
        this.setJustAttacked();
        this.playAttackSound();
        EntityHellfireWave hellfireWave = new EntityHellfireWave(this.getEntityWorld(), this);
        hellfireWave.posY = this.posY;
        hellfireWave.rotation = angle;
        this.getEntityWorld().spawnEntity(hellfireWave);
    }

    // ========== Hellfire Wall ==========
    public void hellfireWallAttack(double angle) {
        this.playAttackSound();
        this.setJustAttacked();

        this.hellfireWallTime = this.hellfireWallTimeMax;
        this.hellfireWallClockwise = this.getRNG().nextBoolean();
    }

    public void hellfireWallUpdate() {
        this.setJustAttacked();

        double hellfireWallNormal = (double)this.hellfireWallTime / this.hellfireWallTimeMax;
        double hellfireWallAngle = 360;
        if(this.hellfireWallClockwise)
            hellfireWallAngle = -360;

        // Left (Positive) Wall:
        if(this.hellfireWallLeft == null) {
            this.hellfireWallLeft = new EntityHellfireBarrier(this.getEntityWorld(), this);
            this.getEntityWorld().spawnEntity(this.hellfireWallLeft);
        }
        this.hellfireWallLeft.time = 0;
        this.hellfireWallLeft.posX = this.posX;
        this.hellfireWallLeft.posY = this.posY;
        this.hellfireWallLeft.posZ = this.posZ;
        this.hellfireWallLeft.rotation = hellfireWallNormal * hellfireWallAngle;

        // Right (Negative) Wall:
        if(this.hellfireWallRight == null) {
            this.hellfireWallRight = new EntityHellfireBarrier(this.getEntityWorld(), this);
            this.getEntityWorld().spawnEntity(this.hellfireWallRight);
        }
        this.hellfireWallRight.time = 0;
        this.hellfireWallRight.posX = this.posX;
        this.hellfireWallRight.posY = this.posY;
        this.hellfireWallRight.posZ = this.posZ;
        this.hellfireWallRight.rotation = 180 + (hellfireWallNormal * hellfireWallAngle);
    }

    public void hellfireWallCleanup() {
        if(this.hellfireWallLeft != null) {
            this.hellfireWallLeft.setDead();
            this.hellfireWallLeft = null;
        }
        if(this.hellfireWallRight != null) {
            this.hellfireWallRight.setDead();
            this.hellfireWallRight = null;
        }
    }

    // ========== Hellfire Barrier ==========
    public void hellfireBarrierAttack(double angle) {
        this.setJustAttacked();
        this.playAttackSound();

        EntityHellfireBarrier hellfireBarrier = new EntityHellfireBarrier(this.getEntityWorld(), this);
        this.getEntityWorld().spawnEntity(hellfireBarrier);
        hellfireBarrier.time = 0;
        hellfireBarrier.posX = this.posX;
        hellfireBarrier.posY = this.posY;
        hellfireBarrier.posZ = this.posZ;
        hellfireBarrier.rotation = angle;
        this.hellfireBarriers.add(hellfireBarrier);
    }

    public void hellfireBarrierUpdate() {
        if(this.hellfireBarrierHealth <= 0) {
            this.hellfireBarrierHealth = 100;
            if(this.hellfireBarriers.size() > 0) {
                EntityHellfireBarrier hellfireBarrier = this.hellfireBarriers.get(this.hellfireBarriers.size() - 1);
                hellfireBarrier.setDead();
                this.hellfireBarriers.remove(this.hellfireBarriers.size() - 1);
            }
        }
        for(EntityHellfireBarrier hellfireBarrier : this.hellfireBarriers) {
            hellfireBarrier.time = 0;
            hellfireBarrier.posX = this.posX;
            hellfireBarrier.posY = this.posY;
            hellfireBarrier.posZ = this.posZ;
        }
    }

    public void hellfireBarrierCleanup() {
        if(this.getEntityWorld().isRemote || this.hellfireBarriers.size() < 1)
            return;
        for(EntityHellfireBarrier hellfireBarrier : this.hellfireBarriers) {
            hellfireBarrier.setDead();
        }
        this.hellfireBarriers = new ArrayList<EntityHellfireBarrier>();
        this.hellfireBarrierHealth = 100;
    }


    // ==================================================
    //                     Movement
    // ==================================================
    // ========== Can Be Pushed ==========
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        if(this.damageTakenThisSec >= 50)
            return true;
        return super.isEntityInvulnerable(source);
    }

    @Override
    public void onDamage(DamageSource damageSrc, float damage) {
        this.damageTakenThisSec += damage;
        super.onDamage(damageSrc, damage);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.WITHER) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public boolean isDamageEntityApplicable(Entity entity) {
        if(entity instanceof EntityPigZombie) {
            entity.setDead();
            return false;
        }
        if(entity instanceof EntityIronGolem) {
            entity.setDead();
            return false;
        }
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!player.capabilities.isCreativeMode && player.posY > this.posY + MobInfo.bossAntiFlight) {
                return false;
            }
        }
        return super.isDamageEntityApplicable(entity);
    }


    // ==================================================
    //                    Taking Damage
    // ==================================================
    // ========== Attacked From ==========
    /** Called when this entity has been attacked, uses a DamageSource and damage value. **/
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damage) {
        if(this.playerTargets != null && damageSrc.getEntity() != null && damageSrc.getEntity() instanceof EntityPlayer) {
            if (!this.playerTargets.contains(damageSrc.getEntity()))
                this.playerTargets.add((EntityPlayer)damageSrc.getEntity());
        }
        return super.attackEntityFrom(damageSrc, damage);
    }


    // ==================================================
    //                       NBT
    // ==================================================
    // ========== Read ===========
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
        super.readEntityFromNBT(nbtTagCompound);
        if(nbtTagCompound.hasKey("HellfireEnergy")) {
            this.hellfireEnergy = nbtTagCompound.getInteger("HellfireEnergy");
        }
        if(nbtTagCompound.hasKey("HellfireWallTime")) {
            this.hellfireWallTime = nbtTagCompound.getInteger("HellfireWallTime");
        }
        if(nbtTagCompound.hasKey("BelphIDs")) {
            NBTTagList belphIDs = nbtTagCompound.getTagList("BelphIDs", 10);
            for(int i = 0; i < belphIDs.tagCount(); i++) {
                NBTTagCompound belphID = belphIDs.getCompoundTagAt(i);
                if(belphID.hasKey("ID")) {
                    Entity entity = this.getEntityWorld().getEntityByID(belphID.getInteger("ID"));
                    if(entity != null && entity instanceof EntityBelph)
                        this.hellfireBelphMinions.add((EntityBelph)entity);
                }
            }
        }
        if(nbtTagCompound.hasKey("BehemothIDs")) {
            NBTTagList behemothIDs = nbtTagCompound.getTagList("BehemothIDs", 10);
            for(int i = 0; i < behemothIDs.tagCount(); i++) {
                NBTTagCompound behemothID = behemothIDs.getCompoundTagAt(i);
                if(behemothID.hasKey("ID")) {
                    Entity entity = this.getEntityWorld().getEntityByID(behemothID.getInteger("ID"));
                    if(entity != null && entity instanceof EntityBehemoth)
                        this.hellfireBehemothMinions.add((EntityBehemoth)entity);
                }
            }
        }
    }

    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("HellfireEnergy", this.hellfireEnergy);
        nbtTagCompound.setInteger("HellfireWallTime", this.hellfireWallTime);
        if(this.getBattlePhase() == 0) {
            NBTTagList belphIDs = new NBTTagList();
            for(EntityBelph entityBelph : this.hellfireBelphMinions) {
                NBTTagCompound belphID = new NBTTagCompound();
                belphID.setInteger("ID", entityBelph.getEntityId());
                belphIDs.appendTag(belphID);
            }
            nbtTagCompound.setTag("BelphIDs", belphIDs);
        }
        if(this.getBattlePhase() == 1) {
            NBTTagList behemothIDs = new NBTTagList();
            for(EntityBehemoth entityBehemoth : this.hellfireBehemothMinions) {
                NBTTagCompound behemothID = new NBTTagCompound();
                behemothID.setInteger("ID", entityBehemoth.getEntityId());
                behemothIDs.appendTag(behemothID);
            }
            nbtTagCompound.setTag("BehemothIDs", behemothIDs);
        }
    }


    // ==================================================
    //                       Sounds
    // ==================================================
    // ========== Step ==========
    @Override
    protected void playStepSound(BlockPos pos, Block block) {
        if(this.hasArenaCenter())
            return;
        super.playStepSound(pos, block);
    }


    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness(float par1) {
        return 1.0F;
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
