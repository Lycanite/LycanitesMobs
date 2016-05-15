package lycanite.lycanitesmobs.mountainmobs.model;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.api.render.RenderCreature;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityYale;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelYale extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelYale() {
        this(1.0F);
    }
    
    public ModelYale(float shadowSize) {
    	// Load Model:
    	this.initModel("yale", MountainMobs.group, "entity/yale");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.85F, 1.0F);
    	setPartCenter("body", 0F, 1.0F, 0F);
    	setPartCenter("fur", 0F, 1.0F, 0F);
    	setPartCenter("armleft", 0.25F, 0.55F, 0.8F);
    	setPartCenter("armright", -0.25F, 0.55F, 0.8F);
    	setPartCenter("legleftfront", 0.2F, 1.2F, 0.85F);
    	setPartCenter("legrightfront", -0.2F, 1.2F, 0.85F);
    	setPartCenter("legleftback", 0.3F, 0.4F, -0.7F);
    	setPartCenter("legrightback", -0.3F, 0.4F, -0.7F);
    	
    	// Trophy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, -0.15F, -0.4F};
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
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.2F);
    	}
    	if(partName.equals("armright")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.2F);
    	}
    	
    	// Walking:
    	if(entity == null || entity.onGround || entity.isInWater()) {
	    	float walkSwing = 0.6F;
	    	if(partName.equals("armleft")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.0F * distance * 0.5F);
				rotZ -= Math.toDegrees(MathHelper.cos(time * walkSwing) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("armright")) {
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.0F * distance * 0.5F);
				rotZ += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 0.5F * distance * 0.5F);
	    	}
	    	if(partName.equals("legleftfront") || partName.equals("legrightback"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing + (float)Math.PI) * 1.4F * distance);
	    	if(partName.equals("legrightfront") || partName.equals("legleftback"))
	    		rotX += Math.toDegrees(MathHelper.cos(time * walkSwing) * 1.4F * distance);
    	}
		
		// Attack:
		if(entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked()) {
	    	if(partName.equals("armleft") || partName.equals("armright"))
	    		rotX += 20.0F;
		}
		
		// Jump:
		if(entity != null && !entity.onGround && !entity.isInWater()) {
	    	if(partName.equals("armleft")) {
		        rotZ -= 10;
		        rotX -= 50;
	    	}
	    	if(partName.equals("armright")) {
		        rotZ += 10;
		        rotX -= 50;
	    	}
	    	if(partName.equals("legleftfront") || partName.equals("legrightfront"))
	    		rotX += 50;
	    	if(partName.equals("legleftback") || partName.equals("legrightback"))
	    		rotX -= 50;
		}
		
		// Fur:
		if(!this.dontColor)
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if(partName.equals("fur") && entity instanceof EntityYale) {
			if(!((EntityYale)entity).hasFur()) {
				this.scale(0, 0, 0);
			}
			else if(entity instanceof EntityCreatureBase && !this.dontColor) {
				int colorID = ((EntityCreatureBase)entity).getColor();
                GlStateManager.color(RenderCreature.colorTable[colorID][0], RenderCreature.colorTable[colorID][1], RenderCreature.colorTable[colorID][2], 1.0F);
			}
		}
    	
    	// Apply Animations:
		this.rotate(rotation, angleX, angleY, angleZ);
    	this.rotate(rotX, rotY, rotZ);
    	this.translate(posX, posY, posZ);
    }
}
