package lycanite.lycanitesmobs.swampmobs.dispenser;

import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.entity.EntityLaser;
import lycanite.lycanitesmobs.swampmobs.entity.EntityPoisonRay;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DispenserBehaviorPoisonRay extends BehaviorProjectileDispense {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
        World world = par1IBlockSource.getWorld();
        IPosition iposition = BlockDispenser.getIPositionFromBlockSource(par1IBlockSource);
        EnumFacing facing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
        
        double targetX = iposition.getX();
		double targetY = iposition.getY();
		double targetZ = iposition.getZ();
		
		/*if(facing.equals(EnumFacing.UP))
			targetY += 1;
		if(facing.equals(EnumFacing.DOWN))
			targetY -= 1;
		if(facing.equals(EnumFacing.NORTH))
			targetZ += 1;
		if(facing.equals(EnumFacing.SOUTH))
			targetZ -= 1;
		if(facing.equals(EnumFacing.EAST))
			targetX += 1;
		if(facing.equals(EnumFacing.WEST))
			targetX -= 1;*/
		
		IProjectile projectile = new EntityPoisonRay(world, targetX, targetY, targetZ, 5 * 20, 10);
		EntityLaser laser = (EntityLaser)projectile;
		
		if(facing.equals(EnumFacing.DOWN))
			targetY -= laser.laserRange;
		if(facing.equals(EnumFacing.UP))
			targetY += laser.laserRange;
		if(facing.equals(EnumFacing.NORTH))
			targetZ -= laser.laserRange;
		if(facing.equals(EnumFacing.SOUTH))
			targetZ += laser.laserRange;
		if(facing.equals(EnumFacing.EAST))
			targetX -= laser.laserRange;
		if(facing.equals(EnumFacing.WEST))
			targetX += laser.laserRange;
		
		laser.setTarget(targetX, targetY, targetZ);
        
        world.spawnEntityInWorld((Entity)laser);
        par2ItemStack.splitStack(1);
        return par2ItemStack;
    }
	
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition par2IPosition) {
		return null;
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource par1IBlockSource) {
        par1IBlockSource.getWorld().playSoundEffect(par1IBlockSource.getX(), par1IBlockSource.getY(), par1IBlockSource.getZ(), AssetManager.getSound("Sandstorm"), 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
    }
}