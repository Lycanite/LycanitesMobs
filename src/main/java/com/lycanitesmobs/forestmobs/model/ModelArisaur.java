package com.lycanitesmobs.forestmobs.model;

import com.lycanitesmobs.core.model.ModelObjOld;
import com.lycanitesmobs.forestmobs.ForestMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelArisaur extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelArisaur() {
        this(1.0F);
    }
    
    public ModelArisaur(float shadowSize) {
    	// Load Model:
    	this.initModel("Arisaur", ForestMobs.instance.group, "entity/arisaur");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 6.4F, 3.7F);
    	setPartCenter("neck", 0F, 4.0F, 1.6F);
    	setPartCenter("body", 0F, 3.0F, 0F);
    	setPartCenter("legleftfront", 1.0F, 2.8F, 1.0F);
    	setPartCenter("legleftback", 1.0F, 2.8F, -1.0F);
    	setPartCenter("legrightfront", -1.0F, 2.8F, 1.0F);
    	setPartCenter("legrightback", -1.0F, 2.8F, -1.0F);
    	setPartCenter("tail", 0F, 2.8F, -2.0F);
    	
    	lockHeadX = true;
    	lockHeadY = true;
    	
    	// Trophy:
        this.trophyScale = 0.5F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.6F};
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
		
    	// Walking:
    	float walkSwing = 0.3F;
    	if(partName.equals("legrightfront") || partName.equals("legleftback"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("legleftfront") || partName.equals("legrightback"))
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
