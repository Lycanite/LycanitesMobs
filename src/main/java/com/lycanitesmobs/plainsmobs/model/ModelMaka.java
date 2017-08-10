package com.lycanitesmobs.plainsmobs.model;

import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.plainsmobs.PlainsMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMaka extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelMaka() {
        this(1.0F);
    }
    
    public ModelMaka(float shadowSize) {
    	// Load Model:
    	this.initModel("Maka", PlainsMobs.group, "entity/maka");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.4F, 2.0F);
    	setPartCenter("body", 0F, 1.4F, 2.0F);
    	setPartCenter("frontleftleg", 0.9F, 1.2F, 0.9F);
    	setPartCenter("backleftleg", 0.9F, 1.2F, -0.8F);
    	setPartCenter("frontrightleg", -0.9F, 1.2F, 0.9F);
    	setPartCenter("backrightleg", -0.9F, 1.2F, -0.8F);
    	setPartCenter("tail", 0F, 1.6F, -1.5F);
    	
    	// Trophy:
        this.trophyScale = 0.6F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
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
    	float walkSwing = 0.3F;
    	if(partName.equals("frontrightleg") || partName.equals("backleftleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("frontleftleg") || partName.equals("backrightleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
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
