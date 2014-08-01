package lycanite.lycanitesmobs.mountainmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityBoulderBlast extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityBoulderBlast(World par1World) {
        super(par1World);
    }

    public EntityBoulderBlast(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityBoulderBlast(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "boulderblast";
    	this.group = MountainMobs.group;
    	this.setBaseDamage(8);
    	this.setProjectileScale(4F);
    	
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	entityLiving.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, this.getEffectDuration(5), 0));
    	return true;
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle("smoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public String getLaunchSound() {
    	return AssetManager.getSound("BoulderBlast");
    }
}
