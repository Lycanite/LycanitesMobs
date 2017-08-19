package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;

public class WalkMoveHelper extends EntityMoveHelper {
    private EntityCreatureBase entityCreature;

    public WalkMoveHelper(EntityCreatureBase entityCreatureBase) {
        super(entityCreatureBase);
        this.entityCreature = entityCreatureBase;
    }

    @Override
    public void onUpdateMoveHelper() {
        if(this.entityCreature != null && this.entityCreature.getControllingPassenger() instanceof EntityPlayer)
            return;
        super.onUpdateMoveHelper();
    }
}
