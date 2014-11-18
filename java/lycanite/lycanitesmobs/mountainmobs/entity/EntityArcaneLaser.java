package lycanite.lycanitesmobs.mountainmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileLaser;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityArcaneLaser extends EntityProjectileLaser {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityArcaneLaser(World world) {
		super(world);
	}

	public EntityArcaneLaser(World world, double par2, double par4, double par6, int setTime, int setDelay) {
		super(world, par2, par4, par6, setTime, setDelay);
	}

	public EntityArcaneLaser(World world, EntityLivingBase entityLiving, int setTime, int setDelay) {
		super(world, entityLiving, setTime, setDelay);
	}

	public EntityArcaneLaser(World world, EntityLivingBase entityLiving, int setTime, int setDelay, EntityLivingBase followEntity) {
		super(world, entityLiving, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "arcanelaser";
    	this.group = MountainMobs.group;
    	this.setBaseDamage(1);
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setRange(16.0F);
        this.setLaserWidth(2.0F);
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    @Override
    public Class getLaserEndClass() {
        return EntityArcaneLaserEnd.class;
    }
    
	
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getBeamTexture() {
    	if(AssetManager.getTexture(this.entityName + "beam") == null)
    		AssetManager.addTexture(this.entityName + "beam", this.group, "textures/items/" + this.entityName.toLowerCase() + "_beam.png");
    	return AssetManager.getTexture(this.entityName + "beam");
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
