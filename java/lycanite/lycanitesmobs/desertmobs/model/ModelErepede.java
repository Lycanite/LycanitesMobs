package lycanite.lycanitesmobs.desertmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelErepede extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelErepede() {
        this(1.0F);
    }
    
    public ModelErepede(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Erepede", DesertMobs.group, "entity/erepede");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.2F, 0.6F);
    	
    	setPartCenter("topmouth", 0F, 1.75F, 1.25F);
    	setPartCenter("leftmouth", 0.14F, 1.6F, 1.25F);
    	setPartCenter("rightmouth", -0.14F, 1.6F, 1.25F);
    	setPartCenter("bottommouth", 0F, 1.4F, 1.25F);
    	
    	setPartCenter("body", 0F, 1.2F, 0.6F);
    	
    	setPartCenter("frontleftleg", 0.4F, 1.1F, 0.25F);
    	setPartCenter("middleleftleg", 0.4F, 1.1F, -0.15F);
    	setPartCenter("backleftleg", 0.4F, 1.1F, -0.5F);
    	
    	setPartCenter("frontrightleg", -0.4F, 1.1F, 0.25F);
    	setPartCenter("middlerightleg", -0.4F, 1.1F, -0.15F);
    	setPartCenter("backrightleg", -0.4F, 1.1F, -0.5F);
    	
    	this.lockHeadY = false;
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
    	if(partName.equals("topmouth") || partName.equals("leftmouth") || partName.equals("rightmouth") || partName.equals("bottommouth")) {
    		this.centerPartToPart(partName, "head");
    		if(!lockHeadX)
    			this.rotate((float)Math.toDegrees(lookX / (180F / (float)Math.PI)), 0, 0);
    		if(!lockHeadY)
    			this.rotate(0, (float)Math.toDegrees(lookY / (180F / (float)Math.PI)), 0);
    		this.uncenterPartToPart(partName, "head");
    	}
    	if(partName.equals("topmouth")) {
    		rotX += (float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    	}
    	if(partName.equals("leftmouth")) {
    		rotY -= (float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    	}
    	if(partName.equals("rightmouth")) {
    		rotY += (float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    	}
    	if(partName.equals("bottommouth")) {
    		rotX -= (float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    	}
    	
    	// Walking:
    	float walkSwing = 0.3F;
    	if(partName.equals("frontrightleg") || partName.equals("middleleftleg") || partName.equals("backrightleg")) {
    		rotZ += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    		rotY += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	}
    	if(partName.equals("frontleftleg") || partName.equals("middlerightleg") || partName.equals("backleftleg")) {
    		rotZ += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    		rotY += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    	}
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("topmouth"))
				rotX -= 20F;
	    	if(partName.equals("leftmouth"))
				rotY += 20F;
	    	if(partName.equals("rightmouth"))
				rotY -= 20F;
	    	if(partName.equals("bottommouth"))
				rotX += 20F;
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
    	super.childScale(partName);
    	if(partName.equals("head") || partName.equals("topmouth") || partName.equals("leftmouth") || partName.equals("rightmouth") || partName.equals("bottommouth")) {
    		scale(2F, 2F, 2F);
    		translate(0F, -(getPartCenter(partName)[1] / 2), 0F);
    	}
    }
}
