package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityHellfireball extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireball(World par1World) {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityHellfireball(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityHellfireball(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "hellfire";
    	this.group = DemonMobs.group;
    	this.setBaseDamage(10);
    	this.setProjectileScale(2.5F);
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(10));
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(int x, int y, int z) {
    	return true;
    }
    
    public boolean canDestroyBlockSub(int x, int y, int z) {
    	if(this.worldObj.getBlock(x, y, z) == Blocks.snow_layer)
    		return true;
    	if(this.worldObj.getBlock(x, y, z) == Blocks.tallgrass)
    		return true;
    	if(this.worldObj.getBlock(x, y, z) == Blocks.fire)
    		return true;
    	if(this.worldObj.getBlock(x, y, z) == Blocks.web)
    		return true;
    	if(ObjectManager.getBlock("PoisonCloud") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("PoisonCloud"))
    		return true;
    	if(ObjectManager.getBlock("FrostCloud") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("FrostCloud"))
    		return true;
    	if(ObjectManager.getBlock("Frostweb") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Frostweb"))
    		return true;
    	if(ObjectManager.getBlock("QuickWeb") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("QuickWeb"))
    		return true;
    	if(ObjectManager.getBlock("Hellfire") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Hellfire"))
    		return true;
        if(ObjectManager.getBlock("Frostfire") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Frostfire"))
            return true;
    	if(ObjectManager.getBlock("Icefire") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Icefire"))
    		return true;
        if(ObjectManager.getBlock("Scorchfire") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Scorchfire"))
            return true;
   	 	return super.canDestroyBlock(x, y, z);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, int x, int y, int z) {
    	if(this.canDestroyBlockSub(x, y, z))
	   		 world.setBlock(x, y, z, ObjectManager.getBlock("Hellfire"), 12, 3);
	   	if(this.canDestroyBlockSub(x + 1, y, z))
	   		 world.setBlock(x + 1, y, z, ObjectManager.getBlock("Hellfire"), 11, 3);
	   	if(this.canDestroyBlockSub(x - 1, y, z))
		   	 world.setBlock(x - 1, y, z, ObjectManager.getBlock("Hellfire"), 11, 3);
	   	if(this.canDestroyBlockSub(x, y, z + 1))
		   	 world.setBlock(x, y, z + 1, ObjectManager.getBlock("Hellfire"), 11, 3);
	   	if(this.canDestroyBlockSub(x, y, z - 1))
		   	 world.setBlock(x, y, z - 1, ObjectManager.getBlock("Hellfire"), 11, 3);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle("reddust", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public String getLaunchSound() {
    	return AssetManager.getSound("Hellfireball");
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
