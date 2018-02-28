package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.LayerScrolling;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;

@SideOnly(Side.CLIENT)
public class ModelWraith extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelWraith() {
        this(1.0F);
    }

    public ModelWraith(float shadowSize) {

		// Load Model:
		this.initModel("wraith", ElementalMobs.instance.group, "entity/wraith");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerEffect(renderer, "overlay", true, LayerEffect.BLEND.NORMAL.id, true));
		renderer.addLayer(new LayerEffect(renderer, "skull", false, LayerEffect.BLEND.NORMAL.id, true));
		renderer.addLayer(new LayerScrolling(renderer, "", true, LayerEffect.BLEND.ADD.id, true, new Vec2f(-8, 0)));
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Idle:
		if(entity instanceof EntityCreatureBase) {
			EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
			if (entityCreature.hasAttackTarget() && partName.equals("mouth")) {
				this.rotate(20 + (float)-Math.toDegrees(MathHelper.cos(loop) * 0.1F), 0.0F, 0.0F);
			}
		}

		// Vibrate:
		float vibration = loop * 2;
		if("head".equals(partName)) {
			this.translate(MathHelper.cos(vibration) * 0.01f, MathHelper.cos(vibration) * 0.01f, MathHelper.cos(vibration) * 0.01f);
		}
		else if("mouth".equals(partName)) {
			this.rotate((float)-Math.toDegrees(MathHelper.cos(vibration) * 0.025F), 0.0F, 0.0F);
		}
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if("head".equals(partName)) {
			return layer == null || "overlay".equals(layer.name);
		}
		if("fire".equals(partName)) {
			return layer != null && layer instanceof LayerScrolling;
		}
		return layer != null && "skull".equals(layer.name);
	}


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	@Override
	public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
    	return new Vector2f(-loop * 8, 0);
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		if(layer == null || layer instanceof LayerScrolling) {
			float glowSpeed = 80;
			float glow = loop * glowSpeed % 360;
			float color = ((float)Math.cos(Math.toRadians(glow)) * 0.1f) + 0.9f;
			float alpha = 1.0f;
			if("fire".equals(partName)) {
				alpha = 0.6f;
			}
			return new Vector4f(color, color, color, alpha);
		}

		return super.getPartColor(partName, entity, layer, trophy, loop);
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public void onRenderStart(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderStart(layer, entity, renderAsTrophy);
		if(layer != null)
			return;
		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
	}

	@Override
	public void onRenderFinish(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, entity, renderAsTrophy);
		if(layer != null)
			return;
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
	}
}
