package lycanite.lycanitesmobs.mountainmobs.dispenser;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorBase;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserStorm;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

import java.util.Random;

public class DispenserBehaviorArcaneLaserStorm extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position) {
        return new EntityArcaneLaserStorm(world, position.getX(), position.getY(), position.getZ());
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource blockSource) {
        blockSource.getWorld().playSoundEffect(blockSource.getX(), blockSource.getY(), blockSource.getZ(), AssetManager.getSound("arcanelaserstorm"), 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
    }
}