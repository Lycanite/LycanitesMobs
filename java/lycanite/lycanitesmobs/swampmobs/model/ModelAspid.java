package lycanite.lycanitesmobs.swampmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.model.ModelCustomObj;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAspid extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelAspid() {
        this(1.0F);
    }
    
    public ModelAspid(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Aspid", SwampMobs.domain, "entity/aspid");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.7F, 0.8F);
    	setPartCenter("body", 0F, 1.7F, 0.8F);
    	setPartCenter("leftarm", 0.5F, 1.5F, 0.6F);
    	setPartCenter("rightarm", -0.5F, 1.5F, 0.6F);
    	setPartCenter("leftleg", 0.6F, 1.15F, -0.3F);
    	setPartCenter("rightleg", -0.6F, 1.15F, -0.3F);
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
    	if(partName.equals("rightarm") || partName.equals("leftleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	}
    	if(partName.equals("leftarm") || partName.equals("rightleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    	}
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("leftarm") || partName.equals("rightarm"))
	    		rotY -= 15F;
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
    	if(partName.equals("head"))
    		translate(-(getPartCenter(partName)[0] / 2), -(getPartCenter(partName)[1] / 2), -(getPartCenter(partName)[2] / 2));
    	else
        	super.childScale(partName);
    }
}
