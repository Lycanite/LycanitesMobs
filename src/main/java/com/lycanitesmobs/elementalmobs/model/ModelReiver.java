package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelReiver extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelReiver() {
        this(1.0F);
    }
    
    public ModelReiver(float shadowSize) {
    	// Load Model:
    	this.initModel("reiver", ElementalMobs.instance.group, "entity/reiver");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.5F, 0.3F);
    	setPartCenter("body", 0F, 1.5F, 0.3F);
    	setPartCenter("leftarm", 0.4F, 1.3F, 0.2F);
    	setPartCenter("rightarm", -0.4F, 1.3F, 0.2F);
    	
    	setPartCenter("outereffect", 0F, 0.8F, -0.1F);
    	setPartCenter("innereffect", 0F, 0.8F, -0.1F);

        // Tropy:
        this.trophyScale = 1.0F;
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
    	if(partName.equals("leftarm")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("rightarm")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
		float bob = -MathHelper.sin(loop * 0.1F) * 0.3F;
		posY += bob;
    	
    	// Effects:
    	if(partName.equals("outereffect") || partName.equals("innereffect"))
    		rotX = 30F;
    	if(partName.equals("outereffect"))
    		rotY += loop * 4;
    	if(partName.equals("innereffect"))
    		rotY -= loop * 4;
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("leftarm"))
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("rightarm"))
	    		rotate(0.0F, 25.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
