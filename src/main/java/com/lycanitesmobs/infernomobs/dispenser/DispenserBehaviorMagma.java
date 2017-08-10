package com.lycanitesmobs.infernomobs.dispenser;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.infernomobs.entity.EntityMagma;
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