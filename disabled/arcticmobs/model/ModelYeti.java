package lycanite.lycanitesmobs.arcticmobs.model;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelYeti extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelYeti() {
        this(1.0F);
    }
    
    public ModelYeti(float shadowSize) {
    	// Load Model:
    	this.initModel("Yeti", ArcticMobs.group, "entity/yeti");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.7F, 1.1F);
    	setPartCenter("body",0F, 1.7F, 1.1F);
    	setPartCenter("legleftfront", 0.8F, 1.5F, 0.5F);
    	setPartCenter("legrightfront", -0.8F, 1.5F, 0.5F);
    	setPartCenter("legleftback", 0.7F, 0.9F, -1.5F);
    	setPartCenter("legrightback", -0.7F, 0.9F, -1.5F);

        // Tropy:
        this.trophyScale = 0.6F;
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
    	
    	// Walking:
    	float walkSwing = 0.3F;
    	if(partName.equals("legrightfront") || partName.equals("legleftback"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("legleftfront") || partName.equals("legrightback"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
				
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("legleftfront"))
	    		rotate(0.0F, -25.0F, 0.0F);
	    	if(partName.equals("legrightfront"))
	    		rotate(0.0F, 25.0F, 0.0F);
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
