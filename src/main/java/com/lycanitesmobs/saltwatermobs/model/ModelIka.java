package com.lycanitesmobs.saltwatermobs.model;

import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.saltwatermobs.SaltwaterMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelIka extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelIka() {
        this(1.0F);
    }

    public ModelIka(float shadowSize) {
    	// Load Model:
    	this.initModel("ika", SaltwaterMobs.instance.group, "entity/ika");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.3F, 1.1F);
    	setPartCenter("body", 0F, 0.3F, 0F);
    	setPartCenter("shell", 0F, 0.4F, 0F);
        setPartCenter("legleft", 0.4F, 0.2F, 0.8F);
        setPartCenter("legright", -0.4F, 0.2F, 0.8F);
        setPartCenter("tail", 0F, 0.3F, -1.0F);
    	
    	// Trophy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
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
    	
    	// Idle:
        if(partName.equals("tail")) {
            rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
            rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
        }

        // Walking:
        float walkSwing = 0.2F;
        if(partName.equals("legleft")) {
            rotY += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float) Math.PI) * walkSwing * distance);
        }
        if(partName.equals("legright")) {
            rotY += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
        }
		
		// Shell:
		if(entity != null && partName.equals("shell") && entity.getHealth() <= entity.getMaxHealth() / 2) {
	    	this.scale(0, 0, 0);
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
