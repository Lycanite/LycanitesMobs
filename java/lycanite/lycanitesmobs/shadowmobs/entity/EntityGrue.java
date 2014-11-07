package lycanite.lycanitesmobs.shadowmobs.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupShadow;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIStealth;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerThreats;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
        this.spawnsInDarkness = true;
        this.hasAttackSound = true;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
        
        // AI Tasks:
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
		baseAttributes.put("maxHealth", 30D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.ender_pearl), 0.5F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Blocks.obsidian), 0.5F).setMaxAmount(2));
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
        		this.playJumpSound();
        		double[] teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().width - 1D, 0);
        		if(this.worldObj.isSideSolid((int)teleportPosition[0], (int)teleportPosition[1], (int)teleportPosition[2], ForgeDirection.DOWN)
        		|| this.worldObj.isSideSolid((int)teleportPosition[0], (int)teleportPosition[1] + 1, (int)teleportPosition[2], ForgeDirection.DOWN))
        			this.setPosition(this.getAttackTarget().posX, this.getAttackTarget().posY, this.getAttackTarget().posZ);
        		else
        			this.setPosition(teleportPosition[0], teleportPosition[1], teleportPosition[2]);
	        }
        }
        
        // Particles:
        if(this.worldObj.isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.worldObj.spawnParticle("witchMagic", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
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
            String particle = "witchMagic";
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
    	if(target instanceof EntityLivingBase) {
    		EntityLivingBase targetLiving = (EntityLivingBase)target;
    		List<Integer> goodEffectIDs = new ArrayList<Integer>();
    		for(Object potionEffectObj : targetLiving.getActivePotionEffects()) {
    			if(potionEffectObj instanceof PotionEffect) {
    				int potionID = ((PotionEffect)potionEffectObj).getPotionID();
    				if(potionID >= Potion.potionTypes.length)
    					continue;
    				Potion potion = Potion.potionTypes[potionID];
    				if(!potion.isBadEffect())
    					goodEffectIDs.add(potionID);
    			}
    		}
    		if(goodEffectIDs.size() > 0) {
    			if(goodEffectIDs.size() > 1)
    				targetLiving.removePotionEffect(goodEffectIDs.get(this.getRNG().nextInt(goodEffectIDs.size())));
    			else
    				targetLiving.removePotionEffect(goodEffectIDs.get(0));
		    	float leeching = this.getAttackDamage(damageScale);
		    	this.heal(leeching);
    		}
    	}
    	
    	// Effects:
        if(target instanceof EntityLivingBase) {
        	((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.blindness.id, this.getEffectDuration(7), 0));
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
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotionID() == Potion.blindness.id) return false;
        if(ObjectManager.getPotionEffect("Fear") != null)
        	if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Fear").id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    /** Returns true if this mob should be damaged by the sun. **/
    public boolean daylightBurns() { return true; }
}
