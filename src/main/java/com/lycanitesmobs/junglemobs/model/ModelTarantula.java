package com.lycanitesmobs.junglemobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTarantula extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelTarantula() {
        this(1.0F);
    }
    
    public ModelTarantula(float shadowSize) {
    	// Load Model:
        this.initModel("Tarantula", JungleMobs.group, "entity/tarantula");
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.3F, 0.2F);
    	setPartCenter("innerleftmouth", 0.07F, 0.3F, 0.35F);
    	setPartCenter("innerrightmouth", -0.07F, 0.3F, 0.35F);
    	setPartCenter("outerleftmouth", 0.29F, 0.3F, 0.25F);
    	setPartCenter("outerrightmouth", -0.29F, 0.3F, 0.25F);
    	setPartCenter("body", 0F, 0.3F, 0.2F);
    	
    	setPartCenter("frontleftleg", 0.25F, 0.3F, 0.17F);
    	setPartCenter("middlefrontleftleg", 0.25F, 0.3F, 0.05F);
    	setPartCenter("middlebackleftleg", 0.25F, 0.3F, -0.07F);
    	setPartCenter("backleftleg", 0.25F, 0.3F, -0.19F);
    	
    	setPartCenter("frontrightleg", -0.25F, 0.3F, 0.17F);
    	setPartCenter("middlefrontrightleg", -0.25F, 0.3F, 0.05F);
    	setPartCenter("middlebackrightleg", -0.25F, 0.3F, -0.07F);
    	setPartCenter("backrightleg", -0.25F, 0.3F, -0.19F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;
    	
    	// Trophy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.35F};
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
    	if(partName.equals("innerleftmouth"))
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F + (float) Math.PI) * 0.05F - 0.05F), 0.0F, 0.0F);
    	if(partName.equals("innerrightmouth"))
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    	if(partName.equals("outerleftmouth"))
    		rotY += (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F - 0.05F);
    	if(partName.equals("outerrightmouth"))
    		rotY -= (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F - 0.05F);
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("frontrightleg") || partName.equals("middlebackrightleg") || partName.equals("middlefrontleftleg") || partName.equals("backleftleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.3331F + (float)Math.PI) * walkSwing * distance);
    		rotZ += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	}
    	if(partName.equals("frontleftleg") || partName.equals("middlebackleftleg") || partName.equals("middlefrontrightleg") || partName.equals("backrightleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.3331F) * walkSwing * distance);
    		rotZ += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    	}
    	
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("innerleftmouth") || partName.equals("innerrightmouth")) {
	    		rotX += 20.0F;
	    	}
	    	if(partName.equals("outerleftmouth"))
	    		rotY -= 15F;
	    	if(partName.equals("outerrightmouth"))
	    		rotY += 15F;
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
