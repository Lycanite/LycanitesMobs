package lycanite.lycanitesmobs.junglemobs.model;

import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelVespidQueen extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelVespidQueen() {
        this(1.0F);
    }
    
    public ModelVespidQueen(float shadowSize) {
    	// Load Model:
    	this.initModel("vespidqueen", JungleMobs.group, "entity/vespidqueen");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.43F, 0.91F);
    	setPartCenter("body", 0F, 1.3F, 0.0F);
    	
    	setPartCenter("wingleft", 0.26F, 1.69F, 0.13F);
    	setPartCenter("wingright", -0.26F, 1.69F, 0.13F);
    	
    	setPartCenter("legleftfront", 0.234F, 1.209F, 0.546F);
    	setPartCenter("legleftmiddle", 0.247F, 1.17F, 0.416F);
    	setPartCenter("legleftback", 0.273F, 1.17F, 0.26F);
    	
    	setPartCenter("legrightfront", -0.234F, 1.209F, 0.546F);
    	setPartCenter("legrightmiddle", -0.247F, 1.17F, 0.416F);
    	setPartCenter("legrightback", -0.273F, 1.17F, 0.26F);
    	
    	// Trophy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.3F};
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
    	if(partName.equals("wingleft")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 3.2F) * 0.6F);
		    rotZ -= Math.toDegrees(MathHelper.sin(loop * 3.2F) * 0.6F);
    	}
    	if(partName.equals("wingright")) {
    		rotX = 20;
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 3.2F) * 0.6F);
	        rotZ -= Math.toDegrees(MathHelper.sin(loop * 3.2F + (float)Math.PI) * 0.6F);
    	}
    	if(partName.equals("legleftfront") || partName.equals("legleftback") || partName.equals("legrightmiddle")) {
    		rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("legrightfront") || partName.equals("legrightback") || partName.equals("legleftmiddle")) {
    		rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
	        rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
		float bob = -MathHelper.sin(loop * 0.2F) * 0.3F;
		if(bob < 0) bob = -bob;
		posY += bob;
		
    	// Apply Animations:
    	translate(posX, posY, posZ);
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    }
}
