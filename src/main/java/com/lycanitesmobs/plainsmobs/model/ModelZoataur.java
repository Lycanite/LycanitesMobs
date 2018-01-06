package com.lycanitesmobs.plainsmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;
import com.lycanitesmobs.plainsmobs.PlainsMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelZoataur extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelZoataur() {
        this(1.0F);
    }
    
    public ModelZoataur(float shadowSize) {
    	// Load Model:
    	this.initModel("Zoataur", PlainsMobs.instance.group, "entity/zoataur");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 2.6F, 1.4F);
    	setPartCenter("mouth", 0F, 2.4F, 1.5F);
    	setPartCenter("neck", 0F, 1.35F, 0.7F);
    	setPartCenter("body", 0F, 1.3F, 0.0F);

    	setPartCenter("leftarm", 0.35F, 2.1F, 1.2F);
    	setPartCenter("rightarm", -0.35F, 2.1F, 1.2F);
    	
    	setPartCenter("frontleftleg", 0.4F, 1.3F, 0.5F);
    	setPartCenter("backleftleg", 0.4F, 1.3F, -0.6F);
    	setPartCenter("frontrightleg", -0.4F, 1.3F, 0.5F);
    	setPartCenter("backrightleg", -0.4F, 1.3F, -0.6F);
    	
    	setPartCenter("tail", 0F, 1.4F, -1.1F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;
    	
    	// Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.2F};
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
    	if(partName.equals("mouth")) {
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    		centerPartToPart("mouth", "neck");
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    		uncenterPartToPart("mouth", "neck");
    	}
    	if(partName.equals("head")) {
    		centerPartToPart("head", "neck");
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    		uncenterPartToPart("head", "neck");
    	}
    	if(partName.equals("neck"))
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    	if(partName.equals("tail")) {
    		rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
    		rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    	}
    	if(partName.equals("leftarm")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("rightarm")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	
    	// Looking:
    	if(partName.equals("mouth")) {
    		centerPartToPart("mouth", "head");
    		if(!lockHeadX)
    			rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    		uncenterPartToPart("mouth", "head");

    		centerPartToPart("mouth", "neck");
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    		uncenterPartToPart("mouth", "neck");
    	}
    	if(partName.equals("head")) {
    		if(!lockHeadX)
    			rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    		
    		centerPartToPart("head", "neck");
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    		uncenterPartToPart("head", "neck");
    	}
    	if(partName.equals("neck")) {
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    	}
		
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("leftarm"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 2.0F * distance * 0.5F);
    	if(partName.equals("rightarm"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 2.0F * distance * 0.5F);
    	if(partName.equals("frontrightleg") || partName.equals("backleftleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("frontleftleg") || partName.equals("backrightleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("mouth")) {
	    		rotate(30.0F, 0.0F, 0.0F);
	    	}
		}
		
		// Blocking:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).isBlocking()) {
	    	if(partName.equals("leftarm")) {
	    		rotX += 45;
	    		rotY += -45;
	    	}
	    	if(partName.equals("rightarm")) {
	    		rotX += 45;
	    		rotY += 45;
	    	}
		}
		
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
