package com.lycanitesmobs.mountainmobs.model;

import com.lycanitesmobs.mountainmobs.MountainMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelJabberwock extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelJabberwock() {
        this(1.0F);
    }
    
    public ModelJabberwock(float shadowSize) {
    	// Load Model:
    	this.initModel("jabberwock", MountainMobs.instance.group, "entity/jabberwock");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 2.2F, 0.1F);
    	setPartCenter("mouth", 0F, 2.25F, 0.8F);
    	setPartCenter("body", 0F, 2.2F, 0.2F);
    	setPartCenter("leftarm", 0.35F, 1.95F, 0F);
    	setPartCenter("rightarm", -0.35F, 1.95F, 0F);
    	setPartCenter("leftleg", 0.3F, 1.0F, -0.15F);
    	setPartCenter("rightleg", -0.3F, 1.0F, -0.15F);
    	
    	// Trophy:
        this.trophyScale = 0.8F;
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
    		this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    	}
    	if(partName.equals("leftarm")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("rightarm")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("tail")) {
    		rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
    		rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    	}
    	
    	// Walking:
    	if(entity == null || entity.onGround || entity.isInWater()) {
	    	float walkSwing = 0.6F;
	    	if(partName.equals("leftarm")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
				rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("rightarm")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.0F * distance * 0.5F);
				rotZ += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("leftleg"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
	    	if(partName.equals("rightleg"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	}
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("leftarm") || partName.equals("rightarm"))
	    		rotX += 20.0F;
	    	if(partName.equals("mouth"))
	    		rotX += 20.0F;
		}
		
		// Jump:
		if(entity != null && !entity.onGround && !entity.isInWater()) {
	    	if(partName.equals("leftarm")) {
		        rotZ -= 10;
		        rotX -= 50;
	    	}
	    	if(partName.equals("rightarm")) {
		        rotZ += 10;
		        rotX -= 50;
	    	}
	    	if(partName.equals("leftleg"))
	    		rotX += 50;
	    	if(partName.equals("rightleg"))
	    		rotX += 50;
		}
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }
}
