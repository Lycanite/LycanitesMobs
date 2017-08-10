package com.lycanitesmobs.infernomobs.model;

import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelKhalk extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelKhalk() {
        this(1.0F);
    }
    
    public ModelKhalk(float shadowSize) {
    	// Load Model:
    	this.initModel("Khalk", InfernoMobs.group, "entity/khalk");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.4F, 2.7F);
    	setPartCenter("body", 0F, 2.0F, 0F);
    	
    	setPartCenter("legleftfront", 1.8F, 1.3F, 1F);
    	setPartCenter("legleftback", 1.8F, 1.3F, -1.4F);
    	setPartCenter("legrightfront", -1.8F, 1.3F, 1F);
    	setPartCenter("legrightback", -1.8F, 1.3F, -1.4F);
    	
    	setPartCenter("tail", 0F, 1.2F, -3.5F);

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.0F};
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
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }
}
