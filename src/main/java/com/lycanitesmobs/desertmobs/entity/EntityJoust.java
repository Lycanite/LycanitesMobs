package com.lycanitesmobs.desertmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityJoust extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityJoust(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.1D;
        this.canGrow = true;
        
        this.setWidth = 0.9F;
        this.setHeight = 2.2F;
        this.attackTime = 10;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
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
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("JoustMeatRaw")), 1).setBurningDrop(new ItemStack(ObjectManager.getItem("JoustMeatCooked"))).setMaxAmount(3));
	}
	
    
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
		if(blockState.getBlock() != Blocks.AIR) {
			if(blockState.getMaterial() == Material.SAND)
				return 10F;
			if(blockState.getMaterial() == Material.CLAY)
				return 7F;
			if(blockState.getMaterial() == Material.ROCK)
				return 5F;
		}
        return super.getBlockPathWeight(x, y, z);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
	    if(!this.hasAttackTarget() && !this.hasMaster())
	        return true;
	    return super.canBeLeashedTo(player);
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
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.HUNGER) return false;
        if(potionEffect.getPotion() == MobEffects.WEAKNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partener) {
		return new EntityJoust(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("cactusfood", testStack);
    }
    
    
    // ==================================================
    //                     Growing
    // ==================================================
	@Override
	public void setGrowingAge(int age) {
		if(age == 0 && this.getAge() < 0)
			if(this.getRNG().nextFloat() >= 0.9F) {
				EntityJoustAlpha alphaJoust = new EntityJoustAlpha(this.getEntityWorld());
				alphaJoust.copyLocationAndAnglesFrom(this);
				this.getEntityWorld().spawnEntity(alphaJoust);
				this.getEntityWorld().removeEntity(this);
			}
        super.setGrowingAge(age);
    }
}
