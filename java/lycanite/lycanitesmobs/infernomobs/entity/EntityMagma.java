package lycanite.lycanitesmobs.infernomobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityMagma extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityMagma(World par1World) {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityMagma(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityMagma(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "Magma";
    	this.mod = InfernoMobs.instance;
    	this.setBaseDamage(6);
    	this.setProjectileScale(4F);
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(5));
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(int x, int y, int z) {
    	if(this.worldObj.getBlockId(x, y, z) == Block.snow.blockID)
    		return true;
    	if(this.worldObj.getBlockId(x, y, z) == Block.tallGrass.blockID)
    		return true;
    	if(this.worldObj.getBlockId(x, y, z) == Block.fire.blockID)
    		return true;
    	if(this.worldObj.getBlockId(x, y, z) == Block.web.blockID)
    		return true;
    	if(ObjectManager.getBlock("PoisonCloud") != null && this.worldObj.getBlockId(x, y, z) == ObjectManager.getBlock("PoisonCloud").blockID)
    		return true;
    	if(ObjectManager.getBlock("Frostweb") != null && this.worldObj.getBlockId(x, y, z) == ObjectManager.getBlock("Frostweb").blockID)
    		return true;
    	if(ObjectManager.getBlock("Hellfire") != null && this.worldObj.getBlockId(x, y, z) == ObjectManager.getBlock("Hellfire").blockID)
    		return true;
   	 	return super.canDestroyBlock(x, y, z);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, int x, int y, int z) {
	   	 world.setBlock(x, y, z, Block.lavaMoving.blockID, 12, 3);
	   	 if(this.canDestroyBlock(x + 1, y, z))
	   		 world.setBlock(x + 1, y, z, Block.lavaMoving.blockID, 11, 3);
	   	 if(this.canDestroyBlock(x - 1, y, z))
		   	 world.setBlock(x - 1, y, z, Block.lavaMoving.blockID, 11, 3);
	   	 if(this.canDestroyBlock(x, y, z + 1))
		   	 world.setBlock(x, y, z + 1, Block.lavaMoving.blockID, 11, 3);
	   	 if(this.canDestroyBlock(x, y, z - 1))
		   	 world.setBlock(x, y, z - 1, Block.lavaMoving.blockID, 11, 3);
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
 	//                      Sounds
 	// ==================================================
    @Override
    public String getLaunchSound() {
    	return AssetManager.getSound("Magma");
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
