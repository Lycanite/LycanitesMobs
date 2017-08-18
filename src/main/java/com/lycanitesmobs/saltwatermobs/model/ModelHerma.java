package com.lycanitesmobs.saltwatermobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateArachnid;
import com.lycanitesmobs.saltwatermobs.SaltwaterMobs;
import net.minecraft.entity.EntityLiving;

public class ModelHerma extends ModelTemplateArachnid {
    protected float walkingAngle = 0;
    protected int walkingAngleChangeTime = 0;
    protected float walkingRotateAmount = 0;

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelHerma() {
        this(1.0F);
    }

    public ModelHerma(float shadowSize) {
        // Load Model:
        this.initModel("herma", SaltwaterMobs.group, "entity/herma");

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

        // Random Walk Rotation:
        if(entity != null && distance > 0) {
            if (this.walkingAngleChangeTime <= 0) {
                float random = entity.getRNG().nextFloat();
                if(random <= 0.3f)
                    this.walkingAngle = 90;
                else if(random <= 0.3f)
                    this.walkingAngle = -90;
                else
                    this.walkingAngle = 0;
                this.walkingRotateAmount = 0;
            }
            if(partName.equals("body")) {
                float walkingAngleScale = 1;
                if(this.walkingRotateAmount < 20) {
                    walkingAngleScale = this.walkingRotateAmount / 20;
                    this.walkingRotateAmount++;
                }
                this.rotate(0, this.walkingAngle * walkingAngleScale, 0);
            }
        }
    }
}
