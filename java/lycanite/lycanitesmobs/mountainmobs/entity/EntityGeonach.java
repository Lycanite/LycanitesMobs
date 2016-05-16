package lycanite.lycanitesmobs.mountainmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupRock;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
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
        this.defense = 3;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.geonachBlockBreakRadius = ConfigBase.getConfig(this.group, "general").getInt("Features", "Rare Geonach Block Break Radius", this.geonachBlockBreakRadius, "Controls how large the Celestial Geonach's block breaking radius is when it is charging towards its target. Set to -1 to disable. For their block breaking radius on spawn, see the ROCK spawn type features instead. Note that this is only for the extremely rare Geonach.");
        
        this.setWidth = 0.8F;
        this.setHeight = 1.8F;
        this.setupMob();

        this.stepHeight = 1.0F;
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
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntitySilverfish.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.28D);
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
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("soulstonemountain")), 1F).setMaxAmount(1).setSubspecies(3));
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
        
        if(!this.worldObj.isRemote && this.getSubspeciesIndex() == 3 && !this.isPetType("familiar")) {
	    	// Random Charging:
	    	if(this.hasAttackTarget() && this.getDistanceSqToEntity(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
	    		if(this.posY - 1 > this.getAttackTarget().posY)
	    			this.leap(6.0F, -1.0D, this.getAttackTarget());
	    		else if(this.posY + 1 < this.getAttackTarget().posY)
	    			this.leap(6.0F, 1.0D, this.getAttackTarget());
	    		else
	    			this.leap(6.0F, 0D, this.getAttackTarget());
	    		if(this.worldObj.getGameRules().getBoolean("mobGriefing") && this.geonachBlockBreakRadius > -1 && !this.isTamed()) {
		    		this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 10, true, this.geonachBlockBreakRadius);
	    		}
	    	}
        }

        // Particles:
        if(this.worldObj.isRemote)
            for(int i = 0; i < 2; ++i) {
                this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        this.posY + this.rand.nextDouble() * (double) this.height,
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
                        0.0D, 0.0D, 0.0D,
                        Blocks.tallgrass.getStateId(Blocks.stone.getDefaultState()));
            }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        // Silverfish Extermination:
        if(this.hasAttackTarget() && this.getAttackTarget() instanceof EntitySilverfish)
            return 4.0F;
        return super.getAISpeedModifier();
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
        		((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.digSlowdown, this.getEffectDuration(7), 0));
        }
        
        // Update Phase:
        this.nextAttackPhase();
        if(this.getAttackPhase() == 2)
        	this.meleeAttackAI.setRate(60);
        else
        	this.meleeAttackAI.setRate(10);

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
    public boolean canFly() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    @Override
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.getEntity() != null) {
            // Silverfish Extermination:
            if(damageSrc.getEntity() instanceof EntitySilverfish) {
                return 0F;
            }

            // Pickaxe Damage:
    		Item heldItem = null;
    		if(damageSrc.getEntity() instanceof EntityLivingBase) {
                EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getEntity();
	    		if(entityLiving.getHeldItem(EnumHand.MAIN_HAND) != null) {
	    			heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND).getItem();
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
        if(potionEffect.getPotion() == MobEffects.digSlowdown) return false;
        if(ObjectManager.getPotionEffect("weight") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("weight")) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public boolean canBurn() { return false; }
}
