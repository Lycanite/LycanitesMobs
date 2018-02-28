package com.lycanitesmobs.core.model;

import com.google.gson.*;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.modelloader.obj.ObjObject;
import com.lycanitesmobs.core.modelloader.obj.TessellatorModel;
import com.lycanitesmobs.core.renderer.LayerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ModelObj extends ModelCustom {
    // Global:
    /** An initial x rotation applied to make Blender models match Minecraft. **/
    public static float modelXRotOffset = 180F;
    /** An initial y offset applied to make Blender models match Minecraft. **/
    public static float modelYPosOffset = -1.5F;

	// Model:
    /** An INSTANCE of the model, the model should only be set once and not during every tick or things will get very laggy! **/
    public TessellatorModel wavefrontObject;

    /** A list of all parts that belong to this model's wavefront obj. **/
    public List<ObjObject> wavefrontParts;

    /** A list of all part definitions that this model will use when animating. **/
    public Map<String, ModelObjPart> animationParts = new HashMap<>();

    // Looking and Head:
    /** Used to scale how far the head part will turn based on the looking X angle. **/
	public float lookHeadScaleX = 1;
    /** Used to scale how far the head part will turn based on the looking Y angle. **/
	public float lookHeadScaleY = 1;
    /** Used to scale how far the neck part will turn based on the looking X angle. **/
    public float lookNeckScaleX = 0;
    /** Used to scale how far the neck part will turn based on the looking Y angle. **/
    public float lookNeckScaleY = 0;
    /** Used to scale how far the head part will turn based on the looking X angle. **/
    public float lookBodyScaleX = 0;
    /** Used to scale how far the head part will turn based on the looking Y angle. **/
    public float lookBodyScaleY = 0;
	/** If true, the head and mouth of this model wont be scaled down when the mob is a child for a bigger head. **/
	public boolean bigChildHead = false;

    // Head Model:
	/** For trophies. Used for displaying a body in place of a head/mouth if the model has the head attached to the body part. Set to false if a head/mouth part is added. **/
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

    // Animating:
    /** The animator INSTANCE, this is a helper class that performs actual GL11 functions, etc. **/
    protected Animator animator;
	/** The current animation part that is having an animation frame generated for. **/
	protected ModelObjPart currentAnimationPart;
    /** A list of models states that hold unique render/animation data for a specific entity INSTANCE. **/
    protected Map<Entity, ModelObjState> modelStates = new HashMap<>();
    /** The current model state for the entity that is being animated and rendered. **/
    protected ModelObjState currentModelState;

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelObj() {
        this(1.0F);
    }

    public ModelObj(float shadowSize) {
    	// Here a model should get its model, collect its parts into a list and then create ModelObjPart objects for each part.
    }


    // ==================================================
    //                    Init Model
    // ==================================================
    public ModelObj initModel(String name, GroupInfo groupInfo, String path) {
        // Load Obj Model:
        this.wavefrontObject = new TessellatorModel(new ResourceLocation(groupInfo.filename, "models/" + path + ".obj"));
        this.wavefrontParts = this.wavefrontObject.objObjects;
        if(this.wavefrontParts.isEmpty())
            LycanitesMobs.printWarning("", "Unable to load any parts for the " + name + " model!");

        // Create Animator:
		this.animator = new Animator();

        // Load Animation Parts:
        ResourceLocation animPartsLoc = new ResourceLocation(groupInfo.filename, "models/" + path + "_parts.json");
        try {
			Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
            InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(animPartsLoc).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
				JsonArray jsonArray = JsonUtils.fromJson(gson, reader, JsonArray.class);
                Iterator<JsonElement> jsonIterator = jsonArray.iterator();
                while (jsonIterator.hasNext()) {
                    JsonObject partJson = jsonIterator.next().getAsJsonObject();
                    String partName = partJson.get("name").getAsString();
                    String partParentName = partJson.get("parent").getAsString();
                    if (partParentName.isEmpty())
                        partParentName = null;
                    float partCenterX = Float.parseFloat(partJson.get("centerX").getAsString());
                    float partCenterY = Float.parseFloat(partJson.get("centerY").getAsString());
                    float partCenterZ = Float.parseFloat(partJson.get("centerZ").getAsString());
                    this.addAnimationPart(partName, partParentName, partCenterX, partCenterY, partCenterZ);
                }
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
        }
        catch (Exception e) {
            LycanitesMobs.printWarning("", "There was a problem loading animation parts for " + name + ":");
            e.printStackTrace();
        }

        // Assign Animation Part Children:
        for(ModelObjPart part : this.animationParts.values()) {
            part.addChildren(this.animationParts.values().toArray(new ModelObjPart[this.animationParts.size()]));
        }

        return this;
    }


    // ==================================================
    //                      Parts
    // ==================================================
    // ========== Add Animation Part ==========
    public void addAnimationPart(String partName, String parentName, float centerX, float centerY, float centerZ) {
        partName = partName.toLowerCase();
        if(this.animationParts.containsKey(partName)) {
            LycanitesMobs.printWarning("", "Tried to add an animation part that already exists: " + partName + ".");
            return;
        }
        if(parentName != null) {
            parentName = parentName.toLowerCase();
            if(parentName.equals(partName))
                parentName = null;
        }
        ModelObjPart animationPart = new ModelObjPart(partName, parentName, centerX, centerY, centerZ);
        this.animationParts.put(partName, animationPart);
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
		boolean renderAsTrophy = false;
		if(scale < 0) {
            renderAsTrophy = true;
			scale = -scale;
		}
		else {
			if(entity instanceof EntityCreatureBase) {
				scale *= 16;
                scale *= ((EntityCreatureBase)entity).getRenderScale();
            }
			else if(entity instanceof EntityProjectileBase) {
				scale *= 4;
				scale *= ((EntityProjectileBase)entity).getProjectileScale();
			}
		}

		// Animation States:
        this.currentModelState = this.getModelState(entity);
        this.updateAttackProgress(entity);

        // Generate Animation Frames:
        for(ObjObject part : this.wavefrontParts) {
            String partName = part.getName().toLowerCase();
            //if(!this.canRenderPart(partName, entity, layer, renderAsTrophy))
                //continue;
            this.currentAnimationPart = this.animationParts.get(partName);
            if(this.currentAnimationPart == null)
            	continue;

            // Animate:
			if(entity instanceof EntityLiving) {
				this.animatePart(partName, (EntityLiving) entity, time, distance, loop, -lookY, lookX, scale);
			}

            // Trophy Positioning:
            if(renderAsTrophy) {
                if(partName.contains("head")) {
                    if(!partName.contains("left")) {
                        this.translate(-0.3F, 0, 0);
                        this.angle(5F, 0, 1, 0);
                    }
                    if(!partName.contains("right")) {
                        this.translate(0.3F, 0, 0);
                        this.angle(-5F, 0, 1, 0);
                    }
                }
                if(this.trophyOffset.length >= 3)
                    this.translate(this.trophyOffset[0], this.trophyOffset[1], this.trophyOffset[2]);
            }
    	}

		// Render Start:
		this.onRenderStart(layer, entity, renderAsTrophy);

		// Render Parts:
        for(ObjObject part : this.wavefrontParts) {
            String partName = part.getName().toLowerCase();
            if(!this.canRenderPart(partName, entity, layer, renderAsTrophy))
                continue;
            this.currentAnimationPart = this.animationParts.get(partName);
            if(this.currentAnimationPart == null) {
            	continue;
			}

            // Begin Rendering Part:
            GlStateManager.pushMatrix();

            // Apply Initial Offsets: (To Match Blender OBJ Export)
            this.animator.doAngle(modelXRotOffset, 1F, 0F, 0F);
            this.animator.doTranslate(0F, modelYPosOffset, 0F);

            // Child Scaling:
            if(this.isChild && !renderAsTrophy) {
                this.childScale(partName);
                if(this.bigChildHead && (partName.equals("head") || partName.equals("mouth")))
                    this.translate(-(this.currentAnimationPart.centerX / 2), -(this.currentAnimationPart.centerY / 2), -(this.currentAnimationPart.centerZ / 2));
            }

            // Trophy Scaling:
            if(renderAsTrophy)
                this.animator.doScale(this.trophyScale, this.trophyScale, this.trophyScale);

            // Apply Entity Scaling:
            this.animator.doScale(scale, scale, scale);

            // Apply Animation Frames:
            this.currentAnimationPart.applyAnimationFrames(this.animator);

            // Render Part:
            this.wavefrontObject.renderGroup(part, this.getPartColor(partName, entity, layer, renderAsTrophy, loop), this.getPartTextureOffset(partName, entity, layer, renderAsTrophy, loop));
			GlStateManager.popMatrix();
        }

		// Render Finish:
		this.onRenderFinish(layer, entity, renderAsTrophy);

		// Clear Animation Frames:
        for(ModelObjPart animationPart : this.animationParts.values()) {
            animationPart.animationFrames.clear();
        }
    }

	/** Called just before a layer is rendered. **/
	public void onRenderStart(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		//GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		if(layer != null) {
			layer.onRenderStart(entity, renderAsTrophy);
		}
	}

	/** Called just after a layer is rendered. **/
	public void onRenderFinish(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		GlStateManager.disableBlend();
		//GlStateManager.enableAlpha();
		if(layer != null) {
			layer.onRenderFinish(entity, renderAsTrophy);
		}
	}


    // ==================================================
    //                Can Render Part
    // ==================================================
    /** Returns true if the part can be rendered, this can do various checks such as Yale wool only rendering in the YaleWoolLayer or hiding body parts in place of armor parts, etc. **/
    @Override
    public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
        if(partName == null)
            return false;
        partName = partName.toLowerCase();

        // Check Animation Part:
        if(!this.animationParts.containsKey(partName))
            return false;

        // Check Trophy:
        if(trophy && !this.isTrophyPart(partName))
            return false;

        return super.canRenderPart(partName, entity, layer, trophy);
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
    	if(this.bodyIsTrophy && partName.contains("body"))
    	    return true;
    	return false;
    }
    
    
    // ==================================================
   	//                   Animate Part
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
    	float rotX = 0F;
    	float rotY = 0F;
    	float rotZ = 0F;
    	
    	// Looking:
    	if(partName.toLowerCase().equals("head")) {
    		rotX += (Math.toDegrees(lookX / (180F / (float)Math.PI)) * this.lookHeadScaleX);
    		rotY += (Math.toDegrees(lookY / (180F / (float)Math.PI))) * this.lookHeadScaleY;
    	}
        if(partName.equals("neck")) {
            rotX += (Math.toDegrees(lookX / (180F / (float)Math.PI)) * this.lookNeckScaleX);
            rotY += (Math.toDegrees(lookY / (180F / (float)Math.PI))) * this.lookNeckScaleY;
        }

        // Create Animation Frames:
        this.rotate(rotX, rotY, rotZ);
    }

    /** Returns an existing or new model state for the given entity. **/
    public ModelObjState getModelState(Entity entity) {
        if(entity == null)
            return null;
        if(this.modelStates.containsKey(entity)) {
            if(entity.isDead) {
                this.modelStates.remove(entity);
                return null;
            }
            return this.modelStates.get(entity);
        }
        ModelObjState modelState = new ModelObjState(entity);
        this.modelStates.put(entity, modelState);
        return modelState;
    }
    
    
    // ==================================================
   	//                  Child Scale
   	// ==================================================
    public void childScale(String partName) {
    	this.animator.doScale(0.5F, 0.5F, 0.5F);
    }


    // ==================================================
    //                   Attack Frame
    // ==================================================
    public void updateAttackProgress(Entity entity) {
        if(this.currentModelState == null || !(entity instanceof EntityCreatureBase))
            return;
        EntityCreatureBase entityCreature = (EntityCreatureBase)entity;

        if(this.currentModelState.attackAnimationPlaying) {
            if (this.currentModelState.attackAnimationIncreasing) {
                this.currentModelState.attackAnimationProgress = Math.min(this.currentModelState.attackAnimationProgress + this.currentModelState.attackAnimationSpeed, 1F);
                if (this.currentModelState.attackAnimationProgress >= 1)
                    this.currentModelState.attackAnimationIncreasing = false;
            }
            else {
                this.currentModelState.attackAnimationProgress = Math.max(this.currentModelState.attackAnimationProgress - this.currentModelState.attackAnimationSpeed, 0F);
                if (this.currentModelState.attackAnimationProgress <= 0) {
                    this.currentModelState.attackAnimationPlaying = false;
                }
            }
        }
        else if(entityCreature.justAttacked()) {
            this.currentModelState.attackAnimationPlaying = true;
            this.currentModelState.attackAnimationIncreasing = true;
            this.currentModelState.attackAnimationProgress = 0;
        }
    }

    public float getAttackProgress() {
        if(this.currentModelState == null)
            return 0;
        return this.currentModelState.attackAnimationProgress;
    }


	// ==================================================
	//                  Create Frames
	// ==================================================
	public void angle(float rotation, float angleX, float angleY, float angleZ) {
		this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("angle", rotation, angleX, angleY, angleZ));
	}
	public void rotate(float rotX, float rotY, float rotZ) {
		this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("rotate", 1, rotX, rotY, rotZ));
	}
	public void translate(float posX, float posY, float posZ) {
		this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("translate", 1, posX, posY, posZ));
	}
	public void scale(float scaleX, float scaleY, float scaleZ) {
		this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("scale", 1, scaleX, scaleY, scaleZ));
	}


	// ==================================================
	//                   Shift Origin
	// ==================================================

	/**
	 * Moves the animation origin to a different part origin.
	 * @param fromPartName The part name to move the origin from.
	 * @param toPartName The part name to move the origin to.
	 */
	public void shiftOrigin(String fromPartName,  String toPartName) {
		ModelObjPart fromPart = this.animationParts.get(fromPartName);
		ModelObjPart toPart = this.animationParts.get(toPartName);
		float offsetX = toPart.centerX - fromPart.centerX;
		float offsetY = toPart.centerY - fromPart.centerY;
		float offsetZ = toPart.centerZ - fromPart.centerZ;
		this.translate(offsetX, offsetY, offsetZ);
	}

	/**
	 * Moves the animation origin back from a different part origin.
	 * @param fromPartName The part name that the origin moved from.
	 * @param toPartName The part name that the origin was moved to.
	 */
	public void shiftOriginBack(String fromPartName,  String toPartName) {
		ModelObjPart fromPart = this.animationParts.get(fromPartName);
		ModelObjPart toPart = this.animationParts.get(toPartName);
		float offsetX = toPart.centerX - fromPart.centerX;
		float offsetY = toPart.centerY - fromPart.centerY;
		float offsetZ = toPart.centerZ - fromPart.centerZ;
		this.translate(-offsetX, -offsetY, -offsetZ);
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
		rotations[0] = this.rotateToPoint(yCenter, -zCenter, yTarget, -zTarget);
		rotations[1] = this.rotateToPoint(-zCenter, xCenter, -zTarget, xTarget);
		rotations[2] = this.rotateToPoint(yCenter, xCenter, yTarget, xTarget);
		return rotations;
	}
}
