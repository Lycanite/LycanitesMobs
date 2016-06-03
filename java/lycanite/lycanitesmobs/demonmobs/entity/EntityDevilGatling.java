package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDevilGatling extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;
	public int expireTime = 15;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityDevilGatling(World world) {
        super(world);
    }

    public EntityDevilGatling(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityDevilGatling(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "devilgatling";
    	this.group = DemonMobs.group;
    	this.setBaseDamage(4);
		this.pierce = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	super.onUpdate();

    	if(this.posY > this.worldObj.getHeight() + 20)
    		this.setDead();
    	
    	if(this.ticksExisted >= this.expireTime * 20)
    		this.setDead();
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	//entityLiving.addPotionEffect(new PotionEffect(MobEffects.WITHER, this.getEffectDuration(10), 0));
    	return super.entityLivingCollision(entityLiving);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("devilgatling");
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
