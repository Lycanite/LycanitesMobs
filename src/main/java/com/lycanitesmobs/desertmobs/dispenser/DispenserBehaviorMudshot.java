package com.lycanitesmobs.desertmobs.dispenser;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.desertmobs.entity.EntityMudshot;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorMudshot extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
        return new EntityMudshot(world, pos.getX(), pos.getY(), pos.getZ());
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
    @Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("mudshot");
    }
}