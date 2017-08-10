package com.lycanitesmobs.demonmobs.dispenser;

import com.lycanitesmobs.core.dispenser.DispenserBehaviorBase;
import com.lycanitesmobs.core.entity.EntityProjectileRapidFire;
import com.lycanitesmobs.demonmobs.entity.EntityDevilstar;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenserBehaviorDevilstar extends DispenserBehaviorBase {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
        return new EntityProjectileRapidFire(EntityDevilstar.class, world, position.getX(), position.getY(), position.getZ(), 100, 5);
    }
}