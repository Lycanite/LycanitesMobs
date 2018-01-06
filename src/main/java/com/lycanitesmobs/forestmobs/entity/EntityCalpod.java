package com.lycanitesmobs.forestmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.MobDrop;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityCalpod extends EntityCreatureBase implements IMob, IGroupPrey {
	private int calpodSwarmLimit = 5;
	private boolean calpodGreifing = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCalpod(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 0;
        this.experience = 3;
        this.hasAttackSound = true;


		this.setWidth = 1.3F;
        this.setHeight = 0.9F;
        this.setupMob();
        
        this.calpodSwarmLimit = ConfigBase.getConfig(this.group, "general").getInt("Features", "Calpod Swarm Limit", this.calpodSwarmLimit, "Limits how many Calpods there can be when swarming.");
		this.calpodGreifing = ConfigBase.getConfig(this.group, "general").getBool("Features", "Calpod Griefing", this.calpodGreifing, "Set to false to disable Calpod block destruction.");
	}

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setRate(20).setLongMemory(true));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupHunter.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupAlpha.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new MobDrop(new ItemStack(Blocks.LOG, 1, 0), 1).setMaxAmount(6));
        this.drops.add(new MobDrop(new ItemStack(Items.STICK), 0.5F).setMaxAmount(6).setBurningDrop(new ItemStack(Items.COAL)));
        this.drops.add(new MobDrop(new ItemStack(Items.SPIDER_EYE), 0.5F).setMaxAmount(6));
        this.drops.add(new MobDrop(new ItemStack(Items.WHEAT_SEEDS), 0.1F).setMaxAmount(3));
        this.drops.add(new MobDrop(new ItemStack(Items.PUMPKIN_SEEDS), 0.05F).setMaxAmount(1));
        this.drops.add(new MobDrop(new ItemStack(Items.MELON_SEEDS), 0.05F).setMaxAmount(1));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.updateTick % 40 == 0) {
			this.allyUpdate();
		}

		// Destroy Blocks:
		if(!this.getEntityWorld().isRemote)
			if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.calpodGreifing) {
				float distance = this.getAttackTarget().getDistanceToEntity(this);
				if(distance <= this.width + 1.0F)
					this.destroyAreaBlock((int)this.posX, (int)this.posY, (int)this.posZ, BlockLog.class, true, 0);
			}
        
        super.onLivingUpdate();
    }
    
    // ========== Spawn Minions ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Minions:
		if(this.calpodSwarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 64D) < this.calpodSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.125F)
				this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	EntityLivingBase minion = new EntityCalpod(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof EntityCreatureBase) {
    		((EntityCreatureBase)minion).setMinion(true);
    		((EntityCreatureBase)minion).setSubspecies(this.getSubspeciesIndex(), true);
    	}
    	this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(IGroupAlpha.class))
        	return false;
        if(targetClass.isAssignableFrom(IGroupPredator.class))
            return false;
    	return super.canAttackClass(targetClass);
    }
    
    
    // ==================================================
    //                       Death
    // ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
    	allyUpdate();
        super.onDeath(par1DamageSource);
    }


	// ==================================================
	//                     Abilities
	// ==================================================
	// ========== Movement ==========
	@Override
	public boolean canClimb() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.SLOWNESS) return false;
        if(ObjectManager.getPotionEffect("paralysis") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("paralysis")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }

    @Override
    public float getFallResistance() {
        return 100;
    }
}
