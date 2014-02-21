package lycanite.lycanitesmobs.swampmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.model.ModelCustomObj;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRemobra extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelRemobra() {
        this(1.0F);
    }
    
    public ModelRemobra(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Remobra", SwampMobs.domain, "entity/remobra");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("body", 0F, 0.8F, 0.0F);
    	setPartCenter("tail", 0F, 0.6F, -0.1F);
    	setPartCenter("leftwing", 0.1F, 0.8F, -0.1F);
    	setPartCenter("rightwing", -0.1F, 0.8F, -0.1F);
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
    	if(partName.equals("leftwing")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
		    rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
    	}
    	if(partName.equals("rightwing")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
	        rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F + (float)Math.PI) * 0.6F);
    	}
    	if(partName.equals("tail")) {
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.1F) * 0.6F);
    	}
		float bob = -MathHelper.sin(loop * 0.2F) * 0.3F;
		if(bob < 0) bob = -bob;
		posY += bob;
		
    	// Apply Animations:
    	translate(posX, posY, posZ);
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    }
}
