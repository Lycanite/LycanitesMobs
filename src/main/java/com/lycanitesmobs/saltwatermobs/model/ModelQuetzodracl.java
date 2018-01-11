package com.lycanitesmobs.saltwatermobs.model;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.template.ModelTemplateDragon;
import com.lycanitesmobs.saltwatermobs.SaltwaterMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelQuetzodracl extends ModelTemplateDragon {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelQuetzodracl() {
        this(1.0F);
    }

    public ModelQuetzodracl(float shadowSize) {
        // Load Model:
        this.initModel("quetzodracl", SaltwaterMobs.instance.group, "entity/quetzodracl");

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
                rotX -= 15 + Math.toDegrees(walkIdle * 0.05F);
                rotY += 45 + Math.toDegrees(walkIdle * 0.05F);
                rotZ += 145 + Math.toDegrees(walkIdle * 0.05F);
            }
            if(partName.equals("armright02")) {
                rotX -= 15 + Math.toDegrees(walkIdle * 0.05F);
                rotY -= 45 + Math.toDegrees(walkIdle * 0.05F);
                rotZ -= 145 + Math.toDegrees(walkIdle * 0.05F);
            }
        }

        // Pickup:
        if (partName.equals("legleft") || partName.equals("legright")) {
            if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).hasPickupEntity()) {
                rotX -= 40D;
            }
        }

        // Apply Animations:
        this.rotate(rotX, rotY, rotZ);
    }
}
