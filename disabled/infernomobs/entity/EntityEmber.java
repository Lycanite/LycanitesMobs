package lycanite.lycanitesmobs.infernomobs.entity;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityEmber extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityEmber(World world) {
        super(world);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityEmber(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityEmber(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "ember";
    	this.group = InfernoMobs.group;
    	this.setBaseDamage(1);
    	this.setProjectileScale(0.5F);
        this.knockbackChance = 0D;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(5) / 20);
    	return true;
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.worldObj.spawnParticle("fire", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    		this.worldObj.spawnParticle("smoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness(float par1) {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
