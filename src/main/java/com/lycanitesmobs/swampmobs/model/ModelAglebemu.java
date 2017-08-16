package com.lycanitesmobs.swampmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.template.ModelTemplateQuadruped;
import com.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.EntityLiving;

public class ModelAglebemu extends ModelTemplateQuadruped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelAglebemu() {
        this(1.0F);
    }

    public ModelAglebemu(float shadowSize) {
        // Load Model:
        this.initModel("aglebemu", SwampMobs.group, "entity/aglebemu");

        // Lock Head:
        this.lockHeadX = true;
        this.lockHeadY = true;

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
        this.bodyIsTrophy = true;
    }


    // ==================================================
    //                 Animate Part
    // ==================================================
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
        super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

        // Attack:
        if(!(entity instanceof EntityCreatureBase) || !((EntityCreatureBase)entity).justAttacked()) {
            if(partName.equals("tongue")) {
                this.scale(0, 0, 0);
            }
        }
    }
}
