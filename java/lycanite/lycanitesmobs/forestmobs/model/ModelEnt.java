package lycanite.lycanitesmobs.forestmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEnt extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelEnt() {
        this(1.0F);
    }
    
    public ModelEnt(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Ent", ForestMobs.domain, "entity/ent");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.2F, 0.3F);
    	setPartCenter("body", 0F, 1.2F, 0.3F);
    	setPartCenter("leftarm", 0.3F, 1.1F, 0F);
    	setPartCenter("rightarm", -0.3F, 1.1F, 0F);
    	
    	setPartCenter("frontmiddleleg", 0F, 0.3F, 0.3F);
    	setPartCenter("frontleftleg", 0.3F, 0.3F, 0.15F);
    	setPartCenter("frontrightleg", -0.3F, 0.3F, 0.15F);
    	
    	setPartCenter("backmiddleleg", 0F, 0.3F, -0.3F);
    	setPartCenter("backleftleg", 0.3F, 0.3F, -0.15F);
    	setPartCenter("backrightleg", -0.3F, 0.3F, -0.15F);
    	
    	lockHeadX = true;
    	lockHeadY = true;
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
		
    	// Leg Angles:
    	if(partName.equals("frontleftleg") || partName.equals("backleftleg")
    			|| partName.equals("frontrightleg") || partName.equals("backrightleg")
    			|| partName.equals("frontmiddleleg") || partName.equals("backmiddleleg"))
    		angleY = 1F;
    	/*if(partName.equals("frontmiddleleg")) angleY = 90F / 360F;
    	if(partName.equals("frontleftleg")) angleY = 35F / 360F;
    	if(partName.equals("frontrightleg")) angleY = -35F / 360F;
    	if(partName.equals("backmiddleleg")) angleY = -90F / 360F;
    	if(partName.equals("backleftleg")) angleY = -35F / 360F;
    	if(partName.equals("backrightleg")) angleY = 35F / 360F;*/
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("frontrightleg") || partName.equals("frontleftleg") || partName.equals("backleftleg") || partName.equals("backrightleg"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("frontmiddleleg") || partName.equals("backmiddleleg"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
				
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
