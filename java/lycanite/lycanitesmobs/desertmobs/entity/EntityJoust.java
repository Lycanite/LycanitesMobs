package lycanite.lycanitesmobs.desertmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowMaster;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIMate;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetMaster;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetMasterAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityJoust extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityJoust(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Joust";
        this.mod = DesertMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.experience = 5;
        this.spawnsOnlyInLight = true;
        this.hasAttackSound = true;
        
        this.spawnsOnPeaceful = DesertMobs.config.getFeatureBool("JoustsOnPeaceful");
        this.despawnNaturally = DesertMobs.config.getFeatureBool("DespawnJoustsNaturally");
        this.eggName = "DesertEgg";
        
        this.setWidth = 0.9F;
        this.setHeight = 2.2F;
        this.attackTime = 10;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIMate(this));
        this.tasks.addTask(2, new EntityAITempt(this).setItemList("CactusFood"));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setRate(10).setLongMemory(false));
        this.tasks.addTask(4, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(5, new EntityAIFollowMaster(this).setSpeed(1.0D).setStrayDistance(8.0F));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpClasses(EntityJoustAlpha.class));
        this.targetTasks.addTask(1, new EntityAITargetMasterAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(2, new EntityAITargetMaster(this).setTargetClass(EntityJoustAlpha.class).setSightCheck(false).setDistance(64.0D));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0.25D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(ObjectManager.getItem("JoustMeatRaw").itemID, 1).setBurningItem(ObjectManager.getItem("JoustMeatCooked").itemID, -1).setMaxAmount(3));
	}
	
    
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		if(this.worldObj.getBlockId(par1, par2 - 1, par3) != 0) {
			Block block = Block.blocksList[this.worldObj.getBlockId(par1, par2 - 1, par3)];
			if(block.blockMaterial == Material.sand)
				return 10F;
			if(block.blockMaterial == Material.clay)
				return 7F;
			if(block.blockMaterial == Material.rock)
				return 5F;
		}
        return super.getBlockPathWeight(par1, par2, par3);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canLeash(EntityPlayer player) {
	    if(!this.hasAttackTarget() && !this.hasMaster())
	        return true;
	    return super.canLeash(player);
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityJoustAlpha.class))
        	return false;
    	return super.canAttackClass(targetClass);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
    	if(type.equals("cactus")) return false;
    	return super.isDamageTypeApplicable(type);
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.hunger.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.weakness.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partener) {
		return new EntityJoust(this.worldObj);
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("CactusFood", testStack);
    }
    
    
    // ==================================================
    //                     Growing
    // ==================================================
	@Override
	public void setGrowingAge(int age) {
        super.setGrowingAge(age);
		if(age == 0)
			if(this.getRNG().nextFloat() >= 0.9F) {
				EntityJoustAlpha alphaJoust = new EntityJoustAlpha(this.worldObj);
				alphaJoust.copyLocationAndAnglesFrom(this);
				this.worldObj.spawnEntityInWorld(alphaJoust);
				this.worldObj.removeEntity(this);
			}
    }
}
