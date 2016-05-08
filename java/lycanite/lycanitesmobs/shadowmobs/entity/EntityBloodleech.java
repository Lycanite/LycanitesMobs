package lycanite.lycanitesmobs.shadowmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityBloodleech extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityBloodleech(World world) {
        super(world);
    }

    public EntityBloodleech(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityBloodleech(World par1World, double x, double y, double z) {
        super(par1World, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "bloodleech";
    	this.group = ShadowMobs.group;
    	this.setBaseDamage(2);
        this.knockbackChance = 0.5D;
    	
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== On Damage ==========
    @Override
    public void onDamage(EntityLivingBase target, float damage, boolean attackSuccess) {
    	if(this.getThrower() != null)
            this.getThrower().heal(this.getEffectStrength(damage));
    	super.onDamage(target, damage, attackSuccess);
    }
    
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("bloodleech");
    }
}
