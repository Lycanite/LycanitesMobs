package com.lycanitesmobs.core.model.template;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObj;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelTemplateAquatic extends ModelObj {

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
        if(partName.equals("body")) {
            this.rotate(0, (float) -Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.05F - 0.05F), 0);
        }
        if(partName.equals("mouth")) {
            this.rotate(15F - (float)-Math.toDegrees(MathHelper.cos(loop * -0.1F) * 0.05F - 0.05F), 0.0F, 0.0F);
        }
        if(partName.equals("armleft")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.25F) * 0.1F);
            rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
        if(partName.equals("armright")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * 0.25F) * 0.1F);
            rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
        if(partName.equals("tail")) {
            rotY += (float)-Math.toDegrees(MathHelper.cos(loop * 0.25f) * 0.25F);
        }

        // Walking:
        if(entity == null || entity.isInWater()) {
            if(partName.equals("body")) {
                rotY += (float)-Math.toDegrees(MathHelper.cos(time * 0.1F) * 0.2F);
            }
        }

        // Jumping/Flying:
        if(entity != null && !entity.onGround && !entity.isInWater()) {
            if(partName.equals("wingleft")) {
                rotX = 20;
                rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
                rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
            }
            if(partName.equals("wingright")) {
                rotX = 20;
                rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
                rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F + (float)Math.PI) * 0.6F);
            }
            if(partName.equals("legleftfront") || partName.equals("legleftback"))
                rotZ -= 25;
            if(partName.equals("legrightfront") || partName.equals("legrightback"))
                rotZ += 25;
        }

        // Attack:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
            if(partName.equals("mouth")) {
                this.rotate(30.0F, 0.0F, 0.0F);
            }
        }

        // Apply Animations:
        this.angle(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
