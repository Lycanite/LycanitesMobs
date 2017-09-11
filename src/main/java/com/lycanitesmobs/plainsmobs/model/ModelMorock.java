package com.lycanitesmobs.plainsmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateDragon;
import com.lycanitesmobs.plainsmobs.PlainsMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelMorock extends ModelTemplateDragon {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelMorock() {
        this(1.0F);
    }

    public ModelMorock(float shadowSize) {
        // Load Model:
        this.initModel("morock", PlainsMobs.group, "entity/morock");

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

        float rotX = 0F;
        float rotY = 0F;
        float rotZ = 0F;

        // Walking:
        if(entity != null && entity.onGround) {
            float walkIdle = MathHelper.sin(loop * 0.1F);
            if(partName.equals("armleft02")) {
                rotZ += 160 + Math.toDegrees(walkIdle * 0.05F);
            }
            if(partName.equals("armright02")) {
                rotZ -= 160 + Math.toDegrees(walkIdle * 0.05F);
            }
        }

        // Apply Animations:
        this.rotate(rotX, rotY, rotZ);
    }
}
