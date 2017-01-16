package lycanite.lycanitesmobs.freshwatermobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.core.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.core.entity.ai.*;
import lycanite.lycanitesmobs.core.info.DropRate;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntitySilex extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySilex(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 1;
        this.experience = 4;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = false;

        this.babySpawnChance = 0.01D;
        this.canGrow = true;
        
        this.setWidth = 0.8F;
        this.setHeight = 0.8F;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIStayByWater(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(3, new EntityAITempt(this).setItem(new ItemStack(Items.DYE, 1, 4)));
        this.tasks.addTask(4, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.tasks.addTask(5, new EntityAIMate(this));
        this.tasks.addTask(6, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        //this.targetTasks.addTask(1, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("silexmeatraw")), 1).setBurningDrop(new ItemStack(ObjectManager.getItem("silexmeatcooked"))).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.DYE, 1, 4), 1).setMinAmount(2).setMaxAmount(4));
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		int waterWeight = 10;

        Block block = this.worldObj.getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
        	return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
		if(block == Blocks.FLOWING_WATER)
			return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
        if(this.worldObj.isRaining() && this.worldObj.canBlockSeeSky(new BlockPos(x, y, z)))
        	return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        
        if(this.getAttackTarget() != null)
        	return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
			return -999999.0F;
		
		return super.getBlockPathWeight(x, y, z);
    }
	
	// Swimming:
	@Override
	public boolean canSwim() {
		return true;
	}
	
	// Walking:
	@Override
	public boolean canWalk() {
		return false;
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(ObjectManager.getPotionEffect("Penetration") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Penetration")) return false;
        if(ObjectManager.getPotionEffect("Paralysis") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Paralysis")) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }

    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntitySilex(this.worldObj);
    }

    // ========== Breeding Item ==========
    @Override
    public boolean isBreedingItem(ItemStack testStack) {
        if(testStack == null || !this.isInWater())
            return false;
        return testStack.getItem() == Items.DYE && testStack.getItemDamage() == 4;
    }
}
