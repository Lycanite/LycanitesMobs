package lycanite.lycanitesmobs.desertmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.model.ModelCustomObj;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCrusk extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCrusk() {
        this(1.0F);
    }
    
    public ModelCrusk(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Crusk", DesertMobs.domain, "entity/crusk");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.7F, 2.4F);
    	setPartCenter("topleftmouth", 0F, 0.7F, 2.4F);
    	setPartCenter("toprightmouth", 0F, 0.7F, 2.4F);
    	setPartCenter("bottomleftmouth", 0F, 0.7F, 2.4F);
    	setPartCenter("bottomrightmouth", 0F, 0.7F, 2.4F);
    	setPartCenter("body", 0F, 0.7F, 1.8F);
    	setPartCenter("body01", 0F, 0.7F, 1.2F);
    	setPartCenter("body02", 0F, 0.7F, 0.6F);
    	setPartCenter("body03", 0F, 0.7F, 0.0F);
    	setPartCenter("body04", 0F, 0.7F, -0.6F);
    	setPartCenter("body05", 0F, 0.7F, -1.2F);
    	setPartCenter("body06", 0F, 0.7F, -1.8F);
    	setPartCenter("body07", 0F, 0.7F, -2.4F);
    	setPartCenter("body08", 0F, 0.7F, -3.0F);
    	setPartCenter("body09", 0F, 0.7F, -3.6F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;
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
    	float scaleX = 1F;
    	float scaleY = 1F;
    	float scaleZ = 1F;
    	
    	// Mouth (Idle, Attack, Sitting):
    	if(partName.equals("topleftmouth")) {
    		translate(0.3F, 0.3F, 0.7F);
    		angleX = -0.5F;
    		angleY = 0.5F;
    	}
    	if(partName.equals("toprightmouth")) {
    		translate(-0.3F, 0.3F, 0.7F);
    		angleX = -0.5F;
    		angleY = -0.5F;
    	}
    	if(partName.equals("bottomleftmouth")) {
    		translate(0.3F, -0.3F, 0.7F);
    		angleX = 0.5F;
    		angleY = 0.5F;
    	}
    	if(partName.equals("bottomrightmouth")) {
    		translate(-0.3F, -0.3F, 0.7F);
    		angleX = 0.5F;
    		angleY = -0.5F;
    	}
    	if(partName.equals("topleftmouth") || partName.equals("toprightmouth") || partName.equals("bottomleftmouth") || partName.equals("bottomrightmouth")) {
    		rotation += -Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    		if(((EntityCreatureBase)entity).justAttacked())
    			rotation = -20;
    		if(((EntityCreatureTameable)entity).isSitting())
    			rotation += 20;
        	rotate(rotation, angleX, angleY, angleZ);
        	rotation = 0F;
    	}
    	if(partName.equals("topleftmouth"))
    		translate(-0.3F, -0.3F, -0.7F);
    	if(partName.equals("toprightmouth"))
    		translate(0.3F, -0.3F, -0.7F);
    	if(partName.equals("bottomleftmouth"))
    		translate(-0.3F, 0.3F, -0.7F);
    	if(partName.equals("bottomrightmouth"))
    		translate(0.3F, 0.3F, -0.7F);
    	
    	// Walking:
    	float walkSwing = 0.8F;
    	if(((EntityCreatureBase)entity).getStealth() > 0 && ((EntityCreatureBase)entity).getStealth() < 1)
    		time = loop;
    	time /= 2;
    	if(partName.equals("head") || partName.equals("topleftmouth") || partName.equals("toprightmouth") || partName.equals("bottomleftmouth") || partName.equals("bottomrightmouth")) {
    		posX += MathHelper.sin((time - walkSwing) * walkSwing) * walkSwing;
    	}
    	if(partName.equals("body")) {
    		posX += MathHelper.sin(time * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time - walkSwing) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body01")) {
    		posX += MathHelper.sin((time + walkSwing) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin(time * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body02")) {
    		posX += MathHelper.sin((time + (walkSwing * 2)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + walkSwing) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body03")) {
    		posX += MathHelper.sin((time + (walkSwing * 3)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 2)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body04")) {
    		posX += MathHelper.sin((time + (walkSwing * 4)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 3)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body05")) {
    		posX += MathHelper.sin((time + (walkSwing * 5)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 4)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body06")) {
    		posX += MathHelper.sin((time + (walkSwing * 6)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 5)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body07")) {
    		posX += MathHelper.sin((time + (walkSwing * 7)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 6)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body08")) {
    		posX += MathHelper.sin((time + (walkSwing * 8)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 7)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	if(partName.equals("body09")) {
    		posX += MathHelper.sin((time + (walkSwing * 9)) * walkSwing) * walkSwing;
    		float parentX = MathHelper.sin((time + (walkSwing * 8)) * walkSwing) * walkSwing;
    		rotY += rotateToPoint(0, posX, -0.6F, parentX);
    	}
    	
    	// Stealth:
    	posY -= (2 * ((EntityCreatureBase)entity).getStealth());
    	
    	// Apply Animations:
    	translate(posX, posY, posZ);
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	scale(scaleX, scaleY, scaleZ);
    }
}
