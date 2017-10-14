package com.lycanitesmobs.infernomobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObj;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelGorger extends ModelObj {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelGorger() {
        this(1.0F);
    }

    public ModelGorger(float shadowSize) {
        // Load Model:
        this.initModel("gorger", InfernoMobs.instance.group, "entity/gorger");

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
        float scaleX = 1F;
        float scaleY = 1F;
        float scaleZ = 1F;

        // No Gorger trophy animation due to head offset.
        if(scale < 0)
            return;

        // Idle:
        if(partName.equals("mouthtop"))
            this.rotate(-(float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
        if(partName.equals("mouthleft"))
            rotY += (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F - 0.05F);
        if(partName.equals("mouthright"))
            rotY -= (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F - 0.05F);
        if(partName.equals("mouthbottom"))
            this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);

        // Attack:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
            if(partName.equals("mouthtop"))
                rotX += -20.0F;
            if(partName.equals("mouthleft"))
                rotY -= 15F;
            if(partName.equals("mouthright"))
                rotY += 15F;
            if(partName.equals("mouthbottom"))
                rotX += 20.0F;
        }

        // Walking:
        float walkSwing = 0.8F;
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).getStealth() > 0 && ((EntityCreatureBase)entity).getStealth() < 1)
            time = loop;
        time /= 2;
        if(partName.equals("head")) {
            posX += MathHelper.sin((time - walkSwing) * walkSwing) * walkSwing;
        }
        if(partName.equals("body")) {
            posX += MathHelper.sin(time * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time - walkSwing) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body01")) {
            posX += MathHelper.sin((time + walkSwing) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin(time * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body02")) {
            posX += MathHelper.sin((time + (walkSwing * 2)) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body03")) {
            posX += MathHelper.sin((time + (walkSwing * 3)) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + (walkSwing * 2)) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body04")) {
            posX += MathHelper.sin((time + (walkSwing * 4)) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + (walkSwing * 3)) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body05")) {
            posX += MathHelper.sin((time + (walkSwing * 5)) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + (walkSwing * 4)) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body06")) {
            posX += MathHelper.sin((time + (walkSwing * 6)) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + (walkSwing * 5)) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body07")) {
            posX += MathHelper.sin((time + (walkSwing * 7)) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + (walkSwing * 6)) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body08")) {
            posX += MathHelper.sin((time + (walkSwing * 8)) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + (walkSwing * 7)) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }
        if(partName.equals("body09")) {
            posX += MathHelper.sin((time + (walkSwing * 9)) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + (walkSwing * 8)) * walkSwing) * walkSwing;
            rotY += this.rotateToPoint(0, posX, -0.6F, parentX);
        }

        // Stealth:
        if(entity instanceof EntityCreatureBase)
            posY -= (2 * ((EntityCreatureBase)entity).getStealth());

        // Apply Animations:
        this.translate(posX, posY, posZ);
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.scale(scaleX, scaleY, scaleZ);
    }
}
