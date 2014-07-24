package lycanite.lycanitesmobs.arcticmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityTundra extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityTundra(World par1World) {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityTundra(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityTundra(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "tundra";
    	this.mod = ArcticMobs.instance;
    	this.setBaseDamage(6);
    	this.setProjectileScale(4F);
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, this.getEffectDuration(2), 0));
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(int x, int y, int z) {
    	if(this.worldObj.getBlock(x, y, z) == Blocks.snow_layer)
    		return true;
    	if(this.worldObj.getBlock(x, y, z) == Blocks.tallgrass)
    		return true;
    	if(this.worldObj.getBlock(x, y, z) == Blocks.fire)
    		return true;
    	if(this.worldObj.getBlock(x, y, z) == Blocks.web)
    		return true;
    	if(this.worldObj.getBlock(x, y, z) == Blocks.flowing_lava)
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
   	 	return super.canDestroyBlock(x, y, z);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, int x, int y, int z) {
    	if(ObjectManager.getBlock("Frostfire") != null) {
		   	 world.setBlock(x, y, z, ObjectManager.getBlock("Frostfire"), 12, 3);
		   	 if(this.canDestroyBlock(x + 1, y, z))
		   		 world.setBlock(x + 1, y, z, ObjectManager.getBlock("Frostfire"), 11, 3);
		   	 if(this.canDestroyBlock(x - 1, y, z))
			   	 world.setBlock(x - 1, y, z, ObjectManager.getBlock("Frostfire"), 11, 3);
		   	 if(this.canDestroyBlock(x, y, z + 1))
			   	 world.setBlock(x, y, z + 1, ObjectManager.getBlock("Frostfire"), 11, 3);
		   	 if(this.canDestroyBlock(x, y, z - 1))
			   	 world.setBlock(x, y, z - 1, ObjectManager.getBlock("Frostfire"), 11, 3);
    	}
    	
    	y++;
    	if(ObjectManager.getBlock("FrostCloud") != null) {
		   	 world.setBlock(x, y, z, ObjectManager.getBlock("FrostCloud"), 12, 3);
		   	 if(this.canDestroyBlock(x + 1, y, z))
		   		 world.setBlock(x + 1, y, z, ObjectManager.getBlock("FrostCloud"), 11, 3);
		   	 if(this.canDestroyBlock(x - 1, y, z))
			   	 world.setBlock(x - 1, y, z, ObjectManager.getBlock("FrostCloud"), 11, 3);
		   	 if(this.canDestroyBlock(x, y, z + 1))
			   	 world.setBlock(x, y, z + 1, ObjectManager.getBlock("FrostCloud"), 11, 3);
		   	 if(this.canDestroyBlock(x, y, z - 1))
			   	 world.setBlock(x, y, z - 1, ObjectManager.getBlock("FrostCloud"), 11, 3);
    	}
    	
    	Block blockBase = world.getBlock(x, y, z);
    	if(blockBase == Blocks.dirt || blockBase == Blocks.grass)
    		world.setBlock(x, y - 1, z, Blocks.snow, 0, 3);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.worldObj.spawnParticle("snowshovel", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    		this.worldObj.spawnParticle("snowshovel", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }
}
