package lycanite.lycanitesmobs.shadowmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntitySpectralbolt extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntitySpectralbolt(World world) {
        super(world);
    }

    public EntitySpectralbolt(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntitySpectralbolt(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "spectralbolt";
    	this.group = ShadowMobs.group;
    	this.setBaseDamage(2);
    	
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(ObjectManager.getPotionEffect("fear") != null)
    		entityLiving.addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("fear"), this.getEffectDuration(2), 0));
    	return true;
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("Spectralbolt");
    }
}
