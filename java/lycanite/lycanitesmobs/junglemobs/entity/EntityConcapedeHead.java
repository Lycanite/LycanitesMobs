package lycanite.lycanitesmobs.junglemobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityConcapedeHead extends EntityCreatureAgeable implements IAnimals, IGroupAnimal, IGroupAlpha {
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConcapedeHead(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Concapede";
        this.mod = JungleMobs.instance;
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.experience = 5;
        this.spawnsInDarkness = false;
        this.hasAttackSound = true;
        
        this.despawnOnPeaceful = this.mod.getConfig().getFeatureBool("DespawnConcapedesOnPeaceful");
        this.despawnNaturally = this.mod.getConfig().getFeatureBool("DespawnConcapedesNaturally");
        this.eggName = "JungleEgg";
        this.canGrow = true;
        this.babySpawnChance = 0D;
        
        this.setWidth = 0.5F;
        this.setHeight = 0.9F;
        this.setupMob();
    	
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(5, new EntityAITempt(this).setItemList("Vegetables"));
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
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(ObjectManager.getItem("ConcapedeMeatRaw").itemID, 1).setMinAmount(3).setMaxAmount(6).setBurningItem(ObjectManager.getItem("ConcapedeMeatCooked").itemID, 0));
        this.drops.add(new DropRate(Item.silk.itemID, 0.5F).setMinAmount(1).setMaxAmount(2));
	}
	
	
    // ==================================================
    //                      Unique
    // ==================================================
	public int getConcapedeSize() {
		return 0;
	}
	
	
    // ==================================================
    //                      Spawn
    // ==================================================
	// ========== On Spawn ==========
	@Override
	public void onSpawn() {
		// Create Starting Segments:
        if(!this.worldObj.isRemote) {
        	this.setGrowingAge(-this.growthTime / 4);
        	int segmentCount = this.getRNG().nextInt(this.mod.getConfig().getFeatureInt("ConcapedeSizeLimit"));
    		EntityCreatureAgeable parentSegment = this;
        	for(int segment = 0; segment < segmentCount; segment++) {
        		EntityConcapedeSegment segmentEntity = new EntityConcapedeSegment(parentSegment.worldObj);
        		segmentEntity.setLocationAndAngles(parentSegment.posX, parentSegment.posY, parentSegment.posZ, 0.0F, 0.0F);
        		parentSegment.worldObj.spawnEntityInWorld(segmentEntity);
				segmentEntity.setParentTarget(parentSegment);
				parentSegment = segmentEntity;
        	}
        }
        super.onSpawn();
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
		if(age == 0) {
			age = -this.growthTime / 4;
			EntityCreatureBase parentSegment = this;
			boolean lastSegment = false;
			while(!lastSegment) {
				if(parentSegment.hasMaster() && parentSegment.getMasterTarget() instanceof EntityCreatureBase)
					parentSegment = (EntityCreatureBase)(parentSegment.getMasterTarget());
				else
					lastSegment = true;
			}
			EntityConcapedeSegment segmentEntity = new EntityConcapedeSegment(this.worldObj);
    		segmentEntity.setLocationAndAngles(parentSegment.posX, parentSegment.posY, parentSegment.posZ, 0.0F, 0.0F);
    		parentSegment.worldObj.spawnEntityInWorld(segmentEntity);
			segmentEntity.setParentTarget(parentSegment);
		}
        super.setGrowingAge(age);
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		if(this.worldObj.getBlockId(par1, par2 - 1, par3) != 0) {
			Block block = Block.blocksList[this.worldObj.getBlockId(par1, par2 - 1, par3)];
			if(block.blockMaterial == Material.grass)
				return 10F;
			if(block.blockMaterial == Material.ground)
				return 7F;
		}
        return super.getBlockPathWeight(par1, par2, par3);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canLeash(EntityPlayer player) {
	    return !this.hasAttackTarget();
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
            byte effectSeconds = 8;
            if(this.worldObj.difficultySetting > 1)
                if (this.worldObj.difficultySetting == 2)
                	effectSeconds = 12;
                else if (this.worldObj.difficultySetting == 3)
                	effectSeconds = 16;
            if(target instanceof EntityPlayer)
            	effectSeconds /= 2;
            if(effectSeconds > 0) {
                ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.poison.id, (int)(effectSeconds * 20), 0));
            }
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
    		return true;
    }
    
    @Override
    public boolean isProtective(Entity entity) {
    	if(this.isInLove())
    		return false;
    	if(entity instanceof EntityConcapedeSegment) {
    		EntityCreatureBase checkSegment = this;
    		boolean finished = false;
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
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.moveSlowdown.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public float getFallResistance() {
    	return 100;
    }
}
