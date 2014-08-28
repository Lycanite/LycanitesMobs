package lycanite.lycanitesmobs.plainsmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIBeg;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityZoataur extends EntityCreatureTameable implements IGroupPredator, IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityZoataur(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 4;
        this.experience = 7;
        this.spawnsInDarkness = true;
        this.spawnsUnderground = true;
        this.hasAttackSound = true;
        this.spreadFire = true;
        
        this.eggName = "PlainsEgg";
        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.setWidth = 1.2F;
        this.setHeight = 2.5F;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(3, new EntityAIAttackMelee(this));
        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setSightCheck(false));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(1, 1));
        if(MobInfo.predatorsAttackAnimals) {
        	this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(1, 3));
        	this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class).setPackHuntingScale(1, 3));
        }
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.leather), 1F).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.bone), 0.5F).setMaxAmount(3));
	}
	
	
    // ==================================================
    //                   Taking Damage
    // ==================================================
    // ========== On Damage ==========
    /** Called when this mob has received damage. Here a random blocking chance is applied. **/
    public void onDamage() {
    	if(this.getRNG().nextDouble() > 0.6D)
    		this.setBlocking();
    }
    
    // ========== Blocking ==========
    public void setBlocking() {
    	this.currentBlockingTime = this.blockingTime + this.getRNG().nextInt(this.blockingTime / 2);
    }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 10; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotionID() == Potion.weakness.id) return false;
        if(potionEffect.getPotionID() == Potion.digSlowdown.id) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public float getFallResistance() {
    	return 100;
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
