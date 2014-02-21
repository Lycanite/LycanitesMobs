package lycanite.lycanitesmobs.swampmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.entity.EntityLaser;
import lycanite.lycanitesmobs.entity.EntityLaserEnd;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityPoisonRayEnd extends EntityLaserEnd {
	public String entityName = "PoisonRay"; // Should be the same as the start entity.
	public ILycaniteMod mod = SwampMobs.instance;
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityPoisonRayEnd(World world) {
        super(world);
    }

    public EntityPoisonRayEnd(World world, double par2, double par4, double par6, EntityLaser laser) {
        super(world, par2, par4, par6, laser);
    }
    
    public EntityPoisonRayEnd(World world, EntityLivingBase shooter, EntityLaser laser) {
        super(world, shooter, laser);
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
    	if(AssetManager.getTexture(this.entityName + "End") == null)
    		AssetManager.addTexture(this.entityName + "End", this.mod.getDomain(), "textures/items/" + this.entityName.toLowerCase() + "_end.png");
    	return AssetManager.getTexture(this.entityName + "End");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public String getLaunchSound() {
    	return AssetManager.getSound(entityName);
	}
}
