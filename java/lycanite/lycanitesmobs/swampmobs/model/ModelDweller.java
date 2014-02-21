package lycanite.lycanitesmobs.swampmobs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.model.ModelBipedCustom;
import lycanite.lycanitesmobs.model.ModelCustom;
import lycanite.lycanitesmobs.model.ModelCustomObj;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

@SideOnly(Side.CLIENT)
public class ModelDweller extends ModelCustomObj {
    	
    	// Animation:
    	boolean attackAlt = false;
    	boolean attacking = false;
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelDweller() {
        this(1.0F);
    }
    
    public ModelDweller(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Dweller", SwampMobs.domain, "entity/dweller");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.5F, 0.2F);
    	setPartCenter("mouth", 0F, 1.5F, 0.2F);
    	setPartCenter("body", 0F, 1.5F, 0.2F);
    	setPartCenter("leftarm", 0.4F, 1.5F, 0.0F);
    	setPartCenter("rightarm", -0.4F, 1.5F, 0.0F);
    	setPartCenter("tail01", 0F, 1.0F, 0.0F);
    	setPartCenter("tail02", 0F, 0.45F, -0.1F);
    	setPartCenter("tail03", 0F, 0.11F, -0.3F);
    	setPartCenter("tail04", 0F, 0.1F, -0.85F);
    	setPartCenter("tail05", 0F, 0.09F, -1.3F);
    	setPartCenter("tail06", 0F, 0.08F, -1.65F);
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
    	
    	// Mouth (Follow Head):
    	if(partName.equals("mouth")) {
    		if(!lockHeadX)
    			rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    	}
    	
    	// Mouth (Idle and Attack):
    	if(partName.equals("mouth")) {
    		translate(0.0F, -0.2F, 0.0F);
    		angleX = -1.0F;
    	}
    	if(partName.equals("mouth")) {
    		rotation += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    		if(((EntityCreatureBase)entity).justAttacked())
    			rotation = 20;
        	rotate(rotation, angleX, angleY, angleZ);
        	rotation = 0F;
    	}
    	if(partName.equals("mouth"))
    		translate(-0.0F, 0.2F, 0.0F);
    	
    	// Arms (Idle and Attack):
    	if(partName.equals("leftarm")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    		if(((EntityCreatureBase)entity).justAttacked()) {
    			if(attackAlt)
    				rotY = 40;
    			else
    				rotY = -25;
    		}
    	}
    	if(partName.equals("rightarm")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    		if(((EntityCreatureBase)entity).justAttacked()) {
    			if(attackAlt)
    				rotY = 40;
    			else
    				rotY = -25;
    		}
    	}
		if(((EntityCreatureBase)entity).justAttacked() && !attacking) {
			attackAlt = !attackAlt;
			attacking = true;
		}
		if(!((EntityCreatureBase)entity).justAttacked())
			attacking = false;
    	
    	// Arms (Match Body):
    	float walkSwing = 0.1F;
    	float walkOffset = 0.1F;
    	time *= 4;
    	if(partName.equals("leftarm"))
    		translate(-0.4F, 0.0F, 0.2F);
    	if(partName.equals("rightarm"))
    		translate(0.4F, 0.0F, 0.2F);
    	if(partName.equals("leftarm") || partName.equals("rightarm"))
    		rotate(0.0F, 0.0F, (float)rotateToPoint(0, 0, -0.5F, MathHelper.sin(time * walkSwing) * walkSwing));
    	if(partName.equals("leftarm"))
    		translate(0.4F, 0.0F, -0.2F);
    	if(partName.equals("rightarm"))
    		translate(-0.4F, 0.0F, -0.2F);
    	
    	// Body and Tail (Walking):
    	if(partName.equals("body")) {
    		rotZ += rotateToPoint(0, 0, -0.5F, MathHelper.sin(time * walkSwing) * walkSwing);
    	}
    	if(partName.equals("tail01") || partName.equals("tail02") || partName.equals("tail03") || partName.equals("tail04") || partName.equals("tail05") || partName.equals("tail06")) {
    		double[] tailAnimation = new double[] {0.0D, 0.0D, 0.0D, 0.0D};
    		if(partName.equals("tail01"))
    			tailAnimation = getTailRotation(partName, "body", 1, walkSwing, walkOffset, time);
    		if(partName.equals("tail02"))
    			tailAnimation = getTailRotation(partName, "tail01", 2, walkSwing, walkOffset, time);
    		if(partName.equals("tail03"))
    			tailAnimation = getTailRotation(partName, "tail02", 3, walkSwing, walkOffset, time);
    		if(partName.equals("tail04"))
    			tailAnimation = getTailRotation(partName, "tail03", 4, walkSwing, walkOffset, time);
    		if(partName.equals("tail05"))
    			tailAnimation = getTailRotation(partName, "tail04", 5, walkSwing, walkOffset, time);
    		if(partName.equals("tail06"))
    			tailAnimation = getTailRotation(partName, "tail05", 6, walkSwing, walkOffset, time);
    		posX += tailAnimation[0];
			rotY += tailAnimation[2];
			if(partName.equals("tail01") || partName.equals("tail02") || partName.equals("tail03"))
				rotZ += tailAnimation[3];
    	}
    		
    	
    	// Apply Animations:
    	translate(posX, posY, posZ);
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	scale(scaleX, scaleY, scaleZ);
    }
    
    public double[] getTailRotation(String tailName, String targetName, int tailID, float walkSwing, float walkOffset, float time) {
    	double[] animation = new double[4]; // xPos, xRot, yRot, zRot
    	animation[0] = MathHelper.sin((time + (walkOffset * tailID)) * walkSwing * tailID) * walkSwing;
    	float parentX = MathHelper.sin((time + (walkOffset * (tailID - 1))) * walkSwing * tailID) * walkSwing;
    	float[] partDifference = comparePartCenters(tailName, targetName);
    	if(tailID == 1)
    		partDifference[1] += 0.5F;
    	double[] rotations = rotateToPoint((float)animation[0], 0.0F, 0.0F, parentX - partDifference[0], -partDifference[1], partDifference[2]);
    	animation[1] = rotations[0];
    	animation[2] = rotations[1];
    	animation[3] = rotations[2];
    	return animation;
    }
}
