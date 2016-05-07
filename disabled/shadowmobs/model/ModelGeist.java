package lycanite.lycanitesmobs.shadowmobs.model;

import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGeist extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelGeist() {
        this(1.0F);
    }

    public ModelGeist(float shadowSize) {
    	// Load Model:
    	this.initModel("geist", ShadowMobs.group, "entity/geist");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.9F, 0.1F);
    	setPartCenter("body", 0F, 1.4F, 0F);
    	setPartCenter("armleft", 0.3F, 1.78F, -0.13F);
    	setPartCenter("armright", -0.3F, 1.78F, -0.13F);
    	setPartCenter("legleft", 0.2F, 0.9F, -0.2F);
    	setPartCenter("legright", -0.2F, 0.9F, -0.2F);
    	
    	// Trophy:
        this.trophyScale = 1.2F;
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
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("armleft"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 2.0F * distance * 0.5F);
    	if(partName.equals("armright"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 2.0F * distance * 0.5F);
    	if(partName.equals("legleft"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
    	if(partName.equals("legright"))
    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
