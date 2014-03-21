package lycanite.lycanitesmobs.infernomobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileRapidFire;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCinder extends EntityCreatureBase implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCinder(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "Cinder";
        this.mod = InfernoMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.spawnsInBlock = true;
        this.hasAttackSound = false;
        
        this.eggName = "InfernoEgg";
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(5, new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(20).setStaminaTime(100).setRange(12.0F).setMinChaseDistance(3.0F).setChaseTime(-1));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        if(ObjectManager.getMob("Reiver") != null)
        	this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Reiver")));
        if(ObjectManager.getMob("Ent") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Ent")));
        if(ObjectManager.getMob("Trent") != null)
        	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(ObjectManager.getMob("Trent")));
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
        this.drops.add(new DropRate(Item.coal.itemID, 0.5F));
        this.drops.add(new DropRate(Item.blazeRod.itemID, 0.1F));
        this.drops.add(new DropRate(ObjectManager.getItem("EmberCharge").itemID, 0.25F));
	}
	
	
    // ==================================================
    //                     Spawning
    // ==================================================
	@Override
    public boolean spawnBlockCheck(World world, int x, int y, int z) {
    	int checkRange = 16;
    	int blocksFound = 0;
    	int targetAmount = 8;
    	for(int i = x - checkRange; i <= x + checkRange; i++)
        	for(int j = y - checkRange; j <= y + checkRange; j++)
            	for(int k = z - checkRange; k <= z + checkRange; k++) {
            		int blockID = world.getBlockId(i, j, k);
            		if(blockID == Block.fire.blockID || (ObjectManager.getBlock("Hellire") != null && blockID == ObjectManager.getBlock("Hellire").blockID))
            			if(++blocksFound >= targetAmount)
            				return true;
            	}
    	return false;
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
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityProjectileRapidFire projectile = new EntityProjectileRapidFire(EntityEmber.class, this.worldObj, this, 15, 3);
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
        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canFly() { return true; }
    
    
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
}
