package lycanite.lycanitesmobs.mountainmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIBreakDoor;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityTroll extends EntityCreatureBase implements IMob {
	
	public boolean trollGreifing = true;
	
	// ========== Unique Entity Variables ==========
	public boolean stoneForm = false;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTroll(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.hasAttackSound = false;

        //this.canGrow = false;
        //this.babySpawnChance = 0.01D;
        
        this.trollGreifing = ConfigBase.getConfig(this.group, "general").getBool("Features", "Troll Griefing", this.trollGreifing, "Set to false to disable Troll block destruction.");
        
        this.setWidth = 1.5F;
        this.setHeight = 3.2F;
        this.setupMob();
    	
        // AI Tasks:
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.5D).setRate(60).setRange(14.0F).setMinChaseDistance(5.0F).setChaseTime(-1));
        //this.tasks.addTask(6, this.aiSit);
        //this.tasks.addTask(7, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        //this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        //this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        //this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 60D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 6D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.log), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.bone), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.leather), 1).setMinAmount(2).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.coal), 1).setMinAmount(2).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("BoulderBlastCharge")), 0.5F).setMinAmount(1).setMaxAmount(1));
	}
    
    
    // ==================================================
    //                       Name
    // ==================================================
    public String getTextureName() {
    	if(this.stoneForm)
    		return super.getTextureName() + "_stone";
    	return super.getTextureName();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Daylight Stone Form:
        if(!this.stoneForm) {
        	if(this.isDaytime() && this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.boundingBox.maxY, (int)this.posZ)) {
        		this.stoneForm = true;
        	}
        }
        else {
        	if(!this.isDaytime() || !this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.boundingBox.maxY, (int)this.posZ)) {
	        	this.stoneForm = false;
        	}
        }
        
        // Destroy Blocks:
 		if(!this.worldObj.isRemote)
 	        if(this.getAttackTarget() != null && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && this.trollGreifing) {
 		    	float distance = this.getAttackTarget().getDistanceToEntity(this);
 		    		if(distance <= this.width + 4.0F)
 		    			this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10, true);
 	        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.stoneForm) // Slower in stone form.
    		return 0.125F;
    	return 1.0F;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityBoulderBlast projectile = new EntityBoulderBlast(this.worldObj, this);
        projectile.setProjectileScale(6f);
    	
    	// Y Offset:
    	projectile.posY -= this.height / 4;
    	
    	// Accuracy:
    	float accuracy = 3.0F * (this.getRNG().nextFloat() - 0.5F);
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX + accuracy;
        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
        double d2 = target.posZ - this.posZ + accuracy;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 6.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(projectile);
        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
    	if(this.stoneForm) {
    		if(damageSrc.getEntity() != null) {
    			Item heldItem = null;
        		if(damageSrc.getEntity() instanceof EntityPlayer) {
        			EntityPlayer entityPlayer = (EntityPlayer)damageSrc.getEntity();
    	    		if(entityPlayer.getHeldItem() != null) {
    	    			heldItem = entityPlayer.getHeldItem().getItem();
    	    		}
        		}
        		else if(damageSrc.getEntity() instanceof EntityLiving) {
    	    		EntityLiving entityLiving = (EntityLiving)damageSrc.getEntity();
    	    		if(entityLiving.getHeldItem() != null) {
    	    			heldItem = entityLiving.getHeldItem().getItem();
    	    		}
        		}
        		
        		if(ObjectLists.isPickaxe(heldItem))
    				return 2.0F;
        	}
    		return 0.25F;
    	}
    	
    	if(damageSrc.isFireDamage())
    		return 2.0F;
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.digSlowdown.id) return false;
        if(ObjectManager.getPotionEffect("Weight") != null)
        	if(par1PotionEffect.getPotionID() == ObjectManager.getPotionEffect("Weight").id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public float getFallResistance() {
    	return 50;
    }
    
    @Override
    public boolean canBurn() { return !this.stoneForm; }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    //@Override
	//public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
	//	return new EntityTroll(this.worldObj);
	//}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return false; }
}
