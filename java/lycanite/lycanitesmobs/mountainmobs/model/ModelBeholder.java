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
public class ModelBeholder extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelBeholder() {
        this(1.0F);
    }
    
    public ModelBeholder(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Beholder", MountainMobs.group, "entity/beholder");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 2.0F, 0F);
    	setPartCenter("mouth", 0F, 1F, -0.5F);
    	setPartCenter("eye", 0F, 2.6F, 1.3F);

    	setPartCenter("tentacleleft01", 1.6F, 2.7F, -0.3F);
    	setPartCenter("tentacleleft02", 0.6F, 3.6F, -0.4F);
    	setPartCenter("tentacleleft03", 1.2F, 3.1F, -0.5F);
    	setPartCenter("tentacleleft04", 1.4F, 2.6F, -0.6F);
    	setPartCenter("tentacleleft05", 0.9F, 3.0F, -0.9F);

    	setPartCenter("tentacleright01", -1.6F, 2.7F, -0.3F);
    	setPartCenter("tentacleright02", -0.6F, 3.6F, -0.4F);
    	setPartCenter("tentacleright03", -1.2F, 3.1F, -0.5F);
    	setPartCenter("tentacleright04", -1.4F, 2.6F, -0.6F);
    	setPartCenter("tentacleright05", -0.9F, 3.0F, -0.9F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;

        // Trophy:
        this.trophyScale = 0.125F;
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
    	
		centerPartToPart(partName, "head");
    	rotZ -= Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.015F + 0.015F);
		uncenterPartToPart(partName, "head");
    	
    	// Look:
    	if(partName.equals("eye")) {
	    	rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
	    	rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    	}
    	
    	// Idle:
    	if(partName.equals("mouth")) {
    		rotX = MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F;
    	}
		float bob = -MathHelper.sin(loop * 0.05F) * 0.3F;
		posY += bob;
    	
    	float animationScaleZ = 0.09F;
    	float animationScaleY = 0.07F;
    	float animationScaleX = 0.05F;
    	float animationDistanceZ = 0.25F;
    	float animationDistanceY = 0.2F;
    	float animationDistanceX = 0.15F;
    	if(partName.equals("tentacleleft01")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	if(partName.equals("tentacleleft02")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	if(partName.equals("tentacleleft03")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	if(partName.equals("tentacleleft04")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	if(partName.equals("tentacleleft05")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	
    	if(partName.equals("tentacleright01")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	if(partName.equals("tentacleright02")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX += Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	if(partName.equals("tentacleright03")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY += Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	if(partName.equals("tentacleright04")) {
	        rotZ += Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	if(partName.equals("tentacleright05")) {
	        rotZ -= Math.toDegrees(MathHelper.cos(loop * animationScaleZ) * animationDistanceZ + animationDistanceZ);
	        rotY -= Math.toDegrees(MathHelper.cos(loop * animationScaleY) * animationDistanceY + animationDistanceY);
	        rotX -= Math.toDegrees(MathHelper.sin(loop * animationScaleX) * animationDistanceX);
    	}
    	
		// Attack:
    	if((entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked())) {
			if(partName.equals("mouth"))
				rotX += 20F;
			if(partName.contains("tentacleleft"))
				rotZ -= 25F;
			if(partName.contains("tentacleright"))
				rotZ += 25F;
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
