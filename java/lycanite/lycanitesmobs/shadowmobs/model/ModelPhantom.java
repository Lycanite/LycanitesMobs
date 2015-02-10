package lycanite.lycanitesmobs.shadowmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPhantom extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelPhantom() {
        this(1.0F);
    }
    
    public ModelPhantom(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("phantom", ShadowMobs.group, "entity/phantom");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.8F, 0F);
    	setPartCenter("body", 0F, 1.0F, 0F);
    	setPartCenter("armleft", 0.3F, 1.4F, 0F);
    	setPartCenter("armright", -0.3F, 1.4F, 0F);

        // Tropy:
        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, 0.0F, 0.0F};
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
    	if(partName.equals("armleft")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("armright")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
		float bob = -MathHelper.sin(loop * 0.1F) * 0.3F;
		posY += bob;
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("armleft"))
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("armright"))
	    		rotate(0.0F, 25.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
