package com.lycanitesmobs.desertmobs.entity;

import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityManticore extends EntityCreatureTameable implements IMob, IGroupHunter {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityManticore(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;
        this.flySoundSpeed = 20;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(4, new EntityAIAttackMelee(this));
        this.tasks.addTask(5, this.aiSit);
        this.tasks.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 3D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.SANDSTONE), 1).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Blocks.STONE), 1).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.FLINT), 0.5F));
        this.drops.add(new DropRate(new ItemStack(Blocks.IRON_ORE), 0.25F));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
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
        	((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.HUNGER, this.getEffectDuration(7), 0));
        	((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, this.getEffectDuration(7), 0));
        }
        
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
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
    	if(type.equals("cactus")) return false;
    	return super.isDamageTypeApplicable(type);
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.HUNGER) return false;
        if(potionEffect.getPotion() == MobEffects.WEAKNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }
}
