package lycanite.lycanitesmobs.api.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.MobInfo;
import lycanite.lycanitesmobs.api.SpawnInfo;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIMoveRestriction;
import lycanite.lycanitesmobs.api.entity.ai.FlightNavigator;
import lycanite.lycanitesmobs.api.inventory.InventoryCreature;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public abstract class EntityCreatureBase extends EntityLiving {
    // UUID:
	//public static final UUID field_110179_h = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
    
	// Info:
	/** A class that contains information about this mob, this class also links to the SpawnInfo class relevant to this mob. **/
	public MobInfo mobInfo;
    /** The code name of this entity, not the title displayed to players, that is set elsewhere, see getEntityName(). **/
	public String entityName = "Name";
    /** A link to the mod instance of this mob, used to get file paths, the mod config, etc. **/
	public ILycaniteMod mod;
    /** The name of the egg item this mob uses. **/
	public String eggName = "SpawnEgg";
    /** What attribute is this creature, used for effects such as Bane of Arthropods. **/
	public EnumCreatureAttribute attribute = EnumCreatureAttribute.UNDEAD;
	
	// Size:
    /** The width of this mob. XZ axis. **/
	public float setWidth = 0.6F;
    /** The depth of this mob. Overrides width's Z axis. This currently doesn't work, use width only for now. **/
	public float setDepth = 0.6F;
    /** The height of this mob. Y axis. **/
	public float setHeight = 1.8F;
	
	// Stats:
	/** The defense rating of this mob. This is how much damage it can withstand.
	 * For example, a damage of 4 with a defense of 1 will result in a new damage of 3.
	 * Defense stat multipliers are applied to this value too, nor whole results are rounded.
	**/
	public int defense = 0;
    /** How much experience this mob drops (overriden to 0 if it is a minion). **/
	public int experience = 5;
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
	
	// Abilities:
	/** Whether or not this mob is hostile by default. Use isHostile() when check if this mob is hostile. **/
	public boolean isHostileByDefault = true;
    /** Whether if this mob is on fire, it should spread it to other entities when melee attacking. **/
	public boolean spreadFire = false;
    /** Used to check if the mob was stealth last update. **/
	public boolean stealthPrev = false;
	
	// Positions:
    /** A location used for mobs that stick around a certain home spot. **/
	private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
    /** How far this mob can move from their home spot. **/
    private float homeDistanceMax = -1.0F;
    
    // Spawning:
    /** Use the onSpawn() method and not this variable. True if this creature has spawned for the first time (naturally or via spawn egg, etc, not reloaded from a saved chunk). **/
    public boolean firstSpawn = true;
    /** Should this mob only spawn in darkness. **/
    public boolean spawnsInDarkness = false;
    /** Should this mob only spawn in light. **/
    public boolean spawnsOnlyInLight = false;
    /** Should this mob check for block collisions when spawning? **/
    public boolean spawnsInBlock = false;
    /** Can this mob spawn where it can't see the sky above? **/
    public boolean spawnsUnderground = true;
    /** Can this mob spawn on land (not in liquids)? Note that setting a mob to WATERCREATURE means that they will only spawn in water anyway. **/
    public boolean spawnsOnLand = true;
    /** Does this mob spawn inside liquids? **/
    public boolean spawnsInWater = false;
    /** Is this mob a minion? (Minions don't drop items and other things). **/
    public boolean isMinion = false;
    /** If true, this creature will swim in and if set, will suffocate without lava instead of without water. **/
    public boolean isLavaCreature = false;
    
    // Movement:
    /** Whether the mob should use it's leash AI or not. **/
    private boolean leashAIActive = false;
    /** Movement AI for mobs that are leashed. **/
    private EntityAIBase leashMoveTowardsRestrictionAI = new EntityAIMoveRestriction(this);
    /** The flight navigator class, a makeshift class that handles flight and free swimming movement, replaces the pathfinder. **/
    public FlightNavigator flightNavigator;
    
    // Targets:
    /** A target used for alpha creatures or connected mobs such as following concapede segements. **/
    private EntityLivingBase masterTarget;
    /** A target used usually for child mobs or connected mobs such as leading concapede segments. **/
    private EntityLivingBase parentTarget;
    /** A target that this mob should usually run away from. **/
    private EntityLivingBase avoidTarget;
	
	// Client:
    /** Used for attack animations, the server uses this more as a boolean, the client uses it as a timer. **/
	public short justAttacked = 0;
    /** The duration of attack animations, used by the server as a boolean true, the client uses it as the animation time. **/
	public short justAttackedTime = 5;
    /** True if this mob should play a sound when attacking. Ranged mobs usually don't use this as their projectiles makes an attack sound instead. **/
	public boolean hasAttackSound = false;
    /** True if this mob should play a sound when walking. Usually footsteps. **/
	public boolean hasStepSound = true;
    /** True if this mob should play a sound when jumping, used mostly for mounts. **/
	public boolean hasJumpSound = false;
    /** The delay in ticks between flying sounds such as wing flapping, set to 0 for no flight sounds. **/
	public int flySoundSpeed = 0;
	
	// Data Watcher:
    /** The starting point for the datawatcher IDs used by this mod, lower IDs are used by vanilla code. **/
	private static byte watcherID = 12;
    /** A collection of IDs used by the datawatcher (used to sync clients and the server with certain values). **/
	public static enum WATCHER_ID {
		HEALTH(watcherID++), TARGET(watcherID++), ANIMATION(watcherID++), ATTACK_PHASE(watcherID++),
		CLIMBING(watcherID++), STEALTH(watcherID++), HUNGER(watcherID++), STAMINA(watcherID++),
		AGE(watcherID++), LOVE(watcherID++),
		TAMED(watcherID++), OWNER(watcherID++), COLOR(watcherID++),
		EQUIPMENT(watcherID++);
		
		public final byte id;
	    private WATCHER_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
    /** Used for the TARGET watcher bitmap, bitmaps save on many packets and make network performance better! **/
	public static enum TARGET_ID {
		ATTACK((byte)1), MASTER((byte)2), PARENT((byte)4), AVOID((byte)8), RIDER((byte)16);
		public final byte id;
	    private TARGET_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
    /** Used for the ANIM_ID watcher bitmap, bitmaps save on many packets and make network performance better! **/
	public static enum ANIM_ID {
		ATTACKED((byte)1), GROUNDED((byte)2);
		public final byte id;
	    private ANIM_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
	
	// Interact:
	/** Used for the tidier interact code, these are commonly used right click item command priorities. **/
	public static enum CMD_PRIOR {
		OVERRIDE(0), IMPORTANT(1), EQUIPPING(2), ITEM_USE(3), EMPTY_HAND(4), MAIN(5);
		public final int id;
	    private CMD_PRIOR(int value) { this.id = value; }
	    public int getValue() { return id; }
	}
	
	// Items:
    /** The inventory object of the creature, this is used for managing and using the creature's inventory. **/
	public InventoryCreature inventory;
    /** A collection of DropRate classes which are used when randomly drop items on death. **/
    public List<DropRate> drops = new ArrayList<DropRate>();
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityCreatureBase(World world) {
        super(world);
        this.mobInfo = MobInfo.mobClassToInfo.get(this.getClass());
        this.flightNavigator = new FlightNavigator(this);
    }
    
    // ========== Setup ==========
    /** This should be called by the specific mob entity and set the default starting values. **/
    public void setupMob() {
        // Size:
        this.width = setWidth;
        this.height = setHeight;
        this.setSize(setWidth, setHeight);
        
        // Stats:
        this.stepHeight = 0.5F;
        this.experienceValue = experience;
        this.inventory = new InventoryCreature(this.getEntityName(), this);
        if(this.mobInfo.defaultDrops)
        	this.loadItemDrops();
        this.loadCustomDrops();
    }
    
    // ========== Load Item Drops ==========
    /** Loads all default item drops, will be ignored if the Enable Default Drops config setting for this mob is set to false, should be overridden to add drops. **/
    public void loadItemDrops() {}
    
    // ========== Load Custom Drops ==========
    /** Loads custom item drops from the config. **/
    public void loadCustomDrops() {
    	this.drops.addAll(this.mobInfo.customDrops);
    }
    
    // ========== Attributes ==========
    /** Creates and sets all the entity attributes with default values. **/
    protected void applyEntityAttributes() {
        HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 0D);
		baseAttributes.put("movementSpeed", 0D);
		baseAttributes.put("knockbackResistance", 0D);
		baseAttributes.put("followRange", 0D);
		baseAttributes.put("attackDamage", 0D);
        this.applyEntityAttributes(baseAttributes);
    }
    
    /** Creates and sets all the entity attributes using a HashMap of values. **/
    protected void applyEntityAttributes(HashMap<String, Double> baseAttributes) {
        super.applyEntityAttributes();
        this.getAttributeMap().func_111150_b(SharedMonsterAttributes.attackDamage);
        if(baseAttributes.containsKey("maxHealth"))
        	this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(baseAttributes.get("maxHealth"));
        if(baseAttributes.containsKey("movementSpeed"))
        	this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(baseAttributes.get("movementSpeed"));
        if(baseAttributes.containsKey("knockbackResistance"))
        	this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(baseAttributes.get("knockbackResistance"));
        if(baseAttributes.containsKey("followRange"))
        	this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(baseAttributes.get("followRange"));
        if(baseAttributes.containsKey("attackDamage"))
        	this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(baseAttributes.get("attackDamage"));
    }
	
	// ========== Init ==========
    /** Initiates the entity setting all the values to be watched by the datawatcher. **/
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(WATCHER_ID.TARGET.id, (byte)0);
        this.dataWatcher.addObject(WATCHER_ID.ATTACK_PHASE.id, (byte)0);
        this.dataWatcher.addObject(WATCHER_ID.ANIMATION.id, (byte)0);
        this.dataWatcher.addObject(WATCHER_ID.CLIMBING.id, (byte)0);
        this.dataWatcher.addObject(WATCHER_ID.STEALTH.id, (float)0.0F);
    }
    
    // ========== Name ==========
    /** Returns the name that this entity uses when checking the config class. Also it's spawner name, etc. **/
    public String getConfigName() {
    	return this.entityName;
    }
    
    /** Returns the display name of this entity. Use this when displaying it's name. **/
    @Override
    public String getEntityName() {
    	if(this.hasCustomNameTag())
    		return this.getCustomNameTag();
    	else
    		return this.getFullName();
    }

    /** Returns the full name of this entity. **/
    public String getFullName() {
    	String name = "";
    	if(getAgeName() != "")
    		name += getAgeName() + " ";
    	if(getSubspeciesName() != "")
    		name += getSubspeciesName() + " ";
    	return name + getSpeciesName();
    }
    
    /** Returns the species name of this entity. **/
    public String getSpeciesName() {
    	String entityName = EntityList.getEntityString(this);
    	if(entityName == null)
    		return "Creature";
    	return StatCollector.translateToLocal("entity." + entityName + ".name");
    }

    /** Returns the subpsecies name of this entity, used for entity subtypes. **/
    public String getSubspeciesName() {
    	return "";
    }

    /** Gets the name of this entity relative to it's age, more useful for EntityCreatureAgeable. **/
    public String getAgeName() {
    	return "";
    }
    
    
    // ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Can Spawn Here ==========
    /** Checks if the creature is able to spawn a it's initial position. **/
    @Override
    public boolean getCanSpawnHere() {
		int i = MathHelper.floor_double(this.posX);
	    int j = MathHelper.floor_double(this.boundingBox.minY);
	    int k = MathHelper.floor_double(this.posZ);
	    return this.spawnCheck(this.worldObj, i, j, k);
    }
    
    public boolean spawnCheck(World world, int i, int j, int k) {
    	LycanitesMobs.printDebug("MobSpawns", " ~O===================================================O~");
    	LycanitesMobs.printDebug("MobSpawns", "Attempting to Spawn: " + this.getConfigName());
    	
    	// Peaceful Check:
    	LycanitesMobs.printDebug("MobSpawns", "Checking for peaceful difficulty...");
        if(!this.mobInfo.peacefulDifficulty && this.worldObj.difficultySetting <= 0) return false;
        
    	LycanitesMobs.printDebug("MobSpawns", "Target Spawn Location: x" + i + " y" + j + " z" + k);
        
        // Fixed Spawning Checks:
    	LycanitesMobs.printDebug("MobSpawns", "Fixed spawn check (light level, obstacles, etc)...");
        if(!this.fixedSpawnCheck(world, i, j, k))
        	return false;
        
    	// Spawner Check:
    	LycanitesMobs.printDebug("MobSpawns", "Checking for nearby spawner...");
        if(this.isSpawnerNearby(i, j, k)) {
        	LycanitesMobs.printDebug("MobSpawns", "Spawner found, skpping other checks.");
        	LycanitesMobs.printDebug("MobSpawns", "Spawn Check Passed!");
        	return true;
        }
    	LycanitesMobs.printDebug("MobSpawns", "No spawner found.");
        
        // Natural Spawning Checks:
    	LycanitesMobs.printDebug("MobSpawns", "Natural spawn check (dimension, area limit, ground type, water, lava, underground)...");
        if(!this.naturalSpawnCheck(world, i, j, k))
        	return false;
        
        // Forced Spawn Chance:
    	LycanitesMobs.printDebug("MobSpawns", "All enviroment checks passed.");
        if(this.mobInfo.spawnInfo.spawnChance < 100) {
        	if(this.mobInfo.spawnInfo.spawnChance <= 0) {
        		LycanitesMobs.printDebug("MobSpawns", "Applying Forced Spawn Chance - Chance is 0 = No Spawning");
        		return false;
        	}
        	else {
	        	double spawnRoll = this.rand.nextDouble();
		        LycanitesMobs.printDebug("MobSpawns", "Applying Forced Spawn Chance - Rolled: " + spawnRoll + " Must be less than: " + this.mobInfo.spawnInfo.spawnChance);
	        	if(spawnRoll < this.mobInfo.spawnInfo.spawnChance)
	        		return false;
        	}
		}
        LycanitesMobs.printDebug("MobSpawns", "Spawn Check Passed!");
        return true;
    }

    // ========== Fixed Spawn Check ==========
    /** First stage checks for spawning, if this check fails the creature will not spawn. **/
    public boolean fixedSpawnCheck(World world, int i, int j, int k) {
    	LycanitesMobs.printDebug("MobSpawns", "Checking light level: Darkness");
    	if(this.spawnsInDarkness && this.testLightLevel(i, j, k) > 1)
    		return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking light level: Lightness");
    	if(this.spawnsOnlyInLight && this.testLightLevel(i, j, k) < 2)
    		return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking entity collision.");
        if(!this.worldObj.checkNoEntityCollision(this.boundingBox))
        	return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking solid block collision.");
        if(!this.spawnsInBlock && !this.spawnsInWater && !this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
        	return false;
    	return true;
    }
    
    // ========== Natural Spawn Check ==========
    /** Second stage checks for spawning, this check is ignored if there is a valid monster spawner nearby. **/
    public boolean naturalSpawnCheck(World world, int i, int j, int k) {
    	LycanitesMobs.printDebug("MobSpawns", "Checking dimension.");
    	if(this.mobInfo.spawnInfo.dimensionIDs.length <= 0)
    		return false;
        else {
        	boolean validDimension = false;
        	for(int spawnDimension : this.mobInfo.spawnInfo.dimensionIDs) {
        		if(this.worldObj.provider.dimensionId == spawnDimension) {
        			validDimension = true;
        			break;
        		}
        	}
        	if(!validDimension)
        		return false;
        }
    	LycanitesMobs.printDebug("MobSpawns", "Block preference.");
        if(this.getBlockPathWeight(i, j, k) < 0.0F)
        	return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking for liquid (water, lava, etc).");
        if(!this.spawnsInWater && this.worldObj.isAnyLiquid(this.boundingBox))
        	return false;
        else if(!this.spawnsOnLand && !this.worldObj.isAnyLiquid(this.boundingBox))
        	return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking for underground.");
        if(!this.spawnsUnderground && this.isBlockUnderground(i, j + 1, k))
        	return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking required blocks.");
        if(!spawnBlockCheck(world, i, j, k))
        	return false;
    	LycanitesMobs.printDebug("MobSpawns", "Checking required blocks.");
        if(!spawnBlockCheck(world, i, j, k))
        	return false;
        if(!spawnLimitCheck(world, i, j, k))
        	return false;
        	
        return true;
    }
    
    // ========== Spawn Limit Check ==========
    /** Checks for nearby blocks from the ijk (xyz) block location, Cinders use this when spawning by Fire Blocks. **/
    public boolean spawnLimitCheck(World world, int i, int j, int k) {
    	 int spawnLimit = this.mobInfo.spawnInfo.spawnAreaLimit;
    	 double range = SpawnInfo.spawnLimitRange;
    	 LycanitesMobs.printDebug("MobSpawns", "Checking spawn area limit. Limit of: " + spawnLimit + " Range of: " + range);
         if(spawnLimit > 0 && range > 0) {
         	AxisAlignedBB searchAABB = AxisAlignedBB.getBoundingBox(i, j, k, i, j, k);
         	List targets = this.worldObj.getEntitiesWithinAABB(this.mobInfo.entityClass, searchAABB.expand(range, range, range));
         	LycanitesMobs.printDebug("MobSpawns", "Found " + targets.size() + " of this mob within the radius (class is " + this.mobInfo.entityClass + ").");
         	if(targets.size() > spawnLimit)
         		return false;
         }
         return true;
    }
    
    // ========== Spawn Block Check ==========
    /** Checks for nearby blocks from the ijk (xyz) block location, Cinders use this when spawning by Fire Blocks. **/
    public boolean spawnBlockCheck(World world, int i, int j, int k) {
        return true;
    }
    
    // ========== Egg Spawn ==========
    /** Called once this mob is spawned with a Spawn Egg. **/
    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData livingData) {
    	livingData = super.onSpawnWithEgg(livingData);
        return livingData;
    }
    
    // ========== Despawning ==========
    /** Returns whether this mob should despawn overtime or not. Config defined forced despawns override everything except tamed creatures. **/
    @Override
    protected boolean canDespawn() {
    	if(this.mobInfo.spawnInfo.despawnForced)
    		return true;
    	if(!this.mobInfo.spawnInfo.despawnNatural)
    		return false;
    	if(this.isPersistant() || this.getLeashed())
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
    	return false;
    }
    
    /** A check that is constantly done, if this returns true, this entity will be removed, used normally for peaceful difficulty removal and temporary minions. **/
    public boolean despawnCheck() {
        if(this.worldObj.isRemote)
        	return false;
        if((!this.mobInfo.peacefulDifficulty && this.worldObj.difficultySetting <= 0) && !(this.getLeashed() || this.isPersistant()))
        	return true;
        return false;
    }
    
    // ========== Spawner Checking ==========
    /** Checks if a Monster Spawner that spawns this mob is near the XYZ locations, checks within an 8 block radius. **/
    public boolean isSpawnerNearby(int x, int y, int z) {
    	int checkRange = 8;
    	for(int i = x - checkRange; i <= x + checkRange; i++)
        	for(int j = y - checkRange; j <= y + checkRange; j++)
            	for(int k = z - checkRange; k <= z + checkRange; k++) {
            		TileEntity tileEntity = this.worldObj.getBlockTileEntity(i, j, k);
            		if(tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
            			if(((TileEntityMobSpawner)tileEntity).getSpawnerLogic().getEntityNameToSpawn().equals(ObjectManager.entityLists.get(this.mod.getDomain()).getEntityString(this)))
            				return true;
            		}
            			
            	}
    	return false;
    }
    
    // ========== Block Checking ==========
    /** Checks if the specified block is underground (unable to see the sky above it). This checks through leaves, plants, grass and vine materials. **/
    public boolean isBlockUnderground(int x, int y, int z) {
    	if(this.worldObj.canBlockSeeTheSky(x, y, z))
    		return false;
    	for(int j = y; j < this.worldObj.getHeight(); j++) {
    		Material blockMaterial = this.worldObj.getBlockMaterial(x, j, z);
    		if(blockMaterial != Material.air
    				&& blockMaterial != Material.leaves
    				&& blockMaterial != Material.plants
    				&& blockMaterial != Material.grass
    				&& blockMaterial != Material.vine)
    			return true;
    	}
    	return false;
    }
    
    // ========== Minion ==========
    /** Set whether this mob is a minion or not, this should be used if this mob is summoned. **/
    public void setMinion(boolean minion) { this.isMinion = minion; }
    /** Returns whether or not with mob is a minion. **/
    public boolean isMinion() { return this.isMinion; }
    
    // ========== On Spawn ==========
    /** This is called when the mob is first spawned to the world either through natural spawning or from a Spawn Egg. **/
    public void onSpawn() {}
	
	
	// ==================================================
	//             Stat Multipliers and Boosts
	// ==================================================
    /** Returns the requested stat multiplier. **/
	public double getStatMultiplier(String stat) {
		double multiplier = 1.0D;
		if("defense".equalsIgnoreCase(stat))
			multiplier = this.mobInfo.multiplierDefense;
		
		else if("speed".equalsIgnoreCase(stat))
			multiplier = this.mobInfo.multiplierSpeed;
		
		else if("damage".equalsIgnoreCase(stat))
			multiplier = this.mobInfo.multiplierDamage;
		
		else if("haste".equalsIgnoreCase(stat))
			multiplier = this.mobInfo.multiplierHaste;
		
		else if("effect".equalsIgnoreCase(stat))
			multiplier = this.mobInfo.multiplierEffect;
		
		return multiplier * this.getDifficultyMultiplier(stat);
	}
	
	/** Returns the shared multiplier for all stats based on difficulty. **/
	public double getDifficultyMultiplier(String stat) {
		int difficulty = this.worldObj.difficultySetting;
		String difficultyName = "Easy";
		if(difficulty >= 3)
			difficultyName = "Hard";
		else if(difficulty == 2)
			difficultyName = "Normal";
		return MobInfo.difficultyMutlipliers.get(difficultyName.toUpperCase() + "-" + stat.toUpperCase());
	}
	
	/** Returns an additional boost stat, useful for mainly defense as many mobs have 0 defense which thus can't be altered by modifiers. **/
	public int getStatBoost(String stat) {
		int boost = 0;
		
		if("defense".equalsIgnoreCase(stat))
			boost = this.mod.getConfig().defenseBoosts.get(this.getConfigName());
		
		return boost;
	}
    
    // ========= Defense Multiplier ==========
    /** Used to scale the defense of this mob, see getDamageAfterDefense() for the logic. **/
    public double getDefenseMultiplier() {
    	return this.getStatMultiplier("defense");
    }
    
    // ========= Speed Multiplier ==========
    /** Used to scale the speed of this mob. **/
    public double getSpeedMultiplier() {
    	return this.getStatMultiplier("speed");
    }
    
    // ========= Effect Multiplier ==========
    /** Used to scale the duration of any effects that this mob uses, can inlcude both buffs and debuffs on the enemy. **/
    public double getEffectMultiplier() {
    	return this.getStatMultiplier("effect");
    }

    /** When given a base time (in seconds) this will return the scaled time with difficulty and other modifiers taken into account
     * seconds - The base duration in seconds that this effect should last for.
    **/
    public int getEffectDuration(int seconds) {
		return Math.round((float)seconds * (float)(this.getEffectMultiplier()));
    }
    
    // ========= Haste Multiplier ==========
    /** Used to scale the rate of abilities such as attack speed. Note: Abilities are normally capped at around 10 ticks minimum due to performance issues and the entity update rate. **/
    public double getHasteMultiplier() {
    	return this.getStatMultiplier("haste");
    }
    
    
    // ==================================================
  	//                     Updates
  	// ==================================================
    // ========== Main ==========
    /** The main update method, all the important updates go here. **/
    public void onUpdate() {
        this.onSyncUpdate();
        super.onUpdate();
        
        if(this.despawnCheck())
        	this.setDead();
        
        // Fire Immunity:
        this.isImmuneToFire = !this.canBurn();
        
        if(!this.worldObj.isRemote) {
        	this.setBesideClimbableBlock(this.isCollidedHorizontally);
        	if(this.flySoundSpeed > 0 && this.ticksExisted % 20 == 0)
        		this.playFlySound();
        }
    }
    
    // ========== AI ==========
    /** Runs through all the AI tasks this mob has on the update, will update the flight navigator if this mob is using it too. **/
    @Override
    protected void updateAITasks() {
		if(this.canFly()) flightNavigator.updateFlight();
        super.updateAITasks();
    }
    
    // ========== Living ==========
    /** he main update method, behaviour and custom update logic should go here. **/
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.updateArmSwingProgress();
        
        // First Spawn:
        if(!this.worldObj.isRemote && this.firstSpawn) {
        	this.onSpawn();
        	this.firstSpawn = false;
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
        if(!this.worldObj.isRemote && this.daylightBurns() && this.worldObj.isDaytime()) {
        	float brightness = this.getBrightness(1.0F);
            if(brightness > 0.5F && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
                boolean shouldBurn = true;
                ItemStack helmet = this.getCurrentItemOrArmor(4);
                if(helmet != null) {
                    if(helmet.isItemStackDamageable()) {
                    	helmet.setItemDamage(helmet.getItemDamageForDisplay() + this.rand.nextInt(2));
                        if(helmet.getItemDamageForDisplay() >= helmet.getMaxDamage()) {
                            this.renderBrokenItemStack(helmet);
                            this.setCurrentItemOrArmor(4, (ItemStack)null);
                        }
                    }
                    shouldBurn = false;
                }
                if(shouldBurn)
                    this.setFire(8);
            }
        }
        
        // Water Damage:
        if(!this.worldObj.isRemote && this.waterDamage() && this.isWet())
            this.attackEntityFrom(DamageSource.drown, 1.0F);
        
        // Out of Water Suffocation:
        if(!this.worldObj.isRemote && !this.canBreatheAboveWater()) {
	        int currentAir = this.getAir();
	        if(this.isEntityAlive()) {
	        	if(
	        			(!this.isLavaCreature && !this.waterContact())
	        			|| (this.isLavaCreature && !this.lavaContact())
	        	) {
		        	currentAir--;
		            this.setAir(currentAir);
		            if(this.getAir() <= -200) {
		                this.setAir(-180);
		                this.attackEntityFrom(DamageSource.drown, 2.0F);
		            }
		        }
		        else {
		            this.setAir(299);
		        }
	        }
        }
        
        // Time Out Quicker In Light:
        float light = this.getBrightness(1.0F);
        if(this.spawnsInDarkness && light > 0.5F)
            this.entityAge += 2;
        
	    // Stealth Invisibility:
    	if(!this.worldObj.isRemote) {
	        if(this.isStealthed() && !this.isInvisible())
	        	setInvisible(true);
	        else if(!this.isStealthed() && this.isInvisible() && !this.isPotionActive(Potion.invisibility.id))
	        	setInvisible(false);
    	}
        if(this.isStealthed()) {
        	if(this.stealthPrev != this.isStealthed())
        		startStealth();
        	onStealth();
        }
        else if(this.isInvisible() && !this.isPotionActive(Potion.invisibility.id))
        	setInvisible(false);
        this.stealthPrev = this.isStealthed();
        
        // Pickup Items:
        if(!this.worldObj.isRemote && this.isEntityAlive() && this.canPickupItems())
        	this.pickupItems();
    }
    
    // ========== Sync Update ==========
    /** An update that is called to sync things with the client and server such as various entity targets, attack phases, animations, etc. **/
    public void onSyncUpdate() {
    	// Sync Target Status:
    	if(!this.worldObj.isRemote) {
    		byte targets = 0;
    		if(this.getAttackTarget() != null)
    			targets += TARGET_ID.ATTACK.id;
    		if(this.getMasterTarget() != null)
    			targets += TARGET_ID.MASTER.id;
    		if(this.getParentTarget() != null)
    			targets += TARGET_ID.PARENT.id;
    		if(this.getAvoidTarget() != null)
    			targets += TARGET_ID.AVOID.id;
    		if(this.getRiderTarget() != null)
    			targets += TARGET_ID.RIDER.id;
    		this.dataWatcher.updateObject(WATCHER_ID.TARGET.id, targets);
    	}
    	
		// Attack Phase:
    	if(!this.worldObj.isRemote)
    		this.dataWatcher.updateObject(WATCHER_ID.ATTACK_PHASE.id, this.attackPhase);
        
    	// Animations Server:
        if(!this.worldObj.isRemote) {
        	byte animations = 0;
        	
        	// Atttacked Animation and Sound:
        	if(this.justAttacked == this.justAttackedTime) {
        		animations += ANIM_ID.ATTACKED.id;
        		this.justAttacked = 0;
        		playAttackSound();
        	}
        	
        	// Airborne Animation:
        	if(this.onGround)
        		animations += ANIM_ID.GROUNDED.id;
        	
        	this.dataWatcher.updateObject(WATCHER_ID.ANIMATION.id, animations);
        }
        
        // Animations Client:
        else if(this.worldObj.isRemote) {
        	byte animations = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.ANIMATION.id);
        	if(this.justAttacked > 0)
        		this.justAttacked--;
        	else if((animations & ANIM_ID.ATTACKED.id) > 0)
        		this.setJustAttacked();
        	this.onGround = (animations & ANIM_ID.GROUNDED.id) > 0;
        }
    }
    
    
    // ==================================================
  	//                     Movement
  	// ==================================================
    /**
		Returns the importance of blocks when pathfinding, also used when checking if this mob can spawn.
	    Returns a float where 0.0F is a standard path, anything higher is a preferred path.
	    For example, animals that spawn on grass return 10.0F for path blocks that are Grass.
	    Mobs that prefer the darkness will return a higher value for darker blocks.
    **/
    // ========== Get Block Path Weight ==========
    public float getBlockPathWeight(int par1, int par2, int par3) {
        if(this.spawnsInDarkness)
        	return 0.5F - this.worldObj.getLightBrightness(par1, par2, par3);
        if(this.spawnsOnlyInLight)
        	return this.worldObj.getLightBrightness(par1, par2, par3) - 0.5F;
    	return 0.0F;
    }
    
    // ========== Move with Heading ==========
    /** Moves the entity, redirects to the flight navigator if this mob should use that instead. **/
    @Override
    public void moveEntityWithHeading(float moveStrafe, float moveForward) {
    	if(!this.canFly()) super.moveEntityWithHeading(moveStrafe, moveForward);
    	else this.flightNavigator.flightMovement(moveStrafe, moveForward);
    }
    
    // ========== Clear Movement ==========
    /** Cuts off all movement for this update, will clear any pathfinder paths, works with the flight navigator too. **/
    public void clearMovement() {
    	if(!canFly())
        	this.getNavigator().clearPathEntity();
        else
        	this.flightNavigator.clearTargetPosition(1.0D);
    }
    
    // ========== Leash ==========
    /** I think this is the leash update that manages all behaviour to do with their entity being leashed or unleashed. **/
    @Override
    protected void func_110159_bB() {
        super.func_110159_bB();
        if(this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().worldObj == this.worldObj) {
            Entity entity = this.getLeashedToEntity();
            this.setHome((int)entity.posX, (int)entity.posY, (int)entity.posZ, 5);
            float distance = this.getDistanceToEntity(entity);
            this.testLeash(distance);
            
            if(!this.leashAIActive) {
                this.tasks.addTask(2, this.leashMoveTowardsRestrictionAI);
                this.getNavigator().setAvoidsWater(false);
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
            this.getNavigator().setAvoidsWater(true);
            this.detachHome();
        }
    }
    
    // ========== Is Moving ==========
    /** Returns true if this entity is moving towards a destination (doesn't check if this entity is being pushed, etc though). **/
    public boolean isMoving() {
    	if(!canFly())
        	return this.getNavigator().getPath() != null;
        else
        	return !this.flightNavigator.atTargetPosition();
    }
    
    @Override
    /** Returns whether or not this entity can be leashed at all. **/
    public boolean allowLeashing() { return false; }
    
    // ========== Can leash ==========
    /** Returns whether or not this entity can be leashed to the specified player. Useful for tamed entites. **/
    public boolean canLeash(EntityPlayer player) {
	    return false;
    }
    
    // ========== Test Leash ==========
    /** Called on the update to see if the leash should snap at the given distance. **/
    public void testLeash(float distance) {}
    
    // ========== Set AI Speed ==========
    /** Used when setting the movement speed of this mob, called by AI classes before movement and is given a speed modifier, a local speed modifier is also applied here. **/
    @Override
    public void setAIMoveSpeed(float speed) {
        super.setAIMoveSpeed((speed * this.getSpeedMod()) * (float)this.getSpeedMultiplier());
    }
    
    // ========== Movement Speed Modifier ==========
    /** The local speed modifier of this mob, AI classes will also provide their own modifiers that will be multiplied by this modifier. **/
    public float getSpeedMod() {
    	return 1.0F;
    }
    
    // ========== Falling Speed Modifier ==========
    /** Used to change the falling speed of this entity, 1.0D does nothing. **/
    public double getFallingMod() {
    	return 1.0D;
    }
    
    // ========== Leap ==========
    /**
     * When called, this entity will leap forwards with the given distance and height.
     * This is very sensitive, a large distance or height can cause the entity to zoom off for thousands of blocks!
     * A distance of 1.0D is around 10 blocks forwards, a height of 0.5D is about 10 blocks up.
     * Tip: Use a negative height for flying and swimming mobs so that they can swoop down in the air or water.
    **/
    public void leap(double distance, double leapHeight) {
    	double angle = Math.toRadians(this.rotationYaw);
    	double xAmount = -Math.sin(angle);
    	double zAmount = Math.cos(angle);
        this.motionX = xAmount * distance + this.motionX * 0.2D;
        this.motionZ = zAmount * distance + this.motionZ * 0.2D;
        this.motionY = leapHeight;
    }
    
    // ========== Leap to Target ==========
    /** 
     * When called, this entity will leap towards the given target entity with the given height.
     * This is very sensitive, a large distance or height can cause the entity to zoom off for thousands of blocks!
     * A distance of 1.0D is around 10 blocks forwards, a height of 0.5D is about 10 blocks up.
     * Tip: Use a negative height for flying and swimming mobs so that they can swoop down in the air or water
    **/
    public void leap(float range, double leapHeight, Entity target) {
        if(target == null) return;
        float distance = target.getDistanceToEntity(this);
    	if(distance > 2.0F && distance <= range) {
            double xDist = this.getAttackTarget().posX - this.posX;
            double zDist = this.getAttackTarget().posZ - this.posZ;
            float mixedDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
            this.motionX = xDist / (double)mixedDist * 0.5D * 0.8D + this.motionX * 0.2D;
            this.motionZ = zDist / (double)mixedDist * 0.5D * 0.8D + this.motionZ * 0.2D;
            this.motionY = leapHeight;
        }
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
    	this.homePosition = new ChunkCoordinates(x, y, z);
    }
    /** Sets the distance this mob is allowed to stray from it's home. -1 will turn off the home restriction. **/
    public void setHomeDistanceMax(float newDist) { this.homeDistanceMax = newDist; }
    /** Returns the home position in ChunkCoordinates. **/
    public ChunkCoordinates getHomePosition() { return this.homePosition; }
    /** Gets the distance this mob is allowed to stray from it's home. -1 is used to unlimited distance. **/
    public float getHomeDistanceMax() { return this.homeDistanceMax; }
    public void detachHome() {
    	this.setHomeDistanceMax(-1);
    }
    /** Returns whether or not this mob has a home set. **/
    public boolean hasHome() {
    	return this.getHomePosition() != null && this.getHomeDistanceMax() >= 0;
    }
    /** Returns whether or not the given XYZ position is near this entity's home position, returns true if no home is set. **/
    public boolean positionNearHome(int par1, int par2, int par3) {
        if(!hasHome()) return true;
        return this.homePosition.getDistanceSquared(par1, par2, par3) < this.getHomeDistanceMax() * this.getHomeDistanceMax();
    }
    
    
    // ==================================================
  	//                      Attacks
  	// ==================================================
    // ========== Can Attack ==========
    /** Returns whether or not this mob is allowed to attack the given target class. **/
	@Override
	public boolean canAttackClass(Class targetClass) { return true; }
    /** Returns whether or not this mob is allowed to attack the given target entity. **/
	public boolean canAttackEntity(EntityLivingBase targetEntity) { return true; }
	
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
    	if(this.fleeHealthPercent > 0 && this.getHealth() / this.getMaxHealth() <= this.fleeHealthPercent)
    		this.setAvoidTarget(entityLivingBase);
    	else
    		super.setRevengeTarget(entityLivingBase);
    }
    
    // ========== Melee ==========
    /** Used to make this entity perform a melee attack on the target entity with the given damage scale. **/
    public boolean meleeAttack(Entity target, double damageScale) {
    	boolean success = true;
    	if(this.attackEntityAsMob(target, damageScale)) {
    		
    		// Spread Fire:
        	if(this.spreadFire && this.isBurning() && this.rand.nextFloat() < this.getStatMultiplier("effect"))
        		target.setFire(Math.round((float)(4 * this.getStatMultiplier("effect"))));
        	
    	}
    	this.setJustAttacked();
    	return success;
    }

    // ========== Ranged ==========
    /** Used to make this entity fire a ranged attack at the target entity, range is also passed which can be used. **/
    public void rangedAttack(Entity target, float range) {
    	this.setJustAttacked();
    }
    
    // ========== Phase ==========
    /** Returns the current attack phase of this mob, used when deciding which attack to use and which animations to use. **/
    public byte getAttackPhase() {
    	return this.dataWatcher.getWatchableObjectByte(WATCHER_ID.ATTACK_PHASE.id);
    }
    /** Sets the current attack phase of this mobs. **/
    public void setAttackPhase(byte setAttackPhase) { attackPhase = setAttackPhase; }
    /** Moves the attack phase to the next step, will loop back to 0 when the max is passed. **/
    public void nextAttackPhase() {
    	if(++attackPhase > (attackPhaseMax - 1))
    		attackPhase = 0;
    }
    
    // ========== Deal Damage ==========
    /** Called when attacking and makes this entity actually deal damage to the target entity. Not used by projectile based attacks. **/
    public boolean attackEntityAsMob(Entity target, double damageScale) {
        float damage = this.getAttackDamage(damageScale);
        int i = 0;
        
        if(target instanceof EntityLivingBase) {
        	damage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)target);
            i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)target);
        }
        
        boolean attackSuccess = target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        
        if(attackSuccess) {
            if(i > 0) {
            	target.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
            
            int j = EnchantmentHelper.getFireAspectModifier(this);
            
            if(j > 0)
            	target.setFire(j * 4);
            
            if(target instanceof EntityLivingBase)
                EnchantmentThorns.func_92096_a(this, (EntityLivingBase)target, this.rand);
        }
        
        return attackSuccess;
    }
    
    // ========== Get Attack Damage ==========
    /** Returns how much attack damage this mob does. **/
    public float getAttackDamage(double damageScale) {
    	float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        damage *= this.getStatMultiplier("damage");
        damage *= damageScale;
        return damage;
    }
    
    // ========= Get Attack Damage Scale ==========
    /** Used to scale how much damage this mob does, this is used by getAttackDamage() for melee and should be passed to projectiles for ranged attacks. **/
    public double getAttackDamageScale() {
    	return this.getStatMultiplier("damage");
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Attacked From ==========
    /** Called when this entity has been attacked, uses a DamageSource and damage value. **/
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damage) {
    	if(this.worldObj.isRemote) return false;
        if(this.isEntityInvulnerable()) return false;
        if(!this.isDamageTypeApplicable(damageSrc.getDamageType())) return false;
        if(!this.isDamageEntityApplicable(damageSrc.getEntity())) return false;
        damage = this.getDamageAfterDefense(damage);
        damage *= this.getDamageModifier(damageSrc);
        
        if(super.attackEntityFrom(damageSrc, damage)) {
            Entity entity = damageSrc.getEntity();
            
            if(entity instanceof EntityLivingBase && this.riddenByEntity != entity && this.ridingEntity != entity) {
                if(entity != this)
                    this.setRevengeTarget((EntityLivingBase)entity);
                return true;
            }
            else
                return true;
        }
        return false;
    }
    
    // ========== Defense ==========
    /** This is provided with how much damage this mob will take and returns the reduced (or sometimes increased) damage with defense applied. Note: Damage Modifiers are applied after this. **/
    public float getDamageAfterDefense(float damage) {
    	float baseDefense = (float)(this.defense + this.getStatBoost("defense"));
    	float scaledDefense = baseDefense * (float)this.getDefenseMultiplier();
    	float minDamage = 1F;
    	return Math.max(damage - scaledDefense, minDamage);
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
    public void onDeath(DamageSource par1DamageSource) {
    	if(ForgeHooks.onLivingDeath(this, par1DamageSource)) return;
        super.onDeath(par1DamageSource);
        if(!this.worldObj.isRemote)
            this.inventory.dropInventory();
    }
    
    
    // ==================================================
  	//                      Targets
  	// ==================================================
    /** Returns true if this mob should attack it's attack targets. Used mostly by attack AIs and update methods. **/
    public boolean isAggressive() { return true; }
    
    /** Returns true if this mob should defend other entities that cry for help. Used mainly by the revenge AI. **/
    public boolean isProtective(Entity entity) { return true; }

    /** Returns true if this mob has an Attack Target. **/
    public boolean hasAttackTarget() {
    	if(!this.worldObj.isRemote)
    		return this.getAttackTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.ATTACK.id) > 0;
    }

    /** Returns this entity's Master Target. **/
    public EntityLivingBase getMasterTarget() { return this.masterTarget; }
    /** Sets this entity's Master Target **/
    public void setMasterTarget(EntityLivingBase setTarget) { this.masterTarget = setTarget; }
    /** Returns true if this mob has a Master Target **/
    public boolean hasMaster() {
    	if(!this.worldObj.isRemote)
    		return this.getMasterTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.MASTER.id) > 0;
    }

    /** Returns this entity's Parent Target. **/
    public EntityLivingBase getParentTarget() { return this.parentTarget; }
    /** Sets this entity's Parent Target **/
    public void setParentTarget(EntityLivingBase setTarget) { this.parentTarget = setTarget; }
    /** Returns true if this mob has a Parent Target **/
    public boolean hasParent() {
    	if(!this.worldObj.isRemote)
    		return this.getParentTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.PARENT.id) > 0;
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
    	if(!this.worldObj.isRemote)
    		return this.getAvoidTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.AVOID.id) > 0;
    }

    /** Returns this entity's Owner Target. **/
    public EntityLivingBase getOwner() { return null; }

    /** Returns this entity's Rider Target as an EntityLivingBase or null if it isn't one, see getRiderTarget(). **/
    public EntityLivingBase getRider() {
    	if(this.riddenByEntity instanceof EntityLivingBase)
    		return (EntityLivingBase)this.riddenByEntity;
    	else
    		return null;
    }
    /** Returns this entity's Rider Target as an Entity, use getRider() for it as an EntityLivingBase or null if it is one. **/
    public Entity getRiderTarget() { return this.riddenByEntity; }
    /** Sets this entity's Rider Target **/
    public void setRiderTarget(Entity setTarget) { this.riddenByEntity = setTarget; }
    /** Returns true if this mob has a Rider Target **/
    public boolean hasRiderTarget() {
    	if(!this.worldObj.isRemote)
    		return this.getRiderTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.RIDER.id) > 0;
    }
    
    // ========== Get Coord Behind ==========
    /** Returns the XYZ coordinate behind this entity with the given distance. **/
    public double[] getCoordBehind(double distance) {
    	double angle = Math.toRadians(this.rotationYaw);
    	double xAmount = -Math.sin(angle);
    	double zAmount = Math.cos(angle);
    	double[] coords = new double[3];
        coords[0] = this.posX - (distance * xAmount);
        coords[1] = this.posY;
        coords[2] = this.posZ - (distance * zAmount);
        return coords;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    /** Returns whether or not this mob is hostile towards players, changes if a mob is tamed, etc too. **/
    public boolean isHostile() {
    	return this.isHostileByDefault;
    }
    
    /** Overrides the vanilla method when check for EnumCreatureType.monster, it will return true if this mob is hostile and false if it is not regardless of this creature's actual EnumCreatureType. Takes tameable mobs into account too. **/
    @Override
	public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
    	// If the mob spawner is checking then we should return if it should take a place in the mob spawn count or not.
    	if(forSpawnCount) {
    		if(this.isMinion()) // Minions shouldn't take up the spawn count.
    			return false;
    		return type == this.mobInfo.spawnInfo.creatureType;
    	}
    	
		if(type.getCreatureClass() == IMob.class) // If checking for EnumCretureType.monster (IMob) return whether or not this creature is hostile instead.
			return this.isHostile();
        return type.getCreatureClass().isAssignableFrom(this.getClass());
    }
    
    // ========== Movement ==========
    /** Can this entity move currently? **/
    public boolean canMove() { return true; }
    /** Can this entity move across land currently? **/
    public boolean canWalk() { return true; }
    /** Can this entity free swim currently? (This doesn't stop the entity from moving in water but is used for smooth flight-like swimming). **/
    public boolean canSwim() { return false; }
    /** Can this entity jump currently? **/
    public boolean canJump() { return true; }
    /** Can this entity climb currently? **/
    public boolean canClimb() { return false; }
    /** Can this entity fly currently? **/
    public boolean canFly() { return false; }
    /** Can this entity by tempted (usually lured by an item) currently? **/
    public boolean canBeTempted() { return true; }
    
    // ========== Stealth ==========
    /** Can this entity stealth currently? **/
    public boolean canStealth() { return false; }

    /** Get the current stealth percentage, 0.0F = not stealthed, 1.0F = completely stealthed, used for animation such as burrowing crusks. **/
    public float getStealth() {
    	return this.dataWatcher.getWatchableObjectFloat(WATCHER_ID.STEALTH.id);
    }

    /** Sets the current stealth percentage. **/
    public void setStealth(float setStealth) {
    	setStealth = Math.min(setStealth, 1);
    	setStealth = Math.max(setStealth, 0);
    	if(this.worldObj != null && !this.worldObj.isRemote)
    		this.dataWatcher.updateObject(WATCHER_ID.STEALTH.id, setStealth);
    }

    /** Returns true if this mob is fully stealthed (1.0F or above). **/
    public boolean isStealthed() {
    	return this.dataWatcher.getWatchableObjectFloat(WATCHER_ID.STEALTH.id) >= 1;
    }

    /** Called when this mob is just started stealthing (reach 1.0F or above). **/
    public void startStealth() {}

    /** Called while this mob is stealthed on the update, can be used to clear enemies targets that are targeting this mob. The main EventListener also helps handling anti-targeting. **/
    public void onStealth() {
    	if(!this.worldObj.isRemote) {
    		if(this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityLiving)
    			if(((EntityLiving) this.getAttackTarget()).getAttackTarget() != null)
    				((EntityLiving)this.getAttackTarget()).setAttackTarget(null);
    	}
    }
    
    // ========== Climbing ==========
    /** Returns true if this entity is climbing a ladder or wall, can be used for animation. **/
    @Override
    public boolean isOnLadder() {
    	if(this.canFly() || this.canSwim()) return false;
    	if(this.canClimb())
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.CLIMBING.id) & 1) != 0;
    	else
    		return super.isOnLadder();
    }
    
    /** Used to set whether this mob is climbing up a block or not. **/
    public void setBesideClimbableBlock(boolean collided) {
    	if(this.canClimb()) {
	        byte climbing = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.CLIMBING.id);
	        if(collided) climbing = (byte)(climbing | 1);
	        else climbing &= -2;
	        this.dataWatcher.updateObject(WATCHER_ID.CLIMBING.id, Byte.valueOf(climbing));
    	}
    }

    /** Returns whether or not this mob is next to a climbable blocks or not. **/
    public boolean isBesideClimbableBlock() {
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.CLIMBING.id) & 1) != 0;
    }
    
    // ========== Falling ==========
    /** 
     * Called when the mob has hit the ground after falling, fallDistance is how far it fell and can be translated into fall damage.
     * getFallResistance() is used to reduce falling damage, if it is at or above 100 no falling damage is taken at all.
     * **/
    @Override
    protected void fall(float fallDistance) {
    	if(this.canFly())
    		return;
    	fallDistance -= this.getFallResistance();
    	if(this.getFallResistance() >= 100)
    		fallDistance = 0;
    	super.fall(fallDistance);
    }
    
    /** Called when this mob is falling, fallDistance is how far the mob has fell so far and onGround is true when it has hit the ground. **/
    @Override
    protected void updateFallState(double fallDistance, boolean onGround) {
    	if(!this.canFly()) super.updateFallState(fallDistance, onGround);
    }
    
    
    // ==================================================
   	//                      Drops
   	// ==================================================
    // ========== Item ID ==========
    /** Gets the item ID of what this mob mostly drops. This is provided for compatibility but is not used by the DropRate code. **/
    @Override
    protected int getDropItemId() {
        if(drops.get(0) != null && !this.isMinion())
        	return drops.get(0).itemID;
        else
        	return 0;
    }
    
    // ========== Drop Items ==========
    /** Cycles through all of this entity's DropRates and drops random loot, usually called on death. If this mob is a minion, this method is cancelled. **/
    @Override
    protected void dropFewItems(boolean playerKill, int lootLevel) {
    	if(this.isMinion()) return;
    	for(DropRate dropRate : this.drops) {
    		int quantity = dropRate.getQuantity(this.rand, lootLevel);
    		ItemStack dropStack = null;
    		if(quantity > 0)
    			dropStack = dropRate.getItemStack(this, quantity);
    		if(dropStack != null)
    			this.dropItem(dropStack);
    	}
    }
    
    // ========== Rare Drop ==========
    /** Called when doing a rare drop, this is part of the vanilla code and is not used, instead the custom DropRate code is used in dropFewItems(). **/
    @Override
    protected void dropRareDrop(int par1) {
    	if(this.isMinion()) return;
    	super.dropRareDrop(par1);
    }
    
    // ========== Drop Item ==========
    /** Tells this entity to drop the specified itemStack, used by DropRate and InventoryCreature, can be used by anything though. **/
    public void dropItem(ItemStack itemStack) {
    	this.entityDropItem(itemStack, 0.0F);
    }
    
    
    // ==================================================
    //                     Interact
    // ==================================================
    // ========== GUI ==========
    /** Opens this entity's GUI to the provided player. Must be called server side and usually called by an interact command. **/
    public void openGUI(EntityPlayer player) {
    	if(this.worldObj.isRemote)
    		return;
    	player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.ENTITY.id, this.worldObj, this.entityId, 0, 0);
    }

    /** The main interact method that is called when a player right clicks this entity. **/
    @Override
    public boolean interact(EntityPlayer player) {
	    ItemStack itemStack = player.inventory.getCurrentItem();
	    if(assessInteractCommand(getInteractCommands(player, itemStack), player, itemStack))
	    	return true;
	    return super.interact(player);
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
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	
    	// Item Commands:
    	if(itemStack != null) {
    		if(itemStack.itemID == Item.leash.itemID && this.canLeash(player))
    			commands.put(CMD_PRIOR.ITEM_USE.id, "Leash");

    		if(itemStack.itemID == Item.nameTag.itemID) {
    			if(this.canNameTag(player))
    				return new HashMap<Integer, String>(); // Cancels all commands so that vanilla can take care of name tagging.
    			else
    				commands.put(CMD_PRIOR.ITEM_USE.id, "Name Tag"); // Calls nothing and therefore cancels name tagging.
    		}
    				
    	}
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    /** Performs the given interact command. Could be used outside of the interact method if needed. **/
    public void performCommand(String command, EntityPlayer player, ItemStack itemStack) {
    	
    	// Leash:
    	if(command == "Leash") {
    		this.setLeashedToEntity(player, true);
    		this.consumePlayersItem(player, itemStack);
    	}
    	
    	// Name Tag:
    	// Vanilla takes care of this, it is in getInteractCommands so that other commands don't override it.
    }
    
    // ========== Can Name Tag ==========
    /** Returns true if this mob can be given a new name with a name tag by the provided player entity. **/
    public boolean canNameTag(EntityPlayer player) {
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
            itemStack.stackSize -= amount;
        if(itemStack.stackSize <= 0)
        	player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
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
    public int getNoBagSize() { return 0; }
    /** Returns the size that this mob's inventory increases by when it is provided with a bag item. (Look at this as the size of the bag item, not the new total creature inventory size.) **/
    public int getBagSize() { return 5; }
    
    /** Returns true if this mob is able to pick items up off the ground. **/
    public boolean canPickupItems() { return false; }
    /** Returns how much of the specified item stack this creature's inventory can hold. (Stack size, not empty slots, this allows the creature to merge stacks when picking up.) **/
    public int getSpaceForStack(ItemStack pickupStack) {
    	return this.inventory.getSpaceForStack(pickupStack);
    }
    
    // ========== Set Equipment ==========
    // Vanilla Conversion: 0 = Weapon/Item,  1 = Feet -> 4 = Head
    /**
     * A vanilla method for setting this mobs equipment, takes a slot ID and a stack.
     * 0 = Weapons, Tools or the item to hold out (like how vanilla zombies hold dropped items).
     * 1 = Feet, 2 = Legs, 3 = Chest and 4 = Head
     * 100 = Not used by vanilla but will convert to the bag slot for other mods to use.
     **/
    @Override
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
     * For instance "gold" is returned if it is wearing gold chest armor.
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
    	 List list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1.0D, 0.0D, 1.0D));
         Iterator iterator = list.iterator();

         while (iterator.hasNext()) {
             EntityItem entityItem = (EntityItem)iterator.next();
             if(!entityItem.isDead && entityItem.getEntityItem() != null) {
            	 ItemStack itemStack = entityItem.getEntityItem();
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
    	if(!entityItem.isDead && entityItem.getEntityItem() != null) {
    		ItemStack leftoverStack = this.inventory.autoInsertStack(entityItem.getEntityItem());
    		if(leftoverStack != null)
    			entityItem.setEntityItemStack(leftoverStack);
    		else
    			entityItem.setDead();
    	}
    }
    
    
    // ==================================================
  	//                     Immunities
  	// ==================================================
    // ========== Damage ==========
    /** Returns whether or not the given damage type is applicable, if not no damage will be taken. **/
    public boolean isDamageTypeApplicable(String type) { return true; }
    /** Returns whether or not this entity can be harmed by the specified entity. **/
    public boolean isDamageEntityApplicable(Entity entity) { return true; }
    /** Returns whether or not the specified potion effect can be applied to this entity. **/
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        return super.isPotionApplicable(potionEffect);
    }
    /** Returns whether or not this entity can be set on fire, this will block both the damage and the fire effect, use isDamageTypeApplicable() to block fire but keep the effect. **/
    public boolean canBurn() { return true; }
    /** Returns true if this mob should be damaged by the sun. **/
    public boolean daylightBurns() { return false; }
    /** Returns true if this mob should be damaged by water. **/
    public boolean waterDamage() { return false; }
    
    // ========== Environmental ==========
    /** If true, this mob isn't slowed down by webs. **/
    public boolean webproof() { return false; }
    /** If webproof() is false, this mob will be affected by webbing on the update that this is called. **/
    @Override
    public void setInWeb() { if(!webproof()) super.setInWeb(); }
    /** If true, this mob wont lose air when underwater. **/
    @Override
    
    // Breathing:
    public boolean canBreatheUnderwater() { return false; }
    /** If true, this mob will lose air when above water. **/
    public boolean canBreatheAboveWater() { return true; }
    /** Sets the current amount of air this mob has. **/
	@Override
	public void setAir(int air) {
		if(air == 300 && !this.canBreatheAboveWater()) return;
    	super.setAir(air);
    }
    
    /** Returns true if this mob is in water the rain. Uses the vanilla isWet() but takes dripping leaves, etc into account. **/
    public boolean waterContact() {
    	if(this.isWet())
    		return true;
    	if(this.worldObj.isRaining() && !this.isBlockUnderground((int)this.posX, (int)this.posY, (int)this.posZ))
    		return true;
    	return false;
    }
    
    /** Returns true if this mob is in lava. **/
    public boolean lavaContact() {
    	return this.handleLavaMovement();
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
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
    	return testLightLevel(i, j, k);
    }

    /** Returns a light rating for the light level the specified XYZ position.
     * Dark enough for spawnsInDarkness: 0 = Dark, 1 = Dim
     * Light enough for spawnsInLight: 2 = Light, 3 = Bright
    **/
    public byte testLightLevel(int x, int y, int z) {
        /*if(this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand.nextInt(32))
            return false;*/
        int light = this.worldObj.getBlockLightValue(x, y, z);
        if(this.worldObj.isThundering()) {
            int i1 = this.worldObj.skylightSubtracted;
            this.worldObj.skylightSubtracted = 10;
            light = this.worldObj.getBlockLightValue(x, y, z);
            this.worldObj.skylightSubtracted = i1;
        }
        
        if(light == 0) return 0;
        if(light <= 8) return 1;
        if(light <= 14) return 2;
        return 3;
    }
    
    /** A client and server friendly solution to check if it is daytime or not. **/
    public boolean isDaytime() {
    	if(!this.worldObj.isRemote)
    		return this.worldObj.isDaytime();
    	long time = this.worldObj.getWorldTime();
    	if(time < 12500)
    		return true;
    	if(time >= 12542 && time < 23460)
    		return false;
    	return true;
    }
    
    // Nearby Creature Count:
    /** Returns how many entities of the specified class around within the specified ranged, used mostly for mobs that summon other mobs and other group behaviours. **/
    public int nearbyCreatureCount(Class targetClass, double range) {
    	List targets = this.worldObj.getEntitiesWithinAABB(targetClass, this.boundingBox.expand(range, range, range));
    	return targets.size();
    }
    
    // ========== Advanced AI ==========
    /** This should always be true, the old AI system doesn't work with this base class. **/
    @Override
    protected boolean isAIEnabled() { return true; }
    
    // ========== Creature Attribute ==========
    /** Returns this creature's attriute. **/
   	@Override
    public EnumCreatureAttribute getCreatureAttribute() { return this.attribute; }

    // ========== Y Offset ==========
    /** A Y Offset used to position the mob that is riding this mob. **/
   	@Override
    public double getYOffset() { return super.getYOffset() - 0.5D; }
    
    
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
    		this.firstSpawn = false;
    	}
    	if(nbtTagCompound.hasKey("Stealth")) {
    		this.setStealth(nbtTagCompound.getFloat("Stealth"));
    	}
    	if(nbtTagCompound.hasKey("IsMinion")) {
    		this.setMinion(nbtTagCompound.getBoolean("IsMinion"));
    	}
        super.readEntityFromNBT(nbtTagCompound);
        this.inventory.readFromNBT(nbtTagCompound);
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
    	nbtTagCompound.setBoolean("FirstSpawn", false);
    	nbtTagCompound.setFloat("Stealth", this.getStealth());
    	nbtTagCompound.setBoolean("IsMinion", this.isMinion());
        super.writeEntityToNBT(nbtTagCompound);
        this.inventory.writeToNBT(nbtTagCompound);
    }
    
    
    // ==================================================
  	//                       Client
  	// ==================================================
    // ========== Just Attacked Animation ==========
    /** Returns true if this creature should play it's attack animation. **/
    public boolean justAttacked() { return justAttacked > 0; }
    /** Called when this mob has just attacked, triggers the attack animation. **/
    public void setJustAttacked() { this.justAttacked = this.justAttackedTime; }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    /** Returns this creature's main texture. **/
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.getTextureName()) == null)
    		AssetManager.addTexture(this.getTextureName(), this.mod.getDomain(), "textures/entity/" + this.getTextureName().toLowerCase() + ".png");
    	return AssetManager.getTexture(this.getTextureName());
    }

    /** Returns this creature's equipment texture. **/
    public ResourceLocation getEquipmentTexture(String equipmentName) {
    	String textureName = this.getTextureName();
    	textureName += "_" + equipmentName;
    	if(AssetManager.getTexture(textureName) == null)
    		AssetManager.addTexture(textureName, this.mod.getDomain(), "textures/entity/" + textureName.toLowerCase() + ".png");
    	return AssetManager.getTexture(textureName);
    }

    /** Gets the name of this creature's texture, normally links to it's code name but can be overriden by subtypes and alpha creatures. **/
    public String getTextureName() {
    	return this.entityName;
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    /** Returns the sound to play when this creature is making a random ambient roar, grunt, etc. **/
    @Override
    protected String getLivingSound() { return AssetManager.getSound(this.entityName + "Say"); }

    // ========== Hurt ==========
    /** Returns the sound to play when this creature is damaged. **/
    @Override
    protected String getHurtSound() { return AssetManager.getSound(this.entityName + "Hurt"); }

    // ========== Death ==========
    /** Returns the sound to play when this creature dies. **/
    @Override
    protected String getDeathSound() { return AssetManager.getSound(this.entityName + "Death"); }
     
    // ========== Step ==========
    /** Plays the footstep sound that this creature makes when moving on the ground. **/
    @Override
    protected void playStepSound(int par1, int par2, int par3, int par4) {
    	 if(this.canFly() || !this.hasStepSound) return;
    	 this.playSound(AssetManager.getSound(this.entityName + "Step"), 0.25F, 1.0F);
    }
     
    // ========== Jump ==========
    /** Plays the jump sound when this creature jumps. **/
    public void playJumpSound() {
    	if(!this.hasJumpSound) return;
     	this.playSound(AssetManager.getSound(this.entityName + "Jump"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
     
    // ========== Fly ==========
    /** Plays a flying sound, usually a wing flap, called randomly when flying. **/
    protected void playFlySound() {
    	if(!this.canFly()) return;
      	this.playSound(AssetManager.getSound(this.entityName + "Fly"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }

    // ========== Attack ==========
    /** Plays an attack sound, called once this creature has attacked. note that ranged attacks normally rely on the projectiles playing their launched sound instead. **/
    protected void playAttackSound() {
     	if(!this.hasAttackSound) return;
     	this.playSound(AssetManager.getSound(this.entityName + "Attack"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}