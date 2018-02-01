package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.MobDrop;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityDjinn extends EntityCreatureTameable implements IMob, IFusable {

	public float fireDamageAbsorbed = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityDjinn(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 10;
        this.hasAttackSound = false;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.6F;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIFollowFuse(this).setLostDistance(16));
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(80).setRange(16.0F).setMinChaseDistance(8.0F));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(8).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupFire.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
        this.targetTasks.addTask(7, new EntityAITargetFuse(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<>();
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.22D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new MobDrop(new ItemStack(Items.FEATHER), 0.5F).setMaxAmount(4));
        this.drops.add(new MobDrop(new ItemStack(Items.CHORUS_FRUIT), 0.5F).setMaxAmount(4));
        this.drops.add(new MobDrop(new ItemStack(Items.SNOWBALL), 1F).setMaxAmount(8));
        this.drops.add(new MobDrop(new ItemStack(ObjectManager.getItem("whirlwindcharge")), 0.25F).setMaxAmount(3));
        this.drops.add(new MobDrop(new ItemStack(ObjectManager.getItem("cleansingcrystal")), 0.05F).setMaxAmount(1));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		if(!this.getEntityWorld().isRemote) {

			// Environmental Transformation:
			if(!this.isTamed()) {
				if (this.fireDamageAbsorbed >= 10) {
					this.transform(EntityZephyr.class, null, false);
				}
			}
		}
        
        // Particles:
        //if(this.getEntityWorld().isRemote)
            //this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOWBALL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	int projectileCount = 10;
    	for(int i = 0; i < projectileCount; i++) {
    		this.fireProjectile(EntityWhirlwind.class, target, range, (360 / 10) * i, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		}
        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return false; }


    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("cactus") || type.equals("inWall"))
            return false;
        if(source.isFireDamage()) {
            this.fireDamageAbsorbed += damage;
            return false;
        }
        if(type.equals("lightningBolt") && !this.isTamed()) {
        	this.transform(EntityZephyr.class, null, false);
        	return false;
		}
        return super.isDamageTypeApplicable(type, source, damage);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.LEVITATION)
            return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                     Interact
    // ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, ItemStack itemStack) {
        HashMap<Integer, String> commands = new HashMap<Integer, String>();
        commands.putAll(super.getInteractCommands(player, itemStack));

        if(itemStack != null) {
            // Water:
            if(itemStack.getItem() == Items.BUCKET && this.isTamed())
                commands.put(CMD_PRIOR.ITEM_USE.id, "Water");
        }

        return commands;
    }

    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, EntityPlayer player, ItemStack itemStack) {

        // Water:
        if(command.equals("Water")) {
            this.replacePlayersItem(player, itemStack, new ItemStack(Items.WATER_BUCKET));
        }

        super.performCommand(command, player, itemStack);
    }


    // ==================================================
    //                      Fusion
    // ==================================================
    protected IFusable fusionTarget;

    @Override
    public IFusable getFusionTarget() {
        return this.fusionTarget;
    }

    @Override
    public void setFusionTarget(IFusable fusionTarget) {
        this.fusionTarget = fusionTarget;
    }

    @Override
    public Class getFusionClass(IFusable fusable) {
		if(fusable instanceof EntityCinder) {
			return EntityZephyr.class;
		}
        if(fusable instanceof EntityJengu) {
            return EntityReiver.class;
        }
        return null;
    }
}
