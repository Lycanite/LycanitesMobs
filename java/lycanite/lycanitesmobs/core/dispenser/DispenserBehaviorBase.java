package lycanite.lycanitesmobs.core.dispenser;

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
        EnumFacing facing = blockSource.getBlockState().getValue(BlockDispenser.FACING);
        
        IProjectile iprojectile = this.getProjectileEntity(world, position, stack);
        if(iprojectile == null)
        	return stack;
        
        iprojectile.setThrowableHeading((double)facing.getFrontOffsetX(), (double)facing.getFrontOffsetY(), (double)facing.getFrontOffsetZ(), this.getProjectileVelocity(), this.getProjectileInaccuracy());
        world.spawnEntity((Entity)iprojectile);
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
        if(soundEvent == null || blockSource == null)
            return;
        blockSource.getWorld().playSound(null, blockSource.getBlockPos(), soundEvent, SoundCategory.AMBIENT, 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
    }

    protected SoundEvent getDispenseSound() {
        return null;
    }
}