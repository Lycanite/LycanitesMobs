package lycanite.lycanitesmobs.api.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.Random;

public class DispenserBehaviorBase extends BehaviorProjectileDispense {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
        World world = blockSource.getWorld();
        IPosition position = BlockDispenser.getDispensePosition(blockSource);
        EnumFacing facing = BlockDispenser.getFacing(blockSource.getBlockMetadata());
        
        IProjectile iprojectile = this.getProjectileEntity(world, position, stack);
        if(iprojectile == null)
        	return stack;
        
        iprojectile.setThrowableHeading((double)facing.getFrontOffsetX(), (double)facing.getFrontOffsetY(), (double)facing.getFrontOffsetZ(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
        world.spawnEntityInWorld((Entity)iprojectile);
        stack.splitStack(1);
        
        return stack;
    }
    
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
        return null;
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource blockSource) {
        SoundEvent soundEvent = this.getDispenseSound();
        if(soundEvent == null)
            return;
        blockSource.getWorld().playSound(blockSource.getX(), blockSource.getY(), blockSource.getZ(), this.getDispenseSound(), SoundCategory.AMBIENT, 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F), false);
    }

    protected SoundEvent getDispenseSound() {
        return null;
    }
}