package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupDemon;
import lycanite.lycanitesmobs.api.IGroupFire;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRahovart extends EntityCreatureBase implements IMob, IBossDisplayData, IGroupDemon {

    public List<EntityPlayer> playerTargets = new ArrayList<EntityPlayer>();
    public int hellfireEnergy = 0;
    public List<EntityHellfireOrb> hellfireOrbs = new ArrayList<EntityHellfireOrb>();

    // First Phase:
    public List<EntityBelph> hellfireBelphMinions = new ArrayList<EntityBelph>();
    public Map<EntityBelph, Integer> hellfireBelphEnergies = new HashMap<EntityBelph, Integer>();
    public Map<EntityBelph, List<EntityHellfireOrb>> hellfireBelphOrbs = new HashMap<EntityBelph, List<EntityHellfireOrb>>();

    // Second Phase:
    public List<EntityBehemoth> hellfireBehemothMinions = new ArrayList<EntityBehemoth>();
    public Map<EntityBehemoth, Integer> hellfireBehemothEnergies = new HashMap<EntityBehemoth, Integer>();
    public Map<EntityBehemoth, List<EntityHellfireOrb>> hellfireBehemothOrbs = new HashMap<EntityBehemoth, List<EntityHellfireOrb>>();
    public int hellfireWallTime = 0;
    public int hellfireWallTimeMax = 10 * 20;
    public boolean hellfireWallClockwise = false;

    // Third Phase:
    //public List<EntityHellfireBarrier> hellfireBarriers = new ArrayList<EntityHellfireBarrier>();


    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRahovart(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 2;
        this.experience = 1000;
        this.hasAttackSound = false;
        
        this.setWidth = 10F;
        this.setHeight = 50F;
        this.setupMob();

        // Boss:
        this.boss = true;
        this.forceBossHealthBar = true;
        this.damageMax = 100;
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(60).setRange(16.0F).setMinChaseDistance(8.0F).setChaseTime(-1));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityBelph.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10000D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 40D);
		baseAttributes.put("attackDamage", 18D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.blaze_powder), 0.5F).setMinAmount(20).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(Items.blaze_rod), 0.5F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.diamond), 0.75F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.nether_star), 0.5F).setMinAmount(5).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("doomfirecharge")), 0.5F).setMinAmount(20).setMaxAmount(100));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("hellfirecharge")), 0.5F).setMinAmount(10).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demonicsoulstone")), 1F).setMinAmount(5).setMaxAmount(5));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Hellfire Update:
        this.updateHellfireOrbs(this, this.updateTick, 5, this.hellfireEnergy, 10, this.hellfireOrbs);

        if(this.updateTick % 100 == 0) {
            this.hellfireWaveAttack(this.rotationYaw);
        }

        // Update Phases:
        if(!this.worldObj.isRemote)
            this.updatePhases();

        // Random Projectiles:
        if(!this.worldObj.isRemote && this.updateTick % 40 == 0) {
            EntityProjectileBase projectile = new EntityHellfireball(this.worldObj, this);
            projectile.setProjectileScale(8f);
            projectile.setThrowableHeading((this.getRNG().nextFloat()) - 0.5F, this.getRNG().nextFloat(), (this.getRNG().nextFloat()) - 0.5F, 1.2F, 6.0F);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.worldObj.spawnEntityInWorld(projectile);
        }

        // Hellfire Trail:
        if(!this.worldObj.isRemote && this.updateTick % 5 == 0 && this.isMoving()) {
            int trailHeight = 5;
            int trailWidth = 1;
            if(this.getSubspeciesIndex() >= 3)
                trailWidth = 10;
            for(int y = 0; y < trailHeight; y++) {
                Block block = this.worldObj.getBlock((int)this.posX, (int)this.posY + y, (int)this.posZ);
                if(block == Blocks.air || block == Blocks.fire || block == Blocks.snow_layer || block == Blocks.tallgrass || block == ObjectManager.getBlock("frostfire")) {
                    if(trailWidth == 1)
                        this.worldObj.setBlock((int) this.posX, (int) this.posY + y, (int) this.posZ, ObjectManager.getBlock("hellfire"));
                    else
                        for(int x = -(trailWidth / 2); x < (trailWidth / 2) + 1; x++) {
                            for(int z = -(trailWidth / 2); z < (trailWidth / 2) + 1; z++) {
                                this.worldObj.setBlock((int) this.posX + x, (int) this.posY + y, (int) this.posZ + z, ObjectManager.getBlock("hellfire"));
                            }
                        }
                }
            }
        }
    }

    // ========== Phases Update ==========
    public void updatePhases() {
        // ===== First Phase - Hellfire Wave =====
        if(this.getBattlePhase() == 0) {
            // Clean Up:
            if(!this.hellfireBehemothMinions.isEmpty())
                this.hellfireBehemothMinions = new ArrayList<EntityBehemoth>();
            this.hellfireWallTime = 0;

            // Hellfire Minion Update - Every Second:
            if(this.updateTick % 20 == 0) {
                for (EntityBelph minion : this.hellfireBelphMinions.toArray(new EntityBelph[this.hellfireBelphMinions.size()])) {
                    if (minion.isDead) {
                        this.onMinionDeath(minion);
                        continue;
                    }
                    if (!this.hellfireBelphEnergies.containsKey(minion))
                        this.hellfireBelphEnergies.put(minion, 0);
                    int minionEnergy = this.hellfireBelphEnergies.get(minion);
                    if (!this.hellfireBelphOrbs.containsKey(minion))
                        this.hellfireBelphOrbs.put(minion, new ArrayList<EntityHellfireOrb>());
                    minionEnergy += 5;
                    if (minionEnergy >= 100) {
                        this.hellfireEnergy += 10;
                        this.onMinionDeath(minion);
                        this.worldObj.createExplosion(minion, minion.posX, minion.posY, minion.posZ, 1, false);
                        minion.setDead();
                        continue;
                    } else
                        this.hellfireBelphEnergies.put(minion, minionEnergy);
                }
            }

            // Hellfire Charged:
            if(this.hellfireEnergy >= 100) {
                this.hellfireEnergy = 0;
                this.hellfireWaveAttack(this.rotationYaw);
            }

            // Every 10 Secs:
            if(this.updateTick % 200 == 0) {
                int summonAmount = this.getRNG().nextInt(6); // 0-5 Hellfire Belphs
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityBelph minion = new EntityBelph(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                    this.hellfireBelphMinions.add(minion);
                }
            }
        }

        // ===== Second Phase - Hellfire Wall =====
        if(this.getBattlePhase() == 1) {
            // Clean Up:
            if(!this.hellfireBelphMinions.isEmpty())
                this.hellfireBelphMinions = new ArrayList<EntityBelph>();

            // Hellfire Minion Update - Every Second:
            if(this.updateTick % 20 == 0) {
                for (EntityBehemoth minion : this.hellfireBehemothMinions.toArray(new EntityBehemoth[this.hellfireBehemothMinions.size()])) {
                    if (minion.isDead) {
                        this.onMinionDeath(minion);
                        continue;
                    }
                    if (!this.hellfireBehemothEnergies.containsKey(minion))
                        this.hellfireBehemothEnergies.put(minion, 0);
                    int minionEnergy = this.hellfireBehemothEnergies.get(minion);
                    if (!this.hellfireBehemothOrbs.containsKey(minion))
                        this.hellfireBehemothOrbs.put(minion, new ArrayList<EntityHellfireOrb>());
                    minionEnergy += 5;
                    if (minionEnergy >= 100) {
                        this.hellfireEnergy += 50;
                        this.onMinionDeath(minion);
                        this.worldObj.createExplosion(minion, minion.posX, minion.posY, minion.posZ, 1, false);
                        minion.setDead();
                        continue;
                    }
                    this.hellfireBehemothEnergies.put(minion, minionEnergy);
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

            // Every 10 Secs:
            if(this.updateTick % 200 == 0) {
                int summonAmount = 2; // 2 Hellfire Behemoth
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityBehemoth minion = new EntityBehemoth(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                    this.hellfireBehemothMinions.add(minion);
                }
            }

            // Every 20 Secs:
            if(this.updateTick % 400 == 0) {
                int summonAmount = this.getRNG().nextInt(4); // 0-3 Belphs
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityBelph minion = new EntityBelph(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
            }
        }

        // ===== Third Phase - Hellfire Barrier =====
        if(this.getBattlePhase() >= 2) {
            // Clean Up:
            if(!this.hellfireBelphMinions.isEmpty())
                this.hellfireBelphMinions = new ArrayList<EntityBelph>();
            if(!this.hellfireBehemothMinions.isEmpty())
                this.hellfireBehemothMinions = new ArrayList<EntityBehemoth>();
            this.hellfireWallTime = 0;

            // Hellfire Energy - Every Second:
            if(this.updateTick % 20 == 0) {
                if (this.hellfireEnergy < 100)
                    this.hellfireEnergy += 10;
            }

            // Hellfire Charged:
            if(this.hellfireEnergy >= 100) {
                this.hellfireEnergy = 0;
                this.hellfireBarrierAttack(this.rotationYaw);
            }

            // Hellfire Barriers:
            //if(this.hellfireBarriers.size() > 0)
            //this.hellfireBarrierUpdate();

            // Every 10 Secs:
            if(this.updateTick % 200 == 0) {
                if(this.getRNG().nextDouble() <= 0.25D) { // 25% Behemoth Chance
                    EntityBehemoth minion = new EntityBehemoth(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
            }

            // Every 20 Secs:
            if(this.updateTick % 400 == 0) {
                int summonAmount = this.getRNG().nextInt(4); // 0-3 Belphs
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityBelph minion = new EntityBelph(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
                summonAmount = this.getRNG().nextInt(3); // 0-2 Nether Souls
                for(int summonCount = 0; summonCount <= summonAmount; summonCount++) {
                    EntityNetherSoul minion = new EntityNetherSoul(this.worldObj);
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                }
            }
        }
    }

    // ========== Minion Update ==========
    @Override
    public void onMinionUpdate(EntityLivingBase minionEntity, long tick) {
        if(minionEntity instanceof EntityBelph) {
            EntityBelph minion = (EntityBelph)minionEntity;
            if(!this.hellfireBelphEnergies.containsKey(minion))
                this.hellfireBelphEnergies.put(minion, 0);
            if(!this.hellfireBelphOrbs.containsKey(minion))
                this.hellfireBelphOrbs.put(minion, new ArrayList<EntityHellfireOrb>());
            this.updateHellfireOrbs(minion, tick, 3, this.hellfireBelphEnergies.get(minion), 0.5F, this.hellfireBelphOrbs.get(minion));
        }
        if(minionEntity instanceof EntityBehemoth) {
            EntityBehemoth minion = (EntityBehemoth)minionEntity;
            if(!this.hellfireBehemothEnergies.containsKey(minion))
                this.hellfireBehemothEnergies.put(minion, 0);
            if(!this.hellfireBehemothOrbs.containsKey(minion))
                this.hellfireBehemothOrbs.put(minion, new ArrayList<EntityHellfireOrb>());
            this.updateHellfireOrbs(minion, tick, 3, this.hellfireBehemothEnergies.get(minion), 1F, this.hellfireBehemothOrbs.get(minion));
        }
    }

    // ========== Minion Death ==========
    @Override
    public void onMinionDeath(EntityLivingBase minion) {
        if(minion instanceof EntityBelph && this.hellfireBelphMinions.contains(minion)) {
            this.hellfireBelphMinions.remove(minion);
            this.hellfireBelphEnergies.remove(minion);
            this.hellfireBelphOrbs.remove(minion);
            return;
        }
        if(minion instanceof EntityBehemoth && this.hellfireBehemothMinions.contains(minion)) {
            this.hellfireBehemothMinions.remove(minion);
            this.hellfireBehemothEnergies.remove(minion);
            this.hellfireBehemothOrbs.remove(minion);
            return;
        }
    }


    // ==================================================
    //                  Battle Phases
    // ==================================================
    @Override
    public void updateBattlePhase() {
        double healthNormal = this.getHealth() / this.getMaxHealth();
        if(healthNormal <= 0.2D) {
            this.battlePhase = 2;
            return;
        }
        if(healthNormal <= 0.6D) {
            this.battlePhase = 1;
            return;
        }
        this.battlePhase = 0;
    }


    // ==================================================
    //                     Hellfire
    // ==================================================
    public void updateHellfireOrbs(EntityLivingBase entity, long orbTick, int hellfireOrbMax, int hellfireOrbEnergy, float orbSize, List<EntityHellfireOrb> hellfireOrbs) {
        if(entity.worldObj.isRemote)
            return;

        int hellfireChargeCount = Math.round((float)Math.min(hellfireOrbEnergy, 100) / (100F / hellfireOrbMax));
        int hellfireOrbRotationTime = 5 * 20;
        double hellfireOrbAngle = 360 * ((float)(orbTick % hellfireOrbRotationTime) / hellfireOrbRotationTime);
        double hellfireOrbAngleOffset = 360 / hellfireOrbMax;

        // Add Required Orbs:
        while(hellfireOrbs.size() < hellfireChargeCount) {
            EntityHellfireOrb hellfireOrb = new EntityHellfireOrb(entity.worldObj, entity);
            hellfireOrbs.add(hellfireOrb);
            entity.worldObj.spawnEntityInWorld(hellfireOrb);
            hellfireOrb.setProjectileSize(orbSize, orbSize);
            hellfireOrb.setProjectileScale(orbSize * 2);
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
            double x = (entity.width * 2) * Math.cos(rotationRadians) - Math.sin(rotationRadians);
            double z = (entity.width * 2) * Math.sin(rotationRadians) + Math.cos(rotationRadians);
            hellfireOrb.posX = entity.posX + x;
            hellfireOrb.posY = entity.posY + (entity.height * 0.75F);
            hellfireOrb.posZ = entity.posZ + z;
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
    	EntityHellfireball projectile = new EntityHellfireball(this.worldObj, this);
        projectile.setProjectileScale(8f);
    	
    	// Y Offset:
    	projectile.posY -= this.height / 2;
    	
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

    // ========== Hellfire Wave ==========
    public void hellfireWaveAttack(double angle) {
        EntityHellfireWave hellfireWave = new EntityHellfireWave(this.worldObj, this);
        hellfireWave.posY = this.posY;
        hellfireWave.rotation = angle;
        this.worldObj.spawnEntityInWorld(hellfireWave);
    }

    // ========== Hellfire Wall ==========
    public void hellfireWallAttack(double angle) {
        this.hellfireWallTime = this.hellfireWallTimeMax;
        this.hellfireWallClockwise = this.getRNG().nextBoolean();
    }

    public void hellfireWallUpdate() {
        double hellfireWallNormal = this.hellfireWallTime / this.hellfireWallTimeMax;
        if(this.hellfireWallClockwise)
            hellfireWallNormal = 1 - hellfireWallNormal;
        // TODO: Hellfire Wall
    }

    // ========== Hellfire Barrier ==========
    public void hellfireBarrierAttack(double angle) {
        // TODO: Hellfire Barrier
    }

    public void hellfireBarrierUpdate() {
        // TODO: Hellfire Barrier
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.wither.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                       NBT
    // ==================================================
    // ========== Read ===========
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
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
                    Entity entity = this.worldObj.getEntityByID(belphID.getInteger("ID"));
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
                    Entity entity = this.worldObj.getEntityByID(behemothID.getInteger("ID"));
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
        nbtTagCompound.setInteger("HellfireEnergy", this.hellfireEnergy);
        nbtTagCompound.setInteger("HellfireWallTime", this.hellfireWallTime);
        if(this.battlePhase == 0) {
            NBTTagList belphIDs = new NBTTagList();
            for(EntityBelph entityBelph : this.hellfireBelphMinions) {
                NBTTagCompound belphID = new NBTTagCompound();
                belphID.setInteger("ID", entityBelph.getEntityId());
                belphIDs.appendTag(belphID);
            }
            nbtTagCompound.setTag("BelphIDs", belphIDs);
        }
        if(this.battlePhase == 1) {
            NBTTagList behemothIDs = new NBTTagList();
            for(EntityBehemoth entityBehemoth : this.hellfireBehemothMinions) {
                NBTTagCompound behemothID = new NBTTagCompound();
                behemothID.setInteger("ID", entityBehemoth.getEntityId());
                behemothIDs.appendTag(behemothID);
            }
            nbtTagCompound.setTag("BehemothIDs", behemothIDs);
        }
    }
}
