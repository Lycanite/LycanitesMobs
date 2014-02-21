package lycanite.lycanitesmobs.demonmobs.dispenser;

import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireball;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class DispenserBehaviorHellfireball extends BehaviorProjectileDispense {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World par1World, IPosition par2IPosition) {
        return new EntityHellfireball(par1World, par2IPosition.getX(), par2IPosition.getY(), par2IPosition.getZ());
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource par1IBlockSource) {
        par1IBlockSource.getWorld().playSoundEffect(par1IBlockSource.getX(), par1IBlockSource.getY(), par1IBlockSource.getZ(), AssetManager.getSound("Hellfireball"), 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
    }
}