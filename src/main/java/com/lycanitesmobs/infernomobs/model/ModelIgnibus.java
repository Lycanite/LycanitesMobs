package com.lycanitesmobs.infernomobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateDragon;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelIgnibus extends ModelTemplateDragon {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelIgnibus() {
        this(1.0F);
    }

    public ModelIgnibus(float shadowSize) {
        // Load Model:
        this.initModel("ignibus", InfernoMobs.instance.group, "entity/ignibus");

        // Looking:
        this.lookHeadScaleX = 0.5f;
        this.lookHeadScaleY = 0.5f;
        this.lookNeckScaleX = 0.5f;
        this.lookNeckScaleY = 0.5f;

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }

    // ==================================================
    //                 Animate Part
    // ==================================================
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
        super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

        // Jumping/Flying:
        if(entity != null && !entity.onGround) {
            if (partName.equals("body")) {
                this.rotate(-20, 0,0);
            }
            if (partName.equals("neck")) {
                this.rotate(20, 0,0);
            }
        }
    }
}
