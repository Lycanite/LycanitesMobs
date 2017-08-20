package com.lycanitesmobs.freshwatermobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateAquatic;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import com.lycanitesmobs.freshwatermobs.renderer.LayerThresher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelThresher extends ModelTemplateAquatic {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelThresher() {
        this(1.0F);
    }

    public ModelThresher(float shadowSize) {
        // Load Model:
        this.initModel("thresher", FreshwaterMobs.group, "entity/thresher");

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
    //             Add Custom Render Layers
    // ==================================================
    @Override
    public void addCustomLayers(RenderCreature renderer) {
        super.addCustomLayers(renderer);
        renderer.addLayer(new LayerThresher(renderer));
    }


    // ==================================================
    //                Can Render Part
    // ==================================================
    /** Returns true if the part can be rendered on the base layer. **/
    @Override
    public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
        if("effect".equals(partName)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
        if("effect".equals(partName)) {
            return layer instanceof LayerThresher;
        }
        return super.canRenderPart(partName, entity, layer, trophy);
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

        // Effect:
        if(partName.contains("effect")) {
            rotY += loop * 30;
            float effectScale = 1.05F + ((0.5F + (MathHelper.sin(loop / 4) / 2)) / 8);
            this.scale(effectScale, effectScale, effectScale);
        }

        // Apply Animations:
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
