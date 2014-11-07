package lycanite.lycanitesmobs.junglemobs.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIStayByHome;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetMasterAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityVespidQueen extends EntityCreatureAgeable implements IMob, IGroupPredator {
	public boolean inHiveCache = false;
	private int hiveCheckCacheTime = 0;
	public class HiveExposedCoordinates {
		public Block block;
		public ChunkCoordinates chunkCoordinates;
		public int orientationMetadata;
		
		public HiveExposedCoordinates(Block block, ChunkCoordinates chunkCoordinates, int orientationMetadata) {
			this.block = block;
			this.chunkCoordinates = chunkCoordinates;
			this.orientationMetadata = orientationMetadata;
		}
	}
	public List<HiveExposedCoordinates> hiveExposedBlocks = new ArrayList<HiveExposedCoordinates>();
	private int hiveExposedBlockCacheTime = 0;
	
	private int vespidQueenSwarmLimit = 20;
	private boolean vespidHiveBuilding = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVespidQueen(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 1;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.hasAttackSound = true;
        
        this.setWidth = 1.2F;
        this.setHeight = 1.6F;
        this.setupMob();
        
        this.canGrow = true;
        this.babySpawnChance = 0D;
        
        this.justAttackedTime = (short)(10);
        
        this.vespidQueenSwarmLimit = ConfigBase.getConfig(this.group, "general").getInt("Features", "Vespid Queen Swarm Limit", this.vespidQueenSwarmLimit, "Limits how many Vespid drones a Queen can have before she will no longer spawn babies in hives.");
        this.vespidHiveBuilding = ConfigBase.getConfig(this.group, "general").getBool("Features", "Vespid Hive Building", this.vespidHiveBuilding, "Set to false to stop Vespids from building hives all together.");
    	
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setRate(10).setLongMemory(true));
        this.tasks.addTask(7, new EntityAIStayByHome(this));
        this.tasks.addTask(8, new EntityAIWander(this).setPauseRate(320));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAITargetMasterAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityConba.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVespidQueen.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVespid.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IAnimals.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 60D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.25D);
		baseAttributes.put("followRange", 24D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.clay_ball), 0.5F).setMaxAmount(16));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("propolis")), 0.5F).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getBlock("veswax")), 0.5F).setMaxAmount(8));
	}
	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	if(this.hiveFoundationsSet())
    		return true;
    	return super.isPersistant();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(this.vespidHiveBuilding) {
	        // Hive Cache Times:
	        this.hiveCheckCacheTime--;
	        if(this.hiveCheckCacheTime < 0)
	        	this.hiveCheckCacheTime = 0;
	        this.hiveExposedBlockCacheTime--;
	        if(this.hiveExposedBlockCacheTime < 0)
	        	this.hiveExposedBlockCacheTime = 0;
	        
	        // Set Home In Hive:
	        if(!this.worldObj.isRemote && !this.hasHome()) {
	        	if(this.hiveFoundationsSet()) { //|| (this.entityAge % 40 == 0 && this.getRNG().nextFloat() <= 0.25F)) {
	        		this.setHome((int)this.posX, (int)this.posY, (int)this.posZ, 16F);
	        	}
	        }
	        
	        // Spawn Babies:
	        if(!this.worldObj.isRemote && this.hiveFoundationsSet() && this.ticksExisted % 20 == 0) {
				this.allyUpdate();
	        }
        }
        
        // Don't Keep Infected Conbas Targeted:
        if(!this.worldObj.isRemote && this.getAttackTarget() instanceof EntityConba) {
        	if(((EntityConba)this.getAttackTarget()).vespidInfection) {
        		this.setAttackTarget(null);
        	}
        }
    }
    
    // ========== Spawn Babies ==========
	public void allyUpdate() {
		if(this.worldObj.isRemote)
			return;
		
		// Spawn Babies:
		if(this.vespidQueenSwarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 32D) < this.vespidQueenSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.1F) {
				EntityLivingBase minion = this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
				if(minion instanceof EntityCreatureAgeable) {
		    		((EntityCreatureAgeable)minion).setGrowingAge(((EntityCreatureAgeable) minion).growthTime);
		    	}
			}
		}
	}
	
    public EntityLivingBase spawnAlly(double x, double y, double z) {
    	EntityLivingBase minion = new EntityVespid(this.worldObj);
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof EntityCreatureBase) {
    		((EntityCreatureBase)minion).setSubspecies(this.getSubspeciesIndex(), true);
    	}
    	this.worldObj.spawnEntityInWorld(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
        return minion;
    }

	// ========== Hive ==========
	public boolean hiveFoundationsSet() { return this.hiveFoundationsSet(false); }
	public boolean hiveFoundationsSet(boolean clearCache) {
		if(clearCache || this.hiveCheckCacheTime <= 0) {
			this.hiveCheckCacheTime = 100;
			if(!this.doesHiveHaveXPositive()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveXNegative()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveYPositive()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveYNegative()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveZPositive()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveZNegative()) {
				this.inHiveCache = false;
				return false;
			}
			
			this.inHiveCache = true;
			return true;
		}
		else {
			return this.inHiveCache;
		}
	}
	
	public boolean doesHiveHaveXPositive() {
		ChunkCoordinates hivePos = this.getHivePosition();
		for(int x = hivePos.posX; x <= hivePos.posX + 28; x++) {
			Block searchBlock = this.worldObj.getBlock(x, hivePos.posY, hivePos.posZ);
			if(searchBlock == ObjectManager.getBlock("veswax")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doesHiveHaveXNegative() {
		ChunkCoordinates hivePos = this.getHivePosition();
		for(int x = hivePos.posX; x >= hivePos.posX - 28; x--) {
			Block searchBlock = this.worldObj.getBlock(x, hivePos.posY, hivePos.posZ);
			if(searchBlock == ObjectManager.getBlock("veswax")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doesHiveHaveYPositive() {
		ChunkCoordinates hivePos = this.getHivePosition();
		for(int y = hivePos.posY; y <= hivePos.posY + 28; y++) {
			Block searchBlock = this.worldObj.getBlock(hivePos.posX, y, hivePos.posZ);
			if(searchBlock == ObjectManager.getBlock("propolis")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doesHiveHaveYNegative() {
		ChunkCoordinates hivePos = this.getHivePosition();
		for(int y = hivePos.posY; y >= hivePos.posY - 28; y--) {
			Block searchBlock = this.worldObj.getBlock(hivePos.posX, y, hivePos.posZ);
			if(searchBlock == ObjectManager.getBlock("propolis")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doesHiveHaveZPositive() {
		ChunkCoordinates hivePos = this.getHivePosition();
		for(int z = hivePos.posZ; z <= hivePos.posZ + 28; z++) {
			Block searchBlock = this.worldObj.getBlock(hivePos.posX, hivePos.posY, z);
			if(searchBlock == ObjectManager.getBlock("veswax")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doesHiveHaveZNegative() {
		ChunkCoordinates hivePos = this.getHivePosition();
		for(int z = hivePos.posZ; z >= hivePos.posZ - 28; z--) {
			Block searchBlock = this.worldObj.getBlock(hivePos.posX, hivePos.posY, z);
			if(searchBlock == ObjectManager.getBlock("veswax")) {
				return true;
			}
		}
		return false;
	}
	
	public ChunkCoordinates getHivePosition() {
		if(this.hasHome())
			return this.getHomePosition();
		
		ChunkCoordinates hivePos = new ChunkCoordinates();
		hivePos.posX = (int)this.posX;
		hivePos.posY = (int)this.posY;
		hivePos.posZ = (int)this.posZ;
		
		return hivePos;
	}
	
	public List<HiveExposedCoordinates> getHiveExposureBlocks() {
		if(this.hiveExposedBlockCacheTime <= 0) {
			this.hiveExposedBlockCacheTime = 200;
			this.hiveExposedBlocks = new ArrayList<HiveExposedCoordinates>();
			ChunkCoordinates hivePosition = this.getHivePosition();
			int hiveMax = 28;
			
			for(int x = hivePosition.posX - hiveMax; x <= hivePosition.posX + hiveMax; x++) {
				for(int y = hivePosition.posY - hiveMax; y <= hivePosition.posY + hiveMax; y++) {
					for(int z = hivePosition.posZ - hiveMax; z <= hivePosition.posZ + hiveMax; z++) {
						if(this.isHiveBlock(x, y, z)) {
							Block block = this.worldObj.getBlock(x, y, z);
							int oriMeta = this.worldObj.getBlockMetadata(x, y, z);
							EnumFacing facing = EnumFacing.getFront(oriMeta);
							if(facing.getFrontOffsetX() == 0) {
								if(!this.isHiveBlock(x - 1, y, z) && this.canPlaceBlockAt(x - 1, y, z))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, new ChunkCoordinates(x - 1, y, z), oriMeta));
								if(!this.isHiveBlock(x + 1, y, z))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, new ChunkCoordinates(x + 1, y, z), oriMeta));
							}
							if(facing.getFrontOffsetY() == 0) {
								if(!this.isHiveBlock(x, y - 1, z))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, new ChunkCoordinates(x, y - 1, z), oriMeta));
								if(!this.isHiveBlock(x, y + 1, z))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, new ChunkCoordinates(x, y + 1, z), oriMeta));
							}
							if(facing.getFrontOffsetZ() == 0) {
								if(!this.isHiveBlock(x, y, z - 1))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, new ChunkCoordinates(x, y, z - 1), oriMeta));
								if(!this.isHiveBlock(x, y, z + 1))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, new ChunkCoordinates(x, y, z + 1), oriMeta));
							}
						}
					}
				}
			}
		}
		return this.hiveExposedBlocks;
	}
	
	public boolean isHiveBlock(int x, int y, int z) {
		Block possibleHiveBlock = this.worldObj.getBlock(x, y, z);
		return possibleHiveBlock != null && (possibleHiveBlock == ObjectManager.getBlock("veswax") || possibleHiveBlock == ObjectManager.getBlock("propolis"));
	}
	
	public boolean canPlaceBlockAt(int x, int y, int z) {
		Block targetBlock = this.worldObj.getBlock(x, y, z);
		if(targetBlock == null)
			return false;
		if(targetBlock == Blocks.air)
			return true;
		if(targetBlock.getMaterial() == Material.water || targetBlock.getMaterial() == Material.lava)
			return true;
		return false;
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(target instanceof EntityConba) {
    		((EntityConba)target).vespidInfection = true;
    		return true;
    	}
    	
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Effect:
        if(target instanceof EntityLivingBase) {
            byte effectSeconds = 16;
            if(target instanceof EntityPlayer)
            	effectSeconds /= 2;
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.poison.id, this.getEffectDuration(effectSeconds), 0));
        }
        
        return true;
    }
    
    // ========== Can Attack Entity ==========
    @Override
    public boolean canAttackEntity(EntityLivingBase targetEntity) {
    	if(targetEntity instanceof EntityConba)
        	if(((EntityConba)targetEntity).vespidInfection)
        		return false;
    	if(targetEntity instanceof EntityVespid) {
    		if(!((EntityVespid)targetEntity).hasMaster() || ((EntityVespid)targetEntity).getMasterTarget() == this)
    			return false;
    	}
    	return super.canAttackEntity(targetEntity);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canFly() { return true; }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 4.0F;
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.moveSlowdown.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public boolean isDamageTypeApplicable(String type) {
    	if(type.equals("inWall")) return false;
    	return super.isDamageTypeApplicable(type);
    }
}
