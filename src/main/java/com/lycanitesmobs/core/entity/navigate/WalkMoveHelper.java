package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityMoveHelper;

public class WalkMoveHelper extends EntityMoveHelper {
    private EntityCreatureBase parentEntity;

    public WalkMoveHelper(EntityCreatureBase entityCreatureBase) {
        super(entityCreatureBase);
        this.parentEntity = entityCreatureBase;
    }

    @Override
    public void onUpdateMoveHelper() {
        super.onUpdateMoveHelper();
    }
}
