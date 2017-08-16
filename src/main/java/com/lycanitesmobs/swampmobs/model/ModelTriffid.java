package com.lycanitesmobs.swampmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObj;
import com.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelTriffid extends ModelObj {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelTriffid() {
        this(1.0F);
    }

    public ModelTriffid(float shadowSize) {
        // Load Model:
        this.initModel("aglebemu", SwampMobs.group, "entity/aglebemu");

        // Lock Head:
        this.lockHeadX = false;
        this.lockHeadY = false;

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

        // Idle:
        if(partName.equals("mouthtop")) {
            rotX = -MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F;
        }
        if(partName.equals("mouthbottom")) {
            rotX = MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F;
        }
        float animationScaleZ = 0.09F;
        float animationScaleY = 0.07F;
        float animationScaleX = 0.05F;
        float animationDistanceZ = 0.25F;
        float animationDistanceY = 0.2F;
        float animationDistanceX = 0.15F;
        if(partName.equals("body")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("tentacleleftmiddle")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("tentaclerightfront")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("tentaclerightback")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("armleftmiddle")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("armleftback")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("armrightfront")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("armrightmiddle")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }

        // Attack:
        if((entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked())) {
            if(partName.equals("mouthtop"))
                rotX += 25F;
            if(partName.equals("mouthbottom"))
                rotX += 25F;
            if(partName.contains("head"))
                rotX -= 25F;
            if(partName.contains("body"))
                rotX += 25F;
            else {
                rotX -= 25F;
            }
        }

        // Apply Animations:
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
