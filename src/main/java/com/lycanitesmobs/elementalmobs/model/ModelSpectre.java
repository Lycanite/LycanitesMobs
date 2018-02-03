package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.LayerGlow;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import com.lycanitesmobs.elementalmobs.entity.EntitySpectre;
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
		renderer.addLayer(new LayerGlow(renderer));
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	/** Returns true if the part can be rendered on the base layer. **/
	@Override
	public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
		if("effect02".equals(partName) || "effect03".equals(partName)) {
			if(entity instanceof EntitySpectre) {
				return ((EntitySpectre)entity).canPull();
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if("effect02".equals(partName) || "effect03".equals(partName)) {
			if(entity instanceof EntitySpectre) {
				return ((EntitySpectre)entity).canPull();
			}
			return false;
		}
		return super.canRenderPart(partName, entity, layer, trophy);
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
	public void onRenderStart(LayerBase layer, String partName, Entity entity, boolean renderAsTrophy) {
		super.onRenderStart(layer, partName, entity, renderAsTrophy);
		if(!partName.contains("effect"))
			return;
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
	}

	@Override
	public void onRenderFinish(LayerBase layer, String partName, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, partName, entity, renderAsTrophy);
		if(!partName.contains("effect"))
			return;
		GL11.glDisable(GL11.GL_BLEND);
	}
}
