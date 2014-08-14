package lycanite.lycanitesmobs.infernomobs.dispenser;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorBase;
import lycanite.lycanitesmobs.infernomobs.entity.EntityScorchfireball;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

import java.util.Random;

public class DispenserBehaviorScorchfire extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World par1World, IPosition par2IPosition) {
		return new EntityScorchfireball(par1World, par2IPosition.getX(), par2IPosition.getY(), par2IPosition.getZ());
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource par1IBlockSource) {
        par1IBlockSource.getWorld().playSoundEffect(par1IBlockSource.getX(), par1IBlockSource.getY(), par1IBlockSource.getZ(), AssetManager.getSound("scorchfireball"), 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
    }
}