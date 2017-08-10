package com.lycanitesmobs.shadowmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelChupacabra extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelChupacabra() {
        this(1.0F);
    }

    public ModelChupacabra(float shadowSize) {
    	// Load Model:
    	this.initModel("chupacabra", ShadowMobs.group, "entity/chupacabra");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.81F, 0.39F);
        setPartCenter("mouth", 0F, 0.65F, 0.8F);

    	setPartCenter("body", 0F, 0.7F, 0F);
    	
    	setPartCenter("armleft", 0.39F, 0.7F, 0.12F);
        setPartCenter("armright", -0.39F, 0.7F, 0.12F);

    	setPartCenter("legleft", 0.25F, 0.55F, -0.5F);
    	setPartCenter("legright", -0.25F, 0.55F, -0.5F);
    	
    	setPartCenter("tailleft", 0.1F, 0.5F, -0.74F);
        setPartCenter("tailright", -0.1F, 0.5F, -0.74F);

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.0F};
    }
    
    
    // ==================================================
   	//                    Animate Part
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

        // Looking (Mouth):
        if(partName.equals("mouth")) {
            this.centerPartToPart("mouth", "head");
            if(!lockHeadX)
                this.rotate((float)Math.toDegrees(lookX / (180F / (float)Math.PI)), 0, 0);
            if(!lockHeadY)
                this.rotate(0, (float)Math.toDegrees(lookY / (180F / (float)Math.PI)), 0);
            this.uncenterPartToPart("mouth", "head");
        }
    	
    	// Idle:
        if(partName.equals("mouth")) {
            this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.1F - 0.1F), 0.0F, 0.0F);
        }
    	if(partName.equals("tailleft")) {
    		rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
    		rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    	}
        if(partName.equals("tailright")) {
            rotX = (float)Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
            rotY = (float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
        }
		
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("armright") || partName.equals("legleft"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("armleft") || partName.equals("legright"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);

        // Attack:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
            if(partName.equals("armleft"))
                rotate(-75.0F, 0.0F, 0.0F);
            if(partName.equals("armright"))
                rotate(-75.0F, 0.0F, 0.0F);
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
        if(partName.equals("head"))
            translate(-(getPartCenter(partName)[0] / 2), -(getPartCenter(partName)[1] / 2), -(getPartCenter(partName)[2] / 2));
        else
            super.childScale(partName);
    }
}
