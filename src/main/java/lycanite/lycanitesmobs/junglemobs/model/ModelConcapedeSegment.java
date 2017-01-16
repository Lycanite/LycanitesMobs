package lycanite.lycanitesmobs.junglemobs.model;

import lycanite.lycanitesmobs.core.model.ModelCustomObj;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelConcapedeSegment extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelConcapedeSegment() {
        this(1.0F);
    }
    
    public ModelConcapedeSegment(float shadowSize) {
        // Load Model:
        this.initModel("concapedesegment", JungleMobs.group, "entity/concapede");
    	
    	// Set Rotation Centers:
    	setPartCenter("body", 0F, 0.5F, 0.6F);
    	setPartCenter("frontleftleg", 0.6F, 0.6F, 0.15F);
    	setPartCenter("backleftleg", 0.6F, 0.6F, -0.15F);
    	setPartCenter("frontrightleg", -0.6F, 0.6F, 0.15F);
    	setPartCenter("backrightleg", -0.6F, 0.6F, -0.15F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;
    	
    	// Trophy:
        this.trophyScale = 0.7F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.6F};
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
    	
    	// Leg Angles:
    	if(partName.equals("frontleftleg") || partName.equals("backleftleg")
    			|| partName.equals("frontrightleg") || partName.equals("backrightleg"))
    		angleZ = 1F;
    	if(partName.equals("frontleftleg")) angleY = 10F / 360F;
    	if(partName.equals("backleftleg")) angleY = -10F / 360F;
    	if(partName.equals("frontrightleg")) angleY = -10F / 360F;
    	if(partName.equals("backrightleg")) angleY = 10F / 360F;
    	
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("frontrightleg") || partName.equals("backleftleg")) {
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float) Math.PI) * walkSwing * distance);
    	}
    	if(partName.equals("frontleftleg") || partName.equals("backrightleg")) {
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    	}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
