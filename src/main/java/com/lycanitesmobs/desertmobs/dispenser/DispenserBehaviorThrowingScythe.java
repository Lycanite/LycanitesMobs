package com.lycanitesmobs.desertmobs.dispenser;

import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.core.entity.EntityProjectileRapidFire;
import com.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class DispenserBehaviorThrowingScythe extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
        return new EntityProjectileRapidFire(EntityThrowingScythe.class, world, pos.getX(), pos.getY(), pos.getZ(), 15, 5);
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
    @Override
    protected SoundEvent getDispenseSound() {
        return null;
    }
}