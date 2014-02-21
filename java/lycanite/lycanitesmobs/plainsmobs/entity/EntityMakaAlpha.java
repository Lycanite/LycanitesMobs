package lycanite.lycanitesmobs.plainsmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.demonmobs.entity.EntityPinky;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.entity.ai.EntityAIFollowMaster;
import lycanite.lycanitesmobs.entity.ai.EntityAIFollowParent;
import lycanite.lycanitesmobs.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.entity.ai.EntityAIMate;
import lycanite.lycanitesmobs.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.junglemobs.entity.EntityUvaraptor;
import lycanite.lycanitesmobs.plainsmobs.PlainsMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMakaAlpha extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityMakaAlpha(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Maka";
        this.mod = PlainsMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.despawnOnPeaceful = DesertMobs.config.getFeatureBool("DespawnMakasOnPeaceful");
        this.despawnNaturally = DesertMobs.config.getFeatureBool("DespawnMakasNaturally");
        this.eggName = "PlainsEgg";
        
        this.setWidth = 0.9F;
        this.setHeight = 2.2F;
        this.attackTime = 10;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIMate(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setRate(10).setLongMemory(false));
        this.tasks.addTask(3, new EntityAITempt(this).setItemList("Vegetables"));
        this.tasks.addTask(4, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(5, new EntityAIFollowMaster(this).setSpeed(1.0D).setStrayDistance(8.0F));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(EntityMakaAlpha.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVentoraptor.class));
        if(ObjectManager.getMob("Uvaraptor") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityUvaraptor.class));
        if(ObjectManager.getMob("Pinky") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPinky.class));
        
        // Drops:
        //XXX this.drops.add(new DropRate(ObjectManager.getItem("MakaMeatRaw").itemID, 1).setBurningItem(ObjectManager.getItem("MakaMeatCooked").itemID, -1).setMinAmount(1).setMaxAmount(3));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 25D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	
	// ==================================================
  	//                      Spawning
  	// ==================================================
	// ========== Spawn Check ==========
	@Override
	public boolean getCanSpawnHere() {
		int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
		if(this.worldObj.getFullBlockLightValue(i, j, k) > 8)
			return super.getCanSpawnHere();
		return false;
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
        return this.worldObj.getLightBrightness(par1, par2, par3) - 0.5F;
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
    	return false;
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
		return new EntityMakaAlpha(this.worldObj);
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("Vegetables", testStack);
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    public String getTextureName() {
    	return this.entityName + "Alpha";
    }
}
