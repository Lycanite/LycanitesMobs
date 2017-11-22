package com.lycanitesmobs.desertmobs.entity;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntitySutiramu extends EntityCreatureTameable implements IMob, IGroupPredator {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySutiramu(World world) {
        super(world);

        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;

        this.setWidth = 1.9F;
        this.setHeight = 1.9F;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(3, new EntityAIAttackMelee(this));
        this.tasks.addTask(5, this.aiSit);
        this.tasks.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(8).setLostDistance(32));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setCheckSight(false));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(2, 1));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(2, 1));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class).setPackHuntingScale(2, 1));
        }
    }

    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<>();
		baseAttributes.put("maxHealth", 25D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }

	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.FLINT), 1).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Blocks.IRON_ORE), 0.5F).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.GOLD_NUGGET), 0.25F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.SPIDER_EYE), 0.25F).setMaxAmount(2));
	}


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;

    	// Hunger:
        if(target instanceof EntityLivingBase) {
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.HUNGER, this.getEffectDuration(7), 0));
        }

        return true;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.HUNGER) return false;
        if(potionEffect.getPotion() == MobEffects.WEAKNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }

    @Override
    public boolean canBurn() { return false; }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntitySutiramu(this.getEntityWorld());
	}


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
