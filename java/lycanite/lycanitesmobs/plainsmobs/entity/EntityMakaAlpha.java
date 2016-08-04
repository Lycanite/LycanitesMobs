package lycanite.lycanitesmobs.plainsmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.core.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.core.entity.ai.*;
import lycanite.lycanitesmobs.core.info.DropRate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityMakaAlpha extends EntityCreatureAgeable implements IAnimals, IGroupAlpha {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityMakaAlpha(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 3;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.setWidth = 3.5F;
        this.setHeight = 3.5F;
        this.attackTime = 10;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        ((PathNavigateGround)this.getNavigator()).setCanSwim(false);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(5, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(6, new EntityAIAttackMelee(this));
        this.tasks.addTask(9, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpClasses(EntityMaka.class));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(IGroupPredator.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityMakaAlpha.class).setChance(100));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class).setOnlyNearby(true).setChance(100));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setOnlyNearby(true).setChance(100));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
	    this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("MakaMeatRaw")), 1).setBurningDrop(new ItemStack(ObjectManager.getItem("MakaMeatCooked"))).setMinAmount(3).setMaxAmount(7));
	    this.drops.add(new DropRate(new ItemStack(Items.LEATHER), 0.75F).setMinAmount(2).setMaxAmount(4));
	}
	
	
	// ==================================================
  	//                      Update
  	// ==================================================
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		// Alpha Sparring Cooldown:
		if(this.hasAttackTarget() && this.getAttackTarget() instanceof EntityMakaAlpha) {
			if(this.getHealth() / this.getMaxHealth() <= 0.25F || this.getAttackTarget().getHealth() / this.getAttackTarget().getMaxHealth() <= 0.25F) {
				this.setAttackTarget(null);
			}
		}
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
    // ========== Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass == EntityMaka.class)
    		return false;
    	else return super.canAttackClass(targetClass);
    }
    
    // ========== Attack Entity ==========
    @Override
    public boolean canAttackEntity(EntityLivingBase entity) {
    	if(entity instanceof EntityMakaAlpha && (this.getHealth() / this.getMaxHealth() <= 0.25F || entity.getHealth() / entity.getMaxHealth() <= 0.25F))
    		return false;
    	else return super.canAttackEntity(entity);
    }
    
    // ========== Set Attack Target ==========
    @Override
    public void setAttackTarget(EntityLivingBase entity) {
    	if(entity == null && this.getAttackTarget() instanceof EntityMakaAlpha && this.getHealth() < this.getMaxHealth()) {
    		this.heal((this.getMaxHealth() - this.getHealth()) / 2);
    		this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 20, 2, false, false));
    	}
    	super.setAttackTarget(entity);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.WEAKNESS) return false;
        if(potionEffect.getPotion() == MobEffects.MINING_FATIGUE) return false;
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
		return false;
    }
}
