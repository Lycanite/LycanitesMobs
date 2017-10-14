package com.lycanitesmobs.swampmobs.model;

import com.lycanitesmobs.LycanitesMobs;
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
        this.initModel("triffid", SwampMobs.instance.group, "entity/triffid");

        // Looking:
        this.lookHeadScaleX = 0.5f;
        this.lookHeadScaleY = 0.5f;
        this.lookBodyScaleX = 0.5f;
        this.lookBodyScaleY = 0.5f;

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
            rotX -= MathHelper.cos(loop * 0.2F) * 4F;
        }
        if(partName.equals("mouthbottom")) {
            rotX += MathHelper.cos(loop * 0.2F) * 4F;
        }

        float animationScaleZ = 0.09F;
        float animationScaleY = 0.07F;
        float animationScaleX = 0.05F;
        float animationDistanceZ = 0.25F;
        float animationDistanceY = 0.2F;
        float animationDistanceX = 0.15F;
        if(partName.equals("body")) {
            rotZ += (animationDistanceZ / 2) - Math.toDegrees(MathHelper.cos(loop * 0.02F) * (animationDistanceZ / 2));
            rotX += (animationDistanceX / 2) - Math.toDegrees(MathHelper.sin(loop * 0.01F) * (animationDistanceX / 2));
        }
        if(partName.equals("tentacleleftmiddle")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("tentaclerightfront")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("tentaclerightback")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("armleftmiddle")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("armleftfront")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("armrightback")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }
        if(partName.equals("armrightmiddle")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
            rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
            rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
        }

        // Attack:
        if(partName.contains("body"))
            rotX += 25F * this.getAttackProgress();
        else if(partName.contains("head"))
            rotX -= 25F * this.getAttackProgress();
        else if(partName.equals("mouthtop"))
            rotX -= 10F * this.getAttackProgress();
        else if(partName.equals("mouthbottom"))
            rotX += 10F * this.getAttackProgress();
        else
            rotX -= 25F * this.getAttackProgress();

        // Apply Animations:
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
