package lycanite.lycanitesmobs.desertmobs.dispenser;

import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileRapidFire;
import lycanite.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class DispenserBehaviorThrowingScythe extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World par1World, IPosition par2IPosition) {
        return new EntityProjectileRapidFire(EntityThrowingScythe.class, par1World, par2IPosition.getX(), par2IPosition.getY(), par2IPosition.getZ(), 15, 5);
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource par1IBlockSource) {
        //par1IBlockSource.getWorld().playSoundEffect(par1IBlockSource.getX(), par1IBlockSource.getY(), par1IBlockSource.getZ(), AssetManager.getSound("ThrowingScythe"), 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
    }
}