package com.lycanitesmobs.swampmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.model.ModelObjOld;
import com.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLurker extends ModelObjOld {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelLurker() {
        this(1.0F);
    }
    
    public ModelLurker(float shadowSize) {
    	// Load Model:
    	this.initModel("Lurker", SwampMobs.instance.group, "entity/lurker");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("topmiddlemouth", 0.0F, 1.0F, 0.6F);
    	setPartCenter("topleftmouth", 0.2F, 1.0F, 0.6F);
    	setPartCenter("toprightmouth", -0.2F, 1.0F, 0.6F);
    	setPartCenter("leftmouth", 0.25F, 0.75F, 0.65F);
    	setPartCenter("rightmouth", -0.25F, 0.75F, 0.65F);
    	setPartCenter("bottommouth", 0.0F, 0.55F, 0.65F);
    	setPartCenter("body", 0.0F, 0.7F, 0.0F);
    	setPartCenter("frontleftleg", 0.3F, 0.85F, 0.35F);
    	setPartCenter("frontrightleg", -0.3F, 0.85F, 0.35F);
    	setPartCenter("backleftleg", 0.25F, 0.5F, -0.45F);
    	setPartCenter("backrightleg", -0.25F, 0.5F, -0.45F);
    	
    	// Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -1.0F};
        this.trophyMouthOffset = new float[] {0.0F, -0.8F, 0.0F};
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
    	if(partName.equals("topmiddlemouth")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.08F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("topleftmouth")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("toprightmouth")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.08F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("leftmouth")) {
	        rotZ += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("rightmouth")) {
	        rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("bottommouth")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.075F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	
    	// Walking:
    	float walkSwing = 0.15F;
    	if(partName.equals("frontleftleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
    	if(partName.equals("frontrightleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	if(partName.equals("backleftleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	if(partName.equals("backrightleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
    	float bob = MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance;
		if(bob < 0) bob += -bob * 2;
		posY += bob;
    	
    	// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
			if(partName.equals("topmiddlemouth") || partName.equals("topleftmouth") || partName.equals("toprightmouth"))
				rotX -= 30F;
			if(partName.equals("leftmouth"))
				rotZ += 30F;
			if(partName.equals("rightmouth"))
				rotZ -= 30F;
			if(partName.equals("bottommouth"))
				rotX += 30F;
		}
    	
    	// Sit:
		if(entity instanceof EntityCreatureTameable && ((EntityCreatureTameable)entity).isSitting()) {
			if(partName.equals("topmiddlemouth") || partName.equals("topleftmouth") || partName.equals("toprightmouth"))
				rotX += 30F;
			if(partName.equals("leftmouth"))
				rotZ -= 30F;
			if(partName.equals("rightmouth"))
				rotZ += 30F;
			if(partName.equals("bottommouth"))
				rotX -= 30F;
		}
    	
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
