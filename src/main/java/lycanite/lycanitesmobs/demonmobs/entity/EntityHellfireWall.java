package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.IGroupBoss;
import lycanite.lycanitesmobs.api.IGroupDemon;
import lycanite.lycanitesmobs.core.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityHellfireWall extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireWall(World par1World) {
        super(par1World);
    }

    public EntityHellfireWall(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityHellfireWall(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "hellfirewall";
    	this.group = DemonMobs.group;
    	this.setBaseDamage(10);
    	this.setProjectileScale(20F);
        this.setSize(10F, 10F);
        this.movement = false;
        this.pierce = true;
        this.pierceBlocks = true;
        this.projectileLife = 2 * 20;
        this.animationFrameMax = 59;
        this.textureTiling = 2;
        this.noClip = true;
        this.waterProof = true;
        this.lavaProof = true;
    }

    @Override
    public boolean isBurning() { return false; }


    // ==================================================
    //                      Update
    // ==================================================
    @Override
    public void onUpdate() {
        super.onUpdate();

        Vec3d vec3 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec31 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);

        if (!this.worldObj.isRemote) {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            EntityLivingBase entitylivingbase = this.getThrower();

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = (Entity)list.get(j);
                this.onImpact(new RayTraceResult(entity));
            }
        }
    }

    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(10) / 20);
    	return true;
    }

    //========== Do Damage Check ==========
    @Override
    public boolean canDamage(EntityLivingBase targetEntity) {
        EntityLivingBase owner = this.getThrower();
        if(owner == null) {
            if(targetEntity instanceof EntityRahovart)
                return false;
            if(targetEntity instanceof IGroupDemon)
                return false;
        }
        return super.canDamage(targetEntity);
    }

    //========== On Damage ==========
    @Override
    public void onDamage(EntityLivingBase target, float damage, boolean attackSuccess) {

        // Remove Good Potion Effects:
        for(PotionEffect potionEffect : target.getActivePotionEffects().toArray(new PotionEffect[target.getActivePotionEffects().size()])) {
            if(ObjectLists.inEffectList("buffs", potionEffect.getPotion()))
                target.removePotionEffect(potionEffect.getPotion());
        }

        boolean obliterate = true;
        if(target instanceof EntityPlayer)
            obliterate = false;
        else if(target instanceof EntityTameable) {
            obliterate = !(((EntityTameable)target).getOwner() instanceof EntityPlayer);
        }
        else if(target instanceof EntityCreatureTameable) {
            obliterate = !(((EntityCreatureTameable)target).getOwner() instanceof EntityPlayer);
        }
        if(target instanceof IGroupBoss)
            obliterate = false;
        if(obliterate)
            target.setHealth(0);
        super.onDamage(target, damage, attackSuccess);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("hellfirewall");
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
