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
public class ModelEttin extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelEttin() {
        this(1.0F);
    }
    
    public ModelEttin(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Ettin", SwampMobs.domain, "entity/ettin");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("lefthead", 0.35F, 3.1F, 0.0F);
    	setPartCenter("righthead", -0.35F, 3.1F, 0.0F);
    	setPartCenter("body", 0F, 3.1F, 0.0F);
    	setPartCenter("leftarm", 0.8F, 2.75F, 0.0F);
    	setPartCenter("rightarm", -0.8F, 2.75F, 0.0F);
    	setPartCenter("leftleg", 0.35F, 1.25F, 0.0F);
    	setPartCenter("rightleg", -0.35F, 1.25F, 0.0F);
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
    	if(partName.equals("leftarm")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("rightarm")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("leftarm"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 2.0F * distance * 0.5F);
    	if(partName.equals("rightarm"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 2.0F * distance * 0.5F);
    	if(partName.equals("leftleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
    	if(partName.equals("rightleg"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	
    	// Attack:
    	if(entity instanceof EntityCreatureBase) {
    		if(((EntityCreatureBase)entity).justAttacked()) {
    			if(partName.equals("leftarm"))
    				if(((EntityCreatureBase)entity).getAttackPhase() == 0)
    					rotX += 30;
    				else
    					rotX -= 30;
    			if(partName.equals("rightarm"))
    				if(((EntityCreatureBase)entity).getAttackPhase() == 0)
    					rotX -= 30;
    				else
    					rotX += 30;
    		}
    	}
    	
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
