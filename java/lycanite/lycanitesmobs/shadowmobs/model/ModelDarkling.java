package lycanite.lycanitesmobs.shadowmobs.model;

import lycanite.lycanitesmobs.core.model.ModelCustomObj;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import lycanite.lycanitesmobs.shadowmobs.entity.EntityDarkling;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDarkling extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelDarkling() {
        this(1.0F);
    }

    public ModelDarkling(float shadowSize) {
    	// Load Model:
        this.initModel("darkling", ShadowMobs.group, "entity/darkling");
    	
    	// Set Rotation Centers:
        this.setPartCenter("head", 0F, 0.24833F, -0.36996F);
        this.setPartCenter("mouthleft", 0.16218F, 0.2703F, -0.46631F);
        this.setPartCenter("mouthright", -0.16218F, 0.2703F, -0.46631F);
        this.setPartCenter("body", 0F, 0.28549F, -0.05173F);

        this.setPartCenter("legleftfront", 0.132F, 0.24F, 0.0168F);
        this.setPartCenter("legleftmiddle", 0.12F, 0.24F, 0.1032F);
        this.setPartCenter("legleftback", 0.072F, 0.24F, 0.192F);

        this.setPartCenter("legrightfront", -0.132F, 0.24F, 0.0168F);
        this.setPartCenter("legrightmiddle", -0.12F, 0.24F, 0.1032F);
        this.setPartCenter("legrightback", -0.072F, 0.24F, 0.192F);

        // Head:
    	this.lockHeadX = true;
    	this.lockHeadY = true;

    	// Trophy:
        this.trophyScale = 1.0F;
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
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
        if(partName.equals("mouthleft") || partName.equals("mouthright")) {
            this.centerPartToPart(partName, "head");
            if(!this.lockHeadX)
                rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
            if(!this.lockHeadY)
                rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
            this.uncenterPartToPart(partName, "head");
        }

        // Idle:
        if(partName.equals("mouthleft"))
            rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F + (float)Math.PI) * 0.05F - 0.05F), 0.0F, 0.0F);
        if(partName.equals("mouthright"))
            rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F), 0.0F, 0.0F);
    	
    	// Leg Angles:
    	if(partName.equals("legleftfront") || partName.equals("legleftback") || partName.equals("legrightmiddle")
    			|| partName.equals("legleftmiddle") || partName.equals("legrightfront") || partName.equals("legrightback"))
    		angleZ = 1F;

    	if(partName.equals("legleftfront")) angleY = 20F / 360F;
    	if(partName.equals("legleftmiddle")) angleY = -5F;
    	if(partName.equals("legleftback")) angleY = -25F / 360F;

    	if(partName.equals("legrightfront")) angleY = -20F / 360F;
    	if(partName.equals("legrightmiddle")) angleY = 5F;
    	if(partName.equals("legrightback")) angleY = 25F / 360F;
    	
    	// Walking:
    	float walkSwing = 0.3F;
    	if(partName.equals("legleftfront") || partName.equals("legleftback") || partName.equals("legrightmiddle"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("legleftmiddle") || partName.equals("legrightfront") || partName.equals("legrightback"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
		float bob = MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance;
		if(bob < 0) bob += -bob * 2;
		posY += bob;

        // Latching:
        if(entity instanceof EntityDarkling) {
            if(((EntityDarkling)entity).hasLatchTarget()) {
                this.uncenterPart(partName);
                this.rotate(-90F, 0, 0);
                this.centerPart(partName);
            }
        }
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
