package lycanite.lycanitesmobs.mountainmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGeonach extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelGeonach() {
        this(1.0F);
    }
    
    public ModelGeonach(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Geonach", MountainMobs.group, "entity/geonach");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.65F, 0.2F);
    	setPartCenter("body", 0F, 1.2F, 0F);
    	setPartCenter("body02", 0.25F, 0.5F, -0.2F);
    	setPartCenter("body03", 0.3F, 0.85F, -0.2F);
    	setPartCenter("armleft", 0.6F, 1.5F, 0F);
    	setPartCenter("armright", -0.6F, 1.5F, 0F);
    	
    	setPartCenter("effectouter", 0F, 0.3F, -0.1F);
    	setPartCenter("effectinner", 0F, 0.3F, -0.1F);
    	
    	this.lockHeadX = true;
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
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("armright")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("body02")) {
	        posX -= MathHelper.cos(loop * 0.09F) * 0.1F;
	        posY += MathHelper.sin(loop * 0.067F) * 0.05F;
    	}
    	if(partName.equals("body03")) {
	        posX += MathHelper.cos(loop * 0.09F) * 0.1F;
	        posY -= MathHelper.sin(loop * 0.067F) * 0.05F;
    	}
		float bob = -MathHelper.sin(loop * 0.1F) * 0.3F;
		posY += bob;
		
    	// Effects:
    	if(partName.equals("effectouter")) {
    		rotY += loop * 8;
    	}
    	if(partName.equals("effectinner"))
    		rotY -= loop * 8;
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("armleft") && ((EntityCreatureBase)entity).getAttackPhase() == 2)
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("armright") && ((EntityCreatureBase)entity).getAttackPhase() != 2)
	    		rotate(0.0F, 25.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
