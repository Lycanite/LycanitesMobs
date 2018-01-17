package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.MobDrop;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySnowman;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityVolcan extends EntityCreatureTameable implements IMob, IGroupRock, IGroupFire {

	private EntityAIAttackMelee meleeAttackAI;

	public int volcanMeltRadius = 2;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVolcan(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 3;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.volcanMeltRadius = ConfigBase.getConfig(this.group, "general").getInt("Features", "Volcan Block Melting Radius", this.volcanMeltRadius, "Controls how far Volcans melt blocks, set to 0 to disable.");
        
        this.setWidth = 0.8F;
        this.setHeight = 1.6F;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.meleeAttackAI = new EntityAIAttackMelee(this).setRate(20).setLongMemory(true);
        this.tasks.addTask(2, meleeAttackAI);
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(8).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
		this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupIce.class));
		this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupWater.class));
		this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntitySnowman.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntitySilverfish.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
		this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPlant.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 30D);
		baseAttributes.put("movementSpeed", 0.3D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new MobDrop(new ItemStack(Blocks.STONE), 1F).setMaxAmount(8));
        this.drops.add(new MobDrop(new ItemStack(Blocks.IRON_ORE), 0.75F).setMaxAmount(2));
        this.drops.add(new MobDrop(new ItemStack(Items.QUARTZ), 0.75F).setMaxAmount(5));
        this.drops.add(new MobDrop(new ItemStack(Blocks.GOLD_ORE), 0.1F).setMaxAmount(1));
		this.drops.add(new MobDrop(new ItemStack(Items.COAL), 0.5F));
		this.drops.add(new MobDrop(new ItemStack(Items.BLAZE_ROD), 0.1F));
	}

    // ========== Set Size ==========
    @Override
    public void setSize(float width, float height) {
        if(this.getSubspeciesIndex() == 3) {
            super.setSize(width * 2, height * 2);
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public double getRenderScale() {
        if(this.getSubspeciesIndex() == 3) {
            return this.sizeScale * 2;
        }
        return this.sizeScale;
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		// Melt Blocks:
		if(this.updateTick % 40 == 0 && this.volcanMeltRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int range = this.volcanMeltRadius;
			for (int w = -((int) Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++) {
				for (int d = -((int) Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++) {
					for (int h = -((int) Math.ceil(this.height) + range); h <= Math.ceil(this.height); h++) {
						Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
						if (block == Blocks.OBSIDIAN || block == Blocks.COBBLESTONE) {
							IBlockState blockState = Blocks.FLOWING_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL, 5);
							if (block == Blocks.OBSIDIAN)
								blockState = Blocks.LAVA.getDefaultState();
							this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
						}
						else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
							this.getEntityWorld().setBlockToAir(this.getPosition().add(w, h, d));
						}
					}
				}
			}
		}

		// Particles:
		if(this.getEntityWorld().isRemote) {
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().spawnParticle(EnumParticleTypes.DRIP_LAVA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
			}
			if(this.ticksExisted % 10 == 0)
				for(int i = 0; i < 2; ++i) {
					this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
				}
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
        	if(this.getAttackPhase() == 2 && ObjectManager.getPotionEffect("weight") != null)
        		((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("weight"), this.getEffectDuration(7), 0));
        	else
        		((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.getEffectDuration(7), 0));
        }

        // Silverfish Extermination:
        if(target instanceof EntitySilverfish) {
            target.setDead();
        }
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
   	//                    Taking Damage
   	// ==================================================
	// ========== Damage Modifier ==========
	public float getDamageModifier(DamageSource damageSrc) {
		if(damageSrc.isFireDamage())
			return 0F;
		else return super.getDamageModifier(damageSrc);
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
        if(potionEffect.getPotion() == MobEffects.MINING_FATIGUE) return false;
        if(ObjectManager.getPotionEffect("weight") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("weight")) return false;
		if(ObjectManager.getPotionEffect("Penetration") != null)
			if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Penetration")) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public boolean canBurn() { return false; }

	@Override
	public boolean waterDamage() { return true; }
}
