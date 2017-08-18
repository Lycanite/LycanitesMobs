package com.lycanitesmobs.arcticmobs.model;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.model.template.ModelTemplateQuadruped;

public class ModelMaug extends ModelTemplateQuadruped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelMaug() {
        this(1.0F);
    }

    public ModelMaug(float shadowSize) {
        // Load Model:
        this.initModel("maug", ArcticMobs.group, "entity/maug");

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }
}
