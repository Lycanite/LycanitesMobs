package lycanite.lycanitesmobs.saltwatermobs.model;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.saltwatermobs.SaltwaterMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAbtu extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelAbtu() {
        this(1.0F);
    }
    
    public ModelAbtu(float shadowSize) {
    	// Load Model:
    	this.initModel("abtu", SaltwaterMobs.group, "entity/abtu");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.4F, 0.8F);
    	setPartCenter("mouth", 0F, 0.25F, 1.0F);
    	setPartCenter("body", 0F, 0.4F, 0.8F);
    	
    	// Lock Head:
    	this.lockHeadX = true;
    	this.lockHeadY = true;
    	
    	// Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
        this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
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
    		if(!lockHeadX)
    			rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    	}
    	
    	// Idle:
    	if(partName.equals("body")) {
			rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.05F - 0.05F);
		}
    	if(partName.equals("mouth")) {
    		this.subCenterPart("mouth");
    		this.rotate(15F - (float)-Math.toDegrees(MathHelper.cos(loop * -0.1F) * 0.05F - 0.05F), 0.0F, 0.0F);
    		this.unsubCenterPart("mouth");
    	}
    	
    	// Walking:
    	if(entity == null || entity.isInWater()) {
	    	if(partName.equals("body")) {
				rotY += (float)-Math.toDegrees(MathHelper.cos(time * 0.1F) * 0.2F);
			}
    	}
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("mouth")) {
	    		this.rotate(30.0F, 0.0F, 0.0F);
	    	}
		}
		
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }
    
    
    // ==================================================
   	//              Rotate and Translate
   	// ==================================================
    @Override
    public void childScale(String partName) {
    	if(partName.equals("head") || partName.equals("mouth"))
    		translate(-(getPartCenter(partName)[0] / 2), -(getPartCenter(partName)[1] / 2), -(getPartCenter(partName)[2] / 2));
    	else
        	super.childScale(partName);
    }
}
