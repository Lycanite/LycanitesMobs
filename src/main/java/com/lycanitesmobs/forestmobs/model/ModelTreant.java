package com.lycanitesmobs.forestmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;
import com.lycanitesmobs.forestmobs.ForestMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTreant extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelTreant() {
        this(1.0F);
    }
    
    public ModelTreant(float shadowSize) {
    	// Load Model:
    	this.initModel("treant", ForestMobs.instance.group, "entity/treant");

    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 6F, 1F);
    	setPartCenter("body", 0F, 5F, 0.8F);

    	setPartCenter("armlefttop", 0.9F, 7.2F, 0F);
        setPartCenter("armleftmiddle", 1.2F, 6.3F, 0F);
        setPartCenter("armleftbottom", 1.2F, 5.4F, 0F);

    	setPartCenter("armrighttop", -0.9F, 7.2F, 0F);
        setPartCenter("armrightmiddle", -1.2F, 6.3F, 0F);
        setPartCenter("armrightbottom", -1.2F, 5.4F, 0F);

    	setPartCenter("legleft", 0.4F, 4.5F, 0F);
    	setPartCenter("legright", -0.4F, 4.5F, 0F);
    	
    	lockHeadX = true;
    	lockHeadY = true;
    	
    	// Trophy:
        this.trophyScale = 0.8F;
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
    	
    	// Idle:
    	if(partName.equals("armlefttop") || partName.equals("armleftbottom") || partName.equals("armrightmiddle")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("armrighttop") || partName.equals("armrightbottom") || partName.equals("armleftmiddle")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	
    	// Walking:
    	float walkSwing = 0.3F;
    	if(partName.equals("armlefttop") || partName.equals("armleftbottom") || partName.equals("armrightmiddle"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 2.0F * distance * 0.5F);
    	if(partName.equals("armrighttop") || partName.equals("armrightbottom") || partName.equals("armleftmiddle"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 2.0F * distance * 0.5F);
        if(partName.equals("legleft"))
            rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
        if(partName.equals("legright"))
            rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).isAttackOnCooldown()) {
	    	if(partName.equals("armlefttop") || partName.equals("armleftbottom") || partName.equals("armrightmiddle"))
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("armrighttop") || partName.equals("armrightbottom") || partName.equals("armleftmiddle"))
	    		rotate(0.0F, 25.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
