package lycanite.lycanitesmobs.infernomobs.model;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.model.ModelCustomObj;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLobber extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelLobber() {
        this(1.0F);
    }
    
    public ModelLobber(float shadowSize) {
    	// Load Model:
    	this.initModel("Lobber", InfernoMobs.group, "entity/lobber");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 3.0F, 0.3F);
    	setPartCenter("body", 0F, 3.0F, 0.3F);
    	setPartCenter("leftarm", 1.1F, 2.5F, 0F);
    	setPartCenter("rightarm", -1.1F, 2.5F, 0F);
    	setPartCenter("leftleg", 0.8F, 0.9F, 0F);
    	setPartCenter("rightleg", -0.8F, 0.9F, 0F);

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.0F};
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
    	if(entity == null || entity.onGround || entity.isInWater()) {
	    	float walkSwing = 0.6F;
	    	if(partName.equals("leftarm")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
				rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("rightarm")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.0F * distance * 0.5F);
				rotZ += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("leftleg"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
	    	if(partName.equals("rightleg"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	}
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("leftarm") || partName.equals("rightarm"))
	    		rotX -= 65.0F;
	    	if(partName.equals("leftarm"))
	    		rotY -= 20.0F;
	    	if(partName.equals("rightarm"))
	    		rotY += 20.0F;
		}
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }
}
