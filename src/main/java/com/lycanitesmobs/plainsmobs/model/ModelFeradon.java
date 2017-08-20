package com.lycanitesmobs.plainsmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateQuadruped;
import com.lycanitesmobs.plainsmobs.PlainsMobs;

public class ModelFeradon extends ModelTemplateQuadruped {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelFeradon() {
        this(1.0F);
    }

    public ModelFeradon(float shadowSize) {
        // Load Model:
        this.initModel("feradon", PlainsMobs.group, "entity/feradon");

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }
}
