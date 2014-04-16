package lycanite.lycanitesmobs.swampmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityDweller extends EntityCreatureAgeable implements IMob {
	
	EntityAIWander wanderAI = new EntityAIWander(this);
	int attackTaskStartID = 2;
	boolean attacksActive = false;
	EntityAIBase[] attackTasks = new EntityAIBase[] {
			(EntityAIBase)(new EntityAIAttackMelee(this).setLongMemory(false))
	};
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityDweller(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Dweller";
        this.mod = SwampMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 7;
        this.spawnsInDarkness = true;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = true;
        
        this.eggName = "SwampEgg";
        this.babySpawnChance = 0.1D;
        this.canGrow = false;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.6F;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(0, new EntityAISwimming(this).setSink(true));
        this.tasks.addTask(6, wanderAI);
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.16D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(Item.fishRaw.itemID, 1).setBurningItem(Item.fishCooked.itemID, 0));
        this.drops.add(new DropRate(Item.fishRaw.itemID, 0.25F).setBurningItem(Item.fishCooked.itemID, 0).setMinAmount(2).setMaxAmount(4));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Don't Attack When Starting to Suffocate:
        if(!this.worldObj.isRemote) {
	        if(this.getAir() > -100)
	        	setAttackTasks(true);
	        else
	        	setAttackTasks(false);
        }
        
        // Wander Pause Rates:
		if(this.isInWater())
			this.wanderAI.setPauseRate(120);
		else
			this.wanderAI.setPauseRate(0);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    public float getSpeedMod() {
    	if(this.isInWater()) // Checks specifically just for water.
    		return 8.0F;
    	else if(this.waterContact()) // Checks for water, rain, etc.
    		return 1.5F;
    	return 1.0F;
    }
    
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		int waterWeight = 10;
		
        if(this.worldObj.getBlockId(par1, par2, par3) == Block.waterStill.blockID)
        	return super.getBlockPathWeight(par1, par2, par3) * (waterWeight + 1);
		if(this.worldObj.getBlockId(par1, par2, par3) == Block.waterMoving.blockID)
			return super.getBlockPathWeight(par1, par2, par3) * waterWeight;
        if(this.worldObj.isRaining() && this.worldObj.canBlockSeeTheSky(par1, par2, par3))
        	return super.getBlockPathWeight(par1, par2, par3) * (waterWeight + 1);
        
        if(this.getAttackTarget() != null)
        	return super.getBlockPathWeight(par1, par2, par3);
        if(this.waterContact())
			return -999999.0F;
		
		return super.getBlockPathWeight(par1, par2, par3);
    }
	
	// Pushed By Water:
	@Override
	public boolean isPushedByWater() {
        return false;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Set Attack Tasks ==========
    public void setAttackTasks(boolean active) {
    	if(active != attacksActive) {
    		int nextTaskID = attackTaskStartID;
			for(EntityAIBase attackTask : attackTasks) {
				if(active)
					this.tasks.addTask(nextTaskID, attackTask);
				else
					this.tasks.removeTask(attackTask);
				nextTaskID++;
			}
    		attacksActive = active;
    	}
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotionID() == Potion.poison.id) return false;
        if(potionEffect.getPotionID() == Potion.blindness.id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }
}
