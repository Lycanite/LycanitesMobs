package lycanite.lycanitesmobs.desertmobs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.model.ModelBipedCustom;
import lycanite.lycanitesmobs.model.ModelCustom;
import lycanite.lycanitesmobs.model.ModelCustomObj;

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
public class ModelErepede extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelErepede() {
        this(1.0F);
    }
    
    public ModelErepede(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Erepede", DesertMobs.domain, "entity/erepede");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.1F, 0.4F);
    	setPartCenter("mouth", 0F, 1.0F, 0.7F);
    	setPartCenter("body", 0F, 1.1F, 0.4F);
    	setPartCenter("frontleftleg", 0.4F, 1.0F, 0.4F);
    	setPartCenter("middleleftleg", 0.5F, 1.2F, 0.0F);
    	setPartCenter("backleftleg", 0.4F, 0.9F, -0.3F);
    	setPartCenter("frontrightleg", -0.4F, 1.0F, 0.4F);
    	setPartCenter("middlerightleg", -0.5F, 1.2F, 0.0F);
    	setPartCenter("backrightleg", -0.4F, 0.9F, -0.3F);
    	
    	lockHeadX = true;
    	lockHeadY = true;
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
    		centerPartToPart("mouth", "head");
    		if(!lockHeadX)
    			rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    		uncenterPartToPart("mouth", "head");
    	}
    	
    	// Mouth:
    	if(partName.equals("mouth"))
			rotX += 20F;
    	
    	// Idle:
    	if(partName.equals("mouth")) {
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    	}
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("frontrightleg") || partName.equals("middleleftleg") || partName.equals("backrightleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	}
    	if(partName.equals("frontleftleg") || partName.equals("middlerightleg") || partName.equals("backleftleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    	}
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("mouth")) {
	    		rotX += 20.0F;
	    	}
		}
    	
    	// Sit:
		if(entity instanceof EntityCreatureTameable && ((EntityCreatureTameable)entity).isSitting()) {
			if(partName.equals("mouth"))
				rotX -= 30F;
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
    
    
    // ==================================================
   	//              Rotate and Translate
   	// ==================================================
    @Override
    public void childScale(String partName) {
    	super.childScale(partName);
    	if(partName.equals("head") || partName.equals("mouth")) {
    		scale(2F, 2F, 2F);
    		translate(0F, -(getPartCenter(partName)[1] / 2), 0F);
    	}
    }
}
