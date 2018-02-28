package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.LayerGlow;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import com.lycanitesmobs.elementalmobs.entity.EntitySpectre;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelSpectre extends ModelTemplateElemental {

	// ==================================================
  	//                  Constructors
  	// ==================================================
    public ModelSpectre() {
        this(1.0F);
    }

    public ModelSpectre(float shadowSize) {

		// Load Model:
		this.initModel("spectre", ElementalMobs.instance.group, "entity/spectre");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
		this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerEffect(renderer, "glow", true, LayerEffect.BLEND.ADD.id, true));
		renderer.addLayer(new LayerEffect(renderer, "", false, LayerEffect.BLEND.SUB.id, true));
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if("effect01".equals(partName)) {
			return layer != null && "".equals(layer.name);
		}
		if("effect02".equals(partName) || "effect03".equals(partName)) {
			if(entity instanceof EntitySpectre && layer != null && "".equals(layer.name)) {
				return ((EntitySpectre)entity).canPull();
			}
			return false;
		}
		return layer == null || "glow".equals(layer.name);
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		boolean isPulling = false;
		if(entity instanceof EntitySpectre) {
			isPulling = ((EntitySpectre)entity).canPull();
		}

		if("effect01".equals(partName)) {
			this.rotate(25, 0, -loop * 10);
			float effectScale = 1 + ((float)Math.cos(loop / 10) * 0.1f);
			if(isPulling) {
				effectScale *= 2;
			}
			this.scale(effectScale, effectScale, effectScale);
		}
		else if("effect02".equals(partName)) {
			this.rotate(0, 0, -loop * 10);
			float effectScale = 2 + ((float)Math.cos(loop / 10));
			this.scale(effectScale, effectScale, effectScale);
		}
		else if("effect03".equals(partName)) {
			this.rotate(0, 0, loop * 10);
			float effectScale = 2 + ((float)Math.cos(loop / 10));
			this.scale(effectScale, effectScale, effectScale);
		}

		else if(partName.contains("mouthleft")) {
			this.rotate((float)Math.cos(loop / 10) * 4, (float)Math.cos(loop / 10) * 4, 0);
		}
		else if(partName.contains("mouthright")) {
			this.rotate((float)Math.cos(loop / 10) * 4, -(float)Math.cos(loop / 10) * 4, 0);
		}

		else if(partName.contains("mawleft")) {
			this.rotate(0, (float)Math.cos(loop / 10) * 10 + (isPulling ? 90 : 0), 0);
		}
		else if(partName.contains("mawright")) {
			this.rotate(0, -(float)Math.cos(loop / 10) * 10 - (isPulling ? 90 : 0), 0);
		}
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public void onRenderStart(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderStart(layer, entity, renderAsTrophy);
	}

	@Override
	public void onRenderFinish(LayerBase layer, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, entity, renderAsTrophy);
	}
}
