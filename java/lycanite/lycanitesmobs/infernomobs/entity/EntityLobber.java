package lycanite.lycanitesmobs.infernomobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupFire;
import lycanite.lycanitesmobs.api.IGroupIce;
import lycanite.lycanitesmobs.api.IGroupPlant;
import lycanite.lycanitesmobs.api.IGroupWater;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityItemCustom;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIStayByWater;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityLobber extends EntityCreatureBase implements IMob, IGroupFire {

	EntityAIWander wanderAI = new EntityAIWander(this);
    public boolean lobberMelting = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityLobber(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 3;
        this.experience = 10;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = false;

        this.lobberMelting = ConfigBase.getConfig(this.group, "general").getBool("Features", "Lobber Melting", this.lobberMelting, "Set to false to disable Lobbers melting certain blocks.");
        
        this.setWidth = 1.9F;
        this.setHeight = 3.5F;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setCanSwim(true);
        this.getNavigator().setAvoidsWater(false);
        this.tasks.addTask(0, new EntityAISwimming(this).setSink(true));
        this.tasks.addTask(1, new EntityAIStayByWater(this).setSpeed(1.25D));
        this.tasks.addTask(3, new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(100).setRange(16.0F).setMinChaseDistance(8.0F).setChaseTime(-1));
        this.tasks.addTask(6, wanderAI);
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityCinder.class));
    	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupIce.class));
    	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupWater.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    	this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPlant.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 80D);
		baseAttributes.put("movementSpeed", 0.16D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.coal), 1.0F).setMaxAmount(16));
        this.drops.add(new DropRate(new ItemStack(Items.magma_cream), 0.75F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.blaze_powder), 0.5F).setMaxAmount(6));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("MagmaCharge")), 0.25F));
	}
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Wander Pause Rates:
		if(this.lavaContact())
			this.wanderAI.setPauseRate(120);
		else
			this.wanderAI.setPauseRate(0);
        
        // Fire Trail:
        if(!this.worldObj.isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
        	int trailHeight = 1;
        	for(int y = 0; y < trailHeight; y++) {
        		Block block = this.worldObj.getBlock((int)this.posX, (int)this.posY + y, (int)this.posZ);
        		if(block == Blocks.air || block == Blocks.fire || block == Blocks.snow_layer || block == Blocks.tallgrass || block == ObjectManager.getBlock("frostfire"))
        			this.worldObj.setBlock((int)this.posX, (int)this.posY + y, (int)this.posZ, Blocks.fire);
        	}
		}

        // Melt Blocks:
        if(!this.worldObj.isRemote && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && this.lobberMelting && this.ticksExisted % 10 == 0 && this.lavaContact()) {
            int range = 2;
            for(int w = -((int)Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++)
                for(int d = -((int)Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++)
                    for(int h = 0; h <= Math.ceil(this.height); h++) {
                        Block block = this.worldObj.getBlock((int)this.posX + w, (int)this.posY + h, (int)this.posZ + d);
                        if(block == Blocks.obsidian || block == Blocks.cobblestone || block == Blocks.dirt || block == Blocks.planks) {
                            this.worldObj.setBlock((int)this.posX + w, (int)this.posY + h, (int)this.posZ + d, Blocks.lava);
                        }
                    }
        }
        
        // Particles:
        if(this.worldObj.isRemote) {
	        for(int i = 0; i < 2; ++i) {
	            this.worldObj.spawnParticle("flame", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	            this.worldObj.spawnParticle("dripLava", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
	        if(this.ticksExisted % 10 == 0)
		        for(int i = 0; i < 2; ++i) {
		            this.worldObj.spawnParticle("lava", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
		        }
        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.lavaContact())
    		return 16.0F;
    	return 1.0F;
    }
    
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		int waterWeight = 10;
		
        if(this.worldObj.getBlock(par1, par2, par3) == Blocks.lava)
        	return (super.getBlockPathWeight(par1, par2, par3) + 1) * (waterWeight + 1);
		if(this.worldObj.getBlock(par1, par2, par3) == Blocks.flowing_lava)
			return (super.getBlockPathWeight(par1, par2, par3) + 1) * waterWeight;
        
        if(this.getAttackTarget() != null)
        	return super.getBlockPathWeight(par1, par2, par3);
        if(this.lavaContact())
			return -999999.0F;
		
		return super.getBlockPathWeight(par1, par2, par3);
    }
	
	// Pushed By Water:
	@Override
	public boolean isPushedByWater() {
        return false;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityCinder.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityProjectileBase projectile = new EntityMagma(this.worldObj, this);
        projectile.setProjectileScale(2f);
    	
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
        super.rangedAttack(target, range);
    }
    
    // ========== Is Aggressive ==========
    @Override
    public boolean isAggressive() {
    	if(this.getAir() <= -100)
    		return false;
    	return super.isAggressive();
    }
    
    
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
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return true;
    }
    
    
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
