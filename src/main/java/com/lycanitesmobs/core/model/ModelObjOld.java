package com.lycanitesmobs.core.model;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.modelloader.obj.ObjObject;
import com.lycanitesmobs.core.modelloader.obj.TessellatorModel;
import com.lycanitesmobs.core.renderer.LayerBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ModelObjOld extends ModelCustom {
    // Global:
    /** An initial x rotation applied to make Blender models match Minecraft. **/
    public static float modelXRotOffset = 180F;
    /** An initial y offset applied to make Blender models match Minecraft. **/
    public static float modelYPosOffset = -1.5F;
	
	// Model:
    /** An instance of the model, the model should only be set once and not during every tick or things will get very laggy! **/
    public TessellatorModel wavefrontObject;

    /** A list of all parts that belong to this model. **/
    public List<ObjObject> wavefrontParts;

    /** A map containing the XYZ offset for each part to use when centering. **/
	public Map<String, float[]> partCenters = new HashMap<>();
    /** A map containing the XYZ sub-offset for each part to use when centering. These are for parts with two centers such as mouth parts that match their centers to the head part but have a subcenter for opening and closing. **/
	public Map<String, float[]> partSubCenters = new HashMap<>();
    /** A map to be used on the fly, this allows one part to apply a position offset to another part. This is no longer used though and will be made redundant when the new model code is created. **/
	public Map<String, float[]> offsets = new HashMap<>();

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
    /** Used for positioning this model's mouth parts when displaying as a trophy. If an empty array, no offset is applied, otherwise it must have at least 3 entries (x, y, z). **/
    public float[] trophyMouthOffset = new float[0];

    // Coloring:
	/** If true, no color effects will be applied, this is usually used for when the model is rendered as a red damage overlay, etc. **/
    public boolean dontColor = false;

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelObjOld() {
        this(1.0F);
    }
    
    public ModelObjOld(float shadowSize) {
    	// Here a model should get its model, collect its parts into a list and then set the centers for each part.
    }


    // ==================================================
    //                    Load Model
    // ==================================================
    public static IModel loadModel(ResourceLocation resourceLocation) {
        return new OBJModel(new OBJModel.MaterialLibrary(), resourceLocation);
    }


    // ==================================================
    //                    Init Model
    // ==================================================
    public ModelObjOld initModel(String name, GroupInfo groupInfo, String path) {
        this.wavefrontObject = new TessellatorModel(new ResourceLocation(groupInfo.filename, "models/" + path + ".obj"));
        this.wavefrontParts = this.wavefrontObject.objObjects;
        if(this.wavefrontParts.isEmpty())
            LycanitesMobs.printWarning("", "Unable to load any parts for the " + name + " model!");

        return this;
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
     * @param layer The layer that is being rendered, if null the default base layer is being rendered.
     * @param scale Use to scale this mob. The default scale is 0.0625 (not sure why)! For a trophy/head-only model, set the scale to a negative amount, -1 will return a head similar in size to that of a Zombie head.
     */
    @Override
    public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale, LayerBase layer) {
        // Assess Scale and Check if Trophy:
		boolean trophyModel = false;
		if(scale < 0) {
            trophyModel = true;
			scale = -scale;
		}
		else {
			scale *= 16;
			if(entity instanceof EntityCreatureBase) {
                scale *= ((EntityCreatureBase)entity).getRenderScale();
            }
		}

		// GUI Render:
		if(entity instanceof EntityCreatureBase) {
			EntityCreatureBase creature = (EntityCreatureBase)entity;
			if(creature.onlyRenderTicks >= 0) {
				loop = creature.onlyRenderTicks;
			}
		}

        // Render and Animate Each Part:
        for(ObjObject part : this.wavefrontParts) {
    		if(part.getName() == null)
    			continue;
            String partName = part.getName().toLowerCase();

            // Trophy - Check if Trophy Part:
    		boolean isTrophyPart = this.isTrophyPart(partName);
    		if(this.bodyIsTrophy && partName.contains("body")) {
                isTrophyPart = true;
    		}

            // Skip Part If Not Rendered:
            if(!this.canRenderPart(partName, entity, layer, trophyModel) || (trophyModel && !isTrophyPart))
                continue;

            // Begin Rendering Part:
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();

            // Apply Initial Offsets: (To Match Blender OBJ Export)
            this.rotate(modelXRotOffset, 1F, 0F, 0F);
            this.translate(0F, modelYPosOffset, 0F);

            // Baby Heads:
            if(this.isChild && !trophyModel)
                this.childScale(partName);

            // Apply Scales:
            this.scale(scale, scale, scale);
            if(trophyModel)
                this.scale(this.trophyScale, this.trophyScale, this.trophyScale);

            // Animate (Part is centered and then animated):
            this.centerPart(partName);
            this.animatePart(partName, (EntityLiving)entity, time, distance, loop, -lookY, lookX, scale);

            // Trophy - Positioning:
            if(trophyModel) {
                if(!partName.contains("head") && !partName.contains("body")) {
                	float[] mouthOffset = this.comparePartCenters(this.bodyIsTrophy ? "body" : "head", partName);
                    this.translate(mouthOffset[0], mouthOffset[1], mouthOffset[2]);
                    if(this.trophyMouthOffset.length >= 3)
                    	this.translate(this.trophyMouthOffset[0], this.trophyMouthOffset[1], this.trophyMouthOffset[2]);
                }
                if(partName.contains("head")) {
                	if(!partName.contains("left")) {
                			this.translate(-0.3F, 0, 0);
                			this.rotate(5F, 0, 1, 0);
                	}
                	if(!partName.contains("right")) {
                			this.translate(0.3F, 0, 0);
                			this.rotate(-5F, 0, 1, 0);
                	}
                }
                this.uncenterPart(partName);
                if(this.trophyOffset.length >= 3)
                    this.translate(this.trophyOffset[0], this.trophyOffset[1], this.trophyOffset[2]);
            }

            // Render:
            this.uncenterPart(partName);
			this.onRenderStart(layer, entity, trophyModel);
            this.wavefrontObject.renderGroup(part, this.getPartColor(partName, entity, layer, trophyModel, loop), this.getPartTextureOffset(partName, entity, layer, trophyModel, loop));
			this.onRenderFinish(layer, entity, trophyModel);
			GlStateManager.popMatrix();
		}
	}

	/** Called just before a part is being rendered. **/
	public void onRenderStart(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		if(layer != null) {
			layer.onRenderStart(entity, renderAsTrophy);
		}
	}

	/** Called just after a part is being rendered. **/
	public void onRenderFinish(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		GlStateManager.disableBlend();
		if(layer != null) {
			layer.onRenderFinish(entity, renderAsTrophy);
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
    	if(partName.contains("head") || partName.contains("mouth") || partName.contains("eye"))
			return true;
    	return false;
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
     * @param scale Used for scale based changes during animation but not to actually apply the scale as it is applied in the renderer method.
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
   	//                   Part Centers
   	// ==================================================
    // ========== Add Animation Part ==========
    // ========== Set and Get ==========
    public void setPartCenter(String partName, float centerX, float centerY, float centerZ) {
    	if(this.isTrophyPart(partName))
    		this.bodyIsTrophy = false;
    	this.partCenters.put(partName, new float[] {centerX, centerY, centerZ});
    }
    public void setPartCenters(float centerX, float centerY, float centerZ, String... partNames) {
    	for(String partName : partNames)
    		this.setPartCenter(partName, centerX, centerY, centerZ);
    }
    public float[] getPartCenter(String partName) {
    	if(!this.partCenters.containsKey(partName)) return new float[] {0.0F, 0.0F, 0.0F};
    	return this.partCenters.get(partName);
    }
    
    // ========== Apply Centers ==========
    public void centerPart(String partName) {
    	if(!this.partCenters.containsKey(partName)) return;
    	float[] partCenter = this.partCenters.get(partName);
    	this.translate(partCenter[0], partCenter[1], partCenter[2]);
    }
    public void uncenterPart(String partName) {
    	if(!this.partCenters.containsKey(partName)) return;
    	float[] partCenter = this.partCenters.get(partName);
    	this.translate(-partCenter[0], -partCenter[1], -partCenter[2]);
    }
    
    // ========== Copy Centers to Other Parts ==========
    public void centerPartToPart(String part, String targetPart) {
    	this.uncenterPart(part);
    	float[] partCenter = this.partCenters.get(targetPart);
    	if(partCenter != null)
    		this.translate(partCenter[0], partCenter[1], partCenter[2]);
    }
    public void uncenterPartToPart(String part, String targetPart) {
    	float[] partCenter = this.partCenters.get(targetPart);
    	if(partCenter != null)
    		this.translate(-partCenter[0], -partCenter[1], -partCenter[2]);
    	this.centerPart(part);
    }
    
    // ========== Compare Centers ==========
    public float[] comparePartCenters(String centerPartName, String targetPartName) {
    	float[] centerPart = getPartCenter(centerPartName);
    	float[] targetPart = getPartCenter(targetPartName);
    	float[] partDifference = new float[3];
    	if(targetPart == null)
    		return partDifference;
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
