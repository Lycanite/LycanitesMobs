package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

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
    public void onDamage(EntityLivingBase target, float damage, boolean attackSuccess) {
    	super.onDamage(target, damage, attackSuccess);
        // Remove Buffs:
        if(this.rand.nextBoolean()) {
            List<Potion> goodEffects = new ArrayList<Potion>();
            for (Object potionEffectObj : target.getActivePotionEffects()) {
                if (potionEffectObj instanceof PotionEffect) {
                    Potion potion = ((PotionEffect) potionEffectObj).getPotion();
                    if (potion != null) {
                        if (ObjectLists.inEffectList("buffs", potion))
                            goodEffects.add(potion);
                    }
                }
            }
            if (!goodEffects.isEmpty()) {
                if (goodEffects.size() > 1)
                    target.removePotionEffect(goodEffects.get(this.rand.nextInt(goodEffects.size())));
                else
                    target.removePotionEffect(goodEffects.get(0));
            }
        }
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
