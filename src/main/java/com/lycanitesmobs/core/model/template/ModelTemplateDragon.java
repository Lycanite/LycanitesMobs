package com.lycanitesmobs.core.model.template;

import com.lycanitesmobs.core.model.ModelObj;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelTemplateDragon extends ModelObj {

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

        // Idle:
        if(partName.equals("mouth")) {
            this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.1F - 0.1F), 0.0F, 0.0F);
        }
        if(partName.equals("neck")) {
            this.rotate((float) -Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
        }
        if(partName.equals("tail") || partName.equals("tail01") || partName.equals("tail02")) {
            rotX += (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
            rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
        }

        // Walking:
        if(entity != null && entity.onGround) {
            float walkLoop = MathHelper.cos(time * 0.3F);
            float walkLoopRev = MathHelper.cos(time * 0.3F + (float)Math.PI);
            float walkSwing = 0.6F;
            if (partName.equals("legrightfront") || partName.equals("armrightfront01") || partName.equals("legleftback")) {
                rotX += Math.toDegrees(walkLoopRev * walkSwing * distance);
            }
            if (partName.equals("legleftfront") || partName.equals("armleftfront01") || partName.equals("legrightback")) {
                rotX += Math.toDegrees(walkLoop * walkSwing * distance);
            }
            if(partName.equals("wingleft01")) {
                rotX += Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.1F);
                rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.1F);
                rotZ += 80;
            }
            if(partName.equals("wingright01")) {
                rotX += Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.1F);
                rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.1F + (float)Math.PI) * 0.1F);
                rotZ -= 80;
            }
            if(partName.equals("wingleft02")) {
                rotZ += Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.1F);
                rotZ -= 170;
                rotX -= 10;
            }
            if(partName.equals("wingright02")) {
                rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.1F);
                rotZ += 170;
                rotX -= 10;
            }
        }

        // Jumping/Flying:
        if(entity != null && !entity.onGround) {
            float flightLoop = MathHelper.sin(loop * 0.4F);
            float flightLoopRev = MathHelper.sin(loop * 0.4F + (float)Math.PI);
            if(partName.equals("body")) {
                rotX -= 20;
                if(entity.getPassengers().isEmpty()) {
                    posY += flightLoop / 2;
                }
            }
            if(partName.equals("neck")) {
                rotX += 20;
            }
            if(partName.equals("wingleft")) {
                rotX -= Math.toDegrees(flightLoop * 0.6F);
                rotZ -= Math.toDegrees(flightLoop * 0.6F);
            }
            if(partName.equals("wingright")) {
                rotX -= Math.toDegrees(flightLoop * 0.6F);
                rotZ -= Math.toDegrees(flightLoopRev * 0.6F);
            }
            if(partName.equals("wingleft01") || partName.equals("armleft01")) {
                rotX -= Math.toDegrees(flightLoop * 0.3F);
                rotZ -= Math.toDegrees(flightLoop * 0.3F);
            }
            if(partName.equals("wingright01") || partName.equals("armright01")) {
                rotX -= Math.toDegrees(flightLoop * 0.3F);
                rotZ -= Math.toDegrees(flightLoopRev * 0.3F);
            }
            if(partName.equals("wingleft02") || partName.equals("armleft02")) {
                rotZ -= Math.toDegrees(flightLoop * 0.3F);
            }
            if(partName.equals("wingright02") || partName.equals("armright02")) {
                rotZ -= Math.toDegrees(flightLoopRev * 0.3F);
            }
            if(partName.equals("legleftfront") || partName.equals("legrightfront")) {
                rotX += 25;
                rotX += Math.toDegrees(flightLoop * 0.1F);
            }
            if(partName.equals("legleftback") || partName.equals("legrightback")) {
                rotX += 25;
                rotX -= Math.toDegrees(flightLoop * 0.1F);
            }
        }

        // Attack:
        if(partName.equals("mouth")) {
            rotX -= 15.0F * this.getAttackProgress();
        }

        // Apply Animations:
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
