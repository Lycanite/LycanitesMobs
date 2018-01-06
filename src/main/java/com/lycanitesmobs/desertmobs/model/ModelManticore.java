package com.lycanitesmobs.desertmobs.model;

import com.lycanitesmobs.desertmobs.DesertMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelManticore extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelManticore() {
        this(1.0F);
    }
    
    public ModelManticore(float shadowSize) {
    	// Load Model:
    	this.initModel("Manticore", DesertMobs.instance.group, "entity/manticore");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("body", 0F, 0.7F, 0.0F);
    	setPartCenter("tail", 0F, 0.35F, -0.15F);
    	setPartCenter("leftwing", 0.2F, 0.7F, -0.1F);
    	setPartCenter("rightwing", -0.2F, 0.7F, -0.1F);
    	setPartCenter("leftleg", 0.1F, 0.3F, -0.0F);
    	setPartCenter("rightleg", -0.1F, 0.3F, -0.0F);
    	
    	// Trophy:
        this.trophyScale = 1.2F;
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
    	if(partName.equals("leftwing")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
		    rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
    	}
    	if(partName.equals("rightwing")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
	        rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F + (float)Math.PI) * 0.6F);
    	}
    	if(partName.equals("tail")) {
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.6F);
    	}
		float bob = -MathHelper.sin(loop * 0.2F) * 0.3F;
		if(bob < 0) bob = -bob;
		posY += bob;
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("leftleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
    	if(partName.equals("rightleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	
    	// Attacking:
    	if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked())
	    	if(partName.equals("leftleg") || partName.equals("rightleg"))
	    		rotX -= 20;
		
    	// Apply Animations:
    	translate(posX, posY, posZ);
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    }
}
