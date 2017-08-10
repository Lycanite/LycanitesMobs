package com.lycanitesmobs.saltwatermobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.saltwatermobs.SaltwaterMobs;
import com.lycanitesmobs.saltwatermobs.renderer.LayerRoa;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class ModelRoa extends ModelCustomObj {

    // ==================================================
    //                    Constructors
    // ==================================================
    public ModelRoa() {
        this(1.0F);
    }

    public ModelRoa(float shadowSize) {
        // Load Model:
        this.initModel("roa", SaltwaterMobs.group, "entity/roa");




        // Set Rotation Centers:
        setPartCenter("head", 0F, 0F, 1.3F);
        setPartCenter("mouth", 0F, 0.25F, 2.1F);
        setPartCenter("body", 0F, 0F, 0F);
        setPartCenter("tail", 0F, 0F, -1.2F);
        setPartCenter("armleft", 0.6F, 0F, 0F);
        setPartCenter("armright", -0.6F, 0F, 0F);
        setPartCenter("effect", 0F, 0F, 0F);

        // Lock Head:
        this.lockHeadX = false;
        this.lockHeadY = false;

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
        renderer.addLayer(new LayerRoa(renderer));
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
            return layer instanceof LayerRoa;
        }
        return super.canRenderPart(partName, entity, layer, trophy);
    }


    // ==================================================
    //                 Animate Part
    // ==================================================
    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
        super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
        float pi = (float)Math.PI;
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

        // Looking:
        if(partName.equals("mouth")) {
            this.centerPartToPart(partName, "head");
            if(!this.lockHeadX)
                this.rotate((float)Math.toDegrees(lookX / (180F / (float)Math.PI)), 0, 0);
            if(!this.lockHeadY)
                this.rotate(0, (float)Math.toDegrees(lookY / (180F / (float)Math.PI)), 0);
            this.uncenterPartToPart(partName, "head");
        }

        // Idle:
        this.centerPartToPart(partName, "body");
        this.rotate(0, (float)-Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.05F - 0.05F), 0);
        this.uncenterPartToPart(partName, "body");

        if(partName.equals("mouth")) {
            this.subCenterPart("mouth");
            this.rotate(15F - (float)-Math.toDegrees(MathHelper.cos(loop * -0.1F) * 0.05F - 0.05F), 0.0F, 0.0F);
            this.unsubCenterPart("mouth");
        }
        if(partName.equals("armleft")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
            rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
        if(partName.equals("armright")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
            rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }

        // Walking:
        if(entity == null || entity.isInWater()) {
            if(partName.equals("body")) {
                rotY += (float)-Math.toDegrees(MathHelper.cos(time * 0.1F) * 0.2F);
            }
        }

        // Attack:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
            if(partName.equals("mouth")) {
                this.rotate(30.0F, 0.0F, 0.0F);
            }
        }

        // Effect:
        if(partName.contains("effect")) {
            rotY += loop * 30;
            float effectScale = 1.05F + ((0.5F + (MathHelper.sin(loop / 4) / 2)) / 8);
            this.scale(effectScale, effectScale, effectScale);
        }

        // Apply Animations:
        this.rotate(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }


    // ==================================================
    //              Rotate and Translate
    // ==================================================
    @Override
    public void childScale(String partName) {
        if(partName.equals("head") || partName.equals("mouth"))
            translate(-(getPartCenter(partName)[0] / 2), -(getPartCenter(partName)[1] / 2), -(getPartCenter(partName)[2] / 2));
        else
            super.childScale(partName);
    }
}
