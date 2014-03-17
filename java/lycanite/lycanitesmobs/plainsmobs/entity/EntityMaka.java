package lycanite.lycanitesmobs.plainsmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowMaster;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIMate;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetMaster;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.plainsmobs.PlainsMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityMaka extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityMaka(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Maka";
        this.mod = PlainsMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.experience = 5;
        this.spawnsOnlyInLight = true;
        this.hasAttackSound = true;
        
        this.spawnsOnPeaceful = DesertMobs.config.getFeatureBool("MakasOnPeaceful");
        this.despawnNaturally = DesertMobs.config.getFeatureBool("DespawnMakasNaturally");
        this.eggName = "PlainsEgg";
        
        this.setWidth = 2.5F;
        this.setHeight = 3.2F;
        this.attackTime = 10;
        this.fleeHealthPercent = 0.9F;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.tasks.addTask(2, new EntityAIMate(this).setMateDistance(5.0D));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(4, new EntityAITempt(this).setItemList("Vegetables"));
        this.tasks.addTask(5, new EntityAIFollowParent(this).setSpeed(1.0D).setStrayDistance(3.0D));
        this.tasks.addTask(6, new EntityAIFollowMaster(this).setSpeed(1.0D).setStrayDistance(18.0F));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpClasses(EntityMakaAlpha.class));
        this.targetTasks.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(2, new EntityAITargetMaster(this).setTargetClass(EntityMakaAlpha.class).setSightCheck(false).setDistance(64.0D));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 25D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 20D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(ObjectManager.getItem("MakaMeatRaw").itemID, 1).setBurningItem(ObjectManager.getItem("MakaMeatCooked").itemID, -1).setMinAmount(1).setMaxAmount(3));
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
	    return true;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass == EntityMaka.class || targetClass == EntityMakaAlpha.class)
    		return false;
    	else return super.canAttackClass(targetClass);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotionID() == Potion.weakness.id) return false;
        if(potionEffect.getPotionID() == Potion.digSlowdown.id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityMaka(this.worldObj);
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("Vegetables", testStack);
    }
    
    
    // ==================================================
    //                     Growing
    // ==================================================
	@Override
	public void setGrowingAge(int age) {
        super.setGrowingAge(age);
		if(age == 0)
			if(this.getRNG().nextFloat() >= 0.9F) {
				EntityMakaAlpha alpha = new EntityMakaAlpha(this.worldObj);
				alpha.copyLocationAndAnglesFrom(this);
				this.worldObj.spawnEntityInWorld(alpha);
				this.worldObj.removeEntity(this);
			}
    }
}
