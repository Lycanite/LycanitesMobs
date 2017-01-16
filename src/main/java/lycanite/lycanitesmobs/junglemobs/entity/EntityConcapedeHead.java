package lycanite.lycanitesmobs.junglemobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.core.config.ConfigBase;
import lycanite.lycanitesmobs.core.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.ai.*;
import lycanite.lycanitesmobs.core.info.DropRate;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityConcapedeHead extends EntityCreatureAgeable implements IAnimals, IGroupAnimal, IGroupAlpha {
	
	public static int CONCAPEDE_SIZE_MAX = 10;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConcapedeHead(World world) {
        super(world);
        
        CONCAPEDE_SIZE_MAX = Math.max(1, ConfigBase.getConfig(group, "general").getInt("Features", "Concapede Size Limit", 10, "The maximum amount of segments long a Concapede can be, including the head."));
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0D;
        
        this.setWidth = 0.5F;
        this.setHeight = 0.9F;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(5, new EntityAITempt(this).setItemList("vegetables"));
        this.tasks.addTask(6, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("concapedemeatraw")), 1).setMinAmount(1).setMaxAmount(1).setBurningDrop(new ItemStack(ObjectManager.getItem("concapedemeatcooked"))));
        this.drops.add(new DropRate(new ItemStack(Items.STRING), 0.5F).setMinAmount(1).setMaxAmount(2));
	}
	
	
    // ==================================================
    //                      Spawn
    // ==================================================
	// ========== On Spawn ==========
	@Override
	public void onFirstSpawn() {
		// Create Starting Segments:
        if(!this.worldObj.isRemote && !this.hasMaster()) {
        	this.setGrowingAge(-this.growthTime / 4);
        	int segmentCount = this.getRNG().nextInt(CONCAPEDE_SIZE_MAX);
    		EntityCreatureAgeable parentSegment = this;
        	for(int segment = 0; segment < segmentCount; segment++) {
        		EntityConcapedeSegment segmentEntity = new EntityConcapedeSegment(parentSegment.worldObj);
        		segmentEntity.setLocationAndAngles(parentSegment.posX, parentSegment.posY, parentSegment.posZ, 0.0F, 0.0F);
				segmentEntity.setParentTarget(parentSegment);
        		parentSegment.worldObj.spawnEntityInWorld(segmentEntity);
				parentSegment = segmentEntity;
        	}
        }
        super.onFirstSpawn();
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }
	
	
	// ==================================================
  	//                        Age
  	// ==================================================
	@Override
	public void setGrowingAge(int age) {
		// Spawn Additional Segments:
		if(age == 0 && ObjectManager.getMob("ConcapedeSegment") != null && !this.worldObj.isRemote) {
			age = -(this.growthTime / 4);
			EntityCreatureBase parentSegment = this;
			boolean lastSegment = false;
			int size = 0;
			while(!lastSegment) {
				size++;
				if(parentSegment.hasMaster() && parentSegment.getMasterTarget() instanceof EntityCreatureBase)
					parentSegment = (EntityCreatureBase)(parentSegment.getMasterTarget());
				else
					lastSegment = true;
			}
			if(size < CONCAPEDE_SIZE_MAX) {
				EntityConcapedeSegment segmentEntity = new EntityConcapedeSegment(this.worldObj);
	    		segmentEntity.setLocationAndAngles(parentSegment.posX, parentSegment.posY, parentSegment.posZ, 0.0F, 0.0F);
	    		parentSegment.worldObj.spawnEntityInWorld(segmentEntity);
				segmentEntity.setParentTarget(parentSegment);
			}
		}
        super.setGrowingAge(age);
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        IBlockState blockState = this.worldObj.getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
            if(blockState.getMaterial() == Material.GRASS)
                return 10F;
            if(blockState.getMaterial() == Material.GROUND)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }

    // ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return true;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Effect:
        if(target instanceof EntityLivingBase) {
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.POISON, this.getEffectDuration(8), 0));
        }
        
        return true;
    }
    
    // ========== Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityConcapedeSegment.class))
        	return false;
    	return super.canAttackClass(targetClass);
    }
    
    
    // ==================================================
  	//                      Targets
  	// ==================================================
    @Override
    public boolean isAggressive() {
    	if(this.isInLove())
    		return false;
    	if(this.worldObj.isDaytime())
    		return this.testLightLevel() < 2;
    	else
    		return super.isAggressive();
    }
    
    @Override
    public boolean isProtective(Entity entity) {
    	if(this.isInLove())
    		return false;
    	if(entity instanceof EntityConcapedeSegment) {
    		EntityCreatureBase checkSegment = this;
    		while(checkSegment != null) {
    			if(checkSegment == entity)
    				return true;
    			if(!checkSegment.hasMaster())
    				break;
    			checkSegment = (EntityCreatureBase)checkSegment.getMasterTarget();
    		}
    	}
    	return false;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partener) {
		return null;
	}
    
    // ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("Vegetables", testStack);
    }
	
	@Override
	public boolean canBreed() {
        return this.getGrowingAge() >= 0;
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
    public float getFallResistance() {
    	return 100;
    }
}
