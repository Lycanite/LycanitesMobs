package lycanite.lycanitesmobs.desertmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.model.ModelCustomObj;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCryptZombie extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCryptZombie() {
        this(1.0F);
    }
    
    public ModelCryptZombie(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("CryptZombie", DesertMobs.domain, "entity/cryptzombie");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.65F, 0.0F);
    	setPartCenter("body", 0F, 1.65F, 0.0F);
    	setPartCenter("leftarm", 0.4F, 1.6F, 0.0F);
    	setPartCenter("rightarm", -0.4F, 1.6F, 0.0F);
    	setPartCenter("leftleg", 0.1F, 0.85F, 0.0F);
    	setPartCenter("rightleg", -0.1F, 0.85F, 0.0F);
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
    	
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
