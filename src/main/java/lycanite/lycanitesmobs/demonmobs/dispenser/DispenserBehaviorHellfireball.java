package lycanite.lycanitesmobs.demonmobs.dispenser;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireball;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorHellfireball extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
        return new EntityHellfireball(world, position.getX(), position.getY(), position.getZ());
    }
    

    // ==================================================
    //                        Sound
    // ==================================================
    @Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("hellfireball");
    }
}