package com.lycanitesmobs.arcticmobs.model;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjOld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSerpix extends ModelObjOld {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelSerpix() {
        this(1.0F);
    }

    public ModelSerpix(float shadowSize) {
    	// Load Model:
    	this.initModel("Serpix", ArcticMobs.instance.group, "entity/serpix");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.5F, 4.1F);
    	setPartCenter("mouthleft", 0.6F, 0.5F, 4.2F);
    	setPartCenter("mouthright", -0.6F, 0.5F, 4.2F);
    	setPartCenter("mouthbottom", 0F, 0.2F, 4.3F);

    	setPartCenter("body", 0F, 0.5F, 3.2F);
    	setPartCenter("body01", 0F, 0.5F, 2.3F);
    	setPartCenter("body02", 0F, 0.5F, 1.4F);
    	setPartCenter("body03", 0F, 0.5F, 0.5F);
    	setPartCenter("body04", 0F, 0.5F, -0.4F);
    	setPartCenter("body05", 0F, 0.5F, -1.3F);
    	setPartCenter("body06", 0F, 0.5F, -2.2F);
    	setPartCenter("body07", 0F, 0.5F, -3.1F);
    	setPartCenter("body08", 0F, 0.5F, -4.0F);
    	setPartCenter("body09", 0F, 0.5F, -4.9F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;
    	
    	// Trophy:
        this.trophyScale = 0.6F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
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
    	float scaleX = 1F;
    	float scaleY = 1F;
    	float scaleZ = 1F;
    	
    	// No Serpix trophy animation due to head offset.
    	if(scale < 0)
    		return;

        // Looking (Mouth):
        if(partName.contains("mouth")) {
            this.centerPartToPart(partName, "head");
            if(!lockHeadX)
                this.rotate((float)Math.toDegrees(lookX / (180F / (float)Math.PI)), 0, 0);
            if(!lockHeadY)
                this.rotate(0, (float)Math.toDegrees(lookY / (180F / (float)Math.PI)), 0);
            this.uncenterPartToPart(partName, "head");
        }

        // Idle:
        if(partName.equals("mouthleft"))
            rotY += (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F - 0.05F);
        if(partName.equals("mouthright"))
            rotY -= (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F - 0.05F);
        if(partName.equals("mouthbottom")) {
            this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
        }

        // Attack:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
            if(partName.equals("mouthleft"))
                rotY -= 15F;
            if(partName.equals("mouthright"))
                rotY += 15F;
            if(partName.equals("mouthbottom"))
                rotX += 20.0F;
        }
    	
    	// Walking:
    	float walkSwing = 0.8F;
    	if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).getStealth() > 0 && ((EntityCreatureBase)entity).getStealth() < 1)
    		time = loop;
    	time /= 2;
    	if(partName.equals("head") || partName.contains("mouth")) {
    		posX += MathHelper.sin((time - walkSwing) * walkSwing) * walkSwing;
    	}
    	if(partName.equals("body")) {
    		posX += MathHelper.sin(time * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time - walkSwing) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body01")) {
    		posX += MathHelper.sin((time + walkSwing) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin(time * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body02")) {
    		posX += MathHelper.sin((time + (walkSwing * 2)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + walkSwing) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body03")) {
    		posX += MathHelper.sin((time + (walkSwing * 3)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 2)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body04")) {
    		posX += MathHelper.sin((time + (walkSwing * 4)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 3)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body05")) {
    		posX += MathHelper.sin((time + (walkSwing * 5)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 4)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body06")) {
    		posX += MathHelper.sin((time + (walkSwing * 6)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 5)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body07")) {
    		posX += MathHelper.sin((time + (walkSwing * 7)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 6)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body08")) {
    		posX += MathHelper.sin((time + (walkSwing * 8)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 7)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body09")) {
    		posX += MathHelper.sin((time + (walkSwing * 9)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 8)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	
    	// Stealth:
    	if(entity instanceof EntityCreatureBase)
    		posY -= (2 * ((EntityCreatureBase)entity).getStealth());
    	
    	// Apply Animations:
    	translate(posX, posY, posZ);
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	scale(scaleX, scaleY, scaleZ);
    }
}
