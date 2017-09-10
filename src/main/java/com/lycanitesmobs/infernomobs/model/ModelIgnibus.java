package com.lycanitesmobs.infernomobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateDragon;
import com.lycanitesmobs.infernomobs.InfernoMobs;

public class ModelIgnibus extends ModelTemplateDragon {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelIgnibus() {
        this(1.0F);
    }

    public ModelIgnibus(float shadowSize) {
        // Load Model:
        this.initModel("ignibus", InfernoMobs.group, "entity/ignibus");

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }
}
