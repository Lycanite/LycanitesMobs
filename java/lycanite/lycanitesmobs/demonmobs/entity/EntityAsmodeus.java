package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupDemon;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.entity.ai.*;
import lycanite.lycanitesmobs.core.entity.navigate.ArenaNode;
import lycanite.lycanitesmobs.core.entity.navigate.ArenaNodeNetwork;
import lycanite.lycanitesmobs.core.entity.navigate.ArenaNodeNetworkGrid;
import lycanite.lycanitesmobs.core.info.DropRate;
import lycanite.lycanitesmobs.core.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityAsmodeus extends EntityCreatureBase implements IMob, IGroupDemon {

    // Data Manager:
    protected static final DataParameter<Byte> ANIMATION_STATES = EntityDataManager.<Byte>createKey(EntityCreatureBase.class, DataSerializers.BYTE);
    public enum ANIMATION_STATES_ID {
        SNAP_TO_ARENA((byte)1), COOLDOWN((byte)2);
        public final byte id;
        ANIMATION_STATES_ID(byte value) { this.id = value; }
        public byte getValue() { return id; }
    }

    // AI:
    public EntityAIAttackRanged aiRangedAttack;

    public List<EntityPlayer> playerTargets = new ArrayList<EntityPlayer>();
    public boolean firstPlayerTargetCheck = false;
    public List<EntityTrite> triteMinions = new ArrayList<EntityTrite>();
    public List<EntityAstaroth> astarothMinions = new ArrayList<EntityAstaroth>();
    public List<EntityCacodemon> cacodemonMinions = new ArrayList<EntityCacodemon>();

    // First Phase:
    public int devilstarStreamTime = 0;
    public int devilstarStreamTimeMax = 5 * 20;
    public int devilstarStreamCharge = 20 * 20;
    public int devilstarStreamChargeMax = 20 * 20;

    // Second Phase:
    public int hellshieldAstarothRespawnTime = 0;
    public int hellshieldAstarothRespawnTimeMax = 30;

    // Third Phase:
    public int rebuildAstarothRespawnTime = 0;
    public int rebuildAstarothRespawnTimeMax = 40;

    // Boss Health:
    public float damageTakenThisSec = 0;
    public float healthLastTick = -1;

    // Arena Movement:
    public ArenaNodeNetwork arenaNodeNetwork;
    public ArenaNode currentArenaNode;
    public int arenaNodeChangeCooldown = 0;
    public int arenaNodeChangeCooldownMax = 200;
    public int arenaJumpingTime = 0;
    public int arenaJumpingTimeMax = 60;
    protected double jumpHeight = 6D;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAsmodeus(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 2;
        this.experience = 1000;
        this.hasAttackSound = false;
        this.justAttackedTime = 30;
        this.hasJumpSound = true;
        
        this.setWidth = 20F;
        this.setHeight = 21F;
        this.solidCollision = true;
        this.entityCollisionReduction = 1.0F;
        this.setupMob();
        this.hitAreaScale = 2F;

        // Boss:
        this.boss = true;
        this.damageMax = 25;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.aiRangedAttack = new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(5).setStaminaTime(200).setStaminaDrainRate(3).setRange(90.0F).setChaseTime(0).setCheckSight(false);
        this.tasks.addTask(2, this.aiRangedAttack);
        //this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D));
        //this.tasks.addTask(7, new EntityAIStayByHome(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityTrite.class));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityAstaroth.class));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityCacodemon.class));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityNetherSoul.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }

    // ========== Init ==========
    /** Initiates the entity setting all the values to be watched by the data manager. **/
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ANIMATION_STATES, (byte) 0);
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5000D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 100D);
		baseAttributes.put("attackDamage", 18D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.REDSTONE), 1F).setMinAmount(20).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(Items.IRON_INGOT), 1F).setMinAmount(20).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(Items.GOLD_INGOT), 1F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.DIAMOND), 1F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.NETHER_STAR), 1F).setMinAmount(1).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("devilstarcharge")), 1F).setMinAmount(10).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demoniclightningcharge")), 1F).setMinAmount(10).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("soulstonedemonic")), 1F).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("demonstone")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("demonstonebrick")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("demonstonetile")), 1F).setMinAmount(64).setMaxAmount(128));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("demoncrystal")), 1F).setMinAmount(64).setMaxAmount(128));
	}

    // ========== Rendering Distance ==========
    /** Returns a larger bounding box for rendering this large entity. **/
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().expand(10, 50, 10).offset(0, 25, 0);
    }

    // ========== Persistence ==========
    @Override
    public boolean isPersistant() {
        if(this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityAsmodeus)
            return true;
        return super.isPersistant();
    }


    // ==================================================
    //                      Positions
    // ==================================================
    // ========== Arena Center ==========
    /** Sets the central arena point for this mob to use. **/
    public void setArenaCenter(BlockPos pos) {
        super.setArenaCenter(pos);
        this.arenaNodeNetwork = new ArenaNodeNetworkGrid(this.getEntityWorld(), pos, 3, 1, 3, 60);
        this.currentArenaNode = this.arenaNodeNetwork.getClosestNode(this.getPosition());
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        // Enforce Damage Limit:
        if (this.healthLastTick < 0)
            this.healthLastTick = this.getHealth();
        if (this.healthLastTick - this.getHealth() > 50)
            this.setHealth(this.healthLastTick);
        this.healthLastTick = this.getHealth();
        if (!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {
            this.damageTakenThisSec = 0;
        }

        super.onLivingUpdate();

        // Player Targets and No Player Healing:
        if(!this.getEntityWorld().isRemote) {
            if (this.updateTick % 200 == 0 || !this.firstPlayerTargetCheck) {
                this.firstPlayerTargetCheck = true;
                this.playerTargets = this.getNearbyEntities(EntityPlayer.class, null, 64);
            }
            if (this.updateTick % 20 == 0) {
                if (this.playerTargets.isEmpty()) {
                    this.heal(50);
                }
            }
        }

        // Update Phases:
        if(!this.getEntityWorld().isRemote) {
            this.updatePhases();
            this.updateCurrentArenaNode();
            this.updateArenaMovement();
        }

        // Passive Attacks:
        if(!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {
            // Flying Player Nether Soul Attack:
            for(EntityPlayer target : this.playerTargets) {
                if(target.capabilities.isCreativeMode || target.isSpectator())
                    continue;
                if(target.posY > this.posY + this.height + 5) {
                    for(int i = 0; i < 3; i++) {
                        EntityNetherSoul minion = new EntityNetherSoul(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 5);
                    }
                }
            }
        }

        // Clean Minion Lists:
        if(this.getEntityWorld().isRemote && this.updateTick % 200 == 0) {
            if(!this.triteMinions.isEmpty()) {
                for (EntityTrite minion : this.triteMinions.toArray(new EntityTrite[this.triteMinions.size()])) {
                    if(minion == null || !minion.isEntityAlive() || minion.getMasterTarget() != this)
                        this.triteMinions.remove(minion);
                }
            }
            if(!this.astarothMinions.isEmpty()) {
                for (EntityAstaroth minion : this.astarothMinions.toArray(new EntityAstaroth[this.astarothMinions.size()])) {
                    if(minion == null || !minion.isEntityAlive() || minion.getMasterTarget() != this)
                        this.astarothMinions.remove(minion);
                }
            }
            if(!this.cacodemonMinions.isEmpty()) {
                for (EntityCacodemon minion : this.cacodemonMinions.toArray(new EntityCacodemon[this.cacodemonMinions.size()])) {
                    if(minion == null || !minion.isEntityAlive() || minion.getMasterTarget() != this)
                        this.cacodemonMinions.remove(minion);
                }
            }
        }

        // Update Animation States:
        if(!this.getEntityWorld().isRemote) {
            byte animationState = 0;
            if(this.aiRangedAttack != null && this.aiRangedAttack.attackOnCooldown)
                animationState += ANIMATION_STATES_ID.COOLDOWN.id;
            this.dataManager.set(ANIMATION_STATES, animationState);
        }

        // Client Attack Cooldown Particles:
        if(this.getEntityWorld().isRemote && (this.dataManager.get(ANIMATION_STATES) & ANIMATION_STATES_ID.COOLDOWN.id) > 0) {
            BlockPos particlePos = this.getFacingPosition(this, 13, this.getRotationYawHead() - this.rotationYaw);
            for(int i = 0; i < 4; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, particlePos.getX() + (this.rand.nextDouble() - 0.5D) * 2, particlePos.getY() + (this.height * 0.2D) + this.rand.nextDouble() * 2, particlePos.getZ() + (this.rand.nextDouble() - 0.5D) * 2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    // ========== Current Arena Node Update ==========
    public void updateCurrentArenaNode() {
        if(!this.hasArenaCenter())
            return;
        if(this.arenaNodeChangeCooldown > 0) {
            this.arenaNodeChangeCooldown--;
            return;
        }

        // Return to center with no target.
        if(this.getAttackTarget() == null || !this.getAttackTarget().isEntityAlive()) {
            this.setCurrentArenaNode(this.arenaNodeNetwork.centralNode);
            return;
        }

        if(this.currentArenaNode != null)
            this.setCurrentArenaNode(this.currentArenaNode.getClosestAdjacentNode(this.getAttackTarget().getPosition()));
        else
            this.setCurrentArenaNode(this.arenaNodeNetwork.getClosestNode(this.getAttackTarget().getPosition()));
    }

    // ========== Set Current Arena Node ==========
    public void setCurrentArenaNode(ArenaNode arenaNode) {
        this.arenaNodeChangeCooldown = this.arenaNodeChangeCooldownMax;
        if(this.currentArenaNode == arenaNode)
            return;
        this.currentArenaNode = arenaNode;

        // Update home position jumping time on node change to new node.
        if(this.currentArenaNode != null && this.currentArenaNode.pos != null) {
            this.arenaJumpingTime = this.arenaJumpingTimeMax;
            this.leap(200, this.jumpHeight, this.currentArenaNode.pos); // First leap for jump height.
            if(this.hasJumpSound)
                this.playJumpSound();
        }
    }

    // ========== Arena Movement Update ==========
    public void updateArenaMovement() {
        this.noClip = false;
        if(this.currentArenaNode == null || this.currentArenaNode.pos == null) {
            return;
        }

        // Jumping:
        if(this.arenaJumpingTime > 0) {
            this.arenaJumpingTime--;
            if(this.updateTick % 4 == 0) {
                double dropForce = -0.5D;
                this.noClip = this.posY > this.currentArenaNode.pos.getY() + 8;
                if(this.posY < this.currentArenaNode.pos.getY()) {
                    this.setPosition(this.posX, this.currentArenaNode.pos.getY(), this.posZ);
                    dropForce = 0;
                }
                this.leap(200, dropForce, this.currentArenaNode.pos); // Leap for XZ movement and negative height for increased weight on update.
            }
            if(this.arenaJumpingTime == 0)
                this.playStepSound(this.currentArenaNode.pos.down(), this.getEntityWorld().getBlockState(this.currentArenaNode.pos.down()).getBlock());
            return;
        }

        // Snap To Node:
        BlockPos arenaPos = this.currentArenaNode.pos;
        double arenaY = this.posY;
        if (this.getEntityWorld().isAirBlock(arenaPos))
            arenaY = arenaPos.getY();
        else if (this.getEntityWorld().isAirBlock(arenaPos.add(0, 1, 0)))
            arenaY = arenaPos.add(0, 1, 0).getY();

        if(this.posX != arenaPos.getX() || this.posY != arenaY || this.posZ != arenaPos.getZ())
            this.setPosition(arenaPos.getX(), arenaY, arenaPos.getZ());
    }


    // ========== Phases Update ==========
    public void updatePhases() {
        int playerCount = Math.max(this.playerTargets.size(), 1);

        // ===== First Phase - Devilstar Stream =====
        if(this.getBattlePhase() == 0) {
            // Devilstar Stream - Fire:
            if(this.devilstarStreamTime > 0) {
                this.devilstarStreamTime--;
                if(this.updateTick % 10 == 0) {
                    for (float angle = 0; angle < 360F; angle += 10F) {
                        this.attackDevilstar(angle);
                    }
                }
            }
            // Devilstar Stream - Recharge:
            else if(this.devilstarStreamCharge > 0) {
                this.devilstarStreamCharge--;
            }
            // Devilstar Stream - Charged
            else {
                this.devilstarStreamCharge = this.devilstarStreamChargeMax;
                this.devilstarStreamTime = this.devilstarStreamTimeMax;
            }

            // Summon Trites:
            if(this.triteMinions.size() < playerCount * 3 && this.updateTick % 10 * 20 == 0) {
                for (int i = 0; i < 3 * playerCount; i++) {
                    EntityTrite minion = new EntityTrite(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 10);
                    this.triteMinions.add(minion);
                }
            }
        }


        // ===== Second Phase - Hellshield =====
        else if(this.getBattlePhase() == 1 && this.updateTick % 20 == 0) {
            // Summon Astaroth:
            if(this.astarothMinions.isEmpty() && this.hellshieldAstarothRespawnTime-- <= 0) {
                for (int i = 0; i < 2 * playerCount; i++) {
                    EntityAstaroth minion = new EntityAstaroth(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 0);
                    minion.setSizeScale(2.5D);
                    this.astarothMinions.add(minion);
                }
                this.hellshieldAstarothRespawnTime = this.hellshieldAstarothRespawnTimeMax;
            }
        }


        // ===== Third Phase - Rebuild =====
        else if(this.updateTick % 20 == 0) {
            if(this.astarothMinions.size() < playerCount * 4) {
                // Summon Astaroth:
                if (this.rebuildAstarothRespawnTime-- <= 0) {
                    for (int i = 0; i < playerCount; i++) {
                        EntityAstaroth minion = new EntityAstaroth(this.getEntityWorld());
                        this.summonMinion(minion, this.getRNG().nextDouble() * 360, 0);
                        minion.setSizeScale(2.5D);
                        this.astarothMinions.add(minion);
                    }
                    this.rebuildAstarothRespawnTime = this.rebuildAstarothRespawnTimeMax;
                }
            }

            // Summon Cacodemon:
            if(this.cacodemonMinions.size() < playerCount * 6 && this.updateTick % 10 * 20 == 0) {
                for (int i = 0; i < 5 * playerCount; i++) {
                    EntityCacodemon minion = new EntityCacodemon(this.getEntityWorld());
                    this.summonMinion(minion, this.getRNG().nextDouble() * 360, 10);
                    minion.posY += 10 + this.getRNG().nextInt(20);
                    this.cacodemonMinions.add(minion);
                }
            }

            // Heal:
            if(!this.astarothMinions.isEmpty()) {
                float healAmount = this.astarothMinions.size();
                if (((this.getHealth() + healAmount) / this.getMaxHealth()) <= 0.2D)
                    this.heal(healAmount);
            }
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
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityTrite.class) || targetClass.isAssignableFrom(EntityCacodemon.class) ||  targetClass.isAssignableFrom(EntityAstaroth.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityProjectileBase projectile = new EntityDevilGatling(this.getEntityWorld(), this);
        projectile.setProjectileScale(4f);
    	
    	// Y Offset:
        BlockPos offset = this.getFacingPosition(this, 13, this.getRotationYawHead() - this.rotationYaw);
        projectile.posX = offset.getX();
        projectile.posY = offset.getY() + (this.height * 0.2D);
        projectile.posZ = offset.getZ();
    	
    	// Set Velocities:
        double d0 = target.posX - projectile.posX;
        double d1 = target.posY - (target.height * 0.25D) - projectile.posY;
        double d2 = target.posZ - projectile.posZ;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.1F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double) f1, d2, velocity, 0.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.getEntityWorld().spawnEntity(projectile);

        super.rangedAttack(target, range);
    }

    // ========== Devilstars ==========
    public void attackDevilstar(float angle) {
        // Type:
        EntityProjectileBase projectile = new EntityDevilstar(this.getEntityWorld(), this);
        projectile.setProjectileScale(4f);

        // Y Offset:
        BlockPos offset = this.getFacingPosition(this, 8, angle);
        projectile.posX = offset.getX();
        projectile.posY = offset.getY() + (this.height * 0.5D);
        projectile.posZ = offset.getZ();

        // Set Velocities:
        float range = 20 + (20 * this.getRNG().nextFloat());
        BlockPos target = this.getFacingPosition(this, range, angle);
        double d0 = target.getX() - projectile.posX;
        double d1 = target.getY() - projectile.posY;
        double d2 = target.getZ() - projectile.posZ;
        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.1F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double) f1, d2, velocity, 0.0F);

        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.getEntityWorld().spawnEntity(projectile);
    }
	
	
	// ==================================================
   	//                      Death
   	// ==================================================
	@Override
	public void onDeath(DamageSource damageSource) {
        if(!this.getEntityWorld().isRemote && MobInfo.getFromName("trite").mobEnabled) {
            int j = 6 + this.rand.nextInt(20) + (getEntityWorld().getDifficulty().getDifficultyId() * 4);
            for(int k = 0; k < j; ++k) {
                float f = ((float)(k % 2) - 0.5F) * this.width / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * this.width / 4.0F;
                EntityTrite trite = new EntityTrite(this.getEntityWorld());
                trite.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
                trite.setMinion(true);
                trite.setSubspecies(this.getSubspeciesIndex(), true);
                this.getEntityWorld().spawnEntity(trite);
                if(this.getAttackTarget() != null)
                	trite.setRevengeTarget(this.getAttackTarget());
            }
        }
        super.onDeath(damageSource);
    }

    // ========== Minion Death ==========
    @Override
    public void onMinionDeath(EntityLivingBase minion) {
        if(minion instanceof EntityTrite && this.triteMinions.contains(minion)) {
            this.triteMinions.remove(minion);
        }
        if(minion instanceof EntityAstaroth && this.astarothMinions.contains(minion)) {
            this.astarothMinions.remove(minion);
        }
        if(minion instanceof EntityCacodemon && this.cacodemonMinions.contains(minion)) {
            this.cacodemonMinions.remove(minion);
        }
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    // ========== Damage ==========
    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        if(this.isBlocking())
            return true;
        return super.isEntityInvulnerable(source);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.WITHER) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }

    // ========== Blocking ==========
    @Override
    public boolean isBlocking() {
        if(this.getEntityWorld().isRemote)
            return super.isBlocking();
        return this.getBattlePhase() == 1 && !this.astarothMinions.isEmpty();
    }

    public boolean canAttackWhileBlocking() {
        return true;
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
        if(nbtTagCompound.hasKey("DevilstarStreamCharge")) {
            this.devilstarStreamCharge = nbtTagCompound.getInteger("DevilstarStreamCharge");
        }
        if(nbtTagCompound.hasKey("DevilstarStreamTime")) {
            this.devilstarStreamTime = nbtTagCompound.getInteger("DevilstarStreamTime");
        }
        if(nbtTagCompound.hasKey("AstarothIDs")) {
            NBTTagList astarothIDs = nbtTagCompound.getTagList("AstarothIDs", 10);
            for(int i = 0; i < astarothIDs.tagCount(); i++) {
                NBTTagCompound astarothID = astarothIDs.getCompoundTagAt(i);
                if(astarothID.hasKey("ID")) {
                    Entity entity = this.getEntityWorld().getEntityByID(astarothID.getInteger("ID"));
                    if(entity != null && entity instanceof EntityAstaroth)
                        this.astarothMinions.add((EntityAstaroth)entity);
                }
            }
        }
    }

    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("DevilstarStreamCharge", this.devilstarStreamCharge);
        nbtTagCompound.setInteger("DevilstarStreamTime", this.devilstarStreamTime);
        if(this.getBattlePhase() > 0) {
            NBTTagList astarothIDs = new NBTTagList();
            for(EntityAstaroth entityAstaroth : this.astarothMinions) {
                NBTTagCompound astarothID = new NBTTagCompound();
                astarothID.setInteger("ID", entityAstaroth.getEntityId());
                astarothIDs.appendTag(astarothID);
            }
            nbtTagCompound.setTag("AstarothIDs", astarothIDs);
        }
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
