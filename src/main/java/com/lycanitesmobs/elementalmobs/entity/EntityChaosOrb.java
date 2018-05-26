package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileModel;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityChaosOrb extends EntityProjectileModel {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityChaosOrb(World world) {
        super(world);
    }

    public EntityChaosOrb(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityChaosOrb(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "chaosorb";
    	this.group = ElementalMobs.instance.group;
    	this.setBaseDamage(2);
    	this.setProjectileScale(1F);
        this.knockbackChance = 0D;
        this.projectileLife = 100;
    	
    	this.waterProof = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	super.onUpdate();
    	if(this.posY > this.getEntityWorld().getHeight() + 20) {
			this.setDead();
		}
		if(!this.getEntityWorld().isRemote && this.updateTick % 5 == 0) {
    		this.addVelocity((0.5D - this.rand.nextDouble()) * 0.5D, (0.5D - this.rand.nextDouble()) * 1D, (0.5D - this.rand.nextDouble()) * 0.5D);
		}
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.001F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
	//========== Entity Living Collision ==========
	@Override
	public boolean onEntityLivingDamage(EntityLivingBase entityLiving) {
		if(ObjectManager.getPotionEffect("instability") != null) {
			entityLiving.addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("instability"), this.getEffectDuration(5), 0));
		}
		return true;
	}

    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {

    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("chaosorb");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
