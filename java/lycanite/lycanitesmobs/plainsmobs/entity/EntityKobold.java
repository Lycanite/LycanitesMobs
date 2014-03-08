package lycanite.lycanitesmobs.plainsmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupHunter;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIGetItem;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.plainsmobs.PlainsMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityKobold extends EntityCreatureAgeable implements IMob, IGroupPrey {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityKobold(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Kobold";
        this.mod = PlainsMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.hasAttackSound = true;
        this.spreadFire = false;
        
        this.eggName = "PlainsEgg";
        this.canGrow = false;
        this.babySpawnChance = 0.1D;
        
        this.setWidth = 0.5F;
        this.setHeight = 0.9F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(2, new EntityAIAttackMelee(this));
        this.tasks.addTask(3, new EntityAIGetItem(this).setDistanceMax(32).setSpeed(1.2D));
        this.tasks.addTask(4, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(16.0D));
        this.tasks.addTask(7, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setSightCheck(false));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupHunter.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupAlpha.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(EntityVillager.class));
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
        this.drops.add(new DropRate(Item.coal.itemID, 0.25F).setMaxAmount(2));
        this.drops.add(new DropRate(Item.ingotIron.itemID, 0.05F).setMaxAmount(1));
        this.drops.add(new DropRate(Item.goldNugget.itemID, 0.025F).setMaxAmount(1));
        this.drops.add(new DropRate(Item.emerald.itemID, 0.01F).setMaxAmount(1));
	}
    
	
    // ==================================================
    //                     Attacks
    // ==================================================
	public boolean canAttackEntity(EntityLivingBase targetEntity) {
		return (targetEntity.getHealth() / targetEntity.getMaxHealth()) <= 0.5F;
	}
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 10; }
    public int getBagSize() { return 10; }
    
    @Override
    public boolean canPickupItems() {
    	return this.mod.getConfig().getFeatureBool("KoboldThievery");
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.weakness.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.digSlowdown.id) return false;
        return super.isPotionApplicable(par1PotionEffect);
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityKobold(this.worldObj);
	}
}
