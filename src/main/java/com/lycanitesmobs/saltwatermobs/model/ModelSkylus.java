package com.lycanitesmobs.saltwatermobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.saltwatermobs.SaltwaterMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSkylus extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelSkylus() {
        this(1.0F);
    }
    
    public ModelSkylus(float shadowSize) {
    	// Load Model:
    	this.initModel("skylus", SaltwaterMobs.group, "entity/skylus");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.2F, 0.9F);
    	setPartCenter("body", 0F, 1F, 1F);
    	setPartCenter("shell", 0F, 1F, 1F);
    	setPartCenter("tentaclem", 0F, 0.7F, 0.9F);
    	setPartCenter("tentaclel1", 0.2F, 0.4F, 0.9F);
    	setPartCenter("tentaclel2", 0.4F, 0.6F, 0.9F);
    	setPartCenter("tentacler1", -0.2F, 0.4F, 0.9F);
    	setPartCenter("tentacler2", -0.4F, 0.6F, 0.9F);
    	
    	// Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.2F};
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
    	if(partName.equals("tentaclem") || partName.equals("tentaclel2") || partName.equals("tentacler2")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.4F) * 0.05F + 0.05F);
	        rotY -= Math.toDegrees(MathHelper.cos(loop * 0.05F) * 0.2F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.3F) * 0.05F);
    	}
    	if(partName.equals("tentaclel1") || partName.equals("tentacler1")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.4F) * 0.05F + 0.05F);
	        rotY += Math.toDegrees(MathHelper.cos(loop * 0.05F) * 0.2F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.3F) * 0.05F);
    	}
    	
    	// Walking:
    	if(entity == null || entity.onGround || entity.isInWater()) {
	    	float walkSwing = 0.6F;
	    	if(partName.equals("tentaclem") || partName.equals("tentaclel2") || partName.equals("tentacler2")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
				rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("tentaclel1") || partName.equals("tentacler1")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.0F * distance * 0.5F);
				rotZ += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.5F * distance * 0.5F);
	    	}
    	}
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("tentaclem") || partName.equals("tentaclel2") || partName.equals("tentacler2"))
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("tentaclel1") || partName.equals("tentacler1"))
	    		rotate(0.0F, 25.0F, 0.0F);
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
}
