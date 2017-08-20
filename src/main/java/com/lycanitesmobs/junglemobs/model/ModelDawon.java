package com.lycanitesmobs.junglemobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateQuadruped;
import com.lycanitesmobs.junglemobs.JungleMobs;

public class ModelDawon extends ModelTemplateQuadruped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelDawon() {
        this(1.0F);
    }

    public ModelDawon(float shadowSize) {
        // Load Model:
        this.initModel("dawon", JungleMobs.group, "entity/dawon");

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }
}
