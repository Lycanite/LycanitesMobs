package com.lycanitesmobs.forestmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.forestmobs.ForestMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSpriggan extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelSpriggan() {
        this(1.0F);
    }

    public ModelSpriggan(float shadowSize) {
    	// Load Model:
    	this.initModel("Spriggan", ForestMobs.instance.group, "entity/spriggan");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.44F, 0F);
    	setPartCenter("body", 0F, 1.3F, 0F);
    	setPartCenter("armleft", 0.32F, 1.35F, 0F);
    	setPartCenter("armright", -0.32F, 1.35F, 0F);
    	
    	setPartCenter("effect01", 0F, 1F, 0F);
    	setPartCenter("effect02", 0F, 1F, 0F);
    	
    	// Trophy:
        this.trophyScale = 1.2F;
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
    	
    	// Idle:
    	if(partName.equals("armleft")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("armright")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
		float bob = -MathHelper.sin(loop * 0.1F) * 0.3F;
		posY += bob;
    	
    	// Effects:
    	if(partName.equals("effect01"))
    		rotX = 45F;
        if(partName.equals("effect02"))
            rotX = -45F;
    	if(partName.equals("effect01"))
    		rotY += loop * 4;
    	if(partName.equals("effect02"))
    		rotY -= loop * 4;
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("armleft"))
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("armright"))
	    		rotate(0.0F, 25.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
