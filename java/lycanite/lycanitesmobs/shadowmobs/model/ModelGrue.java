package lycanite.lycanitesmobs.shadowmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGrue extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelGrue() {
        this(1.0F);
    }
    
    public ModelGrue(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("grue", ShadowMobs.group, "entity/grue");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.5F, 0.4F);
    	setPartCenter("mouth", 0F, 1.3F, 0.5F);
    	setPartCenter("body", 0F, 1.0F, 0F);
    	setPartCenter("armleft", 0.2F, 1.1F, 0F);
    	setPartCenter("armright", -0.2F, 1.1F, 0.2F);
    	
    	setPartCenter("effect01", 0F, 0.8F, 0F);
    	
    	// Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
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
    	
    	// Looking (Mouth):
    	if(partName.equals("mouth")) {
    		this.centerPartToPart("mouth", "head");
    		if(!lockHeadX)
    			this.rotate((float)Math.toDegrees(lookX / (180F / (float)Math.PI)), 0, 0);
    		if(!lockHeadY)
    			this.rotate(0, (float)Math.toDegrees(lookY / (180F / (float)Math.PI)), 0);
    		this.uncenterPartToPart("mouth", "head");
    	}
    	
    	// Idle:
    	if(partName.equals("mouth")) {
    		this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.1F - 0.1F), 0.0F, 0.0F);
    	}
    	if(partName.equals("armleft")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("armright")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
		float bob = -MathHelper.sin(loop * 0.1F) * 0.3F;
		posY += bob;
		
    	// Effects:
    	if(partName.equals("effect01")) {
    		rotY += loop * 8;
    	}
				
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
