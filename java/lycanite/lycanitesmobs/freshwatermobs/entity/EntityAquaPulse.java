package lycanite.lycanitesmobs.freshwatermobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAquaPulse extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityAquaPulse(World world) {
        super(world);
    }

    public EntityAquaPulse(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityAquaPulse(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "aquapulse";
    	this.group = FreshwaterMobs.group;
    	this.setBaseDamage(2);
    	this.setProjectileScale(4F);
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(ObjectManager.getPotionEffect("Penetration") != null)
            entityLiving.addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("Penetration"), this.getEffectDuration(8), 2));
        return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(BlockPos pos) {
    	return true;
    }

    public boolean canDestroyBlockSub(BlockPos pos) {
        Block block = this.worldObj.getBlockState(pos).getBlock();
    	if(block == Blocks.snow_layer)
    		return true;
    	if(block == Blocks.tallgrass)
    		return true;
    	if(block == Blocks.fire)
    		return true;
    	if(block == Blocks.web)
    		return true;
    	if(ObjectManager.getBlock("PoisonCloud") != null && block == ObjectManager.getBlock("PoisonCloud"))
    		return true;
        if(ObjectManager.getBlock("PoopCloud") != null && block == ObjectManager.getBlock("PoopCloud"))
            return true;
    	if(ObjectManager.getBlock("FrostCloud") != null && block == ObjectManager.getBlock("FrostCloud"))
    		return true;
    	if(ObjectManager.getBlock("Frostweb") != null && block == ObjectManager.getBlock("Frostweb"))
    		return true;
    	if(ObjectManager.getBlock("QuickWeb") != null && block == ObjectManager.getBlock("QuickWeb"))
    		return true;
    	if(ObjectManager.getBlock("Hellfire") != null && block == ObjectManager.getBlock("Hellfire"))
    		return true;
        if(ObjectManager.getBlock("Frostfire") != null && block == ObjectManager.getBlock("Frostfire"))
            return true;
    	if(ObjectManager.getBlock("Icefire") != null && block == ObjectManager.getBlock("Icefire"))
    		return true;
        if(ObjectManager.getBlock("Scorchfire") != null && block == ObjectManager.getBlock("Scorchfire"))
            return true;
   	 	return super.canDestroyBlock(pos);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, BlockPos pos) {
        IBlockState flowingWaterBig = Blocks.flowing_water.getStateFromMeta(12);
        IBlockState flowingWater = Blocks.flowing_water.getStateFromMeta(11);
        if(this.canDestroyBlockSub(pos))
            world.setBlockState(pos, flowingWaterBig, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())))
            world.setBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()), flowingWater, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())))
            world.setBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()), flowingWater, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)))
            world.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1), flowingWater, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)))
            world.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1), flowingWater, 3);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    		this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public String getTextureName() {
        return this.entityName.toLowerCase() + "charge";
    }
}
