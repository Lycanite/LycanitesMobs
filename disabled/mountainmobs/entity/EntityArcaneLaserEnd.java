package lycanite.lycanitesmobs.mountainmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileLaser;
import lycanite.lycanitesmobs.api.entity.EntityProjectileLaserEnd;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityArcaneLaserEnd extends EntityProjectileLaserEnd {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityArcaneLaserEnd(World world) {
        super(world);
    }

    public EntityArcaneLaserEnd(World world, double par2, double par4, double par6, EntityProjectileLaser laser) {
        super(world, par2, par4, par6, laser);
    }
    
    public EntityArcaneLaserEnd(World world, EntityLivingBase shooter, EntityProjectileLaser laser) {
        super(world, shooter, laser);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "arcanelaser";
    	this.group = MountainMobs.group;
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setSpeed(1.0D);
    }
    
	
	// ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.entityName + "end") == null)
    		AssetManager.addTexture(this.entityName + "end", this.group, "textures/items/" + this.entityName.toLowerCase() + "_end.png");
    	return AssetManager.getTexture(this.entityName + "end");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public String getLaunchSound() {
    	return null;
	}
}
