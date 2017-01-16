package lycanite.lycanitesmobs.infernomobs.dispenser;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import lycanite.lycanitesmobs.infernomobs.entity.EntityMagma;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorMagma extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
		return new EntityMagma(world, position.getX(), position.getY(), position.getZ());
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("magma");
    }
}