package lycanite.lycanitesmobs.infernomobs.model;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.model.ModelCustomObj;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAfrit extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelAfrit() {
        this(1.0F);
    }
    
    public ModelAfrit(float shadowSize) {
    	// Load Model:
    	this.initModel("afrit", InfernoMobs.group, "entity/afrit");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.07F, 0.1F);
    	setPartCenter("body", 0F, 0.7F, 0F);

    	setPartCenter("armleft", 0.2F, 0.97F, -0.05F);
    	setPartCenter("armright", -0.2F, 0.97F, -0.05F);

    	setPartCenter("legleft", 0.16F, 0.5F, 0F);
    	setPartCenter("legright", -0.16F, 0.5F, 0F);

        setPartCenter("wingleft", 0.06F, 1.1F, -0.13F);
        setPartCenter("wingright", -0.06F, 1.1F, -0.13F);

    	setPartCenter("tail", 0F, 0.47F, -0.2F);

        // Trophy:
        this.trophyScale = 1.8F;
        this.trophyOffset = new float[] {0.0F, -0.05F, -0.1F};
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
    	if(partName.equals("tail")) {
    		rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
    		rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    	}
        if(entity == null || entity.onGround || entity.isInWater()) {
            if(partName.equals("wingright")) {
                rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
                rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
            }
            if(partName.equals("wingleft")) {
                rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
                rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
            }
        }
    	
    	// Walking:
    	if(entity == null || entity.onGround || entity.isInWater()) {
	    	float walkSwing = 0.6F;
	    	if(partName.equals("armleft") || partName.equals("wingright")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
				rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("armright") || partName.equals("wingleft")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.0F * distance * 0.5F);
				rotZ += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("legleft"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
	    	if(partName.equals("legright"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	}
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("mouth")) {
	    		rotX += 20.0F;
	    	}
		}
		
		// Flying:
		if(entity != null && !entity.onGround && !entity.isInWater()) {
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
	    	if(partName.equals("legleft"))
	    		rotX += 50;
	    	if(partName.equals("legright"))
	    		rotX += 50;
		}
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }
}
