package lycanite.lycanitesmobs.forestmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileLaser;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLifeDrain extends EntityProjectileLaser {

    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityLifeDrain(World par1World) {
		super(par1World);
	}

	public EntityLifeDrain(World par1World, double par2, double par4, double par6, int setTime, int setDelay) {
		super(par1World, par2, par4, par6, setTime, setDelay);
	}

	public EntityLifeDrain(World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay) {
		super(par1World, par2EntityLivingBase, setTime, setDelay);
	}

	public EntityLifeDrain(World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay, EntityLivingBase followEntity) {
		super(par1World, par2EntityLivingBase, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "lifedrain";
    	this.group = ForestMobs.group;
    	this.setBaseDamage(3);
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setRange(16.0F);
        this.setLaserWidth(3.0F);
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    @Override
    public Class getLaserEndClass() {
        return EntityLifeDrainEnd.class;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public void updateDamage(Entity targetEntity) {
        float damage = this.getDamage(targetEntity);
    	targetEntity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
        if(this.getThrower() != null)
            this.getThrower().heal(damage);
    }
    
	
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getBeamTexture() {
    	if(AssetManager.getTexture(this.entityName + "Beam") == null)
    		AssetManager.addTexture(this.entityName + "Beam", this.group, "textures/items/" + this.entityName.toLowerCase() + "_beam.png");
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
