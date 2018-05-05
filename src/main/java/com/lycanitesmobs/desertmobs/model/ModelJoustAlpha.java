package com.lycanitesmobs.desertmobs.model;

import com.lycanitesmobs.desertmobs.DesertMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelJoustAlpha extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelJoustAlpha() {
        this(1.0F);
    }
    
    public ModelJoustAlpha(float shadowSize) {
    	// Load Model:
    	this.initModel("JoustAlpha", DesertMobs.instance.group, "entity/joustalpha");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 2.3F, 0F);
    	setPartCenter("mouth", 0F, 2.2F, 0.4F);
    	setPartCenter("neck", 0F, 1.0F, 0F);
    	setPartCenter("body", 0F, 1.0F, 0F);
    	setPartCenter("frontleftleg", 0.2F, 0.85F, -0.2F);
    	setPartCenter("backleftleg", 0.2F, 0.85F, 0.2F);
    	setPartCenter("frontrightleg", -0.2F, 0.85F, -0.2F);
    	setPartCenter("backrightleg", -0.2F, 0.85F, 0.2F);
    	
    	lockHeadX = true;
    	lockHeadY = true;
    	
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
		
    	// Leg Angles:
    	if(partName.equals("frontleftleg") || partName.equals("backleftleg")
    			|| partName.equals("frontrightleg") || partName.equals("backrightleg"))
    		angleZ = 1F;
    	if(partName.equals("frontleftleg")) angleY = 35F / 360F;
    	if(partName.equals("backleftleg")) angleY = -35F / 360F;
    	if(partName.equals("frontrightleg")) angleY = -35F / 360F;
    	if(partName.equals("backrightleg")) angleY = 35F / 360F;
    	
    	// Walking:
    	float walkSwing = 0.3F;
    	if(partName.equals("frontrightleg") || partName.equals("backleftleg"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("frontleftleg") || partName.equals("backrightleg"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
		float bob = MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance;
		if(bob < 0) bob += -bob * 2;
		posY += bob;
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).isAttackOnCooldown()) {
	    	if(partName.equals("mouth")) {
	    		rotate(30.0F, 0.0F, 0.0F);
	    	}
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
