package lycanite.lycanitesmobs.mountainmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityBeholder extends EntityCreatureTameable {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityBeholder(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 2;
        this.experience = 5;
        this.hasAttackSound = false;
        
        this.setWidth = 3.9F;
        this.setHeight = 3.9F;
        
        this.justAttackedTime = 20;
        this.setupMob();

        this.stepHeight = 1.0F;
        
        // AI Tasks:
        this.tasks.addTask(3, new EntityAIAttackRanged(this).setSpeed(0.25D).setRate(80).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 40D);
		baseAttributes.put("attackDamage", 0D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.ghast_tear), 0.25F).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.gunpowder), 0.5F).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.glowstone_dust), 0.5F).setMinAmount(4).setMaxAmount(16));
        this.drops.add(new DropRate(new ItemStack(Items.ender_eye), 0.25F).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("arcanelaserstormcharge")), 0.25F));
	}

    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== On Damage ==========
    /** Called when this mob has received damage. **/
    public void onDamage(DamageSource damageSrc, float damage) {
    	super.onDamage(damageSrc, damage);
    	
    	Entity damageEntity = damageSrc.getSourceOfDamage();
    	if(damageEntity != null && ("mob".equals(damageSrc.damageType) || "player".equals(damageSrc.damageType))) {
    		
    		// Eat Buffs:
        	if(damageEntity instanceof EntityLivingBase) {
        		EntityLivingBase targetLiving = (EntityLivingBase)damageEntity;
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
        		if(goodEffects.size() > 0 && this.getRNG().nextBoolean()) {
        			if(goodEffects.size() > 1)
        				targetLiving.removePotionEffect(goodEffects.get(this.getRNG().nextInt(goodEffects.size())));
        			else
        				targetLiving.removePotionEffect(goodEffects.get(0));
    		    	float leeching = damage * 1.1F;
    		    	this.heal(leeching);
        		}
        	}
    	}
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
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
   	//                      Attacks
   	// ==================================================
	// ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityArcaneLaserStorm projectile = new EntityArcaneLaserStorm(this.worldObj, this);
        projectile.setProjectileScale(1f);
    	
    	// Y Offset:
    	projectile.posY -= this.height * 0.5D;
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX;
        double d1 = target.posY - projectile.posY;
        double d2 = target.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.1F;
        float velocity = 0.5F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 0.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(projectile);

        super.rangedAttack(target, range);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isDamageEntityApplicable(Entity entity) {
    	if(entity instanceof EntityBeholder)
    		return false;
    	return super.isDamageEntityApplicable(entity);
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
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness(float par1) {
        if(justAttacked())
        	return 1.0F;
        else
        	return super.getBrightness(par1);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender(float par1) {
        if(justAttacked())
        	return 15728880;
        else
        	return super.getBrightnessForRender(par1);
    }
}
