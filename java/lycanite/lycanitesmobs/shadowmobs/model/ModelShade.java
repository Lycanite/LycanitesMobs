package lycanite.lycanitesmobs.shadowmobs.model;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.model.ModelCustomObj;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelShade extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelShade() {
        this(1.0F);
    }

    public ModelShade(float shadowSize) {
    	// Load Model:
    	this.initModel("shade", ShadowMobs.group, "entity/shade");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 2.15F, 0.5F);
        setPartCenter("mouth", 0F, 2F, 0.75F);

    	setPartCenter("body", 0F, 1.8F, 0.1F);
    	
    	setPartCenter("armleft", 0.31F, 2.19F, -0.15F);
        setPartCenter("armright", -0.31F, 2.19F, -0.15F);

    	setPartCenter("legleft", 0.3F, 1.1F, 0F);
    	setPartCenter("legright", -0.3F, 1.1F, 0F);
    	
    	setPartCenter("tail", 0F, 1.05F, -0.05F);

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.0F};
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
            this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.2F - 0.1F), 0.0F, 0.0F);
        }
        if(partName.equals("armleft")) {
            rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
            rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
        if(partName.equals("armright")) {
            rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
            rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
        }
        if(partName.equals("tail")) {
            rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
            rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
        }
		
    	// Walking:
    	float walkSwing = 0.6F;
    	if(partName.equals("legleft"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("legright"))
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
        walkSwing = 0.05F;
        if(partName.equals("armright"))
            rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
        if(partName.equals("armleft"))
            rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);

        // Attack:
        if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
            if(partName.equals("mouth"))
                rotate(-25.0F, 0.0F, 0.0F);
            if(partName.equals("armleft"))
                rotate(25.0F, 0.0F, 0.0F);
            if(partName.equals("armright"))
                rotate(25.0F, 0.0F, 0.0F);
        }
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }
}
