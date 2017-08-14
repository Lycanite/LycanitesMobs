package com.lycanitesmobs.core.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.modelloader.obj.ObjObject;
import com.lycanitesmobs.core.modelloader.obj.TessellatorModel;
import com.lycanitesmobs.core.renderer.LayerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
    /** An instance of the model, the model should only be set once and not during every tick or things will get very laggy! **/
    public TessellatorModel wavefrontObject;

    /** A list of all parts that belong to this model's wavefront obj. **/
    public List<ObjObject> wavefrontParts;

    /** A list of all part definitions that this model will use when animating. **/
    public Map<String, ModelObjPart> animationParts = new HashMap<>();

    // Head:
    /** If true, head pieces will ignore the x look rotation when animating. **/
	public boolean lockHeadX = false;
    /** If true, head pieces will ignore the y look rotation when animating. **/
	public boolean lockHeadY = false;
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
    /** The current animation part that is having an animation frame generated for. **/
    protected ModelObjPart currentAnimationPart;

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
    //                    Load Model
    // ==================================================
    public static IModelCustomData loadModel(ResourceLocation resourceLocation) {
        return new OBJModel(new OBJModel.MaterialLibrary(), resourceLocation);
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

        // Load Animation Parts:
        ResourceLocation animPartsLoc = new ResourceLocation(groupInfo.filename, "models/" + path + "_parts.json");
        try {
            InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(animPartsLoc).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(reader);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            Iterator<JsonElement> jsonIterator = jsonArray.iterator();
            while (jsonIterator.hasNext()) {
                JsonObject partJson = jsonIterator.next().getAsJsonObject();
                String partName = partJson.get("name").getAsString();
                String partParentName = partJson.get("parent").getAsString();
                if(partParentName.isEmpty())
                    partParentName = null;
                float partCenterX = Float.parseFloat(partJson.get("centerX").getAsString());
                float partCenterY = Float.parseFloat(partJson.get("centerY").getAsString());
                float partCenterZ = Float.parseFloat(partJson.get("centerZ").getAsString());
                this.addAnimationPart(partName, partParentName, partCenterX, partCenterY, partCenterZ);
            }
        }
        catch (Exception e) {
            LycanitesMobs.printWarning("", "There was a problem loading animation parts for " + name + ":");
            e.printStackTrace();
        }

        // Assign Animation Part Children:
        for(ModelObjPart part : this.animationParts.values()) {
            LycanitesMobs.printDebug("", "Adding children parts for " + part.name);
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
        if(!(entity instanceof EntityLiving))
            return;

        // Assess Scale and Check if Trophy:
		boolean renderAsTrophy = false;
		if(scale < 0) {
            renderAsTrophy = true;
			scale = -scale;
		}
		else {
			scale *= 16;
			if(entity instanceof EntityCreatureBase) {
                scale *= ((EntityCreatureBase)entity).getRenderScale();
            }
		}

        // Generate Animation Frames:
        this.wavefrontObject.entity = entity;
        for(ObjObject part : this.wavefrontParts) {
            if(!this.canRenderPart(part.getName(), entity, layer, renderAsTrophy))
                continue;
            String partName = part.getName().toLowerCase();
            this.currentAnimationPart = this.animationParts.get(partName);

            // Animate:
            this.animatePart(partName, (EntityLiving)entity, time, distance, loop, -lookY, lookX, scale);

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

        // Render Parts:
        for(ObjObject part : this.wavefrontParts) {
            if(!this.canRenderPart(part.getName(), entity, layer, renderAsTrophy))
                continue;
            String partName = part.getName().toLowerCase();
            this.currentAnimationPart = this.animationParts.get(partName);

            // Begin Rendering Part:
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();

            // Apply Initial Offsets: (To Match Blender OBJ Export)
            this.doAngle(modelXRotOffset, 1F, 0F, 0F);
            this.doTranslate(0F, modelYPosOffset, 0F);

            // Child Scaling:
            if(this.isChild && !renderAsTrophy) {
                this.childScale(partName);
                if(this.bigChildHead && (partName.equals("head") || partName.equals("mouth")))
                    this.translate(-(this.currentAnimationPart.centerX / 2), -(this.currentAnimationPart.centerY / 2), -(this.currentAnimationPart.centerZ / 2));
            }

            // Trophy Scaling:
            if(renderAsTrophy)
                this.doScale(this.trophyScale, this.trophyScale, this.trophyScale);

            // Apply Entity Scaling:
            this.doScale(scale, scale, scale);

            // Apply Animation Frames:
            for(ModelObjAnimationFrame animationFrame : this.currentAnimationPart.animationFrames) {
                animationFrame.apply(this);
            }
            this.currentAnimationPart.animationFrames.clear();

            // Render Part:
            this.wavefrontObject.renderGroup(part, this.getPartColor(partName, entity, layer, renderAsTrophy));
            GlStateManager.popMatrix();
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
    	
    	// Head Looking:
    	if(partName.toLowerCase().contains("head")) {
    		if(!lockHeadX)
    			rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    		if(!lockHeadY)
    			rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
            rotate(rotX, rotY, rotZ);
    	}
    }
    
    
    // ==================================================
   	//                  Child Scale
   	// ==================================================
    public void childScale(String partName) {
    	doScale(0.5F, 0.5F, 0.5F);
    }
    
    
    // ==================================================
   	//                  GLL Actions
   	// ==================================================
    public void doAngle(float rotation, float angleX, float angleY, float angleZ) {
    	GL11.glRotatef(rotation, angleX, angleY, angleZ);
    }
    public void doRotate(float rotX, float rotY, float rotZ) {
        GL11.glRotatef(rotX, 1F, 0F, 0F);
        GL11.glRotatef(rotY, 0F, 1F, 0F);
        GL11.glRotatef(rotZ, 0F, 0F, 1F);
    }
    public void doTranslate(float posX, float posY, float posZ) {
    	GL11.glTranslatef(posX, posY, posZ);
    }
    public void doScale(float scaleX, float scaleY, float scaleZ) {
    	GL11.glScalef(scaleX, scaleY, scaleZ);
    }


    // ==================================================
    //                  Create Frames
    // ==================================================
    public void angle(float rotation, float angleX, float angleY, float angleZ) {
        this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame(this.currentAnimationPart, "angle", rotation, angleX, angleY, angleZ));
    }
    public void rotate(float rotX, float rotY, float rotZ) {
        this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame(this.currentAnimationPart, "rotate", 1, rotX, rotY, rotZ));
    }
    public void translate(float posX, float posY, float posZ) {
        this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame(this.currentAnimationPart, "translate", 1, posX, posY, posZ));
    }
    public void scale(float scaleX, float scaleY, float scaleZ) {
        this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame(this.currentAnimationPart, "scale", 1, scaleX, scaleY, scaleZ));
    }
}
