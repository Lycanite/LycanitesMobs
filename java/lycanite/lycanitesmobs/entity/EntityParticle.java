package lycanite.lycanitesmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityParticle extends EntityThrowable {
	// Particle:
	public int particleAge = 0;
	public int particleAgeMax = 20;
	public double particleGravity = 0D;
	public String texture;
	public ILycaniteMod mod;
	
    // ==================================================
    //                      Constructor
    // ==================================================
	public EntityParticle(World world, double x, double y, double z, String texture, ILycaniteMod mod) {
		super(world);
		this.posX = x;
		this.posY = y;
		this.posZ = z;
        this.lastTickPosX = x;
        this.lastTickPosY = y;
        this.lastTickPosZ = z;
		this.texture = texture;
		this.mod = mod;
	}
	
	
    // ==================================================
    //                        Init
    // ==================================================
	@Override
	protected void entityInit() {}
	
	
    // ==================================================
    //                       Update
    // ==================================================
	@Override
    public void onUpdate() {
		System.out.println("Doing something!");
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if(this.particleAge++ >= this.particleAgeMax)
            this.setDead();

        this.motionY -= 0.04D * (double)this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if(this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

	
    // ==================================================
    //                    Interaction
    // ==================================================
	public boolean canAttackWithItem() {
        return false;
    }
	
	protected boolean canTriggerWalking() {
        return false;
    }
	
	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		return;
	}
	
	
    // ==================================================
    //                        NBT
    // ==================================================
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}
	
	
    // ==================================================
    //                      Visuals
    // ==================================================
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.texture) == null)
    		AssetManager.addTexture(this.texture, this.mod.getDomain(), "textures/particles/" + this.texture.toLowerCase() + ".png");
    	return AssetManager.getTexture(this.texture);
    }
}
