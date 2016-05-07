package lycanite.lycanitesmobs.freshwatermobs.model;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelStrider extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelStrider() {
        this(1.0F);
    }

    public ModelStrider(float shadowSize) {
    	// Load Model:
    	this.initModel("strider", FreshwaterMobs.group, "entity/strider");
    	


    	
    	// Set Rotation Centers:
        setPartCenter("mouth", 0F, 8.4F, 0.8F);
    	setPartCenter("body", 0F, 9.0F, 0F);

    	setPartCenter("legleftfront", 0.7F, 7.6F, 0.5F);
        setPartCenter("legrightfront", -0.7F, 7.6F, 0.5F);
    	setPartCenter("legleftback", 0.7F, 7.6F, -0.4F);
    	setPartCenter("legrightback", -0.7F, 7.6F, -0.4F);

        this.lockHeadX = true;
        this.lockHeadY = true;

        // Trophy:
        this.trophyScale = 0.4F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.0F};
        this.bodyIsTrophy = true;
    }
    
    
    // ==================================================
   	//                    Animate Part
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
        if(partName.equals("mouth")) {
            this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.1F - 0.1F), 0.0F, 0.0F);
        }
		
    	// Walking:
    	float walkSwing = 0.15F;
    	if(partName.equals("legrightfront") || partName.equals("legleftback"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("legleftfront") || partName.equals("legrightback"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);

        // Attack:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
            if(partName.equals("legleftfront"))
                rotate(-25.0F, 0.0F, 0.0F);
            if(partName.equals("legrightfront"))
                rotate(-25.0F, 0.0F, 0.0F);
        }
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }


    // ==================================================
    //              Rotate and Translate
    // ==================================================
    /*@Override
    public void childScale(String partName) {
        if(partName.equals("head"))
            translate(-(getPartCenter(partName)[0] / 2), -(getPartCenter(partName)[1] / 2), -(getPartCenter(partName)[2] / 2));
        else
            super.childScale(partName);
    }*/
}
