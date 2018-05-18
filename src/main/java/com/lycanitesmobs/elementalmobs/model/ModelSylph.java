package com.lycanitesmobs.elementalmobs.model;

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

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelSylph extends ModelTemplateElemental {
	LayerEffect hoodLayer;
	LayerEffect wingLayer;

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelSylph() {
        this(1.0F);
    }

    public ModelSylph(float shadowSize) {

		// Load Model:
		this.initModel("sylph", ElementalMobs.instance.group, "entity/sylph");

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

		this.hoodLayer = new LayerEffect(renderer, "hood", false, LayerEffect.BLEND.NORMAL.id, true);
		renderer.addLayer(this.hoodLayer);

		this.wingLayer = new LayerScrolling(renderer, "wing", true, LayerEffect.BLEND.ADD.id, true, new Vec2f(0, 1));
		renderer.addLayer(this.wingLayer);
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		if(this.currentModelState != null) {
			this.currentModelState.attackAnimationSpeed = 0.08F;
		}

		// Idle:
		if (partName.contains("wingleft")) {
			float attackAngle = 30;
			if(partName.equals("wingleft02")) {
				attackAngle = 50;
			}
			else if(partName.equals("wingleft03")) {
				attackAngle = 45;
			}
			this.rotate(
					0,
					5 + (float)Math.toDegrees(MathHelper.sin(loop * 0.2F) * 0.4F),
					4 + (float)Math.toDegrees(MathHelper.sin(loop * 0.2F) * 0.08F) + (attackAngle * this.getAttackProgress())
			);
		}
		else if (partName.contains("wingright")) {
			float attackAngle = -30;
			if(partName.equals("wingright02")) {
				attackAngle = -50;
			}
			else if(partName.equals("wingright03")) {
				attackAngle = -45;
			}
			this.rotate(
					0,
					-5 + (float)Math.toDegrees(MathHelper.sin(loop * 0.2F + (float)Math.PI) * 0.4F),
					-4 + (float)Math.toDegrees(MathHelper.sin(loop * 0.2F + (float)Math.PI) * 0.08F) + (attackAngle * this.getAttackProgress())
			);
		}

		// Fingers:
		else if(partName.contains("finger")) {
			if(partName.contains("thumb")) {
				this.rotate(-(float) Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F), 0, 0);
			}
			else {
				this.rotate((float) Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F), 0, 0);
			}
		}
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if(partName.contains("hood")) {
			return layer == this.hoodLayer;
		}
		if(partName.contains("wing")) {
			return layer == this.wingLayer;
		}
		return layer == null;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	@Override
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		if(layer == null) {
			float glowSpeed = 40;
			float glow = loop * glowSpeed % 360;
			float color = ((float)Math.cos(Math.toRadians(glow)) * 0.1f) + 0.9f;
			return new Vector4f(color, color, color, 1);
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
		GlStateManager.disableLighting();
	}

	@Override
	public void onRenderFinish(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, entity, renderAsTrophy);
		if(layer != null)
			return;
		GlStateManager.enableLighting();
	}
}
