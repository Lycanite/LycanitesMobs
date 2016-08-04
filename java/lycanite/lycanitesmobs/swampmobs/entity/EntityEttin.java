package lycanite.lycanitesmobs.swampmobs.entity;

import lycanite.lycanitesmobs.core.config.ConfigBase;
import lycanite.lycanitesmobs.core.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.core.entity.ai.*;
import lycanite.lycanitesmobs.core.info.DropRate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityEttin extends EntityCreatureAgeable implements IMob {
	public boolean ettinGreifing = true;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEttin(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 10;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.ettinGreifing = ConfigBase.getConfig(this.group, "general").getBool("Features", "Ettin Griefing", this.ettinGreifing, "Set to false to disable Ettin block destruction.");
        
        this.setWidth = 1.5F;
        this.setHeight = 3.2F;
        this.solidCollision = true;
        this.setupMob();
        
        // Stats:
        this.attackPhaseMax = 2;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if(this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
        }
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
		baseAttributes.put("maxHealth", 30D);
		baseAttributes.put("movementSpeed", 0.16D);
		baseAttributes.put("knockbackResistance", 0.5D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 6D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.LOG), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Blocks.BROWN_MUSHROOM), 1).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Blocks.RED_MUSHROOM), 1).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.LEATHER), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.COAL), 1).setMinAmount(2).setMaxAmount(8));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
    	// Destroy Blocks:
		if(!this.worldObj.isRemote)
	        if(this.getAttackTarget() != null && this.worldObj.getGameRules().getBoolean("mobGriefing") && this.ettinGreifing) {
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
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.POISON) return false;
        if(potionEffect.getPotion() == MobEffects.BLINDNESS) return false;
        return super.isPotionApplicable(potionEffect);
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
