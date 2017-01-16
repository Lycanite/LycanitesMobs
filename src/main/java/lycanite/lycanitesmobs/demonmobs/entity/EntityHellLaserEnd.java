package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.entity.EntityProjectileLaser;
import lycanite.lycanitesmobs.core.entity.EntityProjectileLaserEnd;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityHellLaserEnd extends EntityProjectileLaserEnd {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityHellLaserEnd(World world) {
        super(world);
    }

    public EntityHellLaserEnd(World world, double par2, double par4, double par6, EntityProjectileLaser laser) {
        super(world, par2, par4, par6, laser);
    }
    
    public EntityHellLaserEnd(World world, EntityLivingBase shooter, EntityProjectileLaser laser) {
        super(world, shooter, laser);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "helllaser";
    	this.group = DemonMobs.group;
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
    		AssetManager.addTexture(this.entityName + "End", this.group, "textures/items/" + this.entityName.toLowerCase() + "_end.png");
    	return AssetManager.getTexture(this.entityName + "End");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public SoundEvent getLaunchSound() {
    	return AssetManager.getSound(entityName);
	}
}
