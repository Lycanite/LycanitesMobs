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
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public abstract class EntityCreatureBase extends EntityLiving {
    // UUID:
	//public static final UUID field_110179_h = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
    
	// Info:
	public String entityName = "Name";
	public ILycaniteMod mod;
	public String eggName = "SpawnEgg";
	public EnumCreatureAttribute attribute = EnumCreatureAttribute.UNDEAD;
	
	// Size:
	public float setWidth = 0.6F;
	public float setDepth = 0.6F;
	public float setHeight = 1.8F;
	
	// Stats:
	public int experience = 5;
	public int[] rangedDamage = new int[] {0, 0, 0};
	public byte attackPhase = 0;
	public byte attackPhaseMax = 0;
	
	// Abilities:
	public boolean spreadFire = false;
	public boolean stealthPrev = false;
	
	// Positions:
	private ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
    private float homeDistanceMax = -1.0F;
    
    // Spawning:
    public boolean despawnOnPeaceful = true;
    public boolean despawnNaturally = true;
    public boolean spawnsInDarkness = false;
    public boolean spawnsInBlock = false;
    public boolean spawnsUnderground = true;
    public boolean spawnsInWater = false;
    public boolean isMinion = false;
    
    // Movement:
    private boolean leashAIActive = false;
    private EntityAIBase leashMoveTowardsRestrictionAI = new EntityAIMoveRestriction(this);
    public FlightNavigator flightNavigator;
    
    // Targets:
    private EntityLivingBase masterTarget;
    private EntityLivingBase parentTarget;
    private EntityLivingBase avoidTarget;
	
	// Client:
	public short justAttacked = 0;
	public short justAttackedTime = 5;
	public boolean hasAttackSound = false;
	public boolean hasJumpSound = false;
	public int flySoundSpeed = 0;
	
	// Data Watcher:
	private static byte watcherID = 12;
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
	public static enum TARGET_ID {
		ATTACK((byte)1), MASTER((byte)2), PARENT((byte)4), AVOID((byte)8), RIDER((byte)16);
		public final byte id;
	    private TARGET_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
	public static enum ANIM_ID {
		ATTACKED((byte)1), GROUNDED((byte)2);
		public final byte id;
	    private ANIM_ID(byte value) { this.id = value; }
	    public byte getValue() { return id; }
	}
	
	// A list of commonly used interact command priorities.
	public static enum CMD_PRIOR {
		OVERRIDE(0), IMPORTANT(1), EQUIPPING(2), ITEM_USE(3), EMPTY_HAND(4), MAIN(5);
		public final int id;
	    private CMD_PRIOR(int value) { this.id = value; }
	    public int getValue() { return id; }
	}
	
	// Items:
	public InventoryCreature inventory;
    public List<DropRate> drops = new ArrayList<DropRate>();
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityCreatureBase(World world) {
        super(world);
        this.flightNavigator = new FlightNavigator(this);
    }
    
    // ========== Setup ==========
    public void setupMob() {
        // Size:
        this.width = setWidth;
        this.height = setHeight;
        this.setSize(setWidth, setHeight);
        
        // Stats:
        this.stepHeight = 0.5F;
        this.experienceValue = experience;
        this.isImmuneToFire = !this.canBurn();
        this.inventory = new InventoryCreature(entityName, this);
        this.loadCustomDrops();
    }
    
    public void loadCustomDrops() {
    	if(this.mod.getConfig().customDrops.containsKey(this.entityName)) {
    		String customDropsString = this.mod.getConfig().customDrops.get(this.entityName);
    		if(customDropsString != null && customDropsString.length() > 0)
	    		for(String customDropEntryString : customDropsString.split(",")) {
	    			String[] customDropValues = customDropEntryString.split(":");
	    			if(customDropValues.length == 5) {
						int dropID = Integer.parseInt(customDropValues[0]); // Easily change to String for 1.7.2
						int dropMeta = Integer.parseInt(customDropValues[1]);
						float dropChance = Float.parseFloat(customDropValues[2]);
						int dropMin = Integer.parseInt(customDropValues[3]);
						int dropMax = Integer.parseInt(customDropValues[4]);
						this.drops.add(new DropRate(dropID, dropMeta, dropChance).setMinAmount(dropMin).setMaxAmount(dropMax));
	    			}
	    		}
    	}
    }
    
    // ========== Attributes ==========
    protected void applyEntityAttributes() {
        HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 0D);
		baseAttributes.put("movementSpeed", 0D);
		baseAttributes.put("knockbackResistance", 0D);
		baseAttributes.put("followRange", 0D);
		baseAttributes.put("attackDamage", 0D);
        this.applyEntityAttributes(baseAttributes);
    }
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
    @Override
    public String getEntityName() {
    	if(this.hasCustomNameTag())
    		return this.getCustomNameTag();
    	else
    		return this.getFullName();
    }
    
    public String getFullName() {
    	String name = "";
    	if(getAgeName() != "")
    		name += getAgeName() + " ";
    	if(getSubspeciesName() != "")
    		name += getSubspeciesName() + " ";
    	return name + getSpeciesName();
    }
    
    public String getSpeciesName() {
    	String entityName = EntityList.getEntityString(this);
    	if(entityName == null)
    		return "Creature";
    	return StatCollector.translateToLocal("entity." + entityName + ".name");
    }
    
    public String getSubspeciesName() {
    	return "";
    }
    
    public String getAgeName() {
    	return "";
    }
    
    
    // ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Can Spawn Here ==========
    @Override
    public boolean getCanSpawnHere() {
    	
    	// Peaceful Check:
        if(this.despawnOnPeaceful && this.worldObj.difficultySetting <= 0) return false;
        
        // Fixed Spawning Checks:
    	int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        if(this.spawnsInDarkness && this.testLightLevel() > 1) return false;
        if(!this.worldObj.checkNoEntityCollision(this.boundingBox)) return false;
        if(!this.spawnsInBlock && !this.spawnsInWater && !this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()) return false;
        
    	// Spawner Check:
        if(this.isSpawnerNearby(i, j, k))
        	return true;
        
        // Natural Spawning Checks:
        if(ObjectManager.getMobDimensions(this.entityName).length <= 0) return false;
        else {
        	boolean validDimension = false;
        	for(int spawnDimension : ObjectManager.getMobDimensions(this.entityName)) {
        		if(this.dimension == spawnDimension) {
        			validDimension = true;
        			break;
        		}
        	}
        	if(!validDimension) return false;
        }
        if(this.getBlockPathWeight(i, j, k) < 0.0F) return false;
        if(!this.spawnsInWater && this.worldObj.isAnyLiquid(this.boundingBox)) return false;
        if(!this.spawnsUnderground && this.isBlockUnderground(i, j + 1, k)) return false;
        if(!spawnBlockCheck(i, j, k)) return false;
        
        // Forced Spawn Chance:
        if(this.mod.getConfig().spawnChances.containsKey(this.entityName))
	        if(this.mod.getConfig().spawnChances.get(this.entityName) < 100)
	        	if(this.mod.getConfig().spawnChances.get(this.entityName) <= 0 || this.rand.nextInt(99) < this.mod.getConfig().spawnChances.get(this.entityName))
	        		return false;
        
        return true;
    }

    public boolean spawnBlockCheck(int x, int y, int z) {
        return true;
    }
    
    // ========== Max Spawned In Chunk ==========
    @Override
    public int getMaxSpawnedInChunk() {
        return this.mod.getConfig().spawnLimits.get(this.entityName);
    }
    
    // ========== Egg Spawn ==========
    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
        par1EntityLivingData = super.onSpawnWithEgg(par1EntityLivingData);
        return par1EntityLivingData;
    }
    
    // ========== Despawning ==========
    @Override
    protected boolean canDespawn() {
    	if(this.despawnNaturally)
    		return !this.getLeashed();
    	return false;
    }
    
    public boolean despawnCheck() {
        if(this.worldObj.isRemote)
        	return false;
        if(this.despawnOnPeaceful && this.worldObj.difficultySetting <= 0 && !this.getLeashed())
        	return true;
        return false;
    }
    
    // ========== Spawner Checking ==========
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
    public void setMinion(boolean minion) { this.isMinion = true; }
    public boolean isMinion() { return this.isMinion; }
    
    
    
    // ==================================================
  	//                     Updates
  	// ==================================================
    // ========== Main ==========
    public void onUpdate() {
        this.onSyncUpdate();
        super.onUpdate();
        
        if(this.despawnCheck())
        	this.setDead();
        
        if(!this.worldObj.isRemote) {
        	this.setBesideClimbableBlock(this.isCollidedHorizontally);
        	
        	if(this.flySoundSpeed > 0 && this.ticksExisted % 20 == 0)
        		this.playFlySound();
        }
    }
    
    // ========== AI ==========
    @Override
    protected void updateAITasks() {
		if(this.canFly()) flightNavigator.updateFlight();
        super.updateAITasks();
    }
    
    // ========== Living ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.updateArmSwingProgress();
        
        // Gliding:
        if(this.getFallingMod() != 0.0D && !this.onGround && this.motionY < 0.0D) {
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
    // ========== Get Block Path Weight ==========
    public float getBlockPathWeight(int par1, int par2, int par3) {
        if(this.spawnsInDarkness)
        	return 0.5F - this.worldObj.getLightBrightness(par1, par2, par3);
    	return 0.0F;
    }
    
    // ========== Move with Heading ==========
    @Override
    public void moveEntityWithHeading(float moveStrafe, float moveForward) {
    	if(!this.canFly()) super.moveEntityWithHeading(moveStrafe, moveForward);
    	else this.flightNavigator.flightMovement(moveStrafe, moveForward);
    }
    
    // ========== Clear Movement ==========
    public void clearMovement() {
    	if(!canFly())
        	this.getNavigator().clearPathEntity();
        else
        	this.flightNavigator.clearTargetPosition(1.0D);
    }
    
    // ========== Leash ==========
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
    public boolean isMoving() {
    	if(!canFly())
        	return this.getNavigator().getPath() != null;
        else
        	return !this.flightNavigator.atTargetPosition();
    }
    
    @Override
    public boolean allowLeashing() { return false; }
    
    // ========== Can leash ==========
    public boolean canLeash(EntityPlayer player) {
	    return false;
    }
    
    // ========== Test Leash ==========
    public void testLeash(float distance) {}
    
    // ========== Set Ai Speed ==========
    @Override
    public void setAIMoveSpeed(float speed) {
        super.setAIMoveSpeed(speed* this.getSpeedMod());
    }
    
    // ========== Movement Speed Modifier ==========
    public float getSpeedMod() {
    	return 1.0F;
    }
    
    // ========== Falling Speed Modifier ==========
    public double getFallingMod() {
    	return 0.0D;
    }
    
    // ========== Leap ==========
    public void leap(double distance, double leapHeight) {
    	double angle = Math.toRadians(this.rotationYaw);
    	double xAmount = -Math.sin(angle);
    	double zAmount = Math.cos(angle);
        this.motionX = xAmount * distance + this.motionX * 0.2D;
        this.motionZ = zAmount * distance + this.motionZ * 0.2D;
        this.motionY = leapHeight;
    }
    
    // ========== Leap to Target ==========
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
    public void setHome(int x, int y, int z, int distance) {
    	setHomePosition(x, y, z);
    	this.setHomeDistanceMax((int)distance);
    }
    public void setHomePosition(int x, int y, int z) {
    	this.homePosition.set(x, y, z);
    }
    public void setHomeDistanceMax(float newDist) { this.homeDistanceMax = newDist; }
    public ChunkCoordinates getHomePosition() { return this.homePosition; }
    public float getHomeDistanceMax() { return this.homeDistanceMax; }
    public void detachHome() {
    	this.setHomeDistanceMax(-1);
    }
    public boolean hasHome() {
    	return this.getHomePosition() != null && this.getHomeDistanceMax() >= 0;
    }
    public boolean positionNearHome(int par1, int par2, int par3) {
        if(!hasHome()) return true;
        return this.homePosition.getDistanceSquared(par1, par2, par3) < this.getHomeDistanceMax() * this.getHomeDistanceMax();
    }
    
    
    // ==================================================
  	//                      Attacks
  	// ==================================================
    // ========== Can Attack ==========
	@Override
	public boolean canAttackClass(Class targetClass) { return true; }
	public boolean canAttackEntity(EntityLivingBase targetEntity) { return true; }
	
    // ========== Targets ==========
    public EntityLivingBase getMasterAttackTarget() {
    	if(this.masterTarget == null) return null;
    	if(this.masterTarget instanceof EntityLiving)
    		return ((EntityLiving)this.masterTarget).getAttackTarget();
    	return null;
    }
    
    public EntityLivingBase getParentAttackTarget() {
    	if(this.parentTarget == null) return null;
    	if(this.parentTarget instanceof EntityCreatureBase)
    		return ((EntityCreatureBase)this.parentTarget).getAttackTarget();
    	else if(this.parentTarget instanceof net.minecraft.entity.EntityCreature)
    		return ((net.minecraft.entity.EntityCreature)this.parentTarget).getAttackTarget();
    	return null;
    }
    
    // ========== Melee ==========
    public boolean meleeAttack(Entity target, double damageScale) {
    	boolean success = true;
    	int difficulty = Math.max(this.worldObj.difficultySetting, 1) - 1;
    	if(this.attackEntityAsMob(target, damageScale)) {
    		
    		// Spread Fire:
        	if(this.spreadFire && this.isBurning() && this.rand.nextFloat() < (float)difficulty * 0.3F)
        		target.setFire(difficulty * 2);
        	
    	}
    	this.setJustAttacked();
    	return success;
    }

    // ========== Ranged ==========
    public void rangedAttack(Entity target, float range) {
    	this.setJustAttacked();
    }
    
    // ========== Phase ==========
    public byte getAttackPhase() {
    	return this.dataWatcher.getWatchableObjectByte(WATCHER_ID.ATTACK_PHASE.id);
    }
    public void setAttackPhase(byte setAttackPhase) { attackPhase = setAttackPhase; }
    public void nextAttackPhase() {
    	if(++attackPhase > (attackPhaseMax - 1))
    		attackPhase = 0;
    }
    
    // ========== Deal Damage ==========
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
    public float getAttackDamage(double damageScale) {
    	float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int difficulty = Math.max(this.worldObj.difficultySetting, 1);
        damage += difficulty;
        damage *= damageScale;
        return damage;
    }
    
    // ========== Attacked From ==========
    @Override
    public boolean attackEntityFrom(DamageSource damageSrc, float damage) {
    	if(this.worldObj.isRemote) return false;
        if(this.isEntityInvulnerable()) return false;
        if(!this.isDamageTypeApplicable(damageSrc.getDamageType())) return false;
        if(!this.isDamageEntityApplicable(damageSrc.getEntity())) return false;
        damage = damage * this.getDamageModifier(damageSrc);
        
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
    
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                      Death
   	// ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if(!this.worldObj.isRemote)
            this.inventory.dropInventory();
    }
    
    
    // ==================================================
  	//                      Targets
  	// ==================================================
    public boolean isAggressive() { return true; }
    
    public boolean hasAttackTarget() {
    	if(!this.worldObj.isRemote)
    		return this.getAttackTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.ATTACK.id) > 0;
    }
    
    public EntityLivingBase getMasterTarget() { return this.masterTarget; }
    public void setMasterTarget(EntityLivingBase setTarget) { this.masterTarget = setTarget; }
    public boolean hasMaster() {
    	if(!this.worldObj.isRemote)
    		return this.getMasterTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.MASTER.id) > 0;
    }
    
    public EntityLivingBase getParentTarget() { return this.parentTarget; }
    public void setParentTarget(EntityLivingBase setTarget) { this.parentTarget = setTarget; }
    public boolean hasParent() {
    	if(!this.worldObj.isRemote)
    		return this.getParentTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.PARENT.id) > 0;
    }
    
    public EntityLivingBase getAvoidTarget() { return this.avoidTarget; }
    public void setAvoidTarget(EntityLivingBase setTarget) { this.avoidTarget = setTarget; }
    public boolean hasAvoidTarget() {
    	if(!this.worldObj.isRemote)
    		return this.getAvoidTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.AVOID.id) > 0;
    }
    
    public EntityLivingBase getOwner() { return null; }

    public EntityLivingBase getRider() {
    	if(this.riddenByEntity instanceof EntityLivingBase)
    		return (EntityLivingBase)this.riddenByEntity;
    	else
    		return null;
    }
    public Entity getRiderTarget() { return this.riddenByEntity; }
    public void setRiderTarget(Entity setTarget) { this.riddenByEntity = setTarget; }
    public boolean hasRiderTarget() {
    	if(!this.worldObj.isRemote)
    		return this.getRiderTarget() != null;
    	else
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.TARGET.id) & TARGET_ID.RIDER.id) > 0;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    public boolean canMove() { return true; }
    public boolean canWalk() { return true; }
    public boolean canSwim() { return true; }
    public boolean canJump() { return true; }
    public boolean canClimb() { return false; }
    public boolean canFly() { return false; }
    public boolean canBeTempted() { return true; }
    
    // ========== Stealth ==========
    public boolean canStealth() { return false; }
    
    public float getStealth() {
    	return this.dataWatcher.getWatchableObjectFloat(WATCHER_ID.STEALTH.id);
    }
    
    public void setStealth(float setStealth) {
    	setStealth = Math.min(setStealth, 1);
    	setStealth = Math.max(setStealth, 0);
    	if(!this.worldObj.isRemote)
    		this.dataWatcher.updateObject(WATCHER_ID.STEALTH.id, setStealth);
    }
    
    public boolean isStealthed() {
    	return this.dataWatcher.getWatchableObjectFloat(WATCHER_ID.STEALTH.id) >= 1;
    }
    
    public void startStealth() {}
    
    public void onStealth() {
    	if(!this.worldObj.isRemote) {
    		if(this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityLiving)
    			if(((EntityLiving) this.getAttackTarget()).getAttackTarget() != null)
    				((EntityLiving)this.getAttackTarget()).setAttackTarget(null);
    	}
    }
    
    // ========== Climbing ==========
    @Override
    public boolean isOnLadder() {
    	if(this.canFly()) return false;
    	if(this.canClimb())
    		return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.CLIMBING.id) & 1) != 0;
    	else
    		return super.isOnLadder();
    }
    
    public void setBesideClimbableBlock(boolean collided) {
    	if(this.canClimb()) {
	        byte climbing = this.dataWatcher.getWatchableObjectByte(WATCHER_ID.CLIMBING.id);
	        if(collided) climbing = (byte)(climbing | 1);
	        else climbing &= -2;
	        this.dataWatcher.updateObject(WATCHER_ID.CLIMBING.id, Byte.valueOf(climbing));
    	}
    }
    
    public boolean isBesideClimbableBlock() {
        return (this.dataWatcher.getWatchableObjectByte(WATCHER_ID.CLIMBING.id) & 1) != 0;
    }
    
    // ========== Flight ==========
    @Override
    protected void fall(float fallDistance) {
    	if(this.canFly())
    		return;
    	fallDistance -= this.getFallResistance();
    	if(this.getFallResistance() >= 100)
    		fallDistance = 0;
    	super.fall(fallDistance);
    }
    
    @Override
    protected void updateFallState(double fallDistance, boolean onGround) {
    	if(!this.canFly()) super.updateFallState(fallDistance, onGround);
    }
    
    
    // ==================================================
   	//                      Drops
   	// ==================================================
    // ========== Item ID ==========
    @Override
    protected int getDropItemId() {
        if(drops.get(0) != null && !this.isMinion())
        	return drops.get(0).itemID;
        else
        	return 0;
    }
    
    // ========== Drop Items ==========
    @Override
    protected void dropFewItems(boolean playerKill, int lootLevel) {
    	if(this.isMinion()) return;
    	for(DropRate dropRate : drops) {
    		int quantity = dropRate.getQuantity(this.rand, lootLevel);
    		ItemStack dropStack = null;
    		if(quantity > 0)
    			dropStack = dropRate.getItemStack(this, quantity);
    		if(dropStack != null)
    			this.dropItem(dropStack);
    	}
    }
    
    // ========== Rare Drop ==========
    @Override
    protected void dropRareDrop(int par1) {
    	if(this.isMinion()) return;
    	super.dropRareDrop(par1);
    }
    
    // ========== Drop Item ==========
    public void dropItem(ItemStack itemStack) {
    	this.entityDropItem(itemStack, 0.0F);
    }
    
    
    // ==================================================
    //                     Interact
    // ==================================================
    // ========== GUI ==========
    public void openGUI(EntityPlayer player) {
    	if(this.worldObj.isRemote)
    		return;
    	player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.ENTITY.id, this.worldObj, this.entityId, 0, 0);
    }
    
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
    public boolean canNameTag(EntityPlayer player) {
    	return true;
    }
    
    // ========== Consume Player's Item ==========
    public void consumePlayersItem(EntityPlayer player, ItemStack itemStack) {
    	consumePlayersItem(player, itemStack, 1);
    }
    public void consumePlayersItem(EntityPlayer player, ItemStack itemStack, int amount) {
    	if(!player.capabilities.isCreativeMode)
            itemStack.stackSize -= amount;
        if(itemStack.stackSize <= 0)
        	player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
    }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public boolean canCarryItems() { return getInventorySize() > 0; }
    public int getInventorySize() { return this.inventory.getSizeInventory(); }
    public int getInventorySizeMax() { return Math.max(this.getNoBagSize(), this.getBagSize()); }
    public boolean hasBag() {
    	return this.inventory.getEquipmentStack("bag") != null;
    }
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    public boolean canPickupItems() { return false; }
    public int getSpaceForStack(ItemStack pickupStack) {
    	return this.inventory.getSpaceForStack(pickupStack);
    }
    
    // ========== Set Equipment ==========
    // Vanilla Conversion: 0 = Weapon/Item,  1 = Feet -> 4 = Head
    @Override
    public void setCurrentItemOrArmor(int slot, ItemStack itemStack) {
        String type = "item";
    	if(slot == 0) type = "weapon";
    	if(slot == 1) type = "feet";
    	if(slot == 2) type = "legs";
    	if(slot == 3) type = "chest";
    	if(slot == 4) type = "head";
    	this.inventory.setEquipmentStack(type, itemStack);
    }

    // ========== Get Equipment ==========
    public String getEquipmentName(String type) {
    	if(this.inventory.getEquipmentGrade(type) != null)
    		return type + this.inventory.getEquipmentGrade(type);
    	return null;
    }
    
    // ========== Get Total Armor Value ==========
    @Override
    public int getTotalArmorValue() {
    	return this.inventory.getArmorValue();
    }
    
    // ========== Pickup Items ==========
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
    public boolean isDamageTypeApplicable(String type) { return true; }
    public boolean isDamageEntityApplicable(Entity entity) { return true; }
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        return super.isPotionApplicable(potionEffect);
    }
    public boolean canBurn() { return true; }
    public boolean daylightBurns() { return false; }
    public boolean waterDamage() { return false; }
    
    // ========== Environmental ==========
    public boolean webproof() { return false; }
    @Override
    public void setInWeb() { if(!webproof()) super.setInWeb(); }
    @Override
    public boolean canBreatheUnderwater() { return false; }
    
    public float getFallResistance() {
    	return 0;
    }
    
    
    // ==================================================
  	//                     Utilities
  	// ==================================================
    // ========== Get Light Type ==========
    // 0 = Dark, 1 = Dim, 2 = Light, 3 = Bright
    public byte testLightLevel() {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
    	return testLightLevel(i, j, k);
    }
    
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
    
    // Nearby Creature Count:
    public int nearbyCreatureCount(Class targetClass, double range) {
    	List targets = this.worldObj.getEntitiesWithinAABB(targetClass, this.boundingBox.expand(range, range, range));
    	return targets.size();
    }
    
    // ========== Advanced AI ==========
    @Override
    protected boolean isAIEnabled() { return true; }
    
    // ========== Creature Attribute ==========
   	@Override
    public EnumCreatureAttribute getCreatureAttribute() { return attribute; }

    // ========== Y Offset ==========
   	@Override
    public double getYOffset() { return super.getYOffset() - 0.5D; }
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
    	if(nbtTagCompound.hasKey("Stealth"))
    		this.setStealth(nbtTagCompound.getFloat("Stealth"));
    	if(nbtTagCompound.hasKey("Minion"))
    		this.setMinion(nbtTagCompound.getBoolean("Minion"));
        super.readEntityFromNBT(nbtTagCompound);
        this.inventory.readFromNBT(nbtTagCompound);
    }
    
    // ========== Write ==========
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
    	nbtTagCompound.setFloat("Stealth", this.getStealth());
    	nbtTagCompound.setBoolean("Minion", this.isMinion());
        super.writeEntityToNBT(nbtTagCompound);
        this.inventory.writeToNBT(nbtTagCompound);
    }
    
    
    // ==================================================
  	//                       Client
  	// ==================================================
    // ========== Just Attacked Animation ==========
    public boolean justAttacked() { return justAttacked > 0; }
    public void setJustAttacked() { this.justAttacked = this.justAttackedTime; }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.getTextureName()) == null)
    		AssetManager.addTexture(this.getTextureName(), this.mod.getDomain(), "textures/entity/" + this.getTextureName().toLowerCase() + ".png");
    	return AssetManager.getTexture(this.getTextureName());
    }

    public ResourceLocation getEquipmentTexture(String equipmentName) {
    	String textureName = this.getTextureName();
    	textureName += "_" + equipmentName;
    	if(AssetManager.getTexture(textureName) == null)
    		AssetManager.addTexture(textureName, this.mod.getDomain(), "textures/entity/" + textureName.toLowerCase() + ".png");
    	return AssetManager.getTexture(textureName);
    }
    
    public String getTextureName() {
    	return this.entityName;
    }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    @Override
    protected String getLivingSound() { return AssetManager.getSound(entityName + "Say"); }

    // ========== Hurt ==========
    @Override
    protected String getHurtSound() { return AssetManager.getSound(entityName + "Hurt"); }

    // ========== Death ==========
    @Override
    protected String getDeathSound() { return AssetManager.getSound(entityName + "Death"); }
     
    // ========== Step ==========
    @Override
    protected void playStepSound(int par1, int par2, int par3, int par4) {
    	 if(this.canFly()) return;
    	 this.playSound(AssetManager.getSound(entityName + "Step"), 0.25F, 1.0F);
    }
     
    // ========== Jump ==========
    public void playJumpSound() {
    	if(!this.hasJumpSound) return;
     	this.playSound(AssetManager.getSound(entityName + "Jump"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
     
    // ========== Fly ==========
    protected void playFlySound() {
    	if(!this.canFly()) return;
      	this.playSound(AssetManager.getSound(entityName + "Fly"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }

    // ========== Attack ==========
    protected void playAttackSound() {
     	if(!this.hasAttackSound) return;
     	this.playSound(AssetManager.getSound(entityName + "Attack"), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    }
}