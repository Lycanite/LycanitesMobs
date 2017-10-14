package com.lycanitesmobs.freshwatermobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateAquatic;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import com.lycanitesmobs.freshwatermobs.renderer.LayerThresher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelAbaia extends ModelTemplateAquatic {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelAbaia() {
        this(1.0F);
    }

    public ModelAbaia(float shadowSize) {
        // Load Model:
        this.initModel("abaia", FreshwaterMobs.instance.group, "entity/abaia");

        // Looking:
        this.lookHeadScaleX = 0.5f;
        this.lookHeadScaleY = 0.5f;

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
        float posX = 0F;
        float posY = 0F;
        float posZ = 0F;
        float angleX = 0F;
        float angleY = 0F;
        float angleZ = 0F;
        float rotation = 0F;
        float rotX = 0F;
        float rotY = 0F;
        float rotZ = 0F;

        // Tail:
        if(partName.equals("body") || partName.equals("tail01") || partName.equals("tail02")) {
            rotX += (float)-Math.toDegrees(MathHelper.cos(loop * 0.25f) * 0.25F);
            rotY += (float)-Math.toDegrees(MathHelper.cos(loop * 0.5f) * 0.25F);
        }

        // Apply Animations:
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
