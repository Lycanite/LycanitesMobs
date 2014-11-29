package lycanite.lycanitesmobs.swampmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIBreakDoor;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityEttin extends EntityCreatureAgeable implements IMob {
	public boolean ettinGreifing = true;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEttin(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 10;
        this.spawnsInDarkness = true;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.ettinGreifing = ConfigBase.getConfig(this.group, "general").getBool("Features", "Ettin Griefing", this.ettinGreifing, "Set to false to disable Ettin block destruction.");
        
        this.setWidth = 1.5F;
        this.setHeight = 3.2F;
        this.setupMob();
        
        // Stats:
        this.attackPhaseMax = 2;
        
        // AI Tasks:
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(6, new EntityAIWander(this));
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
		baseAttributes.put("maxHealth", 60D);
		baseAttributes.put("movementSpeed", 0.16D);
		baseAttributes.put("knockbackResistance", 0.5D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 6D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.log), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Blocks.brown_mushroom), 1).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Blocks.red_mushroom), 1).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.leather), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.coal), 1).setMinAmount(2).setMaxAmount(8));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
    	// Destroy Blocks:
		if(!this.worldObj.isRemote)
	        if(this.getAttackTarget() != null && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && this.ettinGreifing) {
		    	float distance = this.getAttackTarget().getDistanceToEntity(this);
		    		if(distance <= this.width + 4.0F)
		    			this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10, true);
	        }
        
        super.onLivingUpdate();
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	boolean success = super.meleeAttack(target, damageScale);
    	if(success)
    		this.nextAttackPhase();
    	return success;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.blindness.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityEttin(this.worldObj);
	}
}
