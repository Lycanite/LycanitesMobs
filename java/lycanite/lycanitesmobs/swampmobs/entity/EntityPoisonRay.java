package lycanite.lycanitesmobs.swampmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.ICustomProjectile;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.entity.EntityLaser;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityPoisonRay extends EntityLaser implements ICustomProjectile {
	public String entityName = "PoisonRay";
	public ILycaniteMod mod = SwampMobs.instance;
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityPoisonRay(World par1World) {
		super(par1World);
	}

	public EntityPoisonRay(World par1World, double par2, double par4, double par6, int setTime, int setDelay) {
		super(par1World, par2, par4, par6, setTime, setDelay);
	}

	public EntityPoisonRay(World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay) {
		super(par1World, par2EntityLivingBase, setTime, setDelay);
	}

	public EntityPoisonRay(World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay, EntityLivingBase followEntity) {
		super(par1World, par2EntityLivingBase, setTime, setDelay, followEntity);
	}
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setRange(16.0F);
        this.setDamage(3);
        this.setLaserWidth(2.0F);
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    @Override
    public Class getLaserEndClass() {
        return EntityPoisonRayEnd.class;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public void updateDamage(Entity targetEntity) {
    	targetEntity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float)damage);
		if(targetEntity instanceof EntityLivingBase)
			((EntityLivingBase)targetEntity).addPotionEffect(new PotionEffect(Potion.poison.id, 5 * 20, 0));
    }
    
	
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.entityName) == null)
    		AssetManager.addTexture(this.entityName, this.mod.getDomain(), "textures/items/" + this.entityName.toLowerCase() + ".png");
    	return AssetManager.getTexture(this.entityName);
    }

    @Override
    public ResourceLocation getBeamTexture() {
    	if(AssetManager.getTexture(this.entityName + "Beam") == null)
    		AssetManager.addTexture(this.entityName + "Beam", this.mod.getDomain(), "textures/items/" + this.entityName.toLowerCase() + "_beam.png");
    	return AssetManager.getTexture(this.entityName + "Beam");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public String getLaunchSound() {
    	return AssetManager.getSound(entityName);
    }
	
	@Override
	public String getBeamSound() {
    	return AssetManager.getSound(entityName);
	}
}
