package com.lycanitesmobs.arcticmobs.dispenser;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.arcticmobs.entity.EntityIcefireball;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorIcefire extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
        return new EntityIcefireball(world, position.getX(), position.getY(), position.getZ());
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("icefireball");
    }
}