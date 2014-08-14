package lycanite.lycanitesmobs.infernomobs.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityItemCustom;
import lycanite.lycanitesmobs.api.entity.EntityProjectileRapidFire;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityCinder extends EntityCreatureTameable implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCinder(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.spawnsInBlock = false;
        this.hasAttackSound = false;
        
        this.eggName = "InfernoEgg";
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(20).setStaminaTime(100).setRange(12.0F).setMinChaseDistance(3.0F).setChaseTime(-1));
        this.tasks.addTask(6, this.aiSit);
        this.tasks.addTask(7, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityLobber.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        if(ObjectManager.getMob("Reiver") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Reiver")));
        if(ObjectManager.getMob("Wendigo") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Wendigo")));
        if(ObjectManager.getMob("Ent") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Ent")));
        if(ObjectManager.getMob("Trent") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Trent")));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.coal), 0.5F));
        this.drops.add(new DropRate(new ItemStack(Items.blaze_rod), 0.1F));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("EmberCharge")), 0.25F));
	}
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Fire Sound:
        if(this.rand.nextInt(24) == 0)
            this.worldObj.playSoundEffect(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F);
        
        // Particles:
        if(this.worldObj.isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.worldObj.spawnParticle("largesmoke", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	            this.worldObj.spawnParticle("flame", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityLobber.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	List<EntityProjectileRapidFire> projectiles = new ArrayList<EntityProjectileRapidFire>();
    	
    	EntityProjectileRapidFire projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, this.worldObj, this, 15, 3);
    	projectiles.add(projectileEntry);
    	
    	EntityProjectileRapidFire projectileEntry2 = new EntityProjectileRapidFire(EntityEmber.class, this.worldObj, this, 15, 3);
    	projectileEntry2.offsetX += 1.0D;
    	projectiles.add(projectileEntry2);
    	
    	EntityProjectileRapidFire projectileEntry3 = new EntityProjectileRapidFire(EntityEmber.class, this.worldObj, this, 15, 3);
    	projectileEntry3.offsetX -= 1.0D;
    	projectiles.add(projectileEntry3);
    	
    	EntityProjectileRapidFire projectileEntry4 = new EntityProjectileRapidFire(EntityEmber.class, this.worldObj, this, 15, 3);
    	projectileEntry4.offsetZ += 1.0D;
    	projectiles.add(projectileEntry4);
    	
    	EntityProjectileRapidFire projectileEntry5 = new EntityProjectileRapidFire(EntityEmber.class, this.worldObj, this, 15, 3);
    	projectileEntry5.offsetZ -= 1.0D;
    	projectiles.add(projectileEntry5);
    	
    	EntityProjectileRapidFire projectileEntry6 = new EntityProjectileRapidFire(EntityEmber.class, this.worldObj, this, 15, 3);
    	projectileEntry6.offsetY += 1.0D;
    	projectiles.add(projectileEntry6);
    	
    	EntityProjectileRapidFire projectileEntry7 = new EntityProjectileRapidFire(EntityEmber.class, this.worldObj, this, 15, 3);
    	projectileEntry7.offsetY -= 10D;
    	projectiles.add(projectileEntry7);
    	
    	for(EntityProjectileRapidFire projectile : projectiles) {
	        projectile.setProjectileScale(1f);
	    	
	    	// Y Offset:
	    	projectile.posY -= this.height / 4;
	    	
	    	// Accuracy:
	    	float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);
	    	
	    	// Set Velocities:
	        double d0 = target.posX - this.posX + accuracy;
	        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
	        double d2 = target.posZ - this.posZ + accuracy;
	        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
	        float velocity = 1.2F;
	        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 6.0F);
	        
	        // Launch:
	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.worldObj.spawnEntityInWorld(projectile);
    	}
    	
        super.rangedAttack(target, range);
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
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(ObjectManager.getPotionEffect("Penetration") != null)
            if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Penetration").id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    
    // ==================================================
   	//                       Drops
   	// ==================================================
    // ========== Apply Drop Effects ==========
    /** Used to add effects or alter the dropped entity item. **/
    @Override
    public void applyDropEffects(EntityItemCustom entityitem) {
    	entityitem.setCanBurn(false);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness(float par1) {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
