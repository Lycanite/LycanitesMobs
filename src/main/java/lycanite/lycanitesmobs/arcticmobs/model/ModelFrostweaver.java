package lycanite.lycanitesmobs.arcticmobs.model;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.model.ModelCustomObj;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFrostweaver extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelFrostweaver() {
        this(1.0F);
    }
    
    public ModelFrostweaver(float shadowSize) {
    	// Load Model:
    	this.initModel("Frostweaver", ArcticMobs.group, "entity/frostweaver");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.4F, 0.55F);
    	setPartCenter("leftmouth", 0.1F, 0.3F, 0.9F);
    	setPartCenter("rightmouth", -0.1F, 0.3F, 0.9F);
    	setPartCenter("body", 0F, 0.4F, 0.55F);
    	
    	setPartCenter("frontleftleg", 0.4F, 0.5F, 0.2F);
    	setPartCenter("middlefrontleftleg", 0.4F, 0.5F, 0F);
    	setPartCenter("middlebackleftleg", 0.4F, 0.5F, -0.2F);
    	setPartCenter("backleftleg", 0.4F, 0.5F, -0.4F);
    	
    	setPartCenter("frontrightleg", -0.4F, 0.5F, 0.2F);
    	setPartCenter("middlefrontrightleg", -0.4F, 0.5F, 0F);
    	setPartCenter("middlebackrightleg", -0.4F, 0.5F, -0.2F);
    	setPartCenter("backrightleg", -0.4F, 0.5F, -0.4F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;

        // Tropy:
        this.trophyScale = 1.5F;
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
    	if(partName.equals("leftmouth"))
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F + (float)Math.PI) * 0.05F - 0.05F), 0.0F, 0.0F);
    	if(partName.equals("rightmouth"))
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("frontrightleg") || partName.equals("middlebackrightleg") || partName.equals("middlefrontleftleg") || partName.equals("backleftleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.3331F + (float)Math.PI) * walkSwing * distance);
    		rotZ += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	}
    	if(partName.equals("frontleftleg") || partName.equals("middlebackleftleg") || partName.equals("middlefrontrightleg") || partName.equals("backrightleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.3331F) * walkSwing * distance);
    		rotZ += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    	}
    	
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("leftmouth") || partName.equals("rightmouth")) {
	    		rotX += 20.0F;
	    	}
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
