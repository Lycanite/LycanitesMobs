package lycanite.lycanitesmobs.swampmobs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.entity.EntityCreatureTameable;
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
public class ModelLurker extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelLurker() {
        this(1.0F);
    }
    
    public ModelLurker(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Lurker", SwampMobs.domain, "entity/lurker");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
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
