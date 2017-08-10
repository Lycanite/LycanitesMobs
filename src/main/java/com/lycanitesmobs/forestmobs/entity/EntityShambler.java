package com.lycanitesmobs.forestmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityShambler extends EntityCreatureTameable implements IMob, IGroupPlant {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityShambler(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 3;
        this.experience = 7;
        this.spawnsUnderground = true;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        
        this.setWidth = 1.5F;
        this.setHeight = 3.5F;
        this.setupMob();
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
        this.tasks.addTask(7, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("shamblertreat"))).setTemptDistanceMin(2.0D));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIBeg(this));
        this.tasks.addTask(11, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(12, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityTrent.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupFire.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setCheckSight(false));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.2D);
		baseAttributes.put("knockbackResistance", 0.75D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.LOG, 1, 0), 1).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(Items.STICK), 0.5F).setMaxAmount(6).setBurningDrop(new ItemStack(Items.COAL)));
        this.drops.add(new DropRate(new ItemStack(Blocks.VINE), 0.1F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.WHEAT_SEEDS), 0.1F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.PUMPKIN_SEEDS), 0.05F).setMaxAmount(1));
        this.drops.add(new DropRate(new ItemStack(Items.MELON_SEEDS), 0.05F).setMaxAmount(1));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Water Healing:
        if(this.isInWater())
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3 * 20, 2));
        else if(this.worldObj.isRaining() && this.worldObj.canBlockSeeSky(this.getPosition()))
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3 * 20, 1));
    }

    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Leech:
    	float leeching = this.getEffectStrength(this.getAttackDamage(damageScale) / 2);
    	this.heal(leeching);

        // Paralysis:
        if(target instanceof EntityLivingBase && this.rand.nextFloat() >= 0.5F) {
            if(ObjectManager.getPotionEffect("Paralysis") != null)
                ((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("Paralysis"), this.getEffectDuration(2), 0));
        }
        
        return true;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFireDamage())
            return 4.0F;
        if(damageSrc.getEntity() != null) {
            Item heldItem = null;
            if(damageSrc.getEntity() instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)damageSrc.getEntity();
                if(entityPlayer.getHeldItem(EnumHand.MAIN_HAND) != null) {
                    heldItem = entityPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem();
                }
            }
            else if(damageSrc.getEntity() instanceof EntityLiving) {
                EntityLiving entityLiving = (EntityLiving)damageSrc.getEntity();
                if(entityLiving.getHeldItem(EnumHand.MAIN_HAND) != null) {
                    heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND).getItem();
                }
            }
            if(ObjectLists.isAxe(heldItem))
                return 4.0F;
        }
        return super.getDamageModifier(damageSrc);
    }
    
    
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
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityShambler(this.worldObj);
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.getItem() == ObjectManager.getItem("shamblertreat");
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("vegetables", testStack) || ObjectLists.inItemList("fruit", testStack);
    }
}
