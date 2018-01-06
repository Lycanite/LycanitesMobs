package com.lycanitesmobs.demonmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;
import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRahovart extends ModelObjOld {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelRahovart() {
        this(1.0F);
    }

    public ModelRahovart(float shadowSize) {
    	// Load Model:
    	this.initModel("rahovart", DemonMobs.instance.group, "entity/rahovart");

    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 23.275F, 0F);
        setPartCenter("mouth", 0F, 23.075F, -2.5F);
    	setPartCenter("body", 0F, 11.025F, 0F);
    	setPartCenter("armleft", 5.6F, 19.775F, 0F);
    	setPartCenter("armright", -5.6F, 19.775F, 0F);
    	setPartCenter("legleft", 2.45F, 11.2F, 0F);
    	setPartCenter("legright", -2.45F, 11.2F, 0F);
        setPartCenter("tail", 0F, 18.9F, 4.725F);
    	
    	lockHeadX = false;
    	lockHeadY = false;
    	
    	// Trophy:
        this.trophyScale = 0.1F;
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
            this.centerPartToPart("mouth", "head");
            if(!this.lockHeadX)
                this.rotate((float)Math.toDegrees(lookX / (180F / (float) Math.PI)), 0, 0);
            if(!this.lockHeadY)
                this.rotate(0, (float)Math.toDegrees(lookY / (180F / (float)Math.PI)), 0);
            this.uncenterPartToPart("mouth", "head");
        }
    	
    	// Idle:
        if (partName.equals("mouth")) {
            rotX += (MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
        }
    	if(partName.equals("armleft")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("armright")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
        if(partName.equals("tail")) {
            rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
            rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
        }
    	
    	// Walking:
    	float walkSwing = 0.05F;
    	if(partName.equals("armleft"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * distance * 0.5F);
    	if(partName.equals("armright"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * distance * 0.5F);
    	if(partName.equals("legleft"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * distance);
    	if(partName.equals("legright"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * distance);
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("armleft"))
	    		rotate(-40.0F, 0.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
