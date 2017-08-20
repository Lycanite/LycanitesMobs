package com.lycanitesmobs.freshwatermobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateAquatic;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;

public class ModelIoray extends ModelTemplateAquatic {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelIoray() {
        this(1.0F);
    }

    public ModelIoray(float shadowSize) {
        // Load Model:
        this.initModel("ioray", FreshwaterMobs.group, "entity/ioray");

        // Looking:
        this.lookHeadScaleX = 0f;
        this.lookHeadScaleY = 0f;

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }
}
