package com.lycanitesmobs.demonmobs.model;

import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTrite extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelTrite() {
        this(1.0F);
    }
    
    public ModelTrite(float shadowSize) {
    	// Load Model:
        this.initModel("trite", DemonMobs.group, "entity/trite");
    	
    	// Set Rotation Centers:
        this.setPartCenter("head", 0F, 0.35F, 0F);
        this.setPartCenter("frontleftleg", 0.1F, 0.3F, -0.1F);
        this.setPartCenter("middleleftleg", 0.1F, 0.3F, 0F);
        this.setPartCenter("backleftleg", 0.1F, 0.3F, 0.1F);
        this.setPartCenter("frontrightleg", -0.1F, 0.3F, -0.1F);
        this.setPartCenter("middlerightleg", -0.1F, 0.3F, 0F);
        this.setPartCenter("backrightleg", -0.1F, 0.3F, 0.1F);

        // Head:
    	this.lockHeadX = true;
    	
    	// Trophy:
        this.trophyScale = 1.0F;
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
    	
    	// Leg Angles:
    	if(partName.equals("frontleftleg") || partName.equals("backleftleg") || partName.equals("middlerightleg")
    			|| partName.equals("middleleftleg") || partName.equals("frontrightleg") || partName.equals("backrightleg"))
    		angleZ = 1F;
    	if(partName.equals("frontleftleg")) angleY = 30F / 360F;
    	if(partName.equals("middleleftleg")) angleY = 0F;
    	if(partName.equals("backleftleg")) angleY = -30F / 360F;
    	if(partName.equals("frontrightleg")) angleY = -30F / 360F;
    	if(partName.equals("middlerightleg")) angleY = 0F;
    	if(partName.equals("backrightleg")) angleY = 30F / 360F;
    	
    	// Walking:
    	float walkSwing = 0.3F;
    	if(partName.equals("frontleftleg") || partName.equals("backleftleg") || partName.equals("middlerightleg"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("middleleftleg") || partName.equals("frontrightleg") || partName.equals("backrightleg"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
		float bob = MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance;
		if(bob < 0) bob += -bob * 2;
		posY += bob;
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
