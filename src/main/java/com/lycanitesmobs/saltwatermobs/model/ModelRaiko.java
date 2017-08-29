package com.lycanitesmobs.saltwatermobs.model;

import com.lycanitesmobs.core.model.ModelCustomObj;
import com.lycanitesmobs.saltwatermobs.SaltwaterMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRaiko extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelRaiko() {
        this(1.0F);
    }

    public ModelRaiko(float shadowSize) {
    	// Load Model:
    	this.initModel("raiko", SaltwaterMobs.group, "entity/raiko");
    	
    	// Set Rotation Centers:
        this.setPartCenter("head", 0F, 1.5F, 1.0F);
        this.setPartCenter("mouth", 0F, 1.4F, 1.65F);

        this.setPartCenter("body", 0F, 1.5F, 0.0F);

        this.setPartCenter("legleft", 0.15F, 1.0F, -0.5F);
        this.setPartCenter("legright", -0.15F, 1.0F, -0.5F);

        this.setPartCenter("wingleft", 0.2F, 1.5F, 0.75F);
        this.setPartCenter("wingright", -0.2F, 1.5F, 0.75F);
    	
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

		// Walking:
		if(entity == null || entity.onGround) {
			float walkSwing = 0.6F;
			float wingOffset = 1;
			if(entity.isInWater()) {
				wingOffset = 0;
			}
			if(partName.equals("armleft") || partName.equals("wingright")) {
				rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F) - (45 * wingOffset);
				this.rotate(1, 20 * wingOffset, 0 * wingOffset);
				rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F) + (90 * wingOffset);
			}
			if(partName.equals("armright") || partName.equals("wingleft")) {
				rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F) - (45 * wingOffset);
				this.rotate(1, -20 * wingOffset, 0 * wingOffset);
				rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F) + (90 * wingOffset);
			}
			if(partName.equals("legleft"))
				rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.8F * distance);
			if(partName.equals("legright"))
				rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.8F * distance);
		}

    	// Flying:
    	if(entity != null && !entity.isInWater() && !entity.onGround) {
			if (partName.equals("wingleft")) {
				rotX = 20;
				rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
				rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
			}
			if (partName.equals("wingright")) {
				rotX = 20;
				rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
				rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F + (float) Math.PI) * 0.6F);
			}
			if(partName.equals("legleft")) {
				rotX -= Math.toDegrees(MathHelper.sin(loop * 0.1F + (float)Math.PI) * 0.2F);
			}
			if(partName.equals("legright")) {
				rotX -= Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.2F);
			}
			float bob = -MathHelper.sin(loop * 0.2F) * 0.3F;
			if(bob < 0) bob = -bob;
			posY += bob;
		}
		
    	// Apply Animations:
    	translate(posX, posY, posZ);
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    }
}
