package lycanite.lycanitesmobs.demonmobs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
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
public class ModelAsmodi extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelAsmodi() {
        this(1.0F);
    }
    
    public ModelAsmodi(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Asmodi", DemonMobs.domain, "entity/asmodi");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.1F, 0F);
    	setPartCenter("base", 0F, 0.8F, 0F);
    	setPartCenter("leftarm", 0.75F, 1.4F, 0.3F);
    	setPartCenter("rightarm", -0.75F, 1.4F, 0.3F);
    	setPartCenter("frontleftleg", 0.9F, 0.9F, -0.5F);
    	setPartCenter("middleleftleg", 0.9F, 0.9F, 0F);
    	setPartCenter("backleftleg", 0.9F, 0.9F, 0.5F);
    	setPartCenter("frontrightleg", -0.9F, 0.9F, -0.5F);
    	setPartCenter("middlerightleg", -0.9F, 0.9F, 0F);
    	setPartCenter("backrightleg", -0.9F, 0.9F, 0.5F);
    	
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
    	
    	// Leg Angles:
    	if(partName.equals("frontleftleg") || partName.equals("backleftleg") || partName.equals("middlerightleg")
    			|| partName.equals("middleleftleg") || partName.equals("frontrightleg") || partName.equals("backrightleg"))
    		angleZ = 1F;
    	if(partName.equals("frontleftleg")) angleY = 30F / 360F;
    	if(partName.equals("middleleftleg")) angleY = 0F;
    	if(partName.equals("backleftleg")) angleY = -30F / 360F;
    	if(partName.equals("frontrightleg")) angleY = -30F / 360F;
    	if(partName.equals("middlerightleg")) angleY = 0F;
    	if(partName.equals("backrightleg")) angleY = 30F / 360F;
    	
    	// Idle - Arms:
    	float armSwing = 0.3F;
    	if(partName.equals("leftarm")) {
    		rotZ += -Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    		rotX += -Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("rightarm")) {
    		rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
    		rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	
    	// Walking - Arms:
    	if(partName.equals("leftarm")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * 2.0F * distance * armSwing);
    	}
    	if(partName.equals("rightarm")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * 2.0F * distance * armSwing);
        }
    	
    	// Walking - Legs:
    	float walkSwing = 0.3F;
    	if(partName.equals("frontleftleg") || partName.equals("backleftleg") || partName.equals("middlerightleg"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("middleleftleg") || partName.equals("frontrightleg") || partName.equals("backrightleg"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    	
    	// Walking - Bobbing:
		float bob = MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance;
		if(bob < 0) bob += -bob * 2;
		posY += bob;
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
