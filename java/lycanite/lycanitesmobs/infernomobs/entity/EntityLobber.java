package lycanite.lycanitesmobs.infernomobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityLobber extends EntityCreatureBase implements IMob {

	EntityAIWander wanderAI = new EntityAIWander(this);
	int attackTaskStartID = 2;
	boolean attacksActive = false;
	EntityAIBase[] attackTasks = new EntityAIBase[] {
			(EntityAIBase)(new EntityAIAttackMelee(this).setLongMemory(false))
	};
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityLobber(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Lobber";
        this.mod = InfernoMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 3;
        this.experience = 10;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = false;
        
        this.eggName = "InfernoEgg";
        
        this.setWidth = 1.9F;
        this.setHeight = 3.5F;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this).setSink(true));
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(100).setRange(16.0F).setMinChaseDistance(8.0F).setChaseTime(-1));
        this.tasks.addTask(6, wanderAI);
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
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
        
        // Don't Attack When Starting to Suffocate:
        if(!this.worldObj.isRemote) {
	        if(this.getAir() > -100)
	        	setAttackTasks(true);
	        else
	        	setAttackTasks(false);
        }
        
        // Lava Pause Rates:
		if(this.lavaContact())
			this.wanderAI.setPauseRate(120);
		else
			this.wanderAI.setPauseRate(0);
        
        // Hellfire Trail:
        if(!this.worldObj.isRemote && (this.ticksExisted % 10 == 0 || this.isMoving() && this.ticksExisted % 5 == 0)) {
        	int trailHeight = 1;
        	for(int y = 0; y < trailHeight; y++) {
        		Block block = this.worldObj.getBlock((int)this.posX, (int)this.posY + y, (int)this.posZ);
        		if(block == Blocks.air || block == Blocks.snow || block == Blocks.fire)
        			this.worldObj.setBlock((int)this.posX, (int)this.posY + y, (int)this.posZ, Blocks.fire);
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
    public float getSpeedMod() {
    	if(this.lavaContact())
    		return 16.0F;
    	return 1.0F;
    }
    
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		int waterWeight = 10;
		
        if(this.worldObj.getBlock(par1, par2, par3) == Blocks.lava)
        	return super.getBlockPathWeight(par1, par2, par3) * (waterWeight + 1);
		if(this.worldObj.getBlock(par1, par2, par3) == Blocks.flowing_lava)
			return super.getBlockPathWeight(par1, par2, par3) * waterWeight;
        
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
    
	// ========== Set Attack Tasks ==========
    public void setAttackTasks(boolean active) {
    	if(active != attacksActive) {
    		int nextTaskID = attackTaskStartID;
			for(EntityAIBase attackTask : attackTasks) {
				if(active)
					this.tasks.addTask(nextTaskID, attackTask);
				else
					this.tasks.removeTask(attackTask);
				nextTaskID++;
			}
    		attacksActive = active;
    	}
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.resistance.id && par1PotionEffect.getAmplifier() < 0) return false;
        super.isPotionApplicable(par1PotionEffect);
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
        return false;
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
