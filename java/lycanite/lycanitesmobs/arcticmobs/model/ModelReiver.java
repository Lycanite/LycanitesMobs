package lycanite.lycanitesmobs.arcticmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelReiver extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelReiver() {
        this(1.0F);
    }
    
    public ModelReiver(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Reiver", ArcticMobs.group, "entity/reiver");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.5F, 0.3F);
    	setPartCenter("body", 0F, 1.5F, 0.3F);
    	setPartCenter("leftarm", 0.4F, 1.3F, 0.2F);
    	setPartCenter("rightarm", -0.4F, 1.3F, 0.2F);
    	
    	setPartCenter("outereffect", 0F, 0.8F, -0.1F);
    	setPartCenter("innereffect", 0F, 0.8F, -0.1F);

        // Tropy:
        this.trophyScale = 0.6F;
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
		float bob = -MathHelper.sin(loop * 0.1F) * 0.3F;
		posY += bob;
    	
    	// Effects:
    	if(partName.equals("outereffect") || partName.equals("innereffect"))
    		rotX = 30F;
    	if(partName.equals("outereffect"))
    		rotY += loop * 4;
    	if(partName.equals("innereffect"))
    		rotY -= loop * 4;
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("leftarm"))
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("rightarm"))
	    		rotate(0.0F, 25.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
