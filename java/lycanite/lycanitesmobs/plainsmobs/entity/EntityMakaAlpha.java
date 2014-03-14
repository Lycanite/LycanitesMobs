package lycanite.lycanitesmobs.plainsmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.plainsmobs.PlainsMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityMakaAlpha extends EntityCreatureAgeable implements IAnimals, IGroupAlpha {
	
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
        this.spawnsOnlyInLight = true;
        this.hasAttackSound = true;
        
        this.despawnOnPeaceful = DesertMobs.config.getFeatureBool("DespawnMakasOnPeaceful");
        this.despawnNaturally = DesertMobs.config.getFeatureBool("DespawnMakasNaturally");
        this.eggName = "PlainsEgg";
        
        this.setWidth = 2.9F;
        this.setHeight = 3.5F;
        this.attackTime = 10;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
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
		baseAttributes.put("maxHealth", 100D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 3D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(ObjectManager.getItem("MakaMeatRaw").itemID, 1).setBurningItem(ObjectManager.getItem("MakaMeatCooked").itemID, -1).setMinAmount(3).setMaxAmount(7));
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
    		this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 20 * 20, 2, true));
    	}
    	super.setAttackTarget(entity);
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
		return false;
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    public String getTextureName() {
    	return this.entityName + "Alpha";
    }
}
