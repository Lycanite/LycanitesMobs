package com.lycanitesmobs.core.entity.navigate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessorCustom extends WalkNodeProcessor {

    @Override
    public void initProcessor(IBlockAccess sourceIn, EntityLiving mob) {
        super.initProcessor(sourceIn, mob);
    }

    public void updateEntitySize(Entity updateEntity) {
        this.entitySizeX = MathHelper.floor_float(updateEntity.width + 1.0F);
        this.entitySizeY = MathHelper.floor_float(updateEntity.height + 1.0F);
        this.entitySizeZ = MathHelper.floor_float(updateEntity.width + 1.0F);
    }
}
