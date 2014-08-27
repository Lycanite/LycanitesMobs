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
    // Global:
    /** An initial x rotation applied to make Blender models match Minecraft. **/
    public static float modelXRotOffset = 180F;
    /** An initial y offset applied to make Blender models match Minecraft. **/
    public static float modelYPosOffset = -1.5F;
	
	// Model:
    /** An instance of the model, the model should only be set once and not during every tick or things will get very laggy! **/
	public WavefrontObject model;
    /** A list of all parts that belong to this model. **/
	public ArrayList<GroupObject> parts;
    /** A map containing the XYZ offset for each part to use when centering. **/
	public Map<String, float[]> partCenters = new HashMap<String, float[]>();
    /** A map containing the XYZ sub-offset for each part to use when centering. These are for parts with two centers such as mouth parts that match their centers to the head part but have a subcenter for opening and closing. **/
	public Map<String, float[]> partSubCenters = new HashMap<String, float[]>();
    /** A map to be used on the fly, this allows one part to apply a position offset to another part. This is no longer used though and will be made redundant when the new model code is created. **/
	public Map<String, float[]> offsets = new HashMap<String, float[]>();

    // Head:
    /** If true, head pieces will ignore the x look rotation when animating. **/
	public boolean lockHeadX = false;
    /** If true, head pieces will ignore the y look rotation when animating. **/
	public boolean lockHeadY = false;

    // Head Model:
	/** For trophies. Used for displaying a body in place of a head/mount if the model has the head attached to the body part. Set to false if a head/mouth part is added. **/
	public boolean bodyIsTrophy = true;
    /** Used for scaling this model when displaying as a trophy. **/
    public float trophyScale = 1;
    /** Used for positioning this model when displaying as a trophy. If an empty array, no offset is applied, otherwise it must have at least 3 entries (x, y, z). **/
    public float[] trophyOffset = new float[0];
    
    // Coloring:
	/** If true, no color effects will be applied, this is usually used for when the model is rendered as a red damage overlay, etc. **/
    public boolean dontColor = false;
    
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCustomObj() {
        this(1.0F);
    }
    
    public ModelCustomObj(float shadowSize) {
    	// Here a model should get its model, collect its parts into a list and then set the centers for each part.
    }
    
    
    // ==================================================
   	//                  Render Model
   	// ==================================================
    /**
     * Renders this model. Can be rendered as a trophy (just head, mouth, etc) too, use scale for this.
     * @param entity Can't be null but can be any entity. If the mob's exact entity or an EntityCreatureBase is used more animations will be used.
     * @param time How long the model has been displayed for? This is currently unused.
     * @param distance Used for movement animations, this should just count up form 0 every tick and stop back at 0 when not moving.
     * @param loop A continuous loop counting every tick, used for constant idle animations, etc.
     * @param lookY A y looking rotation used by the head, etc.
     * @param lookX An x looking rotation used by the head, etc.
     * @param scale Use to scale this mob. The default scale is 0.0625 (not sure why)! For a trophy/head-only model, set the scale to a negative amount, -1 will return a head similar in size to that of a Zombie head.
     */
    @Override
    public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.render(entity, time, distance, loop, lookY, lookX, scale);

        // Assess Scale and Check if Trophy:
		boolean trophyModel = false;
		if(scale < 0) {
            trophyModel = true;
			scale = -scale;
		}
		else {
			scale *= 16;
		}

        // Render and Animate Each Part:
    	for(GroupObject part : this.parts) {
    		if(part.name == null)
    			continue;

            // Trophy - Check if Trophy Part:
    		boolean isTrophyPart = this.isTrophyPart(part);
    		if(this.bodyIsTrophy && part.name.toLowerCase().contains("body")) {
                isTrophyPart = true;
    		}
            if(trophyModel && !isTrophyPart)
                continue;

            // Begin Rendering Part:
            GL11.glPushMatrix();

            // Apply Initial Offsets: (To Match Blender OBJ Export)
            this.rotate(modelXRotOffset, 1F, 0F, 0F);
            this.translate(0F, modelYPosOffset, 0F);

            // Baby Heads:
            if(this.isChild && !trophyModel)
                this.childScale(part.name.toLowerCase());

            // Apply Scales:
            this.scale(scale, scale, scale);
            if(trophyModel)
                this.scale(this.trophyScale, this.trophyScale, this.trophyScale);

            // Animate (Part is centered and then animated):
            this.centerPart(part.name.toLowerCase());
            this.animatePart(part.name.toLowerCase(), (EntityLiving)entity, time, distance, loop, -lookY, lookX, scale);

            // Trophy - Positioning:
            if(trophyModel) {
                if("mouth".equalsIgnoreCase(part.name))
                    this.centerPartToPart("mouth", "head");
                this.uncenterPart(part.name.toLowerCase());
                if(this.trophyOffset.length >= 3)
                    this.translate(this.trophyOffset[0], this.trophyOffset[1], this.trophyOffset[2]);
            }

            // Finish Rendering Part (Part is returned to its position then rendered):
            this.uncenterPart(part.name.toLowerCase());
            part.render();
            GL11.glPopMatrix();
    	}
    }
    
    
    // ==================================================
   	//                     Trophy
   	// ==================================================
    /** Returns true if the provided part name should be shown for the trophy model. **/
    public boolean isTrophyPart(String partName) {
    	if(partName == null)
    		return false;
    	partName = partName.toLowerCase();
    	if(partName.contains("head") || partName.contains("mouth"))
			return true;
    	return false;
    }

    /** Returns true if the provided part should be shown for the trophy model. **/
    public boolean isTrophyPart(GroupObject part) {
    	if(part == null)
    		return false;
    	return this.isTrophyPart(part.name.toLowerCase());
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    /**
     * Animates the individual part.
     * @param partName The name of the part (should be made all lowercase).
     * @param entity Can't be null but can be any entity. If the mob's exact entity or an EntityCreatureBase is used more animations will be used.
     * @param time How long the model has been displayed for? This is currently unused.
     * @param distance Used for movement animations, this should just count up form 0 every tick and stop back at 0 when not moving.
     * @param loop A continuous loop counting every tick, used for constant idle animations, etc.
     * @param lookY A y looking rotation used by the head, etc.
     * @param lookX An x looking rotation used by the head, etc.
     * @param scale Used for scale based changes during animation but not to actually apply the scale as it is applied in the render method.
     */
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
    	if(partName.toLowerCase().contains("head")) {
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
    	if(this.isTrophyPart(partName))
    		this.bodyIsTrophy = false;
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
   	//                  Rotate to Point
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
