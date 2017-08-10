package com.lycanitesmobs.swampmobs.dispenser;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.swampmobs.entity.EntityPoisonRay;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorPoisonRay extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack) {
        World world = blockSource.getWorld();
        IPosition iposition = BlockDispenser.getDispensePosition(blockSource);
        EnumFacing facing = blockSource.getBlockState().getValue(BlockDispenser.FACING);

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
		EntityProjectileLaser laser = (EntityProjectileLaser)projectile;
		
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
        itemStack.splitStack(1);
        return itemStack;
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
    @Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("poisonray");
    }
}