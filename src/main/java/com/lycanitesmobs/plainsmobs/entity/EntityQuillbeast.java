package com.lycanitesmobs.plainsmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityQuillbeast extends EntityCreatureTameable implements IMob {
	EntityAIAttackRanged aiAttackRanged;
	EntityAIAttackMelee aiAttackMelee;
	EntityAIAvoid aiAvoid;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityQuillbeast(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.setWidth = 0.6F;
        this.setHeight = 0.9F;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("quillbeasttreat"))).setTemptDistanceMin(4.0D));

        this.aiAttackMelee = new EntityAIAttackMelee(this).setRate(10).setLongMemory(true).setEnabled(false);
        this.tasks.addTask(2, this.aiAttackMelee);

        this.aiAttackRanged = new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(30).setRange(16.0F).setMinChaseDistance(10.0F).setChaseTime(-1);
        this.tasks.addTask(2, this.aiAttackRanged);

        this.aiAvoid = new EntityAIAvoid(this).setNearSpeed(1.5D).setFarSpeed(1.3D).setNearDistance(5.0D).setFarDistance(9.0D);
        this.tasks.addTask(3, this.aiAvoid);

        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupHunter.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        this.targetTasks.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupAlpha.class));
        this.targetTasks.addTask(5, new EntityAITargetAvoid(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("quill")), 0.75F));
        this.drops.add(new DropRate(new ItemStack(Items.LEATHER), 0.75F).setMinAmount(1).setMaxAmount(3));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== AI Update ==========
	@Override
    public void updateAITasks() {
        // Avoid Attack Target:
        if(!this.getEntityWorld().isRemote) {
	        if(this.getAttackTarget() != null && this.getAttackTarget() != this.getAvoidTarget())
	        	this.setAvoidTarget(this.getAttackTarget());
        }
		
        super.updateAITasks();
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
        for(int i = -2; i < 12; i++) {
            // Type:
            EntityQuill projectile = new EntityQuill(this.getEntityWorld(), this);

            // Y Offset:
            projectile.posY -= this.height / 4;

            // Accuracy:
            float accuracy = i * 1.0F * (this.getRNG().nextFloat() - 0.5F);

            // Set Velocities:
            double d0 = target.posX - this.posX + accuracy;
            double d1 = target.posY - projectile.posY + accuracy;
            double d2 = target.posZ - this.posZ + accuracy;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            float velocity = 1.2F;
            projectile.setThrowableHeading(d0, d1 + (double) f1, d2, velocity, 6.0F);

            // Launch:
            if(i == 0)
                this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().spawnEntity(projectile);
        }

        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.WEAKNESS) return false;
        if(potionEffect.getPotion() == MobEffects.SLOWNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }


    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.getItem() == ObjectManager.getItem("quillbeasttreat");
    }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("vegetables", testStack);
    }
}
