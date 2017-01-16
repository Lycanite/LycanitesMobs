package lycanite.lycanitesmobs.shadowmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupShadow;
import lycanite.lycanitesmobs.core.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.core.entity.ai.*;
import lycanite.lycanitesmobs.core.info.DropRate;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityGrue extends EntityCreatureTameable implements IMob, IGroupShadow {
    
	private int teleportTime = 60;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGrue(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 1;
        this.experience = 5;
        this.hasAttackSound = true;
        
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
        this.tasks.addTask(1, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setRate(20).setLongMemory(true));
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
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.ENDER_PEARL), 0.5F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Blocks.OBSIDIAN), 0.5F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("soulstoneshadow")), 1F).setMaxAmount(1).setSubspecies(3));
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
        
        // Random Target Teleporting:
        if(!this.worldObj.isRemote && this.hasAttackTarget()) {
	        if(this.teleportTime-- <= 0) {
	        	this.teleportTime = 60 + this.getRNG().nextInt(40);
                if(this.getAttackTarget() instanceof EntityPlayer)
                    this.teleportTime *= 3;
        		this.playJumpSound();
        		BlockPos teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().width - 1D, 0);
        		if(this.canTeleportTo(this.worldObj, teleportPosition)
        		&& this.canTeleportTo(this.worldObj, new BlockPos(teleportPosition.getX(), teleportPosition.getY() + 1, teleportPosition.getZ())))
                    this.setPosition(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
        		else if(this.canTeleportTo(this.worldObj, teleportPosition)
                && this.canTeleportTo(this.worldObj, teleportPosition))
                    this.setPosition(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ);
	        }
        }
        
        // Particles:
        if(this.worldObj.isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }

    public boolean canTeleportTo(World world, BlockPos pos) {
        IBlockState blockState = this.worldObj.getBlockState(pos);
        if(blockState.getBlock() == null)
            return false;
        if(blockState.isNormalCube())
            return false;
        if(this.getSubspeciesIndex() >= 3)
            return true;
        if(this.testLightLevel(pos) > 1)
            return false;
        return true;
    }
    
    
    // ==================================================
   	//                     Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.worldObj.isRemote) return false;
		if(this.isMoving()) return false;
    	return this.testLightLevel() <= 0;
    }
    
    @Override
    public void startStealth() {
    	if(this.worldObj.isRemote) {
            EnumParticleTypes particle = EnumParticleTypes.SPELL_WITCH;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
            	this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    	super.startStealth();
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
    	if(this.getSubspeciesIndex() > 2 && target instanceof EntityLivingBase) {
    		EntityLivingBase targetLiving = (EntityLivingBase)target;
    		List<Potion> goodEffects = new ArrayList<Potion>();
    		for(Object potionEffectObj : targetLiving.getActivePotionEffects()) {
    			if(potionEffectObj instanceof PotionEffect) {
    				Potion potion = ((PotionEffect)potionEffectObj).getPotion();
                    if(potion != null) {
                        if(ObjectLists.inEffectList("buffs", potion))
                            goodEffects.add(potion);
                    }
    			}
    		}
    		if(goodEffects.size() > 0) {
    			if(goodEffects.size() > 1)
    				targetLiving.removePotionEffect(goodEffects.get(this.getRNG().nextInt(goodEffects.size())));
    			else
    				targetLiving.removePotionEffect(goodEffects.get(0));
		    	float leeching = this.getAttackDamage(damageScale);
		    	this.heal(leeching);
    		}
    	}
    	
    	// Effects:
        if(target instanceof EntityLivingBase) {
        	((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, this.getEffectDuration(7), 0));
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
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.BLINDNESS) return false;
        if(ObjectManager.getPotionEffect("Fear") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Fear")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    /** Returns true if this mob should be damaged by the sun. **/
    public boolean daylightBurns() { return !this.isTamed() && this.getSubspeciesIndex() < 3; }
}
