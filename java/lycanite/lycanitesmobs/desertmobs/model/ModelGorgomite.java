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
public class ModelGorgomite extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelGorgomite() {
        this(1.0F);
    }
    
    public ModelGorgomite(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Gorgomite", DesertMobs.group, "entity/gorgomite");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.4F, 0.45F);
    	setPartCenter("leftmouth", 0.2F, 0.4F, 0.7F);
    	setPartCenter("rightmouth", -0.2F, 0.4F, 0.7F);
    	setPartCenter("body", 0F, 0.4F, 0.45F);
    	setPartCenter("frontleftleg", 0.35F, 0.5F, 0.2F);
    	setPartCenter("backleftleg", 0.35F, 0.5F, -0.3F);
    	setPartCenter("frontrightleg", -0.35F, 0.5F, 0.2F);
    	setPartCenter("backrightleg", -0.35F, 0.5F, -0.3F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;
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
    	
    	// Head Rotation:
    	if(partName.equals("leftmouth") || partName.equals("rightmouth")) {
    		if(partName.equals("leftmouth"))
    			this.centerPartToPart("leftmouth", "head");
    		if(partName.equals("rightmouth"))
    			this.centerPartToPart("rightmouth", "head");
    		if(!lockHeadX)
    			rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    		if(partName.equals("leftmouth"))
    			this.uncenterPartToPart("leftmouth", "head");
    		if(partName.equals("rightmouth"))
    			this.uncenterPartToPart("rightmouth", "head");
    	}
    	
    	// Idle:
    	if(partName.equals("leftmouth"))
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F + (float)Math.PI) * 0.05F - 0.05F), 0.0F, 0.0F);
    	if(partName.equals("rightmouth"))
    		rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("frontrightleg") || partName.equals("backleftleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	}
    	if(partName.equals("frontleftleg") || partName.equals("backrightleg")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
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
