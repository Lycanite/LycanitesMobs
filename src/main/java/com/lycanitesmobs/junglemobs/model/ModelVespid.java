package com.lycanitesmobs.junglemobs.model;

import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelVespid extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelVespid() {
        this(1.0F);
    }
    
    public ModelVespid(float shadowSize) {
    	// Load Model:
    	this.initModel("vespid", JungleMobs.group, "entity/vespid");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.1F, 0.7F);
    	setPartCenter("body", 0F, 1.0F, 0.0F);
    	
    	setPartCenter("wingleft", 0.2F, 1.3F, 0.1F);
    	setPartCenter("wingright", -0.2F, 1.3F, 0.1F);
    	
    	setPartCenter("legleftfront", 0.18F, 0.93F, 0.42F);
    	setPartCenter("legleftmiddle", 0.19F, 0.93F, 0.32F);
    	setPartCenter("legleftback", 0.21F, 0.93F, 0.20F);
    	
    	setPartCenter("legrightfront", -0.18F, 0.93F, 0.42F);
    	setPartCenter("legrightmiddle", -0.19F, 0.93F, 0.32F);
    	setPartCenter("legrightback", -0.21F, 0.93F, 0.20F);
    	
    	// Trophy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.3F};
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
    	if(partName.equals("wingleft")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 3.2F) * 0.6F);
		    rotZ -= Math.toDegrees(MathHelper.sin(loop * 3.2F) * 0.6F);
    	}
    	if(partName.equals("wingright")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 3.2F) * 0.6F);
	        rotZ -= Math.toDegrees(MathHelper.sin(loop * 3.2F + (float)Math.PI) * 0.6F);
    	}
    	if(partName.equals("legleftfront") || partName.equals("legleftback") || partName.equals("legrightmiddle")) {
    		rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("legrightfront") || partName.equals("legrightback") || partName.equals("legleftmiddle")) {
    		rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
		float bob = -MathHelper.sin(loop * 0.2F) * 0.3F;
		if(bob < 0)
			bob = -bob;
		posY += bob;
		
    	// Apply Animations:
    	translate(posX, posY, posZ);
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    }
}
