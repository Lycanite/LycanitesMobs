package lycanite.lycanitesmobs.mountainmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupRock;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerThreats;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.world.World;

public class EntityGeonach extends EntityCreatureTameable implements IMob, IGroupRock {
	
	private EntityAIAttackMelee meleeAttackAI;
	
	public int geonachBlockBreakRadius = 0;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGeonach(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 4;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.hasAttackSound = true;
        
        this.geonachBlockBreakRadius = ConfigBase.getConfig(this.group, "general").getInt("Features", "Rare Geonach Block Break Radius", this.geonachBlockBreakRadius, "Controls how large the RARE Geonach's block breaking radius is when it is charging towards its target. Set to -1 to disable. For their block breaking radius on spawn, see the ROCK spawn type features instead. Note that this is only for the extremely rare Geonach.");
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
        
        this.attackPhaseMax = 3;
        this.justAttackedTime = (short)(10);
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.meleeAttackAI = new EntityAIAttackMelee(this).setRate(20).setLongMemory(true);
        this.tasks.addTask(2, meleeAttackAI);
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 30D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.stone), 1F).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(Blocks.iron_ore), 0.75F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.quartz), 0.75F).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Blocks.gold_ore), 0.1F).setMaxAmount(1));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        if(!this.worldObj.isRemote && this.getSubspeciesIndex() == 3) {
	    	// Random Charging:
	    	if(this.hasAttackTarget() && this.getDistanceSqToEntity(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
	    		if(this.posY - 1 > this.getAttackTarget().posY)
	    			this.leap(6.0F, -1.0D, this.getAttackTarget());
	    		else if(this.posY + 1 < this.getAttackTarget().posY)
	    			this.leap(6.0F, 1.0D, this.getAttackTarget());
	    		else
	    			this.leap(6.0F, 0D, this.getAttackTarget());
	    		if(this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && this.geonachBlockBreakRadius > -1 && !this.isTamed()) {
		    		this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10, true, this.geonachBlockBreakRadius);
	    		}
	    	}
        }
        
        // Particles:
        if(this.worldObj.isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.worldObj.spawnParticle("blockcrack_1_0", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Effects:
        if(target instanceof EntityLivingBase) {
        	if(this.getAttackPhase() == 2 && ObjectManager.getPotionEffect("weight") != null && ObjectManager.getPotionEffect("Weight").id < Potion.potionTypes.length)
        		((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("weight").id, this.getEffectDuration(7), 0));
        	else
        		((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.digSlowdown.id, this.getEffectDuration(7), 0));
        }
        
        // Update Phase:
        this.nextAttackPhase();
        if(this.getAttackPhase() == 2)
        	this.meleeAttackAI.setRate(60);
        else
        	this.meleeAttackAI.setRate(10);
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canFly() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
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
				return 4.0F;
    	}
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
    	if(type.equals("cactus") || type.equals("inWall")) return false;
    	return super.isDamageTypeApplicable(type);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotionID() == Potion.digSlowdown.id) return false;
        if(ObjectManager.getPotionEffect("Weight") != null)
        	if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Weight").id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
}
