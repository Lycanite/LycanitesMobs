package com.lycanitesmobs.junglemobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.MobDrop;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityVespidQueen extends EntityCreatureAgeable implements IMob, IGroupPredator {
	public boolean inHiveCache = false;
	private int hiveCheckCacheTime = 0;
	public class HiveExposedCoordinates {
		public Block block;
		public BlockPos pos;
		public int orientationMeta;
		
		public HiveExposedCoordinates(Block block, BlockPos pos, int orientationMeta) {
			this.block = block;
			this.pos = pos;
			this.orientationMeta = orientationMeta;
		}
	}
	public List<HiveExposedCoordinates> hiveExposedBlocks = new ArrayList<HiveExposedCoordinates>();
	private int hiveExposedBlockCacheTime = 0;
	
	private int vespidQueenSwarmLimit = 10;
	private boolean vespidHiveBuilding = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVespidQueen(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 1;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.setWidth = 1.6F;
        this.setHeight = 1.9F;
        this.solidCollision = true;
        this.setupMob();
        
        this.canGrow = true;
        this.babySpawnChance = 0D;

        this.stepHeight = 1.0F;
        this.justAttackedTime = (short)(10);
        
        this.vespidQueenSwarmLimit = ConfigBase.getConfig(this.group, "general").getInt("Features", "Vespid Queen Swarm Limit", this.vespidQueenSwarmLimit, "Limits how many Vespid drones a Queen can have before she will no longer spawn babies in hives.");
        this.vespidHiveBuilding = ConfigBase.getConfig(this.group, "general").getBool("Features", "Vespid Hive Building", this.vespidHiveBuilding, "Set to false to stop Vespids from building hives all together.");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setRate(10).setLongMemory(true));
        this.tasks.addTask(7, new EntityAIStayByHome(this));
        this.tasks.addTask(8, new EntityAIWander(this).setPauseRate(1200));
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
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.25D);
		baseAttributes.put("followRange", 24D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new MobDrop(new ItemStack(Items.CLAY_BALL), 0.5F).setMaxAmount(16));
        this.drops.add(new MobDrop(new ItemStack(ObjectManager.getBlock("propolis")), 0.5F).setMaxAmount(8));
        this.drops.add(new MobDrop(new ItemStack(ObjectManager.getBlock("veswax")), 0.5F).setMaxAmount(8));
	}
	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	if(this.hasHome() && this.getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL)
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
	        if(!this.getEntityWorld().isRemote && !this.hasHome()) {
	        	if(this.hiveFoundationsSet()) {
	        		this.setHome((int)this.posX, (int)this.posY, (int)this.posZ, 16F);
	        	}
	        }
	        
	        // Spawn Babies:
	        if(!this.getEntityWorld().isRemote && this.hiveFoundationsSet() && this.ticksExisted % 60 == 0) {
				this.allyUpdate();
	        }
        }
        
        // Don't Keep Infected Conbas Targeted:
        if(!this.getEntityWorld().isRemote && this.getAttackTarget() instanceof EntityConba) {
        	if(((EntityConba)this.getAttackTarget()).vespidInfection) {
        		this.setAttackTarget(null);
        	}
        }
    }
    
    // ========== Spawn Babies ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Babies:
		if(this.vespidQueenSwarmLimit > 0 && this.nearbyCreatureCount(EntityVespid.class, 32D) < this.vespidQueenSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.05F) {
				EntityLivingBase minion = this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
				if(minion instanceof EntityCreatureAgeable) {
		    		((EntityCreatureAgeable)minion).setGrowingAge(((EntityCreatureAgeable) minion).growthTime);
		    	}
			}
		}
	}
	
    public EntityLivingBase spawnAlly(double x, double y, double z) {
    	EntityLivingBase minion = new EntityVespid(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof EntityCreatureBase) {
    		((EntityCreatureBase)minion).setSubspecies(this.getSubspeciesIndex(), true);
    	}
    	this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
        return minion;
    }

	// ========== Hive ==========
    public BlockPos getHivePosition() {
        if(this.hasHome())
            return this.getHomePosition();
        return this.getPosition();
    }

	public boolean hiveFoundationsSet() {
        return this.hiveFoundationsSet(false);
    }
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

    public boolean isHiveBlock(BlockPos searchPos) {
        if(this.isHiveWall(searchPos) || this.isHiveFloor(searchPos))
            return true;
        return false;
    }

    public boolean isHiveWall(BlockPos searchPos) {
        IBlockState searchState = this.getEntityWorld().getBlockState(searchPos);
        Block searchBlock = searchState.getBlock();
        if(searchBlock != null)
            if(searchBlock == ObjectManager.getBlock("veswax") && searchBlock.getMetaFromState(searchState) < 8)
                return true;
        return false;
    }

    public boolean isHiveFloor(BlockPos searchPos) {
        IBlockState searchState = this.getEntityWorld().getBlockState(searchPos);
        Block searchBlock = searchState.getBlock();
        if(searchBlock != null)
            if(searchBlock == ObjectManager.getBlock("propolis") && searchBlock.getMetaFromState(searchState) < 8)
                return true;
        return false;
    }
	
	public boolean doesHiveHaveXPositive() {
		BlockPos hivePos = this.getHivePosition();
		for(int x = hivePos.getX(); x <= hivePos.getX() + 28; x++) {
            if(this.isHiveWall(new BlockPos(x, hivePos.getY(), hivePos.getZ())))
                return true;
        }
        return false;
    }

    public boolean doesHiveHaveXNegative() {
        BlockPos hivePos = this.getHivePosition();
        for(int x = hivePos.getX(); x >= hivePos.getX() - 28; x--) {
            if(this.isHiveWall(new BlockPos(x, hivePos.getY(), hivePos.getZ())))
                return true;
        }
        return false;
	}
	
	public boolean doesHiveHaveYPositive() {
		BlockPos hivePos = this.getHivePosition();
		for(int y = hivePos.getY(); y <= hivePos.getY() + 28; y++) {
            if(this.isHiveFloor(new BlockPos(hivePos.getX(), y, hivePos.getZ())))
                return true;
		}
		return false;
	}
	
	public boolean doesHiveHaveYNegative() {
		BlockPos hivePos = this.getHivePosition();
		for(int y = hivePos.getY(); y >= hivePos.getY() - 28; y--) {
            if(this.isHiveFloor(new BlockPos(hivePos.getX(), y, hivePos.getZ())))
                return true;
		}
		return false;
	}
	
	public boolean doesHiveHaveZPositive() {
		BlockPos hivePos = this.getHivePosition();
		for(int z = hivePos.getZ(); z <= hivePos.getZ() + 28; z++) {
            if(this.isHiveWall(new BlockPos(hivePos.getX(), hivePos.getY(), z)))
                return true;
		}
		return false;
	}
	
	public boolean doesHiveHaveZNegative() {
		BlockPos hivePos = this.getHivePosition();
		for(int z = hivePos.getZ(); z >= hivePos.getZ() - 28; z--) {
            if(this.isHiveWall(new BlockPos(hivePos.getX(), hivePos.getY(), z)))
                return true;
		}
		return false;
	}
	
	public List<HiveExposedCoordinates> getHiveExposureBlocks() {
		if(this.hiveExposedBlockCacheTime <= 0) {
			this.hiveExposedBlockCacheTime = 200;
			this.hiveExposedBlocks = new ArrayList<HiveExposedCoordinates>();
			BlockPos hivePos = this.getHivePosition();
			int hiveMax = 28;
			
			for(int x = hivePos.getX() - hiveMax; x <= hivePos.getX() + hiveMax; x++) {
				for(int y = hivePos.getY() - hiveMax; y <= hivePos.getY() + hiveMax; y++) {
					for(int z = hivePos.getZ() - hiveMax; z <= hivePos.getZ() + hiveMax; z++) {
                        BlockPos checkPos = new BlockPos(x, y, z);
						if(this.isHiveBlock(checkPos)) {
                            IBlockState state = this.getEntityWorld().getBlockState(checkPos);
							Block block = state.getBlock();
							int orientationMeta = block.getMetaFromState(state);
							EnumFacing facing = EnumFacing.getFront(orientationMeta);

							if(facing.getFrontOffsetX() == 0) {
								if(!this.isHiveBlock(checkPos.add(-1, 0, 0)) && this.canPlaceBlockAt(checkPos.add(-1, 0, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(-1, 0, 0), orientationMeta));
								if(!this.isHiveBlock(checkPos.add(1, 0, 0)) && this.canPlaceBlockAt(checkPos.add(1, 0, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(1, 0, 0), orientationMeta));
							}

							if(facing.getFrontOffsetY() == 0) {
                                if(!this.isHiveBlock(checkPos.add(0, -1, 0)) && this.canPlaceBlockAt(checkPos.add(0, -1, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, -1, 0), orientationMeta));
                                if(!this.isHiveBlock(checkPos.add(0, 1, 0)) && this.canPlaceBlockAt(checkPos.add(0, 1, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, 1, 0), orientationMeta));
							}

							if(facing.getFrontOffsetZ() == 0) {
                                if(!this.isHiveBlock(checkPos.add(0, 0, -1)) && this.canPlaceBlockAt(checkPos.add(0, 0, -1)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, 0, -1), orientationMeta));
                                if(!this.isHiveBlock(checkPos.add(0, 0, 1)) && this.canPlaceBlockAt(checkPos.add(0, 0, 1)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, 0, 1), orientationMeta));
							}
						}
					}
				}
			}
		}
		return this.hiveExposedBlocks;
	}
	
	public boolean canPlaceBlockAt(BlockPos pos) {
        IBlockState targetState = this.getEntityWorld().getBlockState(pos);
		Block targetBlock = targetState.getBlock();
        if(targetBlock == null)
			return false;
		if(targetBlock == Blocks.AIR)
			return true;
		if(targetState.getMaterial() == Material.WATER || targetState.getMaterial() == Material.LAVA)
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
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.POISON, this.getEffectDuration(effectSeconds), 0));
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
    public boolean isFlying() { return true; }
    
    
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
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.POISON) return false;
        if(potionEffect.getPotion() == MobEffects.SLOWNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }
}
