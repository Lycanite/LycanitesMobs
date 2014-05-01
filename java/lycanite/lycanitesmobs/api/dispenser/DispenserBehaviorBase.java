package lycanite.lycanitesmobs.api.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DispenserBehaviorBase extends BehaviorProjectileDispense {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack) {
        World world = blockSource.getWorld();
        IPosition position = BlockDispenser.func_149939_a(blockSource); // getIPositionFromBlockSource()
        EnumFacing facing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata()); // getFacing()
        
        IProjectile iprojectile = this.getProjectileEntity(world, position);
        if(iprojectile == null)
        	return itemStack;
        
        iprojectile.setThrowableHeading((double)facing.getFrontOffsetX(), (double)facing.getFrontOffsetY(), (double)facing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
        world.spawnEntityInWorld((Entity)iprojectile);
        itemStack.splitStack(1);
        
        return itemStack;
    }
    
	@Override
    protected IProjectile getProjectileEntity(World par1World, IPosition par2IPosition) {
        return null;
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource par1IBlockSource) {
        return;
    }
}