package lycanite.lycanitesmobs.plainsmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityMaka extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityMaka(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 1;
        this.experience = 5;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.1D;
        
        this.setWidth = 2.9F;
        this.setHeight = 3.2F;
        this.attackTime = 10;
        this.fleeHealthPercent = 1.0F;
        this.isHostileByDefault = false;
        this.setupMob();
        
        // AI Tasks:
        ((PathNavigateGround)this.getNavigator()).setCanSwim(false);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.tasks.addTask(3, new EntityAIMate(this).setMateDistance(5.0D));
        this.tasks.addTask(4, new EntityAITempt(this).setItemList("vegetables"));
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
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 20D);
		baseAttributes.put("attackDamage", 2D);
        baseAttributes.put("attackSpeed", 8D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("MakaMeatRaw")), 1).setBurningDrop(new ItemStack(ObjectManager.getItem("MakaMeatCooked"))).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.leather), 0.5F).setMinAmount(1).setMaxAmount(3));
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        IBlockState blockState = this.worldObj.getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
		if(block != Blocks.air) {
			if(blockState.getMaterial() == Material.grass)
				return 10F;
			if(blockState.getMaterial() == Material.ground)
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
        if(potionEffect.getPotion() == Potion.getPotionFromResourceLocation("weakness")) return false;
        if(potionEffect.getPotion() == Potion.getPotionFromResourceLocation("mining_fatigue")) return false;
        return super.isPotionApplicable(potionEffect);
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
		return ObjectLists.inItemList("vegetables", testStack);
    }
    
    
    // ==================================================
    //                     Growing
    // ==================================================
	@Override
	public void setGrowingAge(int age) {
		if(age == 0 && this.getAge() < 0)
			if(this.getRNG().nextFloat() >= 0.9F) {
				EntityMakaAlpha alpha = new EntityMakaAlpha(this.worldObj);
				alpha.copyLocationAndAnglesFrom(this);
				this.worldObj.spawnEntityInWorld(alpha);
				this.worldObj.removeEntity(this);
			}
        super.setGrowingAge(age);
    }
}
