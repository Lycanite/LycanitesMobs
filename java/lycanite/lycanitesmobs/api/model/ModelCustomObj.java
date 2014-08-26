package lycanite.lycanitesmobs.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomObj extends ModelBase {
	
	// Model:
	public static float modelRotationOffset = 180F;
	public static float modelYOffset = -1.5F;
	public WavefrontObject model;
	public ArrayList<GroupObject> parts;
	public Map<String, float[]> partCenters = new HashMap<String, float[]>();
	public Map<String, float[]> partSubCenters = new HashMap<String, float[]>();
	public Map<String, float[]> offsets = new HashMap<String, float[]>();
	public boolean lockHeadX = false;
	public boolean lockHeadY = false;
    
    // Coloring:
	/** If true, no color effects will be applied, this is usually used for red damage overlays, etc. **/
    public boolean dontColor = false;
    
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCustomObj() {
        this(1.0F);
    }
    
    public ModelCustomObj(float shadowSize) {
    	// Load model and parts array.
    }
    
    
    // ==================================================
   	//                  Render Model
   	// ==================================================
    @Override
    public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.render(entity, time, distance, loop, lookY, lookX, scale);
    	for(GroupObject part : parts) {
    		boolean headModel = false;
    		if(scale < 0) {
    			headModel = true;
				scale = -scale;
    		}
    		
    		if(!headModel || "head".equalsIgnoreCase(part.name) || "mouth".equalsIgnoreCase(part.name)) {
		    	GL11.glPushMatrix();
		    	this.rotate(modelRotationOffset, 1F, 0F, 0F);
		    	this.translate(0F, modelYOffset, 0F);
		    	if(this.isChild && !headModel)
		    		this.childScale(part.name.toLowerCase());
		    	this.centerPart(part.name.toLowerCase());
		    	this.animatePart(part.name.toLowerCase(), (EntityLiving)entity, time, distance, loop, -lookY, lookX, scale);
		    	if(headModel) {
		    		if("mouth".equalsIgnoreCase(part.name))
			    		this.centerPartToPart("mouth", "head");
		    		this.uncenterPart(part.name.toLowerCase());
		    	}
		    	this.uncenterPart(part.name.toLowerCase());
		    	
	    		part.render();
	    		GL11.glPopMatrix();
    		}
    	}
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
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
    	
    	// Head:
    	if(partName.equals("head") || partName.equals("lefthead") || partName.equals("righthead")) {
    		if(!lockHeadX)
    			rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
    	}
    	
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
    
    
    // ==================================================
   	//              Rotate and Translate
   	// ==================================================
    public void childScale(String partName) {
    	scale(0.5F, 0.5F, 0.5F);
    }
    
    
    // ==================================================
   	//              Rotate and Translate
   	// ==================================================
    public void rotate(float rotX, float rotY, float rotZ) {
    	GL11.glRotatef(rotX, 1F, 0F, 0F);
    	GL11.glRotatef(rotY, 0F, 1F, 0F);
    	GL11.glRotatef(rotZ, 0F, 0F, 1F);
    }
    public void rotate(float rotation, float angleX, float angleY, float angleZ) {
    	GL11.glRotatef(rotation, angleX, angleY, angleZ);
    }
    public void translate(float posX, float posY, float posZ) {
    	GL11.glTranslatef(posX, posY, posZ);
    }
    public void scale(float scaleX, float scaleY, float scaleZ) {
    	GL11.glScalef(scaleX, scaleY, scaleZ);
    }
    
    
    // ==================================================
   	//                  Part Centers
   	// ==================================================
    // ========== Set and Get ==========
    public void setPartCenter(String partName, float centerX, float centerY, float centerZ) {
    	partCenters.put(partName, new float[] {centerX, centerY, centerZ});
    }
    public void setPartCenters(float centerX, float centerY, float centerZ, String... partNames) {
    	for(String partName : partNames)
    		setPartCenter(partName, centerX, centerY, centerZ);
    }
    public float[] getPartCenter(String partName) {
    	if(!partCenters.containsKey(partName)) return new float[] {0.0F, 0.0F, 0.0F};
    	return partCenters.get(partName);
    }
    
    // ========== Apply Centers ==========
    public void centerPart(String partName) {
    	if(!partCenters.containsKey(partName)) return;
    	float[] partCenter = partCenters.get(partName);
    	translate(partCenter[0], partCenter[1], partCenter[2]);
    }
    public void uncenterPart(String partName) {
    	if(!partCenters.containsKey(partName)) return;
    	float[] partCenter = partCenters.get(partName);
    	translate(-partCenter[0], -partCenter[1], -partCenter[2]);
    }
    
    // ========== Copy Centers to Other Parts ==========
    public void centerPartToPart(String part, String targetPart) {
    	uncenterPart(part);
    	float[] partCenter = partCenters.get(targetPart);
    	translate(partCenter[0], partCenter[1], partCenter[2]);
    }
    public void uncenterPartToPart(String part, String targetPart) {
    	float[] partCenter = partCenters.get(targetPart);
    	translate(-partCenter[0], -partCenter[1], -partCenter[2]);
    	centerPart(part);
    }
    
    // ========== Compare Centers ==========
    public float[] comparePartCenters(String centerPartName, String targetPartName) {
    	float[] centerPart = getPartCenter(centerPartName);
    	float[] targetPart = getPartCenter(targetPartName);
    	float[] partDifference = new float[3];
    	for(int i = 0; i < 3; i++)
    		partDifference[i] = targetPart[i] - centerPart[i];
    	return partDifference;
    }
    
    
    // ==================================================
   	//            Part Sub Centers and Offsets
   	// ==================================================
    public void setPartSubCenter(String partName, float centerX, float centerY, float centerZ) {
    	partSubCenters.put(partName, new float[] {centerX, centerY, centerZ});
    }
    public void setPartSubCenters(float centerX, float centerY, float centerZ, String... partNames) {
    	for(String partName : partNames)
    		setPartSubCenter(partName, centerX, centerY, centerZ);
    }
    public void subCenterPart(String partName) {
    	float[] offset = getSubCenterOffset(partName);
    	if(offset == null) return;
    	translate(offset[0], offset[1], offset[2]);
    }
    public void unsubCenterPart(String partName) {
    	float[] offset = getSubCenterOffset(partName);
    	if(offset == null) return;
    	translate(-offset[0], -offset[1], -offset[2]);
    }
    public float[] getSubCenterOffset(String partName) {
    	if(!partCenters.containsKey(partName)) return null;
    	if(!partSubCenters.containsKey(partName)) return null;
    	float[] partCenter = partCenters.get(partName);
    	float[] partSubCenter = partSubCenters.get(partName);
    	float[] offset = new float[3];
    	for(int coord = 0; coord < 3; coord++)
    		offset[coord] = partSubCenter[coord] - partCenter[coord];
    	return offset;
    }
    
    public void setOffset(String offsetName, float[] offset) {
    	offsets.put(offsetName,  offset);
    }
    public float[] getOffset(String offsetName) {
    	if(!offsets.containsKey(offsetName)) return new float[] { 0.0F, 0.0F, 0.0F };
    	return offsets.get(offsetName);
    }
    
    
    // ==================================================
   	//                      Math
   	// ==================================================
    public double rotateToPoint(double aTarget, double bTarget) {
    	return rotateToPoint(0, 0, aTarget, bTarget);
    }
    public double rotateToPoint(double aCenter, double bCenter, double aTarget, double bTarget) {
    	if(aTarget - aCenter == 0)
    		if(aTarget > aCenter) return 0;
    		else if(aTarget < aCenter) return 180;
    	if(bTarget - bCenter == 0)
    		if(bTarget > bCenter) return 90;
    		else if(bTarget < bCenter) return -90;
    	if(aTarget - aCenter == 0 && bTarget - bCenter == 0)
    		return 0;
    	return Math.toDegrees(Math.atan2(aCenter - aTarget, bCenter - bTarget) - Math.PI / 2);
    }
    public double[] rotateToPoint(double xCenter, double yCenter, double zCenter, double xTarget, double yTarget, double zTarget) {
    	double[] rotations = new double[3];
    	rotations[0] = rotateToPoint(yCenter, -zCenter, yTarget, -zTarget);
    	rotations[1] = rotateToPoint(-zCenter, xCenter, -zTarget, xTarget);
    	rotations[2] = rotateToPoint(yCenter, xCenter, yTarget, xTarget);
    	return rotations;
    }
}
