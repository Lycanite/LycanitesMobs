package lycanite.lycanitesmobs.infernomobs.dispenser;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import lycanite.lycanitesmobs.core.entity.EntityProjectileRapidFire;
import lycanite.lycanitesmobs.infernomobs.entity.EntityEmber;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorEmber extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
		return new EntityProjectileRapidFire(EntityEmber.class, world, position.getX(), position.getY(), position.getZ(), 100, 5);
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("ember");
    }
}