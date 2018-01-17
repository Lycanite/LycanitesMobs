package com.lycanitesmobs.core.model;

import com.google.gson.*;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.modelloader.obj.ObjObject;
import com.lycanitesmobs.core.modelloader.obj.TessellatorModel;
import com.lycanitesmobs.core.renderer.IItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ModelItemBase {

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

	// Animating:
	/** The animator INSTANCE, this is a helper class that performs actual GL11 functions, etc. **/
	protected Animator animator;
	/** The current animation part that is having an animation frame generated for. **/
	protected ModelObjPart currentAnimationPart;
	/** A list of models states that hold unique render/animation data for a specific itemstack INSTANCE. **/
	protected Map<ItemStack, ModelObjState> modelStates = new HashMap<>();
	/** The current model state for the entity that is being animated and rendered. **/
	protected ModelObjState currentModelState;


	// ==================================================
	//                    Init Model
	// ==================================================
	public ModelItemBase initModel(String name, GroupInfo groupInfo, String path) {
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
	//                     Render
	// ==================================================
	/**
	 * Renders this model based on an itemstack.
	 * @param itemStack The itemstack to render.
	 * @param hand The hand that is holding the item or null if in the inventory instead.
	 * @param renderer The renderer that is rendering this model, needed for texture binding.
	 */
	public void render(ItemStack itemStack, EnumHand hand, IItemModelRenderer renderer) {
		if(itemStack == null) {
			return;
		}
		float loop = 0; // TODO Create an animation loop, probably using a ModelObjState.

		// Bind Texture:
		renderer.bindItemTexture(this.getTexture(itemStack));

		// Generate Animation Frames:
		for(ObjObject part : this.wavefrontParts) {
			String partName = part.getName().toLowerCase();
			if(!this.canRenderPart(partName, itemStack))
				continue;
			this.currentAnimationPart = this.animationParts.get(partName);

			// Animate:
			this.animatePart(partName, itemStack, loop);

			// Positioning:
			if("base".equals(partName)) {
				this.translate(0.9f, 1f, -0.5f);
				this.rotate(90, -90, 0);
				this.scale(0.9f, 0.9f, 0.9f);
			}
		}

		// Render Parts:
		for(ObjObject part : this.wavefrontParts) {
			String partName = part.getName().toLowerCase();
			if(!this.canRenderPart(partName, itemStack))
				continue;
			this.currentAnimationPart = this.animationParts.get(partName);

			// Begin Rendering Part:
			GlStateManager.pushMatrix();
			GlStateManager.enableAlpha();

			// Apply Initial Offsets: (To Match Blender OBJ Export)
			this.doAngle(modelXRotOffset, 1F, 0F, 0F);
			this.doTranslate(0F, modelYPosOffset, 0F);

			// Apply Animation Frames:
			this.currentAnimationPart.applyAnimationFrames(this.animator);

			// Render Part:
			this.wavefrontObject.renderGroup(part, this.getPartColor(partName, itemStack), new Vector2f(0, 0));
			GlStateManager.popMatrix();
		}

		// Clear Animation Frames:
		for(ModelObjPart animationPart : this.animationParts.values()) {
			animationPart.animationFrames.clear();
		}
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	/** Returns true if the part can be rendered for the given stack. **/
	public boolean canRenderPart(String partName, ItemStack itemStack) {
		if(partName == null)
			return false;
		partName = partName.toLowerCase();

		// Check Animation Part:
		if(!this.animationParts.containsKey(partName))
			return false;

		return true;
	}


	// ==================================================
	//                   Animate Part
	// ==================================================
	/**
	 * Animates the individual part.
	 * @param partName The name of the part (should be made all lowercase).
	 * @param itemStack The itemstack to render.
	 * @param loop A continuous loop counting every tick, used for constant idle animations, etc.
	 */
	public void animatePart(String partName, ItemStack itemStack, float loop) {

	}


	// ==================================================
	//                   Get Texture
	// ==================================================
	/** Returns a texture ResourceLocation for the provided itemstack. **/
	public ResourceLocation getTexture(ItemStack itemStack) {
		return null;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part for the given itemstack. **/
	public Vector4f getPartColor(String partName, ItemStack itemStack) {
		return new Vector4f(1, 1, 1, 1);
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
