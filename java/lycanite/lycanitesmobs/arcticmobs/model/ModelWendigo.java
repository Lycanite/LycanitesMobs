package lycanite.lycanitesmobs.arcticmobs.model;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWendigo extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelWendigo() {
        this(1.0F);
    }
    
    public ModelWendigo(float shadowSize) {
    	// Load Model:
    	this.initModel("Wendigo", ArcticMobs.group, "entity/wendigo");

    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 4.392F, -0.216F);
        setPartCenter("mouth", 0F, 4.4568F, -1.1376F);
    	setPartCenter("body", 0F, 3.908F, 0.3F);
    	setPartCenter("armleft", 0.504F, 4.032F, -0.036F);
        setPartCenter("armright", -0.504F, 4.032F, -0.036F);
    	setPartCenter("legleft", 0.216F, 2.592F, 0F);
        setPartCenter("legright", -0.216F, 2.592F, 0F);

        this.lockHeadY = true;

        // Tropy:
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

        // Looking:
        if(partName.equals("mouth")) {
            this.centerPartToPart("mouth", "head");
            if(!this.lockHeadX)
                this.rotate((float)Math.toDegrees(lookX / (180F / (float) Math.PI)), 0, 0);
            if(!this.lockHeadY)
                this.rotate(0, (float)Math.toDegrees(lookY / (180F / (float)Math.PI)), 0);
            this.uncenterPartToPart("mouth", "head");
        }
    	
    	// Idle:
        if (partName.equals("mouth")) {
            rotX += (MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
        }
    	if(partName.equals("armleft")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("armright")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	
    	// Walking:
    	if(entity == null || entity.onGround || entity.isInWater()) {
	    	float walkSwing = 0.6F;
	    	if(partName.equals("armleft")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
				rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("armright")) {
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
	    	if(partName.equals("armleft") || partName.equals("armright"))
	    		rotX -= 65.0F;
	    	if(partName.equals("armleft"))
	    		rotY -= 20.0F;
	    	if(partName.equals("armright"))
	    		rotY += 20.0F;
		}
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }
}
