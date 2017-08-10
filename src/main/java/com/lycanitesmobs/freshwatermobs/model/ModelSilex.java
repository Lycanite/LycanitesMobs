package com.lycanitesmobs.freshwatermobs.model;

import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSilex extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelSilex() {
        this(1.0F);
    }

    public ModelSilex(float shadowSize) {
    	// Load Model:
    	this.initModel("silex", FreshwaterMobs.group, "entity/silex");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("body", 0F, 0.5F, 0F);
    	
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
    	if(partName.equals("body")) {
			rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.05F - 0.05F);
		}
    	
    	// Walking:
    	if(entity == null || entity.isInWater()) {
	    	if(partName.equals("body")) {
				rotY += (float)-Math.toDegrees(MathHelper.cos(time * 0.1F) * 0.2F);
			}
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
