package com.lycanitesmobs.core.entity;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.lycanitesmobs.*;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.api.IGroupIce;
import com.lycanitesmobs.core.entity.ai.DirectNavigator;
import com.lycanitesmobs.core.entity.ai.EntityAIMoveRestriction;
import com.lycanitesmobs.core.entity.ai.EntityAITargetAttack;
import com.lycanitesmobs.core.entity.ai.EntityAITargetRevenge;
import com.lycanitesmobs.core.entity.navigate.CreatureMoveHelper;
import com.lycanitesmobs.core.entity.navigate.CreaturePathNavigate;
import com.lycanitesmobs.core.entity.navigate.ICreatureNodeProcessor;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.inventory.ContainerCreature;
import com.lycanitesmobs.core.inventory.InventoryCreature;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public abstract class EntityCreatureBase extends EntityLiving {
    public static Boolean ENABLE_HITAREAS = false;
	public static final IAttribute DEFENSE = (new RangedAttribute(null, "generic.defense", 4.0D, 0.0D, 1024.0D)).setShouldWatch(true);
	public static final IAttribute RANGED_SPEED = (new RangedAttribute(null, "generic.rangedSpeed", 4.0D, 0.0D, 1024.0D)).setShouldWatch(true);

	// Core:
	/** The Creature Info used by this creature. **/
	public CreatureInfo creatureInfo;
	/** The Creature Stats instance used by this Entity instance to get and manage stats. **/
	public CreatureStats creatureStats;
	/** The Subspecies of this creature, if null this creature is the default common species. **/
	public Subspecies subspecies = null;
	/** What attribute is this creature, used for effects such as Bane of Arthropods. **/
	public EnumCreatureAttribute attribute = EnumCreatureAttribute.UNDEAD;
	/** A class that opens up extra stats and behaviours for NBT based customization.**/
	public ExtraMobBehaviour extraMobBehaviour;

	// Info:
	/** The name of the event that spawned this mob if any, an empty string ("") if none. **/
	public String spawnEventType = "";
	/** The number of the event that spawned this mob. Used for despawning this mob when a new event starts. Ignored if the spawnEventType is blank or the count is less than 0. **/
	public int spawnEventCount = -1;
    /** The Pet Entry for this mob, this binds this mob to a entity for special interaction, it will also cause this mob to be removed if the entity it is bound to is removed or dead. **/
    public PetEntry petEntry;
    /** If true, this mob will be treated as if it was spawned from an Altar, this is typically called directly by AltarInfo or by events triggered by AltarInfo. **/
    public boolean altarSummoned = false;
    /** If true, this mob will show a boss health bar, regardless of other properties, unless overridden by a subclass. **/
    public boolean forceBossHealthBar = false;
    /** The living update tick. **/
    public long updateTick = 0;
	
	// Size:
    /** The width of this mob. XZ axis. **/
	public float setWidth;
    /** The depth of this mob. Overrides width's Z axis. This currently doesn't work, use width only for now. **/
	public float setDepth;
    /** The height of this mob. Y axis. **/
	public float setHeight;
    /** The size scale of this mob. Randomly varies normally by 10%. **/
	public double sizeScale = 1.0D;
    /** An array of additional hitboxes for large entities. **/
    public EntityHitArea[][][] hitAreas;
    /** A scale relative to this entity's width for melee and ranged hit collision. **/
    public float hitAreaWidthScale = 1;
    /** A scale relative to this entity's height for melee and ranged hit collision. **/
    public float hitAreaHeightScale = 1;
    /** How many ticks until this mob can attack again. **/
    public int attackTime = 20;
    /** A bounding box used for rendering, usually null as the base bounding box is used unless overridden. **/
    //public AxisAlignedBB renderBoundingBox = null;
	
	// Stats:
	/** The level of this mob, higher levels increase the stat multipliers by a small amount. **/
	protected int level = 1;
    /** Which attack phase this mob is on. This will be replaced with a better system for boss mobs. **/
	public byte attackPhase = 0;
    /** How many attack phases this mob has. This will be replaced with a better system for boss mobs. **/
	public byte attackPhaseMax = 0;
    /** How long this mob should run away for before it stops. **/
	public int fleeTime = 200;
    /** How long has this mob been running away for. **/
	public int currentFleeTime = 0;
    /** What percentage of health this mob will run away at, from 0.0F to 1.0F **/
	public float fleeHealthPercent = 0;
    /** The current Battle Phase of this mob, each Phase uses different behaviours. Used by bosses. **/
    public int battlePhase = 0;
    /** The maximum amount of damage this mob can take. If 0 or less, this is ignored. **/
    public int damageMax = 0;
    /** The gorwing age of this mob. **/
    protected int growingAge;

	// Boss Health:
	/** How much damage this creature has taken over the latest second. **/
	public float damageTakenThisSec = 0;
	/** How much health this creature had last tick. **/
	public float healthLastTick = -1;
	/** If above 0, no more than this much health can be lost per second. **/
	public float damageLimit = 0;
	
	// Abilities:
    /** The battle range of this boss mob, anything out of this range cannot harm the boss. This will also affect other things related to the boss. **/
    public int bossRange = 60;
	/** Whether or not this mob is hostile by default. Use isHostile() when check if this mob is hostile. **/
	public boolean isAggressiveByDefault = true;
    /** Whether if this mob is on fire, it should spread it to other entities when melee attacking. **/
	public boolean spreadFire = false;
    /** Used to check if the mob was stealth last update. **/
	public boolean stealthPrev = false;
	/** When above 0 this mob will be considered blocking and this will count down to 0. Blocking mobs have additional defense. **/
	public int currentBlockingTime = 0;
	/** How long this mob should usually block for in ticks. **/
	public int blockingTime = 60;
	/** The entity picked up by this entity (if any). **/
    public EntityLivingBase pickupEntity;
    /** If true, this entity will have a solid collision box allowing other entities to stand on top of it as well as blocking player movement based on mass more effectively. **/
    public boolean solidCollision = false;
	
	// Positions:
    /** A location used for mobs that stick around a certain home spot. **/
    protected BlockPos homePosition = new BlockPos(0, 0, 0);
    /** How far this mob can move from their home spot. **/
    protected float homeDistanceMax = -1.0F;
    /** A central point set by arenas or events that spawn mobs. Bosses use this to setup arena-based movement. **/
    protected BlockPos arenaCenter = null;
    
    // Spawning:
    /** Use the onFirstSpawn() method and not this variable. True if this creature has spawned for the first time (naturally or via spawn egg, etc, not reloaded from a saved chunk). **/
    public boolean firstSpawn = true;
    /** Should this mob check for block collisions when spawning? **/
    public boolean spawnsInBlock = false;
    /** Can this mob spawn where it can't see the sky above? **/
    public boolean spawnsUnderground = true;
    /** Can this mob spawn on land (not in liquids)? Note that setting a mob to WATERCREATURE means that they will only spawn in water anyway. **/
    public boolean spawnsOnLand = true;
    /** Does this mob spawn inside liquids? **/
    public boolean spawnsInWater = false;
    /** If true, this creature will swim in and if set, will suffocate without lava instead of without water. **/
    public boolean isLavaCreature = false;
    /** Is this mob a minion? (Minions don't drop items and other things). **/
    public boolean isMinion = false;
    /** If true, this mob is temporary and will eventually despawn once the temporaryDuration is at or below 0. **/
	public boolean isTemporary = false;
    /** If this mob is temporary, this will count down to 0, once per tick. Once it hits 0, this creature will despawn. **/
	public int temporaryDuration = 0;
    /** If true, this mob will not despawn naturally regardless of other rules. **/
	public boolean forceNoDespawn = false;
    /** Can be set to true by custom spawners in rare cases. If true, this mob has a higher chance of being a subspecies. **/
    public boolean spawnedRare = false;
    /** Set to true when this mob is spawned as a boss, this is used to make non-boss mobs behave like bosses. **/
	public boolean spawnedAsBoss = false;
    
    // Movement:
    /** Whether the mob should use it's leash AI or not. **/
    private boolean leashAIActive = false;
    /** Movement AI for mobs that are leashed. **/
    private EntityAIBase leashMoveTowardsRestrictionAI = new EntityAIMoveRestriction(this);
    /** The flight navigator class, a makeshift class that handles flight and free swimming movement, replaces the pathfinder. **/
    public DirectNavigator directNavigator;
    
    // Targets:
    /** A target used for alpha creatures or connected mobs such as following concapede segements. **/
    private EntityLivingBase masterTarget;
    /** A target used usually for child mobs or connected mobs such as leading concapede segments. **/
    private EntityLivingBase parentTarget;
    /** A target that this mob should usually run away from. **/
    private EntityLivingBase avoidTarget;
    /** A target that this mob just normally always attack if set. **/
    private EntityLivingBase fixateTarget;
	/** Used to identify the fixate target when loading this saved entity. **/
	private UUID fixateUUID = null;

	// Client:
	/** A list of player entities that need to have their GUI of this mob reopened on refresh. **/
	public List<EntityPlayer> guiViewers = new ArrayList<>();
	/** Counts from the guiRefreshTime down to 0 when a GUI refresh has been scheduled. **/
	public int guiRefreshTick = 0;
	/** The amount of ticks to wait before a GUI refresh. **/
	public int guiRefreshTime = 2;
    /** Used for attack animations, the server uses this more as a boolean, the client uses it as a timer. **/
	public short justAttacked = 0;
    /** The duration of attack animations, used by the server as a boolean (true when greater than 0 then instantly set to 0), the client uses it as the animation time (counts down per tick). **/
	public short justAttackedTime = 5;
    /** True if this mob should play a sound when attacking. Ranged mobs usually don't use this as their projectiles makes an attack sound instead. **/
	public boolean hasAttackSound = false;
    /** True if this mob should play a sound when walking. Usually footsteps. **/
	public boolean hasStepSound = true;
    /** True if this mob should play a sound when jumping, used mostly for mounts. **/
	public boolean hasJumpSound = false;
    /** The delay in ticks between flying sounds such as wing flapping, set to 0 for no flight sounds. **/
	public int flySoundSpeed = 0;
    /** An extra animation boolean. **/
    public boolean extraAnimation01 = false;
    /** Holds Information for this mobs boss health should it be displayed in the boss health bar. Used by bosses and rare subspecies. **/
    public BossInfoServer bossInfo;
    /** If positive, this creature entity is only being used for rendering in a GUI, etc and should play animation based off of this instead. **/
    public float onlyRenderTicks = -1;
	
	// Data Manager:
    protected static final DataParameter<Byte> TARGET = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> ANIMATION = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> ATTACK_PHASE = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.BYTE);

    protected static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.BYTE);
    protected static final DataParameter<Float> STEALTH = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.FLOAT);

    protected static final DataParameter<Boolean> BABY = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Byte> COLOR = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.BYTE);
    protected static final DataParameter<Float> SIZE = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.FLOAT);
	protected static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.VARINT);
    protected static final DataParameter<Byte> SUBSPECIES = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.BYTE);

    public static final DataParameter<ItemStack> EQUIPMENT_HEAD = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<ItemStack> EQUIPMENT_CHEST = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<ItemStack> EQUIPMENT_LEGS = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<ItemStack> EQUIPMENT_FEET = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<ItemStack> EQUIPMENT_BAG = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<ItemStack> EQUIPMENT_SADDLE = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.ITEM_STACK);

    protected static final DataParameter<Optional<BlockPos>> ARENA = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.OPTIONAL_BLOCK_POS);

    /** Used for the TARGET watcher bitmap, bitmaps save on many packets and make network performance better! **/
	public enum TARGET_ID {
		ATTACK((byte)1), MASTER((byte)2), PARENT((byte)4), AVOID((byte)8), RIDER((byte)16), PICKUP((byte)32);
		public final byte id;
	    private TARGET_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
    /** Used for the ANIM_ID watcher bitmap, bitmaps save on many packets and make network performance better! **/
	public enum ANIM_ID {
		ATTACKED((byte)1), GROUNDED((byte)2), IN_WATER((byte)4), BLOCKING((byte)8), MINION((byte)16), EXTRA01((byte)32);
		public final byte id;
	    private ANIM_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
	/** If true, this object has initiated and it is safe to use the datawatcher. **/
	public boolean initiated = false;
	
	// Interact:
	/** Used for the tidier interact code, these are commonly used right click item command priorities. **/
	public enum CMD_PRIOR {
		OVERRIDE(0), IMPORTANT(1), EQUIPPING(2), ITEM_USE(3), EMPTY_HAND(4), MAIN(5);
		public final int id;
	    private CMD_PRIOR(int value) { this.id = value; }
	    public int getValue() { return id; }
	}
	
	// GUI Commands:
	/** A list of GUI command IDs to be used by pet or creature GUIs via a network packet. **/
	public enum GUI_COMMAND_ID {
		CLOSE((byte)0), SITTING((byte)1), FOLLOWING((byte)2), PASSIVE((byte)3), STANCE((byte)4), PVP((byte)5), TELEPORT((byte)6), SPAWNING((byte)7), RELEASE((byte)8);
		public byte id;
		private GUI_COMMAND_ID(byte i) { id = i; }
	}
	
	// Items:
    /** The inventory object of the creature, this is used for managing and using the creature's inventory. **/
	public InventoryCreature inventory;
    /** A collection of drops which are used when randomly drop items on death. **/
    public List<ItemDrop> drops = new ArrayList<>();
	/** A collection of drops to be stored in NBT data. **/
	public List<ItemDrop> savedDrops = new ArrayList<>();
    
    // Override AI:
    public EntityAITargetAttack aiTargetPlayer = new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class);
    public EntityAITargetRevenge aiDefendAnimals = new EntityAITargetRevenge(this).setHelpClasses(IAnimals.class);


    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityCreatureBase(World world) {
        super(world);

        // Size:
		this.setWidth = (float)this.creatureInfo.width;
		this.setDepth = (float)this.creatureInfo.width;
		this.setHeight = (float)this.creatureInfo.height;
        this.width = this.setWidth;
        this.height = this.setHeight;

        // Movement:
        this.moveHelper = this.createMoveHelper();

        // Level:
		this.applyLevel(this.getStartingLevel());

        // Path On Fire or In Lava:
        if(!this.canBurn()) {
            this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
            this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
            if(this.canBreatheUnderwater()) {
                this.setPathPriority(PathNodeType.LAVA, 8.0F);
                if(!this.canBreatheAboveWater() || this.isLavaCreature) {
                    this.setPathPriority(PathNodeType.LAVA, 0.0F);
                }
            }
        }

        // Path In Water:
        if(this.waterDamage())
            this.setPathPriority(PathNodeType.WATER, -1.0F);
        else if(this.canBreatheUnderwater()) {
            this.setPathPriority(PathNodeType.WATER, 8.0F);
            if(!this.canBreatheAboveWater())
                this.setPathPriority(PathNodeType.WATER, 0.0F);
        }

        // Swimming:
        if(this.canWade() && this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround groundNavigator = (PathNavigateGround)this.getNavigator();
            groundNavigator.setCanSwim(true);
        }
    }

	// ========== Attributes and Stats ==========
	/** Creates and sets all the entity attributes with default values. **/
	@Override
	protected void applyEntityAttributes() {
		this.creatureInfo = CreatureManager.getInstance().getCreature(this.getClass());
		this.creatureStats = new CreatureStats(this);
		this.extraMobBehaviour = new ExtraMobBehaviour(this);
		this.directNavigator = new DirectNavigator(this);

		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(DEFENSE);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
		this.getAttributeMap().registerAttribute(RANGED_SPEED);

		this.applyStats();
	}

	/**
	 * Loads this entity's stats.
	 */
	public void applyStats() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.creatureStats.getHealth());
		this.getEntityAttribute(DEFENSE).setBaseValue(this.creatureStats.getDefense());
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(this.creatureStats.getArmor());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.creatureStats.getSpeed());
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(this.creatureStats.getKnockbackResistance());
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(this.creatureStats.getSight());
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(this.creatureStats.getDamage());
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(this.creatureStats.getAttackSpeed());
		this.getEntityAttribute(RANGED_SPEED).setBaseValue(this.creatureStats.getRangedSpeed());
	}

	/**
	 * Called when this entity needs to reload its stats. Should be called when the subspecies, level, etc of this creature changes.
	 */
	public void refreshStats() {
		this.applyStats();
		this.setHealth((float)this.creatureStats.getHealth());
		this.refreshBossHealthName();
	}
    
    // ========== Setup ==========
    /** This should be called by the specific mob entity and set the default starting values. **/
    public void setupMob() {
        // Size:
        this.setWidth *= this.creatureInfo.hitboxScale;
        this.setHeight *= this.creatureInfo.hitboxScale;
        this.updateSize();
        if(this.creatureInfo.sizeScale != 1)
            this.setSizeScale(this.creatureInfo.sizeScale);
        
        // Stats:
        this.stepHeight = 0.5F;
        this.experienceValue = this.creatureInfo.experience;
        this.inventory = new InventoryCreature(this.getName(), this);

        // Drops:
        this.loadItemDrops();
		ItemEquipmentPart itemEquipmentPart = ItemEquipmentPart.MOB_PART_DROPS.get(this.creatureInfo.getEntityId());
		if(itemEquipmentPart != null) {
			this.drops.add(new ItemDrop(new ItemStack(itemEquipmentPart), itemEquipmentPart.dropChance).setMaxAmount(1));
		}
        
        // Fire Immunity:
        this.isImmuneToFire = !this.canBurn();
    }
    
    // ========== Item Drops ==========
    /** Loads all default item drops, will be ignored if the Enable Default Drops config setting for this mob is set to false, should be overridden to add drops. **/
    public void loadItemDrops() {
		for(ItemDrop drop : this.creatureInfo.drops) {
			ItemDrop newDrop = new ItemDrop(drop.itemStack.copy(), drop.chance).setMinAmount(drop.minAmount).setMaxAmount(drop.maxAmount).setChance(drop.chance).setSubspecies(drop.subspeciesID).setBurningDrop(drop.burningItemStack);
			this.drops.add (newDrop);
		}
		for(ItemDrop drop : CreatureManager.getInstance().config.getGlobalDrops()) {
			ItemDrop newDrop = new ItemDrop(drop.itemStack.copy(), drop.chance).setMinAmount(drop.minAmount).setMaxAmount(drop.maxAmount).setChance(drop.chance).setSubspecies(drop.subspeciesID).setBurningDrop(drop.burningItemStack);
			this.drops.add (newDrop);
		}
	}

    /** Adds a saved item drop to this creature instance where it will be read/written from/to NBT Data. **/
    public void addSavedItemDrop(ItemDrop itemDrop) {
    	this.drops.add(itemDrop);
    	this.savedDrops.add(itemDrop);
	}

	
	// ========== Init ==========
    /** Initiates the entity setting all the values to be watched by the datawatcher. **/
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(TARGET, (byte) 0);
        this.dataManager.register(ATTACK_PHASE, (byte) 0);
        this.dataManager.register(ANIMATION, (byte) 0);
        this.dataManager.register(CLIMBING, (byte) 0);
        this.dataManager.register(STEALTH, 0.0F);
        this.dataManager.register(COLOR, (byte) 0);
        this.dataManager.register(SIZE, (float) 1D);
		this.dataManager.register(LEVEL, 1);
        this.dataManager.register(SUBSPECIES, (byte) 0);
        this.dataManager.register(ARENA, Optional.absent());
        InventoryCreature.registerDataParameters(this.dataManager);
        this.initiated = true;
    }
    
    // ========== Name ==========
    /** Returns the display name of this entity. Use this when displaying it's name. **/
    @Override
    public String getName() {
    	if(this.hasCustomName())
    		return this.getCustomNameTag();
    	else
    		return this.getFullName();
    }

    /** Returns the full name of this entity. **/
    public String getFullName() {
    	String name = "";
    	if(!"".equals(this.getAgeName()))
    		name += this.getAgeName() + " ";
    	if(!"".equals(this.getSubspeciesTitle()))
    		name += this.getSubspeciesTitle() + " ";
    	return name + this.getSpeciesName() + this.getLevelName();
    }
    
    /** Returns the species name of this entity. **/
    public String getSpeciesName() {
    	return this.creatureInfo.getTitle();
    }

    /** Returns the subpsecies title (translated name) of this entity, returns a blank string if this is a base species mob. **/
    public String getSubspeciesTitle() {
    	if(this.getSubspecies() != null) {
    		return this.getSubspecies().getTitle();
    	}
    	return "";
    }

	/** Returns a mobs level to append to the name if above level 1. **/
	public String getLevelName() {
		if(this.getLevel() < 2) {
			return "";
		}
		return " " + I18n.translateToLocal("entity.level") + " " + this.getLevel();
	}

    /** Gets the name of this entity relative to it's age, more useful for EntityCreatureAgeable. **/
    public String getAgeName() {
    	return "";
    }


    // ==================================================
    //                     Data Manager
    // ==================================================
    public boolean getBoolFromDataManager(DataParameter<Boolean> key) {
        try {
            return this.getDataManager().get(key);
        }
        catch (Exception e) {
            return false;
        }
    }

    public byte getByteFromDataManager(DataParameter<Byte> key) {
        try {
            return this.getDataManager().get(key);
        }
        catch (Exception e) {
            return 0;
        }
    }

    public int getIntFromDataManager(DataParameter<Integer> key) {
        try {
            return this.getDataManager().get(key);
        }
        catch (Exception e) {
            return 0;
        }
    }

    public float getFloatFromDataManager(DataParameter<Float> key) {
        try {
            return this.getDataManager().get(key);
        }
        catch (Exception e) {
            return 0;
        }
    }

    public String getStringFromDataManager(DataParameter<String> key) {
        try {
            return this.getDataManager().get(key);
        }
        catch (Exception e) {
            return null;
        }
    }

	public Optional<UUID> getUUIDFromDataManager(DataParameter<Optional<UUID>> key) {
		try {
			return this.getDataManager().get(key);
		}
		catch (Exception e) {
			return null;
		}
	}

    public ItemStack getItemStackFromDataManager(DataParameter<ItemStack> key) {
        try {
            return this.getDataManager().get(key);
        }
        catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    public Optional<BlockPos> getBlockPosFromDataManager(DataParameter<Optional<BlockPos>> key) {
        try {
            return this.getDataManager().get(key);
        }
        catch (Exception e) {
            return Optional.absent();
        }
    }
    
    
    // ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Can Spawn Here ==========
    /** Checks if the creature is able to spawn at it's initial position. **/
    @Override
    public boolean getCanSpawnHere() {
	    return this.checkSpawnVanilla(this.getEntityWorld(), this.getPosition());
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return this.creatureInfo.creatureSpawn.spawnGroupMax;
    }

	// ========== Vanilla Spawn Check ==========
    /** Performs checks when spawned by a vanilla spawner or possibly another modded spawner if they use the vanilla checks. **/
    public boolean checkSpawnVanilla(World world, BlockPos pos) {
        if(world.isRemote) {
			return false;
		}

    	LycanitesMobs.printDebug("MobSpawns", " ~O==================== Vanilla Spawn Check: " + this.creatureInfo.getName() + " ====================O~");
    	LycanitesMobs.printDebug("MobSpawns", "Attempting to Spawn: " + this.creatureInfo.getName());
		LycanitesMobs.printDebug("MobSpawns", "Target Spawn Location: " + pos);
    	
    	// Peaceful Check:
    	LycanitesMobs.printDebug("MobSpawns", "Checking for peaceful difficulty...");
        if(!this.creatureInfo.peaceful && this.getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL) {
        	return false;
		}
        
        // Fixed Spawning Checks:
    	LycanitesMobs.printDebug("MobSpawns", "Fixed spawn check (light level, collisions)...");
        if(!this.fixedSpawnCheck(world, pos)) {
			return false;
		}
        
    	// Mob Spawner Check:
    	LycanitesMobs.printDebug("MobSpawns", "Checking for nearby Mob Spawner...");
        if(this.isSpawnerNearby(world, pos)) {
        	LycanitesMobs.printDebug("MobSpawns", "Mob Spawner found, skipping other checks.");
        	LycanitesMobs.printDebug("MobSpawns", "Vanilla Spawn Check Passed!");
        	return true;
        }
    	LycanitesMobs.printDebug("MobSpawns", "No Mob Spawner found.");
        
        // Environment Spawning Checks:
    	LycanitesMobs.printDebug("MobSpawns", "Environment spawn check (dimension, group limit, ground type, water, lava, underground)...");
        if(!this.environmentSpawnCheck(world, pos)) {
			return false;
		}

        LycanitesMobs.printDebug("MobSpawns", "Vanilla Spawn Check Passed!");
        return true;
    }

    // ========== Fixed Spawn Check ==========
    /** First stage checks for vanilla spawning, if this check fails the creature will not spawn. **/
    public boolean fixedSpawnCheck(World world, BlockPos pos) {
		if(!this.checkSpawnLightLevel(world, pos)) {
			return false;
		}

    	LycanitesMobs.printDebug("MobSpawns", "Checking collision...");
        if(!this.checkSpawnCollision(world, pos)) {
        	return false;
		}

		LycanitesMobs.printDebug("MobSpawns", "Counting mobs of the same kind, max allowed is: " + this.creatureInfo.creatureSpawn.spawnAreaLimit);
		if(!this.checkSpawnGroupLimit(world, pos, CreatureManager.getInstance().spawnConfig.spawnLimitRange))
			return false;

    	return true;
    }

    // ========== Environment Spawn Check ==========
    /** Second stage checks for vanilla spawning, this check is ignored if there is a valid monster spawner nearby. **/
    public boolean environmentSpawnCheck(World world, BlockPos pos) {
        if(this.creatureInfo.creatureSpawn.worldDayMin > 0) {
            int currentDay = (int) Math.floor(world.getTotalWorldTime() / 24000D);
            LycanitesMobs.printDebug("MobSpawns", "Checking world age, currently on day: " + currentDay + ", must be at least day: " + this.creatureInfo.creatureSpawn.worldDayMin + ".");
            if (currentDay < this.creatureInfo.creatureSpawn.worldDayMin)
                return false;
        }
    	LycanitesMobs.printDebug("MobSpawns", "Checking dimension.");
    	if(!this.isNativeDimension(this.getEntityWorld()))
    		return false;
    	LycanitesMobs.printDebug("MobSpawns", "Block preference.");
        if(this.getBlockPathWeight(pos.getX(), pos.getY(), pos.getZ()) < 0.0F)
        	return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking for liquid (water, lava, ooze, etc).");
        if(!this.spawnsInWater && this.getEntityWorld().containsAnyLiquid(this.getEntityBoundingBox()))
            return false;
        else if(!this.spawnsOnLand && !this.getEntityWorld().containsAnyLiquid(this.getEntityBoundingBox()))
            return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking for underground.");
        if(!this.spawnsUnderground && this.isBlockUnderground(pos.getX(), pos.getY() + 1, pos.getZ()))
        	return false;
        LycanitesMobs.printDebug("MobSpawns", "Checking for nearby bosses.");
		if(!this.checkSpawnBoss(world, pos))
			return false;

        return true;
    }

    // ========== Spawn Dimension Check ==========
    public boolean isNativeDimension(World world) {
        return this.creatureInfo.creatureSpawn.isAllowedDimension(world);
    }

	// ========== Collision Spawn Check ==========
	/** Returns true if there is no collision stopping this mob from spawning. **/
	public boolean checkSpawnCollision(World world, BlockPos pos) {
		/*if(!this.getEntityWorld().checkNoEntityCollision(this.getEntityBoundingBox())) {
			return false;
		}*/
		if(!this.spawnsInBlock && !this.getEntityWorld().getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
			return false;
		}
		return true;
	}

	// ========== Light Level Spawn Check ==========
	/** Returns true if the light level is valid for spawning. **/
	public boolean checkSpawnLightLevel(World world, BlockPos pos) {
		if(this.creatureInfo.creatureSpawn.spawnsInDark && this.creatureInfo.creatureSpawn.spawnsInLight) {
			return true;
		}
		if(!this.creatureInfo.creatureSpawn.spawnsInDark && !this.creatureInfo.creatureSpawn.spawnsInLight) {
			return false;
		}

		byte light = this.testLightLevel(pos);
		if(this.creatureInfo.creatureSpawn.spawnsInDark && light <= 1) {
			return true;
		}

		if(this.creatureInfo.creatureSpawn.spawnsInLight && light >= 2) {
			return true;
		}

		return false;
	}

	// ========== Group Limit Spawn Check ==========
	/** Checks for nearby entities of this type, mobs use this so that too many don't spawn in the same area. Returns true if the mob should spawn. **/
	public boolean checkSpawnGroupLimit(World world, BlockPos pos, double range) {
		if(range <= 0) {
			return true;
		}

		// Get Limits:
		int typesLimit = CreatureManager.getInstance().spawnConfig.typeSpawnLimit;
		int speciesLimit = this.creatureInfo.creatureSpawn.spawnAreaLimit;
		if(typesLimit <= 0 && speciesLimit <= 0) {
			return true;
		}

		// Count Nearby Entities:
		List<EntityCreatureBase> targets = this.getNearbyEntities(EntityCreatureBase.class, EntityCreatureBase.class, range);
		int typesFound = 0;
		int speciesFound = 0;
		for(EntityCreatureBase targetCreature : targets) {
			if(targetCreature.creatureInfo.peaceful == this.creatureInfo.peaceful) {
				typesFound++;
			}
			if(this.creatureInfo.entityClass.isAssignableFrom(targetCreature.getClass())) {
				speciesFound++;
			}
		}

		// Check Limits:
		if(range > 0) {
			if(typesLimit > 0 && typesFound >= typesLimit) {
				return false;
			}
			if(speciesLimit > 0 && speciesFound >= speciesLimit) {
				return false;
			}
		}
		return true;
	}

	// ========== Boss Spawn Check ==========
	/** Checks for nearby bosses, mobs usually shouldn't randomly spawn near a boss. **/
	public boolean checkSpawnBoss(World world, BlockPos pos) {
		List bosses = this.getNearbyEntities(EntityCreatureBase.class, IGroupBoss.class, CreatureManager.getInstance().spawnConfig.spawnLimitRange);
		return bosses.size() == 0;
	}

    // ========== Egg Spawn ==========
    /** Called once this mob is initially spawned. **/
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingData) {
    	livingData = super.onInitialSpawn(difficulty, livingData);
        return livingData;
    }

    // ========== Despawning ==========
    /** Returns whether this mob should despawn overtime or not. Config defined forced despawns override everything except tamed creatures and tagged creatures. **/
    @Override
    protected boolean canDespawn() {
    	if(this.creatureInfo.creatureSpawn.despawnForced)
    		return true;
    	if(!this.creatureInfo.creatureSpawn.despawnNatural)
    		return false;
        if(this.creatureInfo.boss || this.getSubspeciesIndex() >= 3)
            return false;
    	if(this.isPersistant() || this.getLeashed() || (this.hasCustomName() && "".equals(this.spawnEventType)))
    		return false;
    	return super.canDespawn();
    }

    /** Returns true if this mob should not despawn in unloaded chunks.
     * Most farmable mobs never despawn, but can be set to despawn in the config where this will kick in.
     * Here mobs can check if they have ever been fed or bred or moved from their home dimension.
     * Farmable mobs can then be set to despawn unless they have been farmed by a player.
     * Useful for the Pinky Nether invasion issues! Also good for water animals that can't spawn as CREATURE.
     * Leashed mobs don't ever despawn naturally and don't rely on this.
     * There is also the vanilla variable persistenceRequired which is handled in vanilla code too.
    **/
    public boolean isPersistant() {
    	return this.forceNoDespawn;
    }

    @Override
	public void enablePersistence() {
		super.enablePersistence();
		this.forceNoDespawn = true;
	}

    /** A check that is constantly done, if this returns true, this entity will be removed, used normally for peaceful difficulty removal and temporary minions. **/
    public boolean despawnCheck() {
        if(this.getEntityWorld().isRemote)
        	return false;

        // Disabled Mobs:
        if(!this.creatureInfo.enabled)
        	return true;

        // Temporary Mobs:
        if(this.isTemporary && this.temporaryDuration-- <= 0)
        	return true;

        // Mob Event Despawning:
        if(this.getLeashed() || this.isPersistant()) {
        	this.spawnEventType = "";
        	this.spawnEventCount = -1;
        }
        else {
        	if(!this.creatureInfo.peaceful && this.getEntityWorld().getDifficulty() == EnumDifficulty.PEACEFUL && !this.hasCustomName())
            	return true;

        	ExtendedWorld worldExt = ExtendedWorld.getForWorld(this.getEntityWorld());
        	if(worldExt != null) {
        		if(!"".equals(this.spawnEventType) && this.spawnEventCount >= 0 && this.spawnEventCount != worldExt.getWorldEventCount())
        			return true;
        	}
        }
        return false;
    }

    // ========== Spawner Checking ==========
    /** Checks if a Monster Spawner that spawns this mob is near the xyz locations, checks within an 8 block radius. **/
    public boolean isSpawnerNearby(World world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(world == null)
            return false;
    	int checkRange = 8;
    	for(int i = x - checkRange; i <= x + checkRange; i++)
        	for(int j = y - checkRange; j <= y + checkRange; j++)
            	for(int k = z - checkRange; k <= z + checkRange; k++) {
            		IBlockState spawnerBlock = world.getBlockState(new BlockPos(i, j, k));
            		if(spawnerBlock != null) {
	            		TileEntity tileEntity = world.getTileEntity(new BlockPos(i, j, k));

	            		if(tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
                            try {
                                // Horribly hacky way to get the mob spawned by a spawner using NBT because stupid private variables!
                                TileEntityMobSpawner mobSpawner = (TileEntityMobSpawner) tileEntity;
                                NBTTagCompound nbt = new NBTTagCompound();
                                mobSpawner.getSpawnerBaseLogic().writeToNBT(nbt);
                                if (nbt.hasKey("SpawnData")) {
                                    NBTTagCompound spawnData = nbt.getCompoundTag("SpawnData");
                                    if (spawnData.hasKey("id")) {
                                        String mobSpawnEntityId = spawnData.getString("id");
                                        if (mobSpawnEntityId.equalsIgnoreCase(this.creatureInfo.getEntityId()))
                                            return true;
                                    }
                                }
                            } catch (Exception e) {}

	            		}
	            	}
            	}
    	return false;
    }

    // ========== Block Checking ==========
    /** Checks if the specified block is underground (unable to see the sky above it). This checks through leaves, plants, grass and vine materials. **/
    public boolean isBlockUnderground(int x, int y, int z) {
    	if(this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z)))
    		return false;
    	for(int j = y; j < this.getEntityWorld().getHeight(); j++) {
    		Material blockMaterial = this.getEntityWorld().getBlockState(new BlockPos(x, j, z)).getMaterial();
    		if(blockMaterial != Material.AIR
    				&& blockMaterial != Material.LEAVES
    				&& blockMaterial != Material.PLANTS
    				&& blockMaterial != Material.GRASS
    				&& blockMaterial != Material.VINE)
    			return true;
    	}
    	return false;
    }

    // ========== Boss ==========
    /** Returns whether or not this mob is a boss. **/
    public boolean isBoss() {
        return this.isBossAlways() || this.spawnedAsBoss;
    }

	/** Returns whether or not this mob is always a boss (any mob can be custom spawned as a boss but some mobs are always bosses). **/
	public boolean isBossAlways() {
		return this.creatureInfo.boss;
	}

    @Override
    public boolean isNonBoss() {
        return !this.isBoss();
    }

    public void createBossInfo(BossInfo.Color color, boolean darkenSky) {
        String name = this.getFullName();
        if(this.isBoss())
            name += " (Phase " + (this.getBattlePhase() + 1) + ")";
        this.bossInfo = (BossInfoServer)(new BossInfoServer(new TextComponentString(name), color, BossInfo.Overlay.PROGRESS)).setDarkenSky(darkenSky);
    }

    public BossInfo getBossInfo() {
        if(this.bossInfo == null && this.showBossInfo() && !this.getEntityWorld().isRemote) {
            if(this.isBoss())
                this.createBossInfo(BossInfo.Color.RED, false);
            else
                this.createBossInfo(BossInfo.Color.GREEN, false);
        }
        return this.bossInfo;
    }

    /** Updates the boss name for the health bar. **/
    public void refreshBossHealthName() {
        if(this.bossInfo != null) {
            String name = this.getFullName();
            name += " (Phase " + (this.getBattlePhase() + 1) + ")";
            this.bossInfo.setName(new TextComponentString(name));
        }
    }


    // ========== Summoning ==========
    public void summonMinion(EntityLivingBase minion, double angle, double distance) {
        double angleRadians = Math.toRadians(angle);
        double x = this.posX + ((this.width + distance) * Math.cos(angleRadians) - Math.sin(angleRadians));
        double y = this.posY + 1;
        double z = this.posZ + ((this.width + distance) * Math.sin(angleRadians) + Math.cos(angleRadians));
        minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
        if(minion instanceof EntityCreatureBase) {
            ((EntityCreatureBase)minion).setMinion(true);
            ((EntityCreatureBase)minion).applySubspecies(this.getSubspeciesIndex(), true);
            ((EntityCreatureBase)minion).setMasterTarget(this);
            ((EntityCreatureBase)minion).spawnEventType = this.spawnEventType;
        }
        this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null)
            minion.setRevengeTarget(this.getAttackTarget());
    }

    // ========== Minion Update ==========
    public void onMinionUpdate(EntityLivingBase minion, long tick) {}

    // ========== Minion Death ==========
    public void onMinionDeath(EntityLivingBase minion) {}

    // ========== Minion ==========
    /** Set whether this mob is a minion or not, this should be used if this mob is summoned. **/
    public void setMinion(boolean minion) { this.isMinion = minion; }
    /** Returns whether or not this mob is a minion. **/
    public boolean isMinion() {
        return this.isMinion;
    }

    // ========== Temporary Mob ==========
    /** Make this mob temporary where it will desapwn once the specified duration (in ticks) reaches 0. **/
    public void setTemporary(int duration) {
    	this.temporaryDuration = duration;
    	this.isTemporary = true;
    }
    /** Remove the temporary life duration of this mob, note that this mob will still despawn naturally unless it is set as persistent through other means. **/
    public void unsetTemporary() {
    	this.isTemporary = false;
    	this.temporaryDuration = 0;
    }

    // ========== Minion ==========
    /** Returns true if this mob has a pet entry and is thus bound to another entity. **/
    public boolean isBoundPet() {
        return this.hasPetEntry();
    }

    /** Returns true if this mob has a pet entry. **/
    public boolean hasPetEntry() {
        return this.getPetEntry() != null;
    }

    /** Returns this mob's pet entry if it has one. **/
    public PetEntry getPetEntry() {
        return this.petEntry;
    }

    /** Sets the pet entry for this mob. Mobs with Pet Entries will be removed when te world is reloaded as the pet Entry will spawn a new INSTANCE of them in on load. **/
    public void setPetEntry(PetEntry petEntry) {
        this.petEntry = petEntry;
    }

    /** Returns true if this creature has a pet entry and matches the provided entry type. **/
    public boolean isPetType(String type) {
        if(!this.hasPetEntry())
            return false;
        return type.equals(this.getPetEntry().getType());
    }

    // ========== On Spawn ==========
    /** This is called when the mob is first spawned to the world either through natural spawning or from a Spawn Egg. **/
    public void onFirstSpawn() {
        if(this.hasPetEntry() || this.isMinion())
            return;
        if(CreatureManager.getInstance().config.subspeciesSpawn && !this.creatureInfo.creatureSpawn.disableSubspecies)
    	    this.getRandomSubspecies();
        if(CreatureManager.getInstance().config.randomSizes)
    	    this.getRandomSize();
    }

    // ========== Get Random Subspecies ==========
    public void getRandomSubspecies() {
    	if(this.subspecies == null && !this.isMinion()) {
    		Subspecies randomSubspecies = this.creatureInfo.getRandomSubspecies(this, this.spawnedRare);
    		if(randomSubspecies != null) {
				LycanitesMobs.printDebug("Subspecies", "Setting " + this.getSpeciesName() + " to " + randomSubspecies.getTitle());
				this.applySubspecies(randomSubspecies.index, true);
			}
    		else {
				LycanitesMobs.printDebug("Subspecies", "Setting " + this.getSpeciesName() + " to base species.");
				this.applySubspecies(0, true);
			}
    	}
    }

    // ========== Get Random Size ==========
    public void getRandomSize() {
    	this.setSizeScale(1.0D + (0.35D * (0.5D - this.getRNG().nextDouble())));
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's incremented on each tick, if it's
     * positive, it get's decremented each tick. Don't confuse this with EntityLiving.getAge. With a negative value the
     * Entity is considered a child.
     */
    public int getAge() {
        if (this.world.isRemote) {
            return this.getBoolFromDataManager(BABY) ? -1 : 1;
        }
        else {
            return this.growingAge;
        }
    }


	// ==================================================
	//                       Stats
	// ==================================================
	/** Returns the level of this mob, higher levels have higher stats. **/
	public int getLevel() {
		if(this.getEntityWorld().isRemote) {
			return this.getIntFromDataManager(LEVEL);
		}
		return this.level;
	}

	/** Returns the default starting level to use. **/
	public int getStartingLevel() {
		int startingLevelMin = Math.max(1, CreatureManager.getInstance().config.startingLevelMin);
		if(CreatureManager.getInstance().config.startingLevelMax > startingLevelMin) {
			return startingLevelMin + this.getRNG().nextInt(CreatureManager.getInstance().config.startingLevelMax - startingLevelMin);
		}
		if(CreatureManager.getInstance().config.levelPerDay > 0 && CreatureManager.getInstance().config.levelPerDayMax > 0) {
			int day = (int)Math.floor(this.getEntityWorld().getTotalWorldTime() / 23999D);
			double levelGain = Math.min(CreatureManager.getInstance().config.levelPerDay * day, CreatureManager.getInstance().config.levelPerDayMax);
			startingLevelMin += (int)Math.floor(levelGain);
		}
		if(CreatureManager.getInstance().config.levelPerLocalDifficulty > 0) {
			double levelGain = this.getEntityWorld().getDifficultyForLocation(this.getPosition()).getAdditionalDifficulty();
			startingLevelMin += Math.max(0, (int)Math.floor(levelGain - 1.5D));
		}
		return startingLevelMin;
	}

	/** Sets adn applies the level of this mob refreshing stats, higher levels have higher stats. **/
	public void applyLevel(int level) {
		this.setLevel(level);
		this.refreshStats();
	}

	/** Sets the level of this mob without refreshing stats, used when loading from NBT or from applyLevel(). If a level is changed use applyLevel() instead. **/
	public void setLevel(int level) {
		this.level = level;
		this.dataManager.set(LEVEL, level);
	}

	/** Increases the level of this mob, higher levels have higher stats. **/
	public void addLevel(int level) {
		this.applyLevel(this.level + level);
	}


    // ========= Attack Speeds ==========
	/**
	 * Returns the cooldown time in ticks between melee attacks.
	 * @return Melee attack cooldown ticks.
	 */
	public int getMeleeCooldown() {
		return Math.round((float)(1.0D / this.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D));
	}

	/**
	 * Returns the cooldown time in ticks between ranged attacks.
	 * @return Ranged attack cooldown ticks.
	 */
	public int getRangedCooldown() {
		return Math.round((float)(1.0D / this.getEntityAttribute(RANGED_SPEED).getAttributeValue() * 20.0D));
	}

    // ========= Effect ==========
	/**
	 * When given a base time (in seconds) this will return the scaled time with difficulty and other modifiers taken into account.
	 * @param seconds The base duration in seconds that this effect should last for, this is scaled by stats.
	 * @return The scaled effect duration to use.
	 */
	public int getEffectDuration(int seconds) {
		return Math.round(seconds * (float)this.creatureStats.getEffect() * 20);
    }

	/**
	 * Returns the default amplifier to use for effects.
	 * @param scale The scale to multiplier the amplifier by, use one for default.
	 * @return Potion effects amplifier.
	 */
	public int getEffectAmplifier(float scale) {
		return Math.round((float)this.creatureStats.getAmplifier());
	}

	/**
	 * When given a base effect strength value such as a life drain amount, this will return the scaled value with difficulty and other modifiers taken into account.
	 * @param value The base effect strength to scale.
	 * @return The strength of a special effect.
	 */
	public int getEffectStrength(float value) {
        return Math.round((value * (float)(this.creatureStats.getAmplifier())));
    }


	// ==================================================
	//                    Subspecies
	// ==================================================
	/** Sets the subspecies of this mob by index and refreshes stats. If not a valid ID or 0 it will be set to null which is for base species. **/
	public void applySubspecies(int subspeciesIndex, boolean resetHealth) {
		this.setSubspecies(subspeciesIndex, resetHealth);
		this.refreshStats();
	}

	/** Sets the subspecies of this mob by index without refreshing stats, use applySubspecies() if changing toa  new subspecies. If not a valid ID or 0 it will be set to null which is for base species. **/
	public void setSubspecies(int subspeciesIndex, boolean resetHealth) {
		this.subspecies = this.creatureInfo.getSubspecies(subspeciesIndex);

		if(this.subspecies != null) {
			float scaledExp = this.creatureInfo.experience;
			if ("uncommon".equals(this.subspecies.type))
				scaledExp = Math.round((float) (this.creatureInfo.experience * Subspecies.uncommonExperienceScale));
			else if ("rare".equals(this.subspecies.type))
				scaledExp = Math.round((float) (this.creatureInfo.experience * Subspecies.rareExperienceScale));
			this.experienceValue = Math.round(scaledExp);
			if ("rare".equals(this.subspecies.type)) {
				this.damageLimit = 40;
			}
		}
	}

	/** Gets the subspecies of this mob, will return null if this is a base species mob. **/
	public Subspecies getSubspecies() {
		return this.subspecies;
	}

	/** Gets the subspecies index of this mob.
	 * 0 = Base Subspecies
	 * 1/2 = Uncommon Species
	 * 3+ = Rare Species
	 * Most mobs have 2 uncommon subspecies, some have rare subspecies.
	 * **/
	public int getSubspeciesIndex() {
		return this.getSubspecies() != null ? this.getSubspecies().index : 0;
	}


    // ==================================================
    //                  Battle Phases
    // ==================================================
    /** Called every update, this usually manages which phase this mob is using health but it can use any aspect of the mob to determine the Battle Phase and could even be random. **/
    public void updateBattlePhase() {

    }

    /** Returns the current battle phase. **/
    public int getBattlePhase() {
        return this.battlePhase;
    }

    /** Sets the current battle phase. **/
    public void setBattlePhase(int phase) {
        if(this.getBattlePhase() == phase)
            return;
        this.battlePhase = phase;
        this.refreshBossHealthName();
        this.playPhaseSound();
    }


	// ==================================================
	//                    Transform
	// ==================================================
	/**
	 * Transforms this entity into a new entity instantiated from the given class.
	 * transformClass The entity class to transform into.
	 * partner If not null, various stats, etc will be shared from this partner.
	 * destroyPartner If true and a partner is set, the partner will be removed.
	 * return The transformed entity instance. Null on failure (usually when an invalid class is provided).
	 */
	public EntityLivingBase transform(Class transformClass, Entity partner, boolean destroyPartner) {
		EntityLivingBase transformedEntity = null;
		try {
			transformedEntity = (EntityLivingBase)transformClass.getConstructor(new Class[]{World.class}).newInstance(this.getEntityWorld());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if(transformedEntity == null) {
			return null;
		}

		// Creature Base:
		if(transformedEntity instanceof EntityCreatureBase) {
			EntityCreatureBase transformedCreature = (EntityCreatureBase) transformedEntity;
			transformedCreature.firstSpawn = false;

			// Temporary:
			if (this.isTemporary) {
				transformedCreature.setTemporary(this.temporaryDuration);
			}

			// Minion:
			if(this.isMinion()) {
				transformedCreature.setMinion(true);
			}

			// Master Target:
			if (this.hasMaster()) {
				transformedCreature.setMasterTarget(this.getMasterTarget());
			}

			// With Partner:
			if (partner != null && partner instanceof EntityCreatureBase) {
				EntityCreatureBase partnerCreature = (EntityCreatureBase) partner;
				Subspecies fusionSubspecies = transformedCreature.creatureInfo.getChildSubspecies(this, this.getSubspeciesIndex(), partnerCreature.getSubspecies());
				transformedCreature.applySubspecies(fusionSubspecies != null ? fusionSubspecies.index : 0, true);
				transformedCreature.setSizeScale(this.sizeScale + partnerCreature.sizeScale);

				// Level:
				int transformedLevel = this.getLevel();
				if("lowest".equalsIgnoreCase(CreatureManager.getInstance().config.elementalFusionLevelMix)) {
					if(transformedLevel > partnerCreature.getLevel()) {
						transformedLevel = partnerCreature.getLevel();
					}
				}
				else if("highest".equalsIgnoreCase(CreatureManager.getInstance().config.elementalFusionLevelMix)) {
					if(transformedLevel < partnerCreature.getLevel()) {
						transformedLevel = partnerCreature.getLevel();
					}
				}
				else {
					transformedLevel += partnerCreature.getLevel();
				}
				transformedCreature.applyLevel(Math.round(transformedLevel * (float)CreatureManager.getInstance().config.elementalFusionLevelMultiplier));

				// Tamed:
				if (transformedCreature instanceof EntityCreatureTameable) {
					EntityCreatureTameable fusionTameable = (EntityCreatureTameable) transformedCreature;
					Entity owner = null;
					if(this instanceof EntityCreatureTameable) {
						owner = ((EntityCreatureTameable)this).getPlayerOwner();
					}
					if (owner != null) {
						transformedCreature.applyLevel(transformedLevel);
						fusionTameable.setPlayerOwner((EntityPlayer)owner);
					}

					// Tamed Partner:
					else {
						Entity partnerOwner = null;
						if (partnerCreature instanceof EntityCreatureTameable) {
							partnerOwner = ((EntityCreatureTameable)partnerCreature).getPlayerOwner();
						}
						if (partnerOwner != null) {
							transformedCreature.applyLevel(transformedLevel);
							fusionTameable.setPlayerOwner((EntityPlayer) partnerOwner);

							// Temporary:
							if (partnerCreature.isTemporary) {
								transformedCreature.setTemporary(partnerCreature.temporaryDuration);
							}

							// Minion:
							transformedCreature.setMinion(partnerCreature.isMinion());

							// Master Target:
							if (partnerCreature.hasMaster()) {
								transformedCreature.setMasterTarget(partnerCreature.getMasterTarget());
							}
						}
					}
				}
			}

			// Without Partner:
			else {
				transformedCreature.applySubspecies(this.getSubspeciesIndex(), true);
				transformedCreature.setSizeScale(this.sizeScale);
				transformedCreature.applyLevel(this.getLevel());

				// Tamed:
				if (transformedCreature instanceof EntityCreatureTameable) {
					EntityCreatureTameable fusionTameable = (EntityCreatureTameable) transformedCreature;
					if (this.getOwner() != null && this.getOwner() instanceof EntityPlayer) {
						fusionTameable.setPlayerOwner((EntityPlayer) this.getOwner());
					}
				}
			}
		}

		// Transformed Entity:
		transformedEntity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
		this.getEntityWorld().spawnEntity(transformedEntity);

		// Remove Parts:
		this.setDead();
		if(partner != null && destroyPartner) {
			partner.setDead();
		}

		return transformedEntity;
	}


    // ==================================================
  	//                     Updates
  	// ==================================================
    // ========== Main ==========
    /** The main update method, all the important updates go here. **/
    @Override
    public void onUpdate() {
        super.onUpdate();
        if(this.dataManager != null)
            this.onSyncUpdate();

        if(!this.getEntityWorld().isRemote)
            this.updateHitAreas();

        if(this.despawnCheck()) {
            if(!this.isBoundPet())
        	    this.inventory.dropInventory();
        	this.setDead();
        }

        // Fire Immunity:
        this.isImmuneToFire = !this.canBurn();

        // Not Walking on Land:
        if((!this.canWalk() && !this.isFlying() && !this.isInWater() && this.isMoving()) || !this.canMove())
        	this.clearMovement();

        // Climbing/Flying:
        if(!this.getEntityWorld().isRemote || this.canPassengerSteer()) {
        	this.setBesideClimbableBlock(this.collidedHorizontally);
        	if(!this.onGround && this.flySoundSpeed > 0 && this.ticksExisted % 20 == 0)
        		this.playFlySound();
        }
        if(!this.getEntityWorld().isRemote && this.isFlying() && this.hasAttackTarget() && this.updateTick % 40 == 0) {
            this.leap(0, 0.4D);
        }

        // GUI Refresh Tick:
        if(!this.getEntityWorld().isRemote && this.guiViewers.size() <= 0)
        	this.guiRefreshTick = 0;
        if(!this.getEntityWorld().isRemote && this.guiRefreshTick > 0) {
        	if(--this.guiRefreshTick <= 0) {
        		this.refreshGUIViewers();
        		this.guiRefreshTick = 0;
        	}
        }

        // Boss Health Update:
        if(this.bossInfo != null)
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    // ========== AI ==========
    /** Runs through all the AI tasks this mob has on the update, will update the flight navigator if this mob is using it too. **/
    @Override
    protected void updateAITasks() {
		if(this.useDirectNavigator())
            directNavigator.updateFlight();
        super.updateAITasks();
    }

    // ========== Living ==========
    /** The main update method, behaviour and custom update logic should go here. **/
    @Override
    public void onLivingUpdate() {
		// Enforce Damage Limit:
		if(this.damageLimit > 0) {
			if (this.healthLastTick < 0)
				this.healthLastTick = this.getHealth();
			if (this.healthLastTick - this.getHealth() > this.damageLimit)
				this.setHealth(this.healthLastTick);
			this.healthLastTick = this.getHealth();
			if (!this.getEntityWorld().isRemote && this.updateTick % 20 == 0) {
				this.damageTakenThisSec = 0;
			}
		}

        super.onLivingUpdate();

        this.updateBattlePhase();
        this.updateArmSwingProgress();

        // First Spawn:
        if(!this.getEntityWorld().isRemote && this.firstSpawn) {
            this.onFirstSpawn();
            this.firstSpawn = false;
        }

        // Fixate Target:
		if(!this.getEntityWorld().isRemote && !this.hasFixateTarget() && this.fixateUUID != null) {
			double range = 64D;
			List connections = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(range, range, range));
			Iterator possibleFixateTargets = connections.iterator();
			while(possibleFixateTargets.hasNext()) {
				EntityLivingBase possibleFixateTarget = (EntityLivingBase)possibleFixateTargets.next();
				if(possibleFixateTarget != this && possibleFixateTarget.getUniqueID().equals(this.fixateUUID)) {
					this.setFixateTarget(possibleFixateTarget);
					break;
				}
			}
			this.fixateUUID = null;
		}
		if(this.hasFixateTarget()) {
        	this.setAttackTarget(this.getFixateTarget());
		}

        // Prevent Creative Attack Target:
        if(this.hasAttackTarget()) {
            if(this.getAttackTarget() instanceof EntityPlayer) {
                EntityPlayer targetPlayer = (EntityPlayer)this.getAttackTarget();
                if(targetPlayer.capabilities.isCreativeMode)
                    this.setAttackTarget(null);
            }
        }

        // Fleeing:
        if(this.hasAvoidTarget()) {
        	if(this.currentFleeTime-- <= 0)
        		this.setAvoidTarget(null);
        }

        // Gliding:
        if(!this.onGround && this.motionY < 0.0D) {
            this.motionY *= this.getFallingMod();
        }

        // Sunlight Damage:
        if(!this.getEntityWorld().isRemote && this.daylightBurns() && this.getEntityWorld().isDaytime()) {
        	float brightness = this.getBrightness();
            if(brightness > 0.5F && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
                boolean shouldBurn = true;
                ItemStack helmet = this.inventory.getEquipmentStack("head");
                if(helmet != null) {
                    if(helmet.isItemStackDamageable()) {
                    	helmet.setItemDamage(helmet.getItemDamage() + this.rand.nextInt(2));
                        if(helmet.getItemDamage() >= helmet.getMaxDamage()) {
                            this.renderBrokenItemStack(helmet);
                            this.setCurrentItemOrArmor(4, ItemStack.EMPTY);
                        }
                    }
                    shouldBurn = false;
                }
                if(shouldBurn) {
					this.setFire(8);
				}
            }
        }

        // Water Damage:
        if(!this.getEntityWorld().isRemote && this.waterDamage() && this.isWet() && !this.isInLava()) {
            this.attackEntityFrom(DamageSource.DROWN, 1.0F);
        }

        // Out of Water Suffocation:
        if(!this.getEntityWorld().isRemote && !this.canBreatheAboveWater()) {
	        int currentAir = this.getAir();
	        if(this.isEntityAlive()) {
	        	if((!this.isLavaCreature && !this.waterContact())
	        		|| (this.isLavaCreature && !this.lavaContact())) {
		        	currentAir--;
		            this.setAir(currentAir);
		            if(this.getAir() <= -200) {
		                this.setAir(-180);
		                this.attackEntityFrom(DamageSource.DROWN, 2.0F);
		            }
		        }
		        else {
		            this.setAir(299);
		        }
	        }
        }

        // Time Out Quicker In Light:
        float light = this.getBrightness();
        if(!this.creatureInfo.creatureSpawn.spawnsInLight && light > 0.5F)
            this.idleTime += 2;

	    // Stealth Invisibility:
    	if(!this.getEntityWorld().isRemote) {
	        if(this.isStealthed() && !this.isInvisible())
	        	this.setInvisible(true);
	        else if(!this.isStealthed() && this.isInvisible() && !this.isPotionActive(MobEffects.INVISIBILITY))
                this.setInvisible(false);
    	}
        if(this.isStealthed()) {
        	if(this.stealthPrev != this.isStealthed())
                this.startStealth();
            this.onStealth();
        }
        else if(this.isInvisible() && !this.isPotionActive(MobEffects.INVISIBILITY) && !this.getEntityWorld().isRemote) {
            this.setInvisible(false);
        }
        this.stealthPrev = this.isStealthed();

        // Blocking:
        if(this.currentBlockingTime > 0) {
        	this.currentBlockingTime--;
        }
        if(this.currentBlockingTime < 0)
        	this.currentBlockingTime = 0;

        // Pickup Items:
        if(this.ticksExisted % 20 == 0 && !this.getEntityWorld().isRemote && this.isEntityAlive() && this.canPickupItems())
        	this.pickupItems();

        // Entity Pickups:
        if(!this.getEntityWorld().isRemote && this.pickupEntity != null) {
			if(!this.pickupEntity.isEntityAlive())
				this.dropPickupEntity();
			else if(Math.sqrt(this.getDistance(this.pickupEntity)) > 32D) {
				this.dropPickupEntity();
			}
        }

        // Boss Health Bar:
        this.getBossInfo();

        // Minion To Master Update:
        if(this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityCreatureBase)
            ((EntityCreatureBase)this.getMasterTarget()).onMinionUpdate(this, this.updateTick);

        this.updateTick++;
    }

    // ========== Sync Update ==========
    /** An update that is called to sync things with the client and server such as various entity targets, attack phases, animations, etc. **/
    public void onSyncUpdate() {
    	// Sync Target Status:
    	if(!this.getEntityWorld().isRemote) {
    		byte targets = 0;
    		if(this.getAttackTarget() != null)
    			targets += TARGET_ID.ATTACK.id;
    		if(this.getMasterTarget() != null)
    			targets += TARGET_ID.MASTER.id;
    		if(this.getParentTarget() != null)
    			targets += TARGET_ID.PARENT.id;
    		if(this.getAvoidTarget() != null)
    			targets += TARGET_ID.AVOID.id;
    		if(this.getControllingPassenger() != null)
    			targets += TARGET_ID.RIDER.id;
			if(this.getPickupEntity() != null)
				targets += TARGET_ID.PICKUP.id;
    		this.dataManager.set(TARGET, targets);
    	}

		// Attack Phase:
    	if(!this.getEntityWorld().isRemote)
    		this.dataManager.set(ATTACK_PHASE, this.attackPhase);

    	// Animations Server:
        if(!this.getEntityWorld().isRemote) {
        	byte animations = 0;

        	// Atttacked Animation and Sound:
        	if(this.justAttacked == this.justAttackedTime) {
        		animations += ANIM_ID.ATTACKED.id;
        		this.justAttacked = 0;
        		this.playAttackSound();
        	}

        	// Airborne Animation:
        	if(this.onGround)
        		animations += ANIM_ID.GROUNDED.id;

            // Swimming Animation:
            if(this.inWater)
                animations += ANIM_ID.IN_WATER.id;

        	// Blocking Animation:
        	if(this.isBlocking())
        		animations += ANIM_ID.BLOCKING.id;

        	// Blocking Animation:
        	if(this.isMinion())
        		animations += ANIM_ID.MINION.id;

        	// Extra Animation 01:
        	if(this.extraAnimation01())
        		animations += ANIM_ID.EXTRA01.id;

        	this.dataManager.set(ANIMATION, animations);
        }

        // Animations Client:
        else if(this.getEntityWorld().isRemote) {
        	byte animations = this.getByteFromDataManager(ANIMATION);
        	if(this.justAttacked > 0)
        		this.justAttacked--;
        	else if((animations & ANIM_ID.ATTACKED.id) > 0)
        		this.setJustAttacked();
        	this.onGround = (animations & ANIM_ID.GROUNDED.id) > 0;
            this.inWater = (animations & ANIM_ID.IN_WATER.id) > 0;
        	this.extraAnimation01 = (animations & ANIM_ID.EXTRA01.id) > 0;
        }

        // Is Minion:
        if(this.getEntityWorld().isRemote) {
    		this.isMinion = (this.getByteFromDataManager(ANIMATION) & ANIM_ID.MINION.id) > 0;
        }

        // Subspecies:
        if(!this.getEntityWorld().isRemote) {
    		this.dataManager.set(SUBSPECIES, (byte)this.getSubspeciesIndex());
        }
        else {
        	if(this.getSubspeciesIndex() != this.getByteFromDataManager(SUBSPECIES))
        		this.applySubspecies(this.getByteFromDataManager(SUBSPECIES), false);
        }

        // Size:
        if(!this.getEntityWorld().isRemote) {
    		this.dataManager.set(SIZE, (float)this.sizeScale);
        }
        else {
        	if(this.sizeScale != this.getFloatFromDataManager(SIZE)) {
        		this.setSizeScale(this.getFloatFromDataManager(SIZE));
        	}
        }

        // Arena:
        if(!this.getEntityWorld().isRemote) {
            this.dataManager.set(ARENA, this.getArenaCenter() != null ? Optional.of(this.getArenaCenter()) : Optional.absent());
        }
    }

    // ========== Hit Areas ==========
    public void updateHitAreas() {
        if(!ENABLE_HITAREAS)
            return;
        int hitAreaWidthCount = Math.max(1, Math.round((this.width * this.hitAreaWidthScale) / 4));
        int hitAreaHeightCount = Math.max(1, Math.round((this.height * this.hitAreaHeightScale) / 4));
        if(hitAreaWidthCount < 2 && hitAreaHeightCount < 2) {
            this.hitAreas = null;
            return;
        }

        if(this.hitAreas == null || this.hitAreas[0] == null || this.hitAreas[0][0] == null ||
                this.hitAreas.length != hitAreaHeightCount || this.hitAreas[0].length != hitAreaWidthCount || this.hitAreas[0][0].length != hitAreaWidthCount)
            this.hitAreas = new EntityHitArea[hitAreaHeightCount][hitAreaWidthCount][hitAreaWidthCount];

        for(int y = 0; y < hitAreaHeightCount; y++) {
            for(int x = 0; x < hitAreaWidthCount; x++) {
                for(int z = 0; z < hitAreaWidthCount; z++) {
                    if(y != 0 && y != hitAreaHeightCount - 1 && x != 0 && x != hitAreaWidthCount - 1 && z != 0 && z != hitAreaWidthCount - 1)
                        continue;
                    if(this.hitAreas[y][x][z] == null) {
                        this.hitAreas[y][x][z] = new EntityHitArea(this, (this.width / hitAreaWidthCount) * this.hitAreaWidthScale, (this.height / hitAreaHeightCount) * this.hitAreaHeightScale);
                        this.getEntityWorld().spawnEntity(this.hitAreas[y][x][z]);
                    }
                    /*this.hitAreas[y][x][z].setPositionAndUpdate(
                            this.posX - ((this.width * this.hitAreaScale) / 2) + (((this.width * this.hitAreaScale) / hitAreaWidthCount) / 2) + (((this.width * this.hitAreaScale) / hitAreaWidthCount) * x),
                            this.posY + ((this.height / hitAreaHeightCount) * y),
                            posZ = this.posZ - ((this.width * this.hitAreaScale) / 2) + (((this.width * this.hitAreaScale) / hitAreaWidthCount) / 2) + (((this.width * this.hitAreaScale) / hitAreaWidthCount) * z)
                    );*/
                    this.hitAreas[y][x][z].posX = this.posX - ((this.width * this.hitAreaWidthScale) / 2) + (((this.width * this.hitAreaWidthScale) / hitAreaWidthCount) / 2) + (((this.width * this.hitAreaWidthScale) / hitAreaWidthCount) * x);
                    this.hitAreas[y][x][z].posY = this.posY + ((this.height / hitAreaHeightCount) * this.hitAreaHeightScale * y);
                    this.hitAreas[y][x][z].posZ = this.posZ - ((this.width * this.hitAreaWidthScale) / 2) + (((this.width * this.hitAreaWidthScale) / hitAreaWidthCount) / 2) + (((this.width * this.hitAreaWidthScale) / hitAreaWidthCount) * z);
                    this.hitAreas[y][x][z].rotationYaw = this.rotationYaw;
                }
            }
        }
    }

    // ========== Get Collision Bounding Box ==========
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        if(this.solidCollision)
            return this.getEntityBoundingBox();
        return null;
    }

    // ========== Get Contact Bounding Box ==========
    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return super.getCollisionBox(entity);
    }

    // ========== Get Render Bounding Box ==========
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox();
    }
    
    
    // ==================================================
  	//                     Movement
  	// ==================================================
    /**
		Returns the importance of blocks when pathfinding, also used when checking if this mob can spawn.
	    Returns a float where 0.0F is a standard path, anything higher is a preferred path.
	    For example, animals that spawn on grass return 10.0F for path blocks that are Grass.
	    Mobs that prefer the darkness will return a higher value for darker blocks.
        TODO: Deprecated and now only used when spawning.
    **/
    // ========== Get Block Path Weight ==========
    public float getBlockPathWeight(int par1, int par2, int par3) {
        if(this.creatureInfo.creatureSpawn.spawnsInDark && !this.creatureInfo.creatureSpawn.spawnsInLight)
        	return 0.5F - this.getEntityWorld().getLightBrightness(new BlockPos(par1, par2, par3));
        if(this.creatureInfo.creatureSpawn.spawnsInLight && !this.creatureInfo.creatureSpawn.spawnsInDark)
        	return this.getEntityWorld().getLightBrightness(new BlockPos(par1, par2, par3)) - 0.5F;
    	return 0.0F;
    }
    
    // ========== Use Direct Navigator ==========
    /**
     * Returns true if this entity should use a direct navigator with no pathing.
     * Used mainly for flying 'ghost' mobs that should fly through the terrain.
     */
    public boolean useDirectNavigator() {
    	return false;
    }

    // ========== Should Swim ==========
    /**
     * Returns true if this entity should use swimming movement.
     */
    public boolean shouldSwim() {
        if(!this.isInWater() && !this.isInLava())
            return false;
        if(this.canWade() && this.canBreatheUnderwater()) {
            // If the target is not in water and this entity is at the water surface, don't use water movement:
            boolean targetInWater = true;
            if(this.getAttackTarget() != null)
                targetInWater = this.getAttackTarget().isInWater();
            else if(this.getParentTarget() != null)
                targetInWater = this.getParentTarget().isInWater();
            else if(this.getMasterTarget() != null)
                targetInWater = this.getMasterTarget().isInWater();
            if(!targetInWater) {
                IBlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().up());
                if (blockState.getBlock().isAir(blockState, this.getEntityWorld(), this.getPosition().up())) {
                    return false;
                }
            }
            return true;
        }
        return this.isStrongSwimmer();
    }

	@Override
	public boolean isInLava() {
		AxisAlignedBB boundingBox = this.getEntityBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D);
		if(this.width / 2 > 2 || this.height > 2) {
			double radius =  Math.min((double)this.width / 2, 2);
			double height = Math.min((double)this.height, 2);
			boundingBox = new AxisAlignedBB(this.posX - radius, this.posY, this.posZ - radius, this.posX + radius, this.posY + height, this.posZ + radius);
		}
		return this.world.isMaterialInBB(boundingBox, Material.LAVA);
	}

    // ========== Move with Heading ==========
    /** Moves the entity, redirects to the direct navigator if this mob should use that instead. **/
    @Override
    public void travel(float strafe, float up, float forward) {
    	if(!this.useDirectNavigator()) {
            if(this.isFlying() && !this.isInWater() && !this.isInLava()) {
                this.moveFlyingWithHeading(strafe, forward);
                this.updateLimbSwing();
            }
            else if(this.shouldSwim()) {
                this.moveSwimmingWithHeading(strafe, forward);
                this.updateLimbSwing();
            }
            else {
                super.travel(strafe, up, forward);
            }
        }
    	else {
            this.directNavigator.flightMovement(strafe, forward);
            this.updateLimbSwing();
        }
    }

    /** Updates limb swing animation, used when flying or swimming as their movements don't update it like the standard walking movement. **/
    public void updateLimbSwing() {
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double distanceX = this.posX - this.prevPosX;
        double distanceZ = this.posZ - this.prevPosZ;
        float distance = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ) * 4.0F;
        if (distance > 1.0F) {
            distance = 1.0F;
        }
        this.limbSwingAmount += (distance - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    // ========== Move Flying with Heading ==========
    public void moveFlyingWithHeading(float strafe, float forward) {
        float f = 0.91F;
        if (this.onGround) {
            f = this.getEntityWorld().getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
        }

        float f1 = 0.16277136F / (f * f * f);
        this.moveFlying(strafe, forward, this.onGround ? 0.1F * f1 : 0.02F);
        f = 0.91F;

        if (this.onGround) {
            f = this.getEntityWorld().getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
        }
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)f;
        this.motionY *= (double)f;
        this.motionZ *= (double)f;

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d1 = this.posX - this.prevPosX;
        double d0 = this.posZ - this.prevPosZ;
        float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
        if (f2 > 1.0F) {
            f2 = 1.0F;
        }
        this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    public void moveFlying(float strafe, float forward, float friction) {
        if(!this.isPushedByWater() && this.canWalk() && this.isInWater()) {
            float sliperryness = 0.91F;
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(this.posX, this.getEntityBoundingBox().minY - 1.0D, this.posZ);
            if (this.onGround) {
                sliperryness = this.getEntityWorld().getBlockState(blockpos$pooledmutableblockpos).getBlock().slipperiness * sliperryness * 2;
            }
            float f7 = 0.16277136F / (sliperryness * sliperryness * sliperryness);
            friction = this.getAIMoveSpeed() * f7;
        }
    }

    // ========== Move Swimming with Heading ==========
    public void moveSwimmingWithHeading(float strafe, float forward) {
        super.moveRelative(strafe, 0, forward, 0.1F);
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8999999761581421D;
        this.motionY *= 0.8999999761581421D;
        this.motionZ *= 0.8999999761581421D;
    }

    // ========== Get New Navigator ==========
    /** Called when this entity is constructed for initial navigator. **/
    @Override
    protected PathNavigate createNavigator(World world) {
        return new CreaturePathNavigate(this, world);
    }

    // ========== Get New Move Helper ==========
    /** Called when this entity is constructed for initial move helper. **/
    protected EntityMoveHelper createMoveHelper() {
        return new CreatureMoveHelper(this);
    }

    // ========== Get Navigator ==========
    /** Returns the movement helper that this entity should use. **/
    public PathNavigate getNavigator() {
        return super.getNavigator();
    }

    // ========== Get Move Helper ==========
    /** Returns the movement helper that this entity should use. **/
    public EntityMoveHelper getMoveHelper() {
        return super.getMoveHelper();
    }
    
    // ========== Clear Movement ==========
    /** Cuts off all movement for this update, will clear any pathfinder paths, works with the flight navigator too. **/
    public void clearMovement() {
    	if(!this.useDirectNavigator() && this.getNavigator() != null)
        	this.getNavigator().clearPath();
        else
        	this.directNavigator.clearTargetPosition(1.0D);
    }
    
    // ========== Leash ==========
    /** The leash update that manages all behaviour to do with the entity being leashed or unleashed. **/
    @Override
    protected void updateLeashedState() {
        super.updateLeashedState();
        if(this.getLeashed() && this.getLeashHolder().getEntityWorld() == this.getEntityWorld()) {
            Entity entity = this.getLeashHolder();
            this.setHome((int)entity.posX, (int)entity.posY, (int)entity.posZ, 5);
            float distance = this.getDistance(entity);
            this.testLeash(distance);
            
            if(!this.leashAIActive) {
                this.tasks.addTask(2, this.leashMoveTowardsRestrictionAI);
                if (!this.isStrongSwimmer())
                    this.setPathPriority(PathNodeType.WATER, 0.0F);
                this.leashAIActive = true;
            }

            if(distance > 4.0F)
                this.getNavigator().tryMoveToEntityLiving(entity, 1.0D);

            if(distance > 6.0F) {
                double d0 = (entity.posX - this.posX) / (double)distance;
                double d1 = (entity.posY - this.posY) / (double)distance;
                double d2 = (entity.posZ - this.posZ) / (double)distance;
                this.motionX += d0 * Math.abs(d0) * 0.4D;
                this.motionY += d1 * Math.abs(d1) * 0.4D;
                this.motionZ += d2 * Math.abs(d2) * 0.4D;
            }

            if(distance > 10.0F)
                this.clearLeashed(true, true);
        }
        else if(!this.getLeashed() && this.leashAIActive) {
            this.leashAIActive = false;
            this.tasks.removeTask(this.leashMoveTowardsRestrictionAI);
            if (!this.isStrongSwimmer())
                this.setPathPriority(PathNodeType.WATER, PathNodeType.WATER.getPriority());
            this.detachHome();
        }
    }
    
    /** ========== Pushed By Water ==========
     * Returns true if this mob should be pushed by water currents.
     * This will usually return false if the mob isStrongSwimmer()
     */
    @Override
	public boolean isPushedByWater() {
        return !this.isStrongSwimmer();
    }
    
    // ========== Is Moving ==========
    /** Returns true if this entity is moving towards a destination (doesn't check if this entity is being pushed, etc though). **/
    public boolean isMoving() {
    	if(!this.useDirectNavigator())
        	return this.getNavigator().getPath() != null;
        else
        	return !this.directNavigator.atTargetPosition();
    }

    // ========== Can Be Pushed ==========
    @Override
    public boolean canBePushed() {
        return super.canBePushed();
    }
    
    // ========== Can Be Leashed To ==========
    /** Returns whether or not this entity can be leashed to the specified player. Useful for tamed entites. **/
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) { return false; }
    
    // ========== Test Leash ==========
    /** Called on the update to see if the leash should snap at the given distance. **/
    public void testLeash(float distance) {}
    
    // ========== Set AI Speed ==========
    /** Used when setting the movement speed of this mob, called by AI classes before movement and is given a speed modifier, a local speed modifier is also applied here. **/
    @Override
    public void setAIMoveSpeed(float speed) {
        super.setAIMoveSpeed(speed * this.getAISpeedModifier());
    }
    
    // ========== Movement Speed Modifier ==========
    /** The local speed modifier of this mob, AI classes will also provide their own modifiers that will be multiplied by this modifier. To be used dynamically by various mob behaviours. Not to be confused with getSpeedMultiplier(). **/
    public float getAISpeedModifier() {
    	return 1.0F;
    }
    
    // ========== Falling Speed Modifier ==========
    /** Used to change the falling speed of this entity, 1.0D does nothing. **/
    public double getFallingMod() {
    	return 1.0D;
    }

    // ========== Water Modifier ==========
    /** Modifies movement resistance in water. **/
    @Override
    protected float getWaterSlowDown() {
        if(!this.isPushedByWater())
            return 1F;
        return 0.8F;
    }
    
    // ========== Leap ==========
    /**
     * When called, this entity will leap forwards with the given distance and height.
     * This is very sensitive, a large distance or height can cause the entity to zoom off for thousands of blocks!
     * A distance of 1.0D is around 10 blocks forwards, a height of 0.5D is about 10 blocks up.
     * Tip: Use a negative height for flying and swimming mobs so that they can swoop down in the air or water.
    **/
    public void leap(double distance, double leapHeight) {
    	float yaw = this.rotationYaw;
    	float pitch = this.rotationPitch;
    	/*if(this.getRider() != null) {
    		yaw = this.getRider().rotationYaw;
			pitch = this.getRider().rotationPitch;
		}*/
    	double angle = Math.toRadians(yaw);
        double xAmount = -Math.sin(angle);
        double yAmount = leapHeight;
    	double zAmount = Math.cos(angle);
    	if(this.isFlying()) {
    	    yAmount = Math.sin(Math.toRadians(pitch)) * distance + this.motionY * 0.2D;
        }
        this.addVelocity(
                xAmount * distance + this.motionX * 0.2D,
                yAmount,
                zAmount * distance + this.motionZ * 0.2D
        );
    }
    
    // ========== Leap to Target ==========
    /** 
     * When called, this entity will leap towards the given target entity with the given height.
     * This is very sensitive, a large distance or height can cause the entity to zoom off for thousands of blocks!
     * If the target distance is greater than range, the leap will be cancelled.
     * A distance of 1.0D is around 10 blocks forwards, a height of 0.5D is about 10 blocks up.
     * Tip: Use a negative height for flying and swimming mobs so that they can swoop down in the air or water
    **/
    public void leap(float range, double leapHeight, Entity target) {
        if(target == null)
            return;
        this.leap(range, leapHeight, target.getPosition());
    }

	/**
	 * When called, this entity will leap towards the given target entity with the given height.
	 * This is very sensitive, a large distance or height can cause the entity to zoom off for thousands of blocks!
	 * If the target distance is greater than range, the leap will be cancelled.
	 * A distance of 1.0D is around 10 blocks forwards, a height of 0.5D is about 10 blocks up.
	 * Tip: Use a negative height for flying and swimming mobs so that they can swoop down in the air or water
	 **/
	public void leap(float range, double leapHeight, BlockPos targetPos) {
		if(targetPos == null)
			return;
		double distance = MathHelper.sqrt(targetPos.distanceSq(this.getPosition()));
		if(distance > 2.0F && distance <= range) {
			double xDist = targetPos.getX() - this.getPosition().getX();
			double zDist = targetPos.getZ() - this.getPosition().getZ();
			double xzDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
            /*this.motionX = xDist / xzDist * 0.5D * 0.8D + this.motionX * 0.2D;
            this.motionZ = zDist / xzDist * 0.5D * 0.8D + this.motionZ * 0.2D;
            this.motionY = leapHeight;*/
			this.addVelocity(
					xDist / xzDist * 0.5D * 0.8D + this.motionX * 0.2D,
					leapHeight,
					zDist / xzDist * 0.5D * 0.8D + this.motionZ * 0.2D
			);
		}
	}

	// ========== Strafe ==========
	/**
	 * When called, this entity will strafe sideways with the given distance and height.
	 * This is very sensitive, a large distance or height can cause the entity to zoom off for thousands of blocks!
	 * A distance of 1.0D is around 10 blocks sideways, a height of 0.5D is about 10 blocks up.
	 * Tip: Use a negative height for flying and swimming mobs so that they can swoop down in the air or water.
	 **/
	public void strafe(double distance, double leapHeight) {
		boolean opposite = false;
		if(distance < 0) {
			distance = -distance;
			opposite = true;
		}
		float yaw = this.rotationYaw + (opposite ? -90F : 90F);
		float pitch = this.rotationPitch;
		double angle = Math.toRadians(yaw);
		double xAmount = -Math.sin(angle);
		double yAmount = leapHeight;
		double zAmount = Math.cos(angle);
		if(this.isFlying()) {
			yAmount = Math.sin(Math.toRadians(pitch)) * distance + this.motionY * 0.2D;
		}
		this.addVelocity(
				xAmount * distance + this.motionX * 0.2D,
				yAmount,
				zAmount * distance + this.motionZ * 0.2D
		);
	}
    
    
    // ==================================================
  	//                     Positions
  	// ==================================================
    // ========== Home ==========
    /** Sets the home position for this entity to stay around and the distance it is allowed to stray from. **/
    public void setHome(int x, int y, int z, float distance) {
    	this.setHomePosition(x, y, z);
    	this.setHomeDistanceMax(distance);
    }
    /** Sets the home position for this entity to stay around. **/
    public void setHomePosition(int x, int y, int z) {
    	this.homePosition = new BlockPos(x, y, z);
    }

    /** Sets the distance this mob is allowed to stray from it's home. -1 will turn off the home restriction. **/
    public void setHomeDistanceMax(float newDist) { this.homeDistanceMax = newDist; }

    /** Returns the home position in BlockPos. **/
    public BlockPos getHomePosition() { return this.homePosition; }

    /** Gets the distance this mob is allowed to stray from it's home. -1 is used to unlimited distance. **/
    public float getHomeDistanceMax() { return this.homeDistanceMax; }

    /** Clears the current home position. **/
    public void detachHome() {
    	this.setHomeDistanceMax(-1);
    }

    /** Returns whether or not this mob has a home set. **/
    public boolean hasHome() {
    	return this.getHomePosition() != null && this.getHomeDistanceMax() >= 0;
    }

    /** Returns whether or not the given XYZ position is near this entity's home position, returns true if no home is set. **/
    public boolean positionNearHome(int x, int y, int z) {
        if(!hasHome()) return true;
        return this.getDistanceFromHome(x, y, z) < this.getHomeDistanceMax() * this.getHomeDistanceMax();
    }

    /** Returns the distance that the specified XYZ position is from the home position. **/
    public double getDistanceFromHome(int x, int y, int z) {
    	if(!hasHome()) return 0;
    	return this.homePosition.getDistance(x, y, z);
    }

    /** Returns the distance that the entity's position is from the home position. **/
    public double getDistanceFromHome() {
    	return this.homePosition.getDistance((int) this.posX, (int) this.posY, (int) this.posZ);
    }

    // ========== Arena Center ==========
    /** Returns true if this mob was spawned by an arena and has been set an arena center (typically used arena-based movement by bosses, etc). **/
    public boolean hasArenaCenter() {
        return this.getArenaCenter() != null;
    }

    /** Sets the central arena point for this mob to use. **/
    public void setArenaCenter(BlockPos pos) {
        this.arenaCenter = pos;
    }

    /** Returns the central arena position that this mob is using or null if not set. **/
    public BlockPos getArenaCenter() {
        return this.arenaCenter;
    }

    // ========== Get Wander Position ==========
    /** Takes an initial chunk coordinate for a random wander position and then allows the entity to make changes to the position or react to it. **/
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        return wanderPosition;
    }

    // ========== Restrict Y Height From Ground ==========
    /** Takes an initial coordinate and returns an altered Y position relative to the ground using a minimum and maximum distance. **/
    public int restrictYHeightFromGround(BlockPos coords, int minY, int maxY) {
        int groundY = this.getGroundY(coords);
        int airYMax = Math.min(this.getAirY(coords), groundY + maxY);
        int airYMin = Math.min(airYMax, groundY + minY);
        if(airYMin >= airYMax)
            return airYMin;
        return airYMin + this.getRNG().nextInt(airYMax - airYMin);
    }

    // ========== Get Ground Y Position ==========
    /** Returns the Y position of the ground from the starting X, Y, Z position, this will work for getting the ground of caves or indoor areas too.
     * The Y position returned will be the last air block found before the ground it hit and will thus not be the ground block Y position itself but the air above it. **/
    public int getGroundY(BlockPos pos) {
        int y = pos.getY();
        if(y <= 0)
            return 0;
        IBlockState startBlock = this.getEntityWorld().getBlockState(pos);
        if(startBlock.getBlock().isAir(startBlock, this.getEntityWorld(), pos)) {
            for(int possibleGroundY = Math.max(0, y - 1); possibleGroundY >= 0; possibleGroundY--) {
                IBlockState possibleGroundBlock = this.getEntityWorld().getBlockState(new BlockPos(pos.getX(), possibleGroundY, pos.getZ()));
                if(possibleGroundBlock.getBlock().isAir(possibleGroundBlock, this.getEntityWorld(), new BlockPos(pos.getX(), possibleGroundY, pos.getZ())))
                    y = possibleGroundY;
                else
                    break;
            }
        }
        return y;
    }

    // ========== Get Air Y Position ==========
    /** Returns the Y position of the highest air block from the starting x, y, z position until either a solid block is hit or the sky is accessible. **/
    public int getAirY(BlockPos pos) {
        int y = pos.getY();
        int yMax = this.getEntityWorld().provider.getActualHeight() - 1;
        if(y >= yMax)
            return yMax;
        if(this.getEntityWorld().canBlockSeeSky(pos))
            return yMax;

        IBlockState startBlock = this.getEntityWorld().getBlockState(pos);
        if(startBlock.getBlock().isAir(startBlock, this.getEntityWorld(), pos)) {
            for(int possibleAirY = Math.min(yMax, y + 1); possibleAirY <= yMax; possibleAirY++) {
                IBlockState possibleGroundBlock = this.getEntityWorld().getBlockState(new BlockPos(pos.getX(), possibleAirY, pos.getZ()));
                if(possibleGroundBlock.getBlock().isAir(possibleGroundBlock, this.getEntityWorld(), new BlockPos(pos.getX(), possibleAirY, pos.getZ())))
                    y = possibleAirY;
                else
                    break;
            }
        }
        return y;
    }

    // ========== Get Water Surface Y Position ==========
    /** Returns the Y position of the water surface (the first air block found when searching up in water).
     * If the water is covered by a solid block, the highest Y water position will be returned instead.
     * This will search up to 24 blocks up. **/
        public int getWaterSurfaceY(BlockPos pos) {
        int y = pos.getY();
        if(y <= 0)
            return 0;
        int yMax = this.getEntityWorld().provider.getActualHeight() - 1;
        if(y >= yMax)
            return yMax;
        int yLimit = 24;
        yMax = Math.min(yMax, y + yLimit);
        IBlockState startBlock = this.getEntityWorld().getBlockState(pos);
        if(startBlock.getMaterial() == Material.WATER) {
            int possibleSurfaceY = y;
            for(possibleSurfaceY += 1; possibleSurfaceY <= yMax; possibleSurfaceY++) {
                IBlockState possibleSurfaceBlock = this.getEntityWorld().getBlockState(new BlockPos(pos.getX(), possibleSurfaceY, pos.getZ()));
                if(possibleSurfaceBlock.getBlock().isAir(possibleSurfaceBlock, this.getEntityWorld(), new BlockPos(pos.getX(), possibleSurfaceY, pos.getZ())))
                    return possibleSurfaceY;
                else if(possibleSurfaceBlock.getMaterial() != Material.WATER)
                    return possibleSurfaceY - 1;
            }
            return Math.max(possibleSurfaceY - 1, y);
        }
        return y;
    }

	
	// ==================================================
  	//                        Size
  	// ==================================================
    /** Sets the width and height of this mob. This applies sizeScale to the provided arguments. **/
	@Override
	protected void setSize(float width, float height) {
        width *= (float)this.sizeScale;
        height *= (float)this.sizeScale;
        super.setSize(width, height);
        this.hitAreas = null;
        if(!this.getEntityWorld().isRemote && this.getNavigator() != null && this.getNavigator().getNodeProcessor() instanceof ICreatureNodeProcessor) {
            ((ICreatureNodeProcessor) this.getNavigator().getNodeProcessor()).updateEntitySize(this);
        }
    }

    /** When called, this reapplies the initial width and height of this mob and then applies sizeScale. **/
	public void updateSize() {
        this.setSize(Math.max(this.setWidth, 0.5F), Math.max(this.setHeight, 0.5F));
    }

    /** Sets the size scale and updates the mobs size. **/
	public void setSizeScale(double scale) {
		this.sizeScale = scale;
        this.updateSize();
    }

    /** Returns the model scale for rendering. **/
    public double getRenderScale() {
        return this.sizeScale;
    }
    
    
    // ==================================================
  	//                      Attacks
  	// ==================================================
    // ========== Can Attack ==========
    /** Returns whether or not this mob is allowed to attack the given target class. **/
	@Override
	public boolean canAttackClass(Class targetClass) {
		if(!CreatureManager.getInstance().config.mobsAttackVillagers && targetClass == EntityVillager.class)
			return false;
		return true;
	}

    /** Returns whether or not this mob is allowed to attack the given target entity. **/
	public boolean canAttackEntity(EntityLivingBase targetEntity) {
		if(!CreatureManager.getInstance().config.mobsAttackVillagers && targetEntity instanceof EntityVillager) {
			return false;
		}

		// Players:
        if(targetEntity instanceof EntityPlayer) {
            EntityPlayer targetPlayer = (EntityPlayer)targetEntity;
            if(targetPlayer.capabilities.isCreativeMode) {
				return false;
			}
        }

        // Team:
		if(this.isOnSameTeam(targetEntity)) {
			return false;
		}

        // Creatures:
        if(targetEntity instanceof EntityCreatureBase) {
			EntityCreatureBase targetCreature = (EntityCreatureBase)targetEntity;
            if(targetCreature.getMasterTarget() == this) {
				return false;
			}
            if(!(this instanceof IGroupBoss)) {
                if(!this.isTamed()) {
					if(targetEntity instanceof IGroupBoss) {
						return false;
					}
					if(targetCreature.getSubspeciesIndex() >= 3) {
						return false;
					}
				}
            }
        }

        // Inaccessible From Water:
        if(!this.isStrongSwimmer() && this.isFlying() && targetEntity.isInWater()) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the melee attack range of this creature.
	 * @return The attack range.
	 */
	public double getMeleeAttackRange() {
		double range = this.width * 1.55D;
		if(this.isCurrentlyFlying()) {
			range += 0.5D;
		}
		return range;
	}
	
    // ========== Targets ==========
    /** Gets the attack target of this entity's Master Target Entity. **/
    public EntityLivingBase getMasterAttackTarget() {
    	if(this.masterTarget == null) return null;
    	if(this.masterTarget instanceof EntityLiving)
    		return ((EntityLiving)this.masterTarget).getAttackTarget();
    	return null;
    }

    /** Gets the attack target of this entity's Parent Target Entity. **/
    public EntityLivingBase getParentAttackTarget() {
    	if(this.parentTarget == null) return null;
    	if(this.parentTarget instanceof EntityCreatureBase)
    		return ((EntityCreatureBase)this.parentTarget).getAttackTarget();
    	else if(this.parentTarget instanceof net.minecraft.entity.EntityCreature)
    		return ((net.minecraft.entity.EntityCreature)this.parentTarget).getAttackTarget();
    	return null;
    }

    // ========== Revenge Target ==========
    /**
     * Used when giving this entity a revenge target, usually used when this entity is attacked by another entity.
     * Can be called by anything to tell this entity to attack the given target, however it will still check canAttackClass() and canAttackEntity() first.
     * Mobs with a fleeHealthPercent set above 0 will flee instead if it's health percentage is not above the fleehealthPercent value.
    **/
    @Override
    public void setRevengeTarget(EntityLivingBase entityLivingBase) {
    	boolean aggressiveOverride = CreatureManager.getInstance().config.animalsFightBack;
    	if(!aggressiveOverride && this.extraMobBehaviour != null)
    		aggressiveOverride = this.extraMobBehaviour.aggressiveOverride;
    	if(!aggressiveOverride && this.fleeHealthPercent > 0 && this.getHealth() / this.getMaxHealth() <= this.fleeHealthPercent)
    		this.setAvoidTarget(entityLivingBase);
    	else
    		super.setRevengeTarget(entityLivingBase);
    }
    
    // ========== Melee ==========
    /** Used to make this entity perform a melee attack on the target entity with the given damage scale. **/
    public boolean attackMelee(Entity target, double damageScale) {
    	if(this.isBlocking() && !this.canAttackWhileBlocking()) {
    		return false;
		}

    	if(this.attackEntityAsMob(target, damageScale)) {
    		
    		// Spread Fire:
        	if(this.spreadFire && this.isBurning() && this.rand.nextFloat() < this.creatureStats.getEffect())
        		target.setFire(this.getEffectDuration(4) / 20);

        	// Element Effects:
			if(target instanceof EntityLivingBase && this.creatureStats.getAmplifier() >= 0) {
				this.creatureInfo.element.debuffEntity((EntityLivingBase) target, this.getEffectDuration(1), this.getEffectAmplifier(1));
			}

			this.setJustAttacked();
			return true;
    	}

    	return false;
    }

    // ========== Ranged ==========
    /** Used to make this entity fire a ranged attack at the target entity, range is also passed which can be used. **/
    public void attackRanged(Entity target, float range) {
		if(this.isBlocking() && !this.canAttackWhileBlocking()) {
			return;
		}

		this.setJustAttacked();
    }

	/**
	 * Deals damage to target entity from a projectile fired by this entity.
	 * @param target The target entity to damage.
	 * @param projectile The projectile that caused the damage.
	 * @param damage The amount of damage the projectile deals. This will be scaled by this creature's damage stat.
	 * @return True if damage is dealt.
	 */
	public boolean doRangedDamage(Entity target, EntityThrowable projectile, float damage) {
		damage *= this.creatureStats.getDamage() / 2;
		double pierceDamage = this.creatureStats.getPierce();
		boolean success;
		if(damage <= pierceDamage) {
			success = target.attackEntityFrom(this.getDamageSource((EntityDamageSource) DamageSource.causeThrownDamage(projectile, this).setDamageBypassesArmor()).setDamageIsAbsolute(), damage);
		}
		else {
			int hurtResistantTimeBefore = target.hurtResistantTime;
			target.attackEntityFrom(this.getDamageSource((EntityDamageSource)DamageSource.causeThrownDamage(projectile, this).setDamageBypassesArmor()).setDamageIsAbsolute(), (float)pierceDamage);
			target.hurtResistantTime = hurtResistantTimeBefore;
			damage -= pierceDamage;
			success = target.attackEntityFrom(this.getDamageSource((EntityDamageSource)DamageSource.causeThrownDamage(projectile, this)), damage);
		}

		// Element Effects:
		if(success && target instanceof EntityLivingBase && this.creatureStats.getAmplifier() >= 0) {
			this.creatureInfo.element.debuffEntity((EntityLivingBase) target, this.getEffectDuration(1), this.getEffectAmplifier(1));
		}

		return success;
	}

	/**
	 * Fires a projectile from this mob.
	 * @param projectileClass The class of the projectile. Must extend EntityProjectileBase.
	 * @param target The target entity to fire at. If null, the projectile is fired from the facing direction instead.
	 * @param range The range to the target.
	 * @param angle The angle offset away from the target in degrees.
	 * @param offset The xyz offset to fire from. Note that the Y offset is relative to 75% of this mob's height.
	 * @param velocity The velocity of the projectile.
	 * @param scale The size scale of the projectile.
	 * @param inaccuracy How inaccurate the projectile aiming is.
	 * @return The newly created projectile.
	 */
	public EntityProjectileBase fireProjectile(Class projectileClass, Entity target, float range, float angle, Vec3d offset, float velocity, float scale, float inaccuracy) {
		EntityProjectileBase projectile = null;
		try {
			projectile = (EntityProjectileBase) projectileClass.getConstructor(World.class, EntityLivingBase.class).newInstance(this.getEntityWorld(), this);
		}
		catch (Exception e) {
			LycanitesMobs.printWarning("", "Unable to create a projectile from the class: " + projectileClass);
		}
		if(projectile == null) {
			return null;
		}

		projectile.posX += offset.x;
		projectile.posY -= (this.height / 4) + offset.y;
		projectile.posZ += offset.z;
		projectile.setProjectileScale(scale);

		Vec3d facing = this.getFacingPositionDouble(this.posX, this.posY, this.posZ, range, angle);
		double distanceX = facing.x - this.posX;
		double distanceY = 0;
		double distanceZ = facing.z - this.posZ;
		if(target != null) {
			double targetX = target.posX - this.posX;
			double targetZ = target.posZ - this.posZ;
			double newX = targetX * Math.cos(angle) - targetZ * Math.sin(angle);
			double newY = targetX * Math.sin(angle) + targetZ * Math.cos(angle);
			targetX = newX + this.posX;
			targetZ = newY + this.posZ;

			distanceX = targetX - this.posX;
			distanceY = target.getEntityBoundingBox().minY + (target.height * 0.25D) - projectile.posY;
			distanceZ = targetZ - this.posZ;
		}

		float distanceXZ = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ) * 0.1F;
		projectile.shoot(distanceX, distanceY + distanceXZ, distanceZ, velocity, inaccuracy);
		this.getEntityWorld().spawnEntity(projectile);

		if(projectile.getLaunchSound() != null) {
			this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
		}

		return projectile;
	}
    
    // ========== Phase ==========
    /** Returns the current attack phase of this mob, used when deciding which attack to use and which animations to use. **/
    public byte getAttackPhase() {
    	return this.getByteFromDataManager(ATTACK_PHASE);
    }
    /** Sets the current attack phase of this mobs. **/
    public void setAttackPhase(byte setAttackPhase) { attackPhase = setAttackPhase; }
    /** Moves the attack phase to the next step, will loop back to 0 when the max is passed. **/
    public void nextAttackPhase() {
    	if(++this.attackPhase > (this.attackPhaseMax - 1)) {
			this.attackPhase = 0;
		}
    }
    
    // ========== Deal Damage ==========
    /** Called when attacking and makes this entity actually deal damage to the target entity. Not used by projectile based attacks. **/
    public boolean attackEntityAsMob(Entity target, double damageScale) {
        if(!this.isEntityAlive())
            return false;
        if(target == null)
            return false;
        if(!this.canEntityBeSeen(target))
            return false;

        float damage = this.getAttackDamage(damageScale);
        int i = 0;
        
        //if(target instanceof EntityLivingBase) { // TODO Enchanted Weapon Damage and Knockback
        	//damage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE));
            //i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)target);
        //}
        
        boolean attackSuccess;
        double pierceDamage = this.creatureStats.getPierce();
        if(damage <= pierceDamage)
        	attackSuccess = target.attackEntityFrom(this.getDamageSource(null).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
        else {
        	int hurtResistantTimeBefore = target.hurtResistantTime;
        	target.attackEntityFrom(this.getDamageSource(null).setDamageBypassesArmor().setDamageIsAbsolute(), (float)pierceDamage);
        	target.hurtResistantTime = hurtResistantTimeBefore;
    		damage -= pierceDamage;
        	attackSuccess = target.attackEntityFrom(this.getDamageSource(null), damage);
        }
        
        if(attackSuccess) {
            if(i > 0) {
            	target.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
            
            int fireEnchantDuration = EnchantmentHelper.getFireAspectModifier(this);
            if(fireEnchantDuration > 0)
            	target.setFire(fireEnchantDuration * 4);
        }
        
        return attackSuccess;
    }
    
    // ========== Get Attack Damage ==========
    /** Returns how much attack damage this mob does. **/
    public float getAttackDamage(double damageScale) {
    	float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        damage *= damageScale;
        return damage;
    }

    // ========= Get Damage Source ==========
    /**
     * Returns the damage source to be used by this mob when dealing damage.
     * @param nestedDamageSource This can be null or can be a passed damage source for all kinds of use, mainly for minion damage sources. This will override the damage source for EntityBase+Ageable.
     * @return The damage source to use.
     */
     public DamageSource getDamageSource(EntityDamageSource nestedDamageSource) {
         if(nestedDamageSource != null)
             return nestedDamageSource;
        return DamageSource.causeMobDamage(this);
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Attacked From ==========
    /** Called when this entity has been attacked, uses a DamageSource and damage value. **/
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damage) {
    	if(this.getEntityWorld().isRemote) return false;
        if(this.isEntityInvulnerable(damageSrc)) return false;
        if(!this.isDamageTypeApplicable(damageSrc.getDamageType(), damageSrc, damage)) return false;
        if(!this.isDamageEntityApplicable(damageSrc.getTrueSource())) return false;
        damage *= this.getDamageModifier(damageSrc);
        damage = this.getDamageAfterDefense(damage);
        if(this.isBoss() || this.getSubspeciesIndex() >= 3) {
            if (!(damageSrc.getTrueSource() instanceof EntityPlayer))
                damage *= 0.25F;
        }
        
        if(super.attackEntityFrom(damageSrc, damage)) {
        	this.onDamage(damageSrc, damage);
            Entity entity = damageSrc.getImmediateSource();
            if(entity instanceof EntityThrowable)
            	entity = ((EntityThrowable)entity).getThrower();
            
            if(entity instanceof EntityLivingBase && this.getRider() != entity && this.getRidingEntity() != entity) {
                if(entity != this)
                    this.setRevengeTarget((EntityLivingBase)entity);
                return true;
            }
            else
                return true;
        }
        return false;
    }

    // ========== Attacked From Part ==========
    /** Called when this entity has been attacked from a specific hit area entity, uses the HitArea Entity, DamageSource and damage value. **/
    public boolean attackEntityFromArea(EntityHitArea entityHitArea, DamageSource damageSrc, float damage) {
        return this.attackEntityFrom(damageSrc, damage);
    }
    
    // ========== Defense ==========
    /** This is provided with how much damage this mob will take and returns the reduced (or sometimes increased) damage with defense applied. Note: Damage Modifiers are applied after this. This also applies the blocking ability. **/
    public float getDamageAfterDefense(float damage) {
		float defense = (float)this.creatureStats.getDefense();
    	float minDamage = 0F;
    	if(this.isBlocking()) {
	    	if(defense <= 0)
	    		defense = 1;
	    	defense *= this.getBlockingMultiplier();
    	}
        damage = Math.max(damage - defense, 1);
        if(this.damageMax > 0)
            damage = Math.min(damage, this.damageMax);
    	return Math.max(damage, minDamage);
    }
    
    // ========== On Damage ==========
    /** Called when this mob has received damage. **/
    public void onDamage(DamageSource damageSrc, float damage) {
		this.damageTakenThisSec += damage;
	}
    
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                      Death
   	// ==================================================
    /** Called when this entity dies, drops items from the inventory. **/
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if(!this.dead)
            return;
        if(!this.getEntityWorld().isRemote) {
            if(!this.isBoundPet())
                this.inventory.dropInventory();
            if(damageSource.getTrueSource() != null) {
                if(damageSource.getTrueSource() instanceof EntityPlayer) {
                    try {
                        EntityPlayer player = (EntityPlayer) damageSource.getTrueSource();
                        player.addStat(ObjectManager.getStat(this.creatureInfo.getName() + ".kill"), 1);
                        if (this.isBoss() || this.getRNG().nextDouble() <= CreatureManager.getInstance().config.beastiaryAddOnDeathChance) {
                            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                            if (playerExt != null && !playerExt.getBeastiary().hasFullKnowledge(this.creatureInfo.getName())) {
                                CreatureKnowledge creatureKnowledge = new CreatureKnowledge(playerExt.getBeastiary(), this.creatureInfo.getName(), 1);
                                playerExt.getBeastiary().addToKnowledgeList(creatureKnowledge);
                                playerExt.getBeastiary().sendNewToClient(creatureKnowledge);
                                playerExt.getBeastiary().sendAddedMessage(this.creatureInfo);
                            }
                        }
                    }
                    catch(Exception e) {}
                }
            }
        }
        if(this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityCreatureBase)
            ((EntityCreatureBase)this.getMasterTarget()).onMinionDeath(this);
    }
    
    
    // ==================================================
  	//                      Targets
  	// ==================================================
    /** Returns true if this mob should attack it's attack targets. Used mostly by attack AIs and update methods. **/
    public boolean isAggressive() {
    	if(this.extraMobBehaviour != null)
    		if(this.extraMobBehaviour.aggressiveOverride)
    			return true;
    	return this.isAggressiveByDefault;
    }
    
    /** Returns true if this mob should defend other entities that cry for help. Used mainly by the revenge AI. **/
    public boolean isProtective(Entity entity) { return true; }

    /** Returns true if this mob has an Attack Target. **/
    public boolean hasAttackTarget() {
    	if(!this.getEntityWorld().isRemote)
    		return this.getAttackTarget() != null;
    	else
    		return (this.getByteFromDataManager(TARGET) & TARGET_ID.ATTACK.id) > 0;
    }

    /** Returns this entity's Master Target. **/
    public EntityLivingBase getMasterTarget() { return this.masterTarget; }
    /** Sets this entity's Master Target **/
    public void setMasterTarget(EntityLivingBase setTarget) { this.masterTarget = setTarget; }
    /** Returns true if this mob has a Master Target **/
    public boolean hasMaster() {
    	if(!this.getEntityWorld().isRemote)
    		return this.getMasterTarget() != null;
    	else
    		return (this.getByteFromDataManager(TARGET) & TARGET_ID.MASTER.id) > 0;
    }

    /** Returns this entity's Parent Target. **/
    public EntityLivingBase getParentTarget() { return this.parentTarget; }
    /** Sets this entity's Parent Target **/
    public void setParentTarget(EntityLivingBase setTarget) { this.parentTarget = setTarget; }
    /** Returns true if this mob has a Parent Target **/
    public boolean hasParent() {
    	if(!this.getEntityWorld().isRemote)
    		return this.getParentTarget() != null;
    	else
    		return (this.getByteFromDataManager(TARGET) & TARGET_ID.PARENT.id) > 0;
    }

    /** Returns this entity's Avoid Target. **/
    public EntityLivingBase getAvoidTarget() { return this.avoidTarget; }
    /** Sets this entity's Avoid Target **/
    public void setAvoidTarget(EntityLivingBase setTarget) {
    	this.currentFleeTime = this.fleeTime;
    	this.avoidTarget = setTarget;
    }
    /** Returns true if this mob has a Avoid Target **/
    public boolean hasAvoidTarget() {
    	if(!this.getEntityWorld().isRemote)
    		return this.getAvoidTarget() != null;
    	else
    		return (this.getByteFromDataManager(TARGET) & TARGET_ID.AVOID.id) > 0;
    }

	/** Gets the fixate target of this entity. **/
	public EntityLivingBase getFixateTarget() {
		return this.fixateTarget;
	}
	/** Sets the fixate target of this entity. **/
	public void setFixateTarget(EntityLivingBase target) {
		this.fixateTarget = target;
	}
	/** Returns if the creature has a fixate target. **/
	public boolean hasFixateTarget() {
		return this.getFixateTarget() != null;
	}

    /** Returns this entity's Owner Target. **/
    public Entity getOwner() {
    	return null;
    }

	/**
	 * Gets the unique id of the entity that owns this entity.
	 * @return The owner entity UUID.
	 */
	public UUID getOwnerId() {
		return null;
	}

    /** Returns this entity's Rider Target as an EntityLivingBase or null if it isn't one, see getRiderTarget(). **/
    public EntityLivingBase getRider() {
    	if(this.getControllingPassenger() instanceof EntityLivingBase)
    		return (EntityLivingBase)this.getControllingPassenger();
    	else
    		return null;
    }
    /** Sets this entity's Rider Target **/
    public void setRiderTarget(Entity setTarget) { this.addPassenger(setTarget); }

    /** Returns true if this mob has a Rider Target **/
    public boolean hasRiderTarget() {
    	if(!this.getEntityWorld().isRemote)
    		return this.getControllingPassenger() != null;
    	else
    		return (this.getByteFromDataManager(TARGET) & TARGET_ID.RIDER.id) > 0;
    }

    @Override
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    /** Returns true if this creature can ride the provided entity. **/
    @Override
    protected boolean canBeRidden(Entity entity) {
        if (this.isBoss())
            return false;
        return super.canBeRidden(entity);
    }

    @Override
	public boolean canBeSteered() {
		return false;
	}

	/**
	 * Called when this creature is being targeted by the provided entity and returns if it should be.
	 * @param entity The entity trying to target this entity.
	 * @return True if this entity can be targeted.
	 */
	public boolean canBeTargetedBy(EntityLivingBase entity) {
		if(this.isBoss() && entity instanceof EntityCreatureBase) {
			EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
			if(entityCreature instanceof EntityCreatureTameable) {
				EntityCreatureTameable entityTameable = (EntityCreatureTameable) entity;
				if (entityTameable.getPlayerOwner() != null) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

    
    // ========== Get Facing Coords ==========
    /** Returns the BlockPos in front or behind this entity (using its rotation angle) with the given distance, use a negative distance for behind. **/
    public BlockPos getFacingPosition(double distance) {
        return this.getFacingPosition(this, distance, 0D);
    }

    /** Returns the BlockPos in front or behind the provided entity with the given distance and angle offset (in degrees), use a negative distance for behind. **/
    public BlockPos getFacingPosition(Entity entity, double distance, double angleOffset) {
        return this.getFacingPosition(entity.posX, entity.posY, entity.posZ, distance, entity.rotationYaw + angleOffset);
    }

    /** Returns the BlockPos in front or behind the provided XYZ coords with the given distance and angle (in degrees), use a negative distance for behind. **/
    public BlockPos getFacingPosition(double x, double y, double z, double distance, double angle) {
        angle = Math.toRadians(angle);
    	double xAmount = -Math.sin(angle);
    	double zAmount = Math.cos(angle);
        return new BlockPos(x + (distance * xAmount), y, z + (distance * zAmount));
    }

    /** Returns the XYZ in front or behind the provided XYZ coords with the given distance and angle (in degrees), use a negative distance for behind. **/
    public Vec3d getFacingPositionDouble(double x, double y, double z, double distance, double angle) {
    	if(distance == 0) {
			distance = 1;
		}
        angle = Math.toRadians(angle);
        double xAmount = -Math.sin(angle);
        double zAmount = Math.cos(angle);
        return new Vec3d(x + (distance * xAmount), y, z + (distance * zAmount));
    }


	// ==================================================
	//                       Taming
	// ==================================================
	/**
	 * Returns if this creature is considered to be tamed where it behaves a bit differently.
	 * @return True if tamed.
	 */
	public boolean isTamed() {
		return false;
	}
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    /** Returns whether or not this mob is hostile towards players, changes if a mob is tamed, etc too. **/
    public boolean isHostile() {
    	return this.isAggressive();
    }
    
    /** Overrides the vanilla method when check for EnumCreatureType.monster, it will return true if this mob is hostile and false if it is not regardless of this creature's actual EnumCreatureType. Takes tameable mobs into account too. **/
    @Override
	public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
    	// If the mob spawner is checking then we should return if it should take a place in the mob spawn count or not.
    	if(forSpawnCount) {
    		if(this.isMinion()) // Minions shouldn't take up the spawn count.
    			return false;
    		for(EnumCreatureType creatureType : this.creatureInfo.creatureSpawn.creatureTypes) {
    			if(creatureType == type)
    				return true;
    		}
    		return false;
    	}

		if(type.getCreatureClass() == IMob.class) // If checking for EnumCretureType.monster (IMob) return whether or not this creature is hostile instead.
			return this.isHostile();
        return type.getCreatureClass().isAssignableFrom(this.getClass());
    }
    
    // ========== Movement ==========
    /** Can this entity move currently? **/
    public boolean canMove() { return !this.isBlocking(); }
    /** Can this entity move across land currently? Usually used for swimming mobs to prevent land movement. **/
    public boolean canWalk() { return true; }
    /** Can this entity wade through water (walks on ground and through water but does not freely swim). **/
    public boolean canWade() {
        return true;
    }
    /** Returns true if this entity should swim to the water surface when pathing, by default entities that can't breather underwater will try to surface. **/
    public boolean canFloat() {
        return !this.canBreatheUnderwater();
    }
    /** Returns true if this entity should dive underwater when pathing, by default entities that can breathe underwater will try to dive. **/
    public boolean canDive() {
        return this.canBreatheUnderwater();
    }
    /** Should this entity use smoother, faster swimming? (This doesn't stop the entity from moving in water but is used for smooth flight-like swimming). **/
    public boolean isStrongSwimmer() {
    	if(this.extraMobBehaviour != null)
    		if(this.extraMobBehaviour.swimmingOverride)
    			return true;
    	return false;
    }
    /** Can this entity jump currently? **/
    public boolean canJump() { return !this.isBlocking(); }
    /** Can this entity climb currently? **/
    public boolean canClimb() { return false; }
    /** Is this entity flying currently? If true it will use flight navigation, etc. **/
    public boolean isFlying() {
    	if(this.extraMobBehaviour != null)
    		if(this.extraMobBehaviour.flightOverride)
    			return true;
    	return false;
    }
    /** Returns how high this mob prefers to fly about the ground, usually when randomly wandering. **/
    public int getFlyingHeight() {
        if(!this.isFlying())
            return 20;
        return 0;
    }
    /** Returns true if this creature can safely land from its current position. **/
    public boolean isSafeToLand() {
        if(this.onGround)
            return true;
        if(this.getEntityWorld().getBlockState(this.getPosition().down()).getMaterial().isSolid())
            return true;
        if(this.getEntityWorld().getBlockState(this.getPosition().down(2)).getMaterial().isSolid())
            return true;
        return false;
    }
    /** Returns how high above attack targets this mob should fly when chasing. **/
    public double getFlightOffset() {
        return 0D;
    }
    /** Returns true if this mob is currently flying. **/
    public boolean isCurrentlyFlying() { return this.isFlying(); }
    /** Can this entity by tempted (usually lured by an item) currently? **/
    public boolean canBeTempted() { return this.getSubspeciesIndex() < 3; }
    
    /** Called when the creature has eaten. Some special AIs use this such as EntityAIEatBlock. **/
    public void onEat() {}
    
    // ========== Stealth ==========
    /** Can this entity stealth currently? **/
    public boolean canStealth() {
    	if(this.extraMobBehaviour != null)
    		if(this.extraMobBehaviour.stealthOverride)
    			return true;
    	return false;
    }

    /** Get the current stealth percentage, 0.0F = not stealthed, 1.0F = completely stealthed, used for animation such as burrowing crusks. **/
    public float getStealth() {
    	return this.getFloatFromDataManager(STEALTH);
    }

    /** Sets the current stealth percentage. **/
    public void setStealth(float setStealth) {
    	setStealth = Math.min(setStealth, 1);
    	setStealth = Math.max(setStealth, 0);
    	if(!this.getEntityWorld().isRemote)
    		this.dataManager.set(STEALTH, setStealth);
    }

    /** Returns true if this mob is fully stealthed (1.0F or above). **/
    public boolean isStealthed() {
    	return this.getStealth() >= 1.0F;
    }

    /** Called when this mob is just started stealthing (reach 1.0F or above). **/
    public void startStealth() {}

    /** Called while this mob is stealthed on the update, can be used to clear enemies targets that are targeting this mob, although a new event listener is in place now to handle this. The main EventListener also helps handling anti-targeting. **/
    public void onStealth() {
    	if(!this.getEntityWorld().isRemote) {
    		if(this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityLiving)
    			if(((EntityLiving) this.getAttackTarget()).getAttackTarget() != null)
    				((EntityLiving)this.getAttackTarget()).setAttackTarget(null);
    	}
    }
    
    // ========== Climbing ==========
    /** Returns true if this entity is climbing a ladder or wall, can be used for animation. **/
    @Override
    public boolean isOnLadder() {
    	if(this.isFlying() || (this.isStrongSwimmer() && this.isInWater())) return false;
    	if(this.canClimb()) {
            return (this.getByteFromDataManager(CLIMBING) & 1) != 0;
        }
    	else
    		return super.isOnLadder();
    }
    
    /** Used to set whether this mob is climbing up a block or not. **/
    public void setBesideClimbableBlock(boolean collided) {
    	if(this.canClimb()) {
	        byte climbing = this.getByteFromDataManager(CLIMBING);
	        if(collided)
                climbing = (byte)(climbing | 1);
	        else climbing &= -2;
	        this.dataManager.set(CLIMBING, climbing);
    	}
    }

    /** Returns whether or not this mob is next to a climbable blocks or not. **/
    public boolean isBesideClimbableBlock() {
        return (this.getByteFromDataManager(CLIMBING) & 1) != 0;
    }
    
    // ========== Falling ==========
    /** 
     * Called when the mob has hit the ground after falling, fallDistance is how far it fell and can be translated into fall damage.
     * getFallResistance() is used to reduce falling damage, if it is at or above 100 no falling damage is taken at all.
     * **/
    @Override
    public void fall(float fallDistance, float damageMultiplier) {
        if(this.isFlying())
    		return;
    	fallDistance -= this.getFallResistance();
    	if(this.getFallResistance() >= 100)
    		fallDistance = 0;
    	super.fall(fallDistance, damageMultiplier);
    }
    
    /** Called when this mob is falling, y is how far the mob has fell so far and onGround is true when it has hit the ground. **/
    @Override
    protected void updateFallState(double y, boolean onGround, IBlockState state, BlockPos pos) {
        if(!this.isFlying())
            super.updateFallState(y, onGround, state, pos);
    }
    
    // ========== Blocking ==========
    /** When called, this will set the mob as blocking, can be overridden to randomize the blocking duration. **/
    public void setBlocking() {
    	this.currentBlockingTime = this.blockingTime;
    }
    
    /** Returns true if this mob is blocking. **/
    public boolean isBlocking() {
    	if(this.getEntityWorld().isRemote)
    		return (this.getByteFromDataManager(ANIMATION) & ANIM_ID.BLOCKING.id) > 0;
    	return this.currentBlockingTime > 0;
    }

    /** Returns true if this mob can attack while blocking. **/
    public boolean canAttackWhileBlocking() {
        return false;
    }
    
    /** Returns the blocking defense multiplier, when blocking this mobs defense is multiplied by this, also if this mobs defense is below 1 it will be moved up to one. **/
    public int getBlockingMultiplier() {
    	return 4;
    }
    
    // ========== Pickup ==========
    public boolean canPickupEntity(EntityLivingBase entity) {
        if(this.getPickupEntity() == entity)
            return false;
        if(entity instanceof IGroupBoss || entity instanceof IGroupHeavy)
            return false;
    	ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
		if(extendedEntity == null)
			return false;
		if((entity.getRidingEntity() != null && !(entity.getRidingEntity() instanceof EntityBoat) && !(entity.getRidingEntity() instanceof EntityMinecart)) || entity.getControllingPassenger() != null)
			return false;
        if(ObjectManager.getPotionEffect("weight") != null)
            if((entity).isPotionActive(ObjectManager.getPotionEffect("weight")))
                return false;
		return extendedEntity.pickedUpByEntity == null || extendedEntity.pickedUpByEntity instanceof EntityFear;
    }
    
    public void pickupEntity(EntityLivingBase entity) {
    	ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
		if(extendedEntity != null)
			extendedEntity.setPickedUpByEntity(this);
    	this.pickupEntity = entity;
        this.clearMovement();
    }
    
    public EntityLivingBase getPickupEntity() {
    	return this.pickupEntity;
    }
    
    public boolean hasPickupEntity() {
		if(!this.getEntityWorld().isRemote)
			return this.getPickupEntity() != null;
		else
			return (this.getByteFromDataManager(TARGET) & TARGET_ID.PICKUP.id) > 0;
    }
    
    public void dropPickupEntity() {
    	ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
		if(extendedEntity != null)
			extendedEntity.setPickedUpByEntity(null);
    	this.pickupEntity = null;
    }
    
    public double[] getPickupOffset(Entity entity) {
    	return new double[]{0, 0, 0};
    }
    
    // ========== Destroy Blocks ==========
    public void destroyArea(int x, int y, int z, float strength, boolean drop) {
    	destroyArea(x, y, z, strength, drop, 0);
    }
    public void destroyArea(int x, int y, int z, float strength, boolean drop, int range) {
    	this.destroyArea(x, y, z, strength, drop, range, null, 0);
    }
	public void destroyArea(int x, int y, int z, float strength, boolean drop, int range, EntityPlayer player, int chain) {
		range = Math.max(range -1, 0);
		for(int w = -((int)Math.ceil(this.width) - range); w <= (Math.ceil(this.width) + range); w++) {
			for (int d = -((int) Math.ceil(this.width) - range); d <= (Math.ceil(this.width) + range); d++) {
				for (int h = 0; h <= Math.ceil(this.height); h++) {
					BlockPos breakPos = new BlockPos(x + w, y + h, z + d);
					IBlockState blockState = this.getEntityWorld().getBlockState(breakPos);
					float hardness = blockState.getBlockHardness(this.getEntityWorld(), breakPos);
					Material material = blockState.getMaterial();
					if (hardness >= 0 && strength >= hardness && strength >= blockState.getBlock().getExplosionResistance(this) && material != Material.WATER && material != Material.LAVA) {
						// If a player is set this is from a spawner in which case don't destroy the central block.
						if(player == null || !(w == 0 && h == 0 && d == 0)) {
							SpawnerEventListener.getInstance().onBlockBreak(this.getEntityWorld(), breakPos, blockState, player, chain);
							this.getEntityWorld().destroyBlock(breakPos, drop);
						}
					}
				}
			}
		}
	}
	public void destroyAreaBlock(int x, int y, int z, Class<? extends Block> blockClass, boolean drop, int range) {
		for(int w = -((int)Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++)
			for(int d = -((int)Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++)
				for(int h = 0; h <= Math.ceil(this.height); h++) {
					IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x + w, y + h, z + d));
					if(blockClass.isInstance(blockState.getBlock())) {
						this.getEntityWorld().destroyBlock(new BlockPos(x + w, y + h, z + d), drop);
					}
				}
	}
    
    // ========== Extra Animations ==========
    /** An additional animation boolean that is passed to all clients through the animation mask. **/
    public boolean extraAnimation01() { return this.extraAnimation01; }
    
    
    // ==================================================
   	//                      Drops
   	// ==================================================
    // ========== Item ID ==========
    /** Gets the item ID of what this mob mostly drops. This is provided for compatibility but is not used by the DropRate code. **/
    @Override
    protected Item getDropItem() {
        if(this.drops != null && this.drops.get(0) != null && !this.isMinion() && !this.isBoundPet())
        	return this.drops.get(0).itemStack.getItem();
        else
        	return null;
    }
    
    // ========== Drop Items ==========
    /** Cycles through all of this entity's DropRates and drops random loot, usually called on death. If this mob is a minion, this method is cancelled. **/
    @Override
    protected void dropFewItems(boolean playerKill, int lootLevel) {
    	if(this.getEntityWorld().isRemote || this.isMinion() || this.isBoundPet()) return;
    	int subspeciesScale = 1;
    	if(this.getSubspeciesIndex() > 2)
    		subspeciesScale = Subspecies.rareDropScale;
    	else if(this.getSubspeciesIndex() > 0)
    		subspeciesScale = Subspecies.uncommonDropScale;

    	for(ItemDrop itemDrop : this.drops) {
            if(itemDrop.subspeciesID >= 0 && itemDrop.subspeciesID != this.getSubspeciesIndex())
                continue;
    		int quantity = itemDrop.getQuantity(this.rand, lootLevel);
            if(itemDrop.subspeciesID < 0)
                quantity *= subspeciesScale;
    		if(this.extraMobBehaviour != null && this.extraMobBehaviour.itemDropMultiplierOverride != 1)
    			quantity = Math.round((float)quantity * (float)this.extraMobBehaviour.itemDropMultiplierOverride);
    		ItemStack dropStack = null;
    		if(quantity > 0)
    			dropStack = itemDrop.getItemStack(this, quantity);
    		if(dropStack != null)
    			this.dropItem(dropStack);
    	}
    }
    
    // ========== Drop Item ==========
    /** Tells this entity to drop the specified itemStack, used by DropRate and InventoryCreature, can be used by anything though. **/
    public void dropItem(ItemStack itemStack) {
    	if(itemStack.getItem() instanceof ItemEquipmentPart) {
			((ItemEquipmentPart)itemStack.getItem()).initializePart(this.world, itemStack);
		}
    	this.entityDropItem(itemStack, 0.0F);
    }

    // ========== Entity Drop Item ==========
    /** The vanilla item drop method, overridden to make use of the EntityItemCustom class. I recommend using dropItem() instead. **/
    @Override
    public EntityItem entityDropItem(ItemStack itemStack, float heightOffset) {
        if(itemStack.getCount() != 0) {
            EntityItemCustom entityItem = new EntityItemCustom(this.getEntityWorld(), this.posX, this.posY + (double)heightOffset, this.posZ, itemStack);
            entityItem.setPickupDelay(10);
            this.applyDropEffects(entityItem);
            
            if(captureDrops) {
                capturedDrops.add(entityItem);
            }
            else {
                this.getEntityWorld().spawnEntity(entityItem);
            }
            return entityItem;
        }
        else {
            return null;
        }
    }
    
    // ========== Apply Drop Effects ==========
    /** Used to add effects or alter the dropped entity item. **/
    public void applyDropEffects(EntityItemCustom entityItem) {}
    
    
    // ==================================================
    //                     Interact
    // ==================================================
    // ========== GUI ==========
    /** This adds the provided PlayerEntity to the guiViewers array list, where on the next GUI refresh it will open the GUI. **/
    public void openGUI(EntityPlayer player) {
    	if(this.getEntityWorld().isRemote)
    		return;
    	this.addGUIViewer(player);
    	this.refreshGUIViewers();
    	this.openGUIToPlayer(player);
    }
    
    /** This adds the provided PlayerEntity to the guiViewers array list, where on the next GUI refresh it will open the GUI. **/
    public void addGUIViewer(EntityPlayer player) {
    	if(!this.getEntityWorld().isRemote)
    		this.guiViewers.add(player);
    }
    
    /** This removes the provided PlayerEntity from the guiViewers array list. **/
    public void removeGUIViewer(EntityPlayer player) {
    	if(!this.getEntityWorld().isRemote)
    		this.guiViewers.remove(player);
    }
    
    /** Called when all players viewing their entity's gui need to be refreshed. Usually after a GUI command on inventory change. Should be called using scheduleGUIRefresh(). **/
    public void refreshGUIViewers() {
    	if(this.getEntityWorld().isRemote)
    		return;
    	if(this.guiViewers.size() > 0) {
        	for(EntityPlayer player : this.guiViewers.toArray(new EntityPlayer[this.guiViewers.size()])) {
        		if(player.openContainer != null && player.openContainer instanceof ContainerCreature) {
        			if(((ContainerCreature)player.openContainer).creature == this)
        				this.openGUIToPlayer(player);
        			else
        				this.removeGUIViewer(player);
        		}
        	}
    	}
    }
    
    /** Actually opens the GUI to the player, should be used by openGUI() for an initial opening and then by refreshGUIViewers() for constant updates. **/
    public void openGUIToPlayer(EntityPlayer player) {
    	if(player != null)
    		player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.ENTITY.id, this.getEntityWorld(), this.getEntityId(), 0, 0);
    }
    
    /** Schedules a GUI refresh, normally takes 2 ticks for everything to update for display. **/
    public void scheduleGUIRefresh() {
    	this.guiRefreshTick = this.guiRefreshTime + 1;
    }

    /** The main interact method that is called when a player right clicks this entity. **/
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
	    if(assessInteractCommand(getInteractCommands(player, player.getHeldItem(hand)), player, player.getHeldItem(hand)))
	    	return true;
	    return super.processInteract(player, hand);
    }

    // ========== Assess Interact Command ==========
    /** Performs the best possible command and returns true or false if there isn't one. **/
    public boolean assessInteractCommand(HashMap<Integer, String> commands, EntityPlayer player, ItemStack itemStack) {
    	if(commands.isEmpty())
    		return false;
    	int priority = 100;
    	for(int testPriority : commands.keySet())
    		if(testPriority < priority)
    			priority = testPriority;
    	if(!commands.containsKey(priority))
    		return false;
    	performCommand(commands.get(priority), player, itemStack);
    	return true;
    }
    
    // ========== Get Interact Commands ==========
    /** Gets a map of all possible interact events with the key being the priority, lower is better. **/
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<>();
    	
    	// Item Commands:
    	if(itemStack != null) {
    		// Leash:
    		if(itemStack.getItem() == Items.LEAD && this.canBeLeashedTo(player))
    			commands.put(CMD_PRIOR.ITEM_USE.id, "Leash");
    		
    		// Name Tag:
    		if(itemStack.getItem() == Items.NAME_TAG) {
    			if(this.canNameTag(player))
    				return new HashMap<>(); // Cancels all commands so that vanilla can take care of name tagging.
    			else
    				commands.put(CMD_PRIOR.ITEM_USE.id, "Name Tag"); // Calls nothing and therefore cancels name tagging.
    		}
    		
    		// Coloring:
    		if(this.canBeColored(player) && itemStack.getItem() == Items.DYE)
    			commands.put(CMD_PRIOR.ITEM_USE.id, "Color");
    				
    	}
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    /** Performs the given interact command. Could be used outside of the interact method if needed. **/
    public void performCommand(String command, EntityPlayer player, ItemStack itemStack) {
    	
    	// Leash:
    	if("Leash".equals(command)) {
    		this.setLeashHolder(player, true);
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Name Tag:
    	// Vanilla takes care of this, it is in getInteractCommands so that other commands don't override it.
    	
    	// Color:
    	if("Color".equals(command)) {
    		int colorID = 15 - itemStack.getItemDamage();
            if(colorID != this.getColor()) {
                this.setColor(colorID);
        		this.consumePlayersItem(player, itemStack);
            }
    	}
    }
    
    // ========== Can Name Tag ==========
    /** Returns true if this mob can be given a new name with a name tag by the provided player entity. **/
    public boolean canNameTag(EntityPlayer player) {
    	return true;
    }
    
    // ========== Get Render Name Tag ==========
    /** Gets whether this mob should always display its nametag client side. **/
    @SideOnly(Side.CLIENT)
    public boolean getAlwaysRenderNameTagForRender() {
        if(this.renderSubspeciesNameTag() && this.getSubspecies() != null)
    		return CreatureManager.getInstance().config.subspeciesTags;
        return super.getAlwaysRenderNameTagForRender();
    }
    
    // ========== Render Subspecies Name Tag ==========
    /** Gets whether this mob should always display its nametag if it's a subspecies. **/
    public boolean renderSubspeciesNameTag() {
    	return true;
    }
    
    // ========== Consume Player's Item ==========
    /** Consumes 1 item from the the item stack currently held by the specified player. **/
    public void consumePlayersItem(EntityPlayer player, ItemStack itemStack) {
    	consumePlayersItem(player, itemStack, 1);
    }
    /** Consumes the specified amount from the item stack currently held by the specified player. **/
    public void consumePlayersItem(EntityPlayer player, ItemStack itemStack, int amount) {
    	if(!player.capabilities.isCreativeMode)
            itemStack.setCount(Math.max(0, itemStack.getCount() - amount));
        if(itemStack.getCount() <= 0)
        	player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
    }

    // ========== Replace Player's Item ==========
    /** Replaces 1 of the specified itemstack with a new itemstack. **/
    public void replacePlayersItem(EntityPlayer player, ItemStack itemStack, ItemStack newStack) {
    	replacePlayersItem(player, itemStack, 1, newStack);
    }
    /** Replaces the specified itemstack and amount with a new itemstack. **/
    public void replacePlayersItem(EntityPlayer player, ItemStack itemStack, int amount, ItemStack newStack) {
    	if(!player.capabilities.isCreativeMode)
            itemStack.setCount(Math.max(0, itemStack.getCount() - amount));
    	
        if(itemStack.getCount() <= 0)
    		 player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
         
    	 else if(!player.inventory.addItemStackToInventory(newStack))
        	 player.dropItem(newStack, false);
    	
    }
    
    // ========== Perform GUI Command ==========
    public void performGUICommand(EntityPlayer player, byte guiCommandID) {
    	scheduleGUIRefresh();
    }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    /** Returns true if this mob is able to carry items. **/
    public boolean canCarryItems() { return getInventorySize() > 0; }
    /** Returns the current size of this mob's inventory. (Some mob inventories can vary in size such as mounts with and without bag items equipped.) **/
    public int getInventorySize() { return this.inventory.getSizeInventory(); }
    /** Returns the maximum possible size of this mob's inventory. (The creature inventory is not actually resized, instead some slots are locked and made unavailalbe.) **/
    public int getInventorySizeMax() { return Math.max(this.getNoBagSize(), this.getBagSize()); }
    /** Returns true if this mob is equipped with a bag item. **/
    public boolean hasBag() {
    	return this.inventory.getEquipmentStack("bag") != null;
    }
    /** Returns the size of this mob's inventory when it doesn't have a bag item equipped. **/
    public int getNoBagSize() {
    	if(this.extraMobBehaviour != null)
    		if(this.extraMobBehaviour.inventorySizeOverride > 0)
    			return this.extraMobBehaviour.inventorySizeOverride;
    	return 0;
    }
    /** Returns the size that this mob's inventory increases by when it is provided with a bag item. (Look at this as the size of the bag item, not the new total creature inventory size.) **/
    public int getBagSize() { return 5; }
    
    /** Returns true if this mob is able to pick items up off the ground. **/
    public boolean canPickupItems() {
    	if(this.extraMobBehaviour != null)
    		if(this.extraMobBehaviour.itemPickupOverride)
    			return true;
    	return false;
    }
    /** Returns how much of the specified item stack this creature's inventory can hold. (Stack size, not empty slots, this allows the creature to merge stacks when picking up.) **/
    public int getSpaceForStack(ItemStack pickupStack) {
    	return this.inventory.getSpaceForStack(pickupStack);
    }

    /** Returns true if the player is allowed to equip this creature with items such as armor or saddles. **/
    public boolean canEquip() {
        return this.creatureInfo.isTameable();
    }
    
    // ========== Set Equipment ==========
    // Vanilla Conversion: 0 = Weapon/Item,  1 = Feet -> 4 = Head
    /**
     * A 1.7.10 vanilla method for setting this mobs equipment, takes a slot ID and a stack.
     * 0 = Weapons, Tools or the item to hold out (like how vanilla zombies hold dropped items).
     * 1 = Feet, 2 = Legs, 3 = Chest and 4 = Head
     * 100 = Not used by vanilla but will convert to the bag slot for other mods to use.
     **/
    public void setCurrentItemOrArmor(int slot, ItemStack itemStack) {
        String type = "item";
    	if(slot == 0) type = "weapon";
    	if(slot == 1) type = "feet";
    	if(slot == 2) type = "legs";
    	if(slot == 3) type = "chest";
    	if(slot == 4) type = "head";
    	if(slot == 100) type = "bag";
    	this.inventory.setEquipmentStack(type, itemStack);
    }

    // ========== Get Equipment ==========
    /**
     * Returns the equipment grade, used mostly for texturing the armor.
     * For INSTANCE "gold" is returned if it is wearing gold chest armor.
     * Type is a string that is the equipment slot, it can be: feet, legs, chest or head. All lower case.
    **/
    public String getEquipmentName(String type) {
    	if(this.inventory.getEquipmentGrade(type) != null)
    		return type + this.inventory.getEquipmentGrade(type);
    	return null;
    }
    
    // ========== Get Total Armor Value ==========
    /** Returns the total armor value of this mob. **/
    @Override
    public int getTotalArmorValue() {
    	return this.inventory.getArmorValue();
    }
    
    // ========== Pickup Items ==========
    /** Called on the update if this mob is able to pickup items. Searches for all nearby item entities and picks them up. **/
    public void pickupItems() {
    	 List list = this.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D));
         Iterator iterator = list.iterator();

         while (iterator.hasNext()) {
             EntityItem entityItem = (EntityItem)iterator.next();
             if(!entityItem.isDead && entityItem.getItem() != null) {
            	 ItemStack itemStack = entityItem.getItem();
            	 int space = this.getSpaceForStack(itemStack);
            	 if(space > 0) {
            		 this.onPickupStack(itemStack);
            		 this.doItemPickup(entityItem);
            	 }
             }
         }
    }

    /** Called when this mob picks up an item entity, provides the itemStack it has picked up. **/
    public void onPickupStack(ItemStack itemStack) {}
    
    public void doItemPickup(EntityItem entityItem) {
    	if(!entityItem.isDead && entityItem.getItem() != null) {
    		ItemStack leftoverStack = this.inventory.autoInsertStack(entityItem.getItem());
    		if(leftoverStack != null)
    			entityItem.setItem(leftoverStack);
    		else
    			entityItem.setDead();
    	}
    }
    
    
    // ==================================================
  	//                     Immunities
  	// ==================================================
    // ========== Damage ==========
    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
    	// Damage Limit:
    	if(this.damageLimit > 0) {
			if (this.damageTakenThisSec >= this.damageLimit)
				return true;
		}
        return super.isEntityInvulnerable(source);
    }

    /** Returns whether or not the given damage type is applicable, if not no damage will be taken. **/
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(("inWall".equals(type) || "cactus".equals(type)) && (this.getSubspeciesIndex() >= 3 || this.isBoss()))
            return false;
		if("inWall".equals(type))
			return !CreatureManager.getInstance().config.suffocationImmunity;
		if("drown".equals(type))
			return !CreatureManager.getInstance().config.drownImmunity;
        return true;
    }

    /** Returns whether or not this entity can be harmed by the specified entity. **/
    public boolean isDamageEntityApplicable(Entity entity) {
        if(this.isBoss()) {
            if(entity == null)
                return false;
            return this.getDistance(entity) <= this.bossRange;
        }
        return true;
    }

    /** Returns whether or not the specified potion effect can be applied to this entity. **/
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        return this.creatureInfo.element.isEffectApplicable(potionEffect);
    }

    /** Returns whether or not this entity can be set on fire, this will block both the damage and the fire effect, use isDamageTypeApplicable() to block fire but keep the effect. **/
    public boolean canBurn() {
    	if(this.extraMobBehaviour != null)
    		if(this.extraMobBehaviour.fireImmunityOverride)
    			return false;
    	return this.creatureInfo.element.canBurn();
    }

    /** Returns true if this mob should be damaged by the sun. **/
    public boolean daylightBurns() { return false; }

    /** Returns true if this mob should be damaged by extreme cold such as from ooze. **/
    public boolean canFreeze() {
    	if(this instanceof IGroupIce) {
    		return false;
		}
		return this.creatureInfo.element.canFreeze();
    }

    /** Returns true if this mob should be damaged by water. **/
    public boolean waterDamage() { return false; }
    
    // ========== Environmental ==========
    /** If true, this mob isn't slowed down by webs. **/
    public boolean webProof() { return false; }
    /** If webProof() is false, this mob will be affected by webbing on the update that this is called. **/
    @Override
    public void setInWeb() { if(!webProof()) super.setInWeb(); }
    
    // Breathing:
    /** If true, this mob wont lose air when underwater. **/
    @Override
    public boolean canBreatheUnderwater() {
    	if(this.extraMobBehaviour != null)
    		if(this.extraMobBehaviour.waterBreathingOverride)
    			return true;
    	return false;
    }
    /** If false, this mob will lose air when above water or lava if isLavaCreature is true. **/
    public boolean canBreatheAboveWater() { return true; }
    /** Sets the current amount of air this mob has. **/
	@Override
	public void setAir(int air) {
		if(air == 300 && !this.canBreatheAboveWater()) return;
    	super.setAir(air);
    }
	
	/** Returns true if this mob is in water. If this mob is a lava creature, this will return true if it is in lava too.
	 * Use waterContact() or lavaContact() to check for damage, speed boosts, etc.
	**/
	@Override
	public boolean isInWater() {
		if(this.isLavaCreature)
			return this.isInLava() || super.isInWater();
		else
			return super.isInWater();
	}
    
    /** Returns true if this mob is in water or the rain. Uses the vanilla isWet() but takes dripping leaves, etc into account. **/
    public boolean waterContact() {
    	if(this.isWet())
    		return true;
    	if(this.getEntityWorld().isRaining() && !this.isBlockUnderground((int)this.posX, (int)this.posY, (int)this.posZ))
    		return true;
    	return false;
    }
    
    /** Returns true if this mob is in lava. TODO Remove as it is now replaced with isInLava() **/
    public boolean lavaContact() {
    	return this.isInLava();
    }
    
    /** Returns true if the target location has a block that this mob can breathe in (air, water, lava, depending on the creature). **/
    public boolean canBreatheAtLocation(BlockPos pos) {
    	IBlockState blockState = this.getEntityWorld().getBlockState(pos);
    	if(blockState == null)
    		return true;
    	if(this.canBreatheAboveWater() && blockState.getMaterial() == Material.AIR)
    		return true;
    	if(this.canBreatheUnderwater()) {
	    	if(!this.isLavaCreature && blockState.getMaterial() == Material.WATER)
	    		return true;
	    	if(this.isLavaCreature && blockState.getMaterial() == Material.LAVA)
	    		return true;
    	}
    	return false;
    }
	
	/** Returns true if the specified xyz coordinate is in water swimmable by this mob. (Checks for lava for lava creatures).
	 * @param x Block x position.
	 * @param y Block y position.
	 * @param z Block z position.
	 * @return True if swimmable.
	 */
	public boolean isSwimmable(int x, int y, int z) {
        IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y, z));
		if(blockState == null)
			return false;
		if(this.isLavaCreature && Material.LAVA.equals(blockState.getMaterial()))
			return true;
		else if(Material.WATER.equals(blockState.getMaterial()))
			return true;
		return false;
	}
    
    /** Returns how many extra blocks this mob can fall for, the default is around 3.0F I think, if this is set to or above 100 then this mob wont receive falling damage at all. **/
    public float getFallResistance() {
    	return 0;
    }
    
    
    // ==================================================
  	//                     Utilities
  	// ==================================================
    // ========== Get Light Type ==========
    /** Returns a light rating for the light level of this mob's current position.
     * Dark enough for spawnsInDarkness: 0 = Dark, 1 = Dim
     * Light enough for spawnsInLight: 2 = Light, 3 = Bright
    **/
    public byte testLightLevel() {
    	return testLightLevel(this.getPosition());
    }

    /** Returns a light rating for the light level the specified XYZ position.
     * Dark enough for spawnsInDarkness: 0 = Dark, 1 = Dim
     * Light enough for spawnsInLight: 2 = Light, 3 = Bright
    **/
    public byte testLightLevel(BlockPos pos) {
        IBlockState spawnBlockState = this.getEntityWorld().getBlockState(pos);
        if(pos.getY() < 0)
            return 0;
        if(spawnBlockState != null && spawnBlockState.getMaterial() == Material.WATER && CreatureManager.getInstance().spawnConfig.useSurfaceLightLevel)
            pos = new BlockPos(pos.getX(), this.getWaterSurfaceY(pos), pos.getZ());
        else
            pos = new BlockPos(pos.getX(), this.getGroundY(pos), pos.getZ());

        int light = this.getEntityWorld().getLightFromNeighbors(pos);
        if (this.getEntityWorld().isThundering()) {
            int originalSkylight = this.getEntityWorld().getSkylightSubtracted();
            this.getEntityWorld().setSkylightSubtracted(10);
            light = this.getEntityWorld().getLightFromNeighbors(pos);
            this.getEntityWorld().setSkylightSubtracted(originalSkylight);
        }

        if(light == 0) return 0;
        if(light <= 7) return 1;
        if(light <= 14) return 2;
        return 3;
    }
    
    /** A client and server friendly solution to check if it is daytime or not. **/
    public boolean isDaytime() {
    	if(!this.getEntityWorld().isRemote)
    		return this.getEntityWorld().isDaytime();
    	long time = this.getEntityWorld().getWorldTime();
    	if(time < 12500)
    		return true;
    	if(time >= 12542 && time < 23460)
    		return false;
    	return true;
    }
    
    // Nearby Creature Count:
    /** Returns how many entities of the specified class around within the specified ranged, used mostly for mobs that summon other mobs and other group behaviours. **/
    public int nearbyCreatureCount(Class targetClass, double range) {
    	return this.getNearbyEntities(Entity.class, targetClass, range).size();
    }
    
    // ========== Creature Attribute ==========
    /** Returns this creature's attriute. **/
   	@Override
    public EnumCreatureAttribute getCreatureAttribute() { return this.attribute; }

    // ========== Mounted Y Offset ==========
    /** A Y Offset used to position the mob that is riding this mob. **/
   	@Override
    public double getMountedYOffset() {
        return super.getMountedYOffset() - 0.5D;
    }
    
   	// ========== Get Nearby Entities ==========
    /** Get entities that are near this entity. **/
    public <T extends Entity> List<T> getNearbyEntities(Class <? extends T > clazz, final Class filterClass, double range) {
        return this.getEntityWorld().getEntitiesWithinAABB(clazz, this.getEntityBoundingBox().grow(range, range, range), (Predicate<Entity>) entity -> {
			if(filterClass == null)
				return true;
			return filterClass.isAssignableFrom(entity.getClass());
		});
    }

    // ========== Get Nearest Entity ==========
    /** Get the entity closest to this entity. **/
    public <T extends Entity> T getNearestEntity(Class <? extends T > clazz, Class filterClass, double range, boolean canAttack) {
        List aoeTargets = this.getNearbyEntities(clazz, filterClass, range);
        if(aoeTargets.size() == 0)
            return null;
        double nearestDistance = range + 10;
        T nearestEntity = null;
        for(Object entityObj : aoeTargets) {
            T targetEntity = (T)entityObj;
            if(canAttack && (!(targetEntity instanceof EntityLivingBase) || !this.canAttackEntity((EntityLivingBase)targetEntity)))
                continue;
            if(targetEntity == this.getControllingPassenger())
                continue;
            double distance = this.getDistance(targetEntity);
            if(distance < nearestDistance) {
                nearestDistance = distance;
                nearestEntity = targetEntity;
            }
        }
        return nearestEntity;
    }
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
    	if(nbtTagCompound.hasKey("FirstSpawn")) {
            this.firstSpawn = nbtTagCompound.getBoolean("FirstSpawn");
    	}
    	else {
    		this.firstSpawn = true;
    	}
    	
    	if(nbtTagCompound.hasKey("SpawnEventType")) {
    		this.spawnEventType = nbtTagCompound.getString("SpawnEventType");
    	}
    	
    	if(nbtTagCompound.hasKey("SpawnEventCount")) {
    		this.spawnEventCount = nbtTagCompound.getInteger("SpawnEventCount");
    	}
    	
    	if(nbtTagCompound.hasKey("Stealth")) {
    		this.setStealth(nbtTagCompound.getFloat("Stealth"));
    	}
    	
    	if(nbtTagCompound.hasKey("IsMinion")) {
    		this.setMinion(nbtTagCompound.getBoolean("IsMinion"));
    	}
    	
    	if(nbtTagCompound.hasKey("IsTemporary") && nbtTagCompound.getBoolean("IsTemporary") && nbtTagCompound.hasKey("TemporaryDuration")) {
    		this.setTemporary(nbtTagCompound.getInteger("TemporaryDuration"));
    	}
    	else {
    		this.unsetTemporary();
    	}

        if(nbtTagCompound.hasKey("IsBoundPet")) {
            if(nbtTagCompound.getBoolean("IsBoundPet")) {
                if(!this.hasPetEntry())
                    this.setDead();
            }
        }
    	
    	if(nbtTagCompound.hasKey("ForceNoDespawn")) {
    		this.forceNoDespawn = nbtTagCompound.getBoolean("ForceNoDespawn");
    	}
    	
    	if(nbtTagCompound.hasKey("Color")) {
    		this.setColor(nbtTagCompound.getByte("Color"));
    	}

		if(nbtTagCompound.hasKey("Size")) {
			this.setSizeScale(nbtTagCompound.getDouble("Size"));
		}

        if(nbtTagCompound.hasKey("Subspecies")) {
    		if(this.firstSpawn) {
				this.applySubspecies(nbtTagCompound.getByte("Subspecies"), false);
			}
			else {
				this.setSubspecies(nbtTagCompound.getByte("Subspecies"), false);
			}
        }

		if(nbtTagCompound.hasKey("MobLevel")) {
			if(this.firstSpawn) {
				this.applyLevel(nbtTagCompound.getInteger("MobLevel"));
			}
			else {
				this.setLevel(nbtTagCompound.getInteger("MobLevel"));
			}
		}

		if(nbtTagCompound.hasKey("SpawnedAsBoss")) {
			this.spawnedAsBoss = nbtTagCompound.getBoolean("SpawnedAsBoss");
		}
    	
        super.readEntityFromNBT(nbtTagCompound);
        this.inventory.readFromNBT(nbtTagCompound);

        if(nbtTagCompound.hasKey("Drops")) {
			NBTTagList nbtDropList = nbtTagCompound.getTagList("Drops", 10);
			for(int i = 0; i < nbtDropList.tagCount(); i++) {
				NBTTagCompound dropNBT = nbtDropList.getCompoundTagAt(i);
				ItemDrop drop = new ItemDrop(dropNBT);
				this.addSavedItemDrop(drop);
			}
		}
        
        if(nbtTagCompound.hasKey("ExtraBehaviour")) {
        	this.extraMobBehaviour.readFromNBT(nbtTagCompound.getCompoundTag("ExtraBehaviour"));
        }
        
        if(nbtTagCompound.hasKey("HomeX") && nbtTagCompound.hasKey("HomeY") && nbtTagCompound.hasKey("HomeZ") && nbtTagCompound.hasKey("HomeDistanceMax")) {
        	this.setHome(nbtTagCompound.getInteger("HomeX"), nbtTagCompound.getInteger("HomeY"), nbtTagCompound.getInteger("HomeZ"), nbtTagCompound.getFloat("HomeDistanceMax"));
        }

        if(nbtTagCompound.hasKey("ArenaX") && nbtTagCompound.hasKey("ArenaY") && nbtTagCompound.hasKey("ArenaZ")) {
            this.setArenaCenter(new BlockPos(nbtTagCompound.getInteger("ArenaX"), nbtTagCompound.getInteger("ArenaY"), nbtTagCompound.getInteger("ArenaZ")));
        }

		if(nbtTagCompound.hasKey("FixateUUIDMost") && nbtTagCompound.hasKey("FixateUUIDLeast")) {
			this.fixateUUID = new UUID(nbtTagCompound.getLong("FixateUUIDMost"), nbtTagCompound.getLong("FixateUUIDLeast"));
		}
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
    	nbtTagCompound.setBoolean("FirstSpawn", this.firstSpawn);
    	nbtTagCompound.setString("SpawnEventType", this.spawnEventType);
    	nbtTagCompound.setInteger("SpawnEventCount", this.spawnEventCount);
    	
    	nbtTagCompound.setFloat("Stealth", this.getStealth());
    	nbtTagCompound.setBoolean("IsMinion", this.isMinion());
    	nbtTagCompound.setBoolean("IsTemporary", this.isTemporary);
    	nbtTagCompound.setInteger("TemporaryDuration", this.temporaryDuration);
        nbtTagCompound.setBoolean("IsBoundPet", this.isBoundPet());
    	nbtTagCompound.setBoolean("ForceNoDespawn", this.forceNoDespawn);
    	nbtTagCompound.setByte("Color", (byte) this.getColor());
        nbtTagCompound.setByte("Subspecies", (byte) this.getSubspeciesIndex());
    	nbtTagCompound.setDouble("Size", this.sizeScale);
		nbtTagCompound.setInteger("MobLevel", this.getLevel());
		nbtTagCompound.setBoolean("SpawnedAsBoss", this.spawnedAsBoss);
    	
    	if(this.hasHome()) {
    		BlockPos homePos = this.getHomePosition();
    		nbtTagCompound.setInteger("HomeX", homePos.getX());
    		nbtTagCompound.setInteger("HomeY", homePos.getY());
    		nbtTagCompound.setInteger("HomeZ", homePos.getZ());
    		nbtTagCompound.setFloat("HomeDistanceMax", this.getHomeDistanceMax());
    	}

        if(this.hasArenaCenter()) {
            BlockPos arenaPos = this.getArenaCenter();
            nbtTagCompound.setInteger("ArenaX", arenaPos.getX());
            nbtTagCompound.setInteger("ArenaY", arenaPos.getY());
            nbtTagCompound.setInteger("ArenaZ", arenaPos.getZ());
        }

		if(this.getFixateTarget() != null) {
			nbtTagCompound.setLong("FixateUUIDMost", this.getFixateTarget().getUniqueID().getMostSignificantBits());
			nbtTagCompound.setLong("FixateUUIDLeast", this.getFixateTarget().getUniqueID().getLeastSignificantBits());
		}
    	
        super.writeEntityToNBT(nbtTagCompound);
        this.inventory.writeToNBT(nbtTagCompound);
		NBTTagList nbtDropList = new NBTTagList();
        for(ItemDrop drop : this.savedDrops) {
			NBTTagCompound dropNBT = new NBTTagCompound();
			dropNBT = drop.writeToNBT(dropNBT);
			nbtDropList.appendTag(dropNBT);
		}
		nbtTagCompound.setTag("Drops", nbtDropList);
        
        NBTTagCompound extTagCompound = new NBTTagCompound();
        this.extraMobBehaviour.writeToNBT(extTagCompound);
        nbtTagCompound.setTag("ExtraBehaviour", extTagCompound);
    }
    
    
    // ==================================================
  	//                       Client
  	// ==================================================
    // ========== Just Attacked Animation ==========
    /** Returns true if this creature should play it's attack animation. **/
    public boolean justAttacked() {
    	return this.justAttacked > 0;
    }
    /** Called when this mob has just attacked, triggers the attack animation. **/
    public void setJustAttacked() {
    	this.justAttacked = this.justAttackedTime;
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    /** Returns this creature's main texture. Also checks for for subspecies. **/
    public ResourceLocation getTexture() {
        return this.getTexture("");
    }

	/** Returns this creature's main texture. Also checks for for subspecies. **/
	public ResourceLocation getTexture(String suffix) {
		String textureName = this.getTextureName();
		if(this.getSubspecies() != null) {
			textureName += "_" + this.getSubspecies().name;
		}
		if(!"".equals(suffix)) {
			textureName += "_" + suffix;
		}
		if(AssetManager.getTexture(textureName) == null)
			AssetManager.addTexture(textureName, this.creatureInfo.group, "textures/entity/" + textureName.toLowerCase() + ".png");
		return AssetManager.getTexture(textureName);
	}

    /** Returns this creature's equipment texture. **/
    public ResourceLocation getEquipmentTexture(String equipmentName) {
        if(!this.canEquip())
            return this.getTexture();
    	return this.getSubTexture(equipmentName);
    }

    /** Returns this creature's equipment texture. **/
    public ResourceLocation getSubTexture(String subName) {
        subName = subName.toLowerCase();
        String textureName = this.getTextureName();
        textureName += "_" + subName;
        if(AssetManager.getTexture(textureName) == null)
            AssetManager.addTexture(textureName, this.creatureInfo.group, "textures/entity/" + textureName.toLowerCase() + ".png");
        return AssetManager.getTexture(textureName);
    }

    /** Gets the name of this creature's texture, normally links to it's code name but can be overridden by subspecies and alpha creatures. **/
    public String getTextureName() {
    	return this.creatureInfo.getName();
    }
    
    
    // ========== Coloring ==========
    /**
     * Returns true if this mob can be dyed different colors. Usually for wool and collars.
     * @param player The player to check for when coloring, this is to stop players from dying other players pets. If provided with null it should return if this creature can be dyed in general.
     * @return True if tis entity can be dyed by the player or if the player is null, if it can be dyed at all (null is passed by the renderer).
     */
    public boolean canBeColored(EntityPlayer player) {
    	return false;
    }
    
    /**
     * Gets the color ID of this mob.
     * @return A color ID that is used by the static RenderCreature.colorTable array.
     */
    public int getColor() {
		if(this.getDataManager() == null) return 0;
        return Integer.valueOf(this.getDataManager().get(COLOR)) & 15;
    }
    
    /**
     * Sets the color ID of this mob.
     * @param color The color ID to use (see the static RenderCreature.colorTable array).
     */
    public void setColor(int color) {
    	if(this.getEntityWorld() != null && !this.getEntityWorld().isRemote)
    		this.dataManager.set(COLOR, Byte.valueOf((byte)(color & 15)));
    }


    // ========== Boss Info ==========
    public boolean showBossInfo() {
        if(this.forceBossHealthBar || (this.isBoss() && this.isBossAlways()))
            return true;
        // Rare subspecies health bar:
        if(this.getSubspeciesIndex() >= 3)
            return Subspecies.rareHealthBars;
        return false;
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        if(this.getBossInfo() != null)
            this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        if(this.getBossInfo() != null)
            this.bossInfo.removePlayer(player);
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    /** Returns the volume of this entity. **/
    @Override
    protected float getSoundVolume() {
        if(this.isBoss())
            return 4.0F;
        if(this.getSubspeciesIndex() >= 3)
            return 2.0F;
        return 1.0F;
    }

    // ========== Idle ==========
    /** Get number of ticks, at least during which the living entity will be silent. **/
    @Override
    public int getTalkInterval() {
        return 80;
    }

    /** Returns the sound to play when this creature is making a random ambient roar, grunt, etc. **/
    @Override
    protected SoundEvent getAmbientSound() { return AssetManager.getSound(this.creatureInfo.getName() + "_say"); }

    // ========== Hurt ==========
    /** Returns the sound to play when this creature is damaged. **/
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) { return AssetManager.getSound(this.creatureInfo.getName() + "_hurt"); }

    // ========== Death ==========
    /** Returns the sound to play when this creature dies. **/
    @Override
    protected SoundEvent getDeathSound() { return AssetManager.getSound(this.creatureInfo.getName() + "_death"); }
     
    // ========== Step ==========
    /** Plays an additional footstep sound that this creature makes when moving on the ground (all mobs use the block's stepping sounds by default). **/
    @Override
    protected void playStepSound(BlockPos pos, Block block) {
    	 if(this.isCurrentlyFlying())
             return;
        if(!this.hasStepSound) {
            super.playStepSound(pos, block);
            return;
        }
        this.playSound(AssetManager.getSound(this.creatureInfo.getName() + "_step"), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }

    // ========== Fall ==========
    @Override
    protected SoundEvent getFallSound(int height) {
        return height > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
    }

    // ========== Swim ==========
    @Override
    protected SoundEvent getSwimSound()
    {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    // ========== Splash ==========
    @Override
    protected SoundEvent getSplashSound()
    {
        return SoundEvents.ENTITY_HOSTILE_SPLASH;
    }
     
    // ========== Jump ==========
    /** Plays the jump sound when this creature jumps. **/
    public void playJumpSound() {
    	if(!this.hasJumpSound) return;
    	this.playSound(AssetManager.getSound(this.creatureInfo.getName() + "_jump"), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
     
    // ========== Fly ==========
    /** Plays a flying sound, usually a wing flap, called randomly when flying. **/
    public void playFlySound() {
    	if(!this.isFlying()) return;
      	this.playSound(AssetManager.getSound(this.creatureInfo.getName() + "_fly"), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }

    // ========== Attack ==========
    /** Plays an attack sound, called once this creature has attacked. note that ranged attacks normally rely on the projectiles playing their launched sound instead. **/
    public void playAttackSound() {
     	if(!this.hasAttackSound) return;
     	this.playSound(AssetManager.getSound(this.creatureInfo.getName() + "_attack"), this.getSoundVolume(), 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }

    // ========== Phase ==========
    /** Plays a sound for when this mob changes battle phase, normally used by bosses. **/
    public void playPhaseSound() {
        if(AssetManager.getSound(this.creatureInfo.getName() + "_phase") == null)
            return;
        this.playSound(AssetManager.getSound(this.creatureInfo.getName() + "_phase"), this.getSoundVolume() * 2, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
    
    // ========== Play Sound ==========
    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
    	super.playSound(sound, volume, pitch);
    }




    // ==================================================
    //                  Group Data
    // ==================================================
    public class GroupData implements IEntityLivingData {
        public boolean isChild;

        public GroupData(boolean child) {
            this.isChild = child;
        }
    }
}