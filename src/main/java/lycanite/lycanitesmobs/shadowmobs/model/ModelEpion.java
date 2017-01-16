package lycanite.lycanitesmobs.shadowmobs.model;

import lycanite.lycanitesmobs.core.model.ModelCustomObj;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEpion extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelEpion() {
        this(1.0F);
    }
    
    public ModelEpion(float shadowSize) {
    	// Load Model:
    	this.initModel("epion", ShadowMobs.group, "entity/epion");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.5F, 0.55F);
    	setPartCenter("body", 0F, 0.5F, 0.0F);
    	
    	setPartCenter("wingleft", 0.29F, 0.55F, 0.4F);
    	setPartCenter("wingright", -0.29F, 0.55F, 0.4F);
    	
    	// Trophy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.2F};
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
    	if(partName.equals("wingleft")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
		    rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
    	}
    	if(partName.equals("wingright")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.4F) * 0.6F);
	        rotZ -= Math.toDegrees(MathHelper.sin(loop * 0.4F + (float)Math.PI) * 0.6F);
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
