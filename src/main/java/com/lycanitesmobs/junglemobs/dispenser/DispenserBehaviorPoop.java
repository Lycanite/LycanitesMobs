package com.lycanitesmobs.junglemobs.dispenser;

import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.junglemobs.entity.EntityPoop;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorPoop extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
        return new EntityPoop(world, position.getX(), position.getY(), position.getZ());
    }


    // ==================================================
    //                        Sound
    // ==================================================
    @Override
    protected SoundEvent getDispenseSound() {
        return AssetManager.getSound("poop");
    }
}