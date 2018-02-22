package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelObjAnimationFrame;
import com.lycanitesmobs.core.model.ModelObjPart;
import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelAegis extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelAegis() {
        this(1.0F);
    }

    public ModelAegis(float shadowSize) {

		// Load Model:
		this.initModel("aegis", ElementalMobs.instance.group, "entity/aegis");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }


	// ==================================================
	//                 Animate Part
	// ==================================================
	float maxLeg = 0F;
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Core:
		if(partName.equals("core")) {
			//this.rotate(0, loop * 8, 0);
		}
		if(partName.contains("shieldupper")) {
			this.shiftOrigin(partName, "body");
			this.rotate(0, loop * 8, 0);
			this.shiftOriginBack(partName, "body");
		}

		// Sword Mode:
		if(entity instanceof EntityCreatureBase) {
			EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
			if(!entityCreature.isBlocking()) {

				if(partName.equals("core")) {
					//this.rotate(0, loop * 16, 0);
				}

				if(partName.contains("shieldupper")) {
					float orbit = loop * 16;
					if(partName.contains("left")) {
						orbit += 90;
					}
					this.shiftOrigin(partName, "body");
					this.rotate(0, orbit, 0);
					this.shiftOriginBack(partName, "body");

					/**if(partName.contains("left")) {
						this.shiftOrigin(partName, "body");
						this.rotate(0, 90, 0);
						this.shiftOriginBack(partName, "body");
					}**/
					this.translate(0, -0.25f, 0);
					this.scale(0.5f, 1, 1);
					if ("shieldupperleft01".equals(partName) || "shieldupperright01".equals(partName)) {
						this.rotate(-90, 0, 0);
					}
					else if ("shieldupperleft02".equals(partName) || "shieldupperright02".equals(partName)) {
						this.rotate(90, 0, 0);
					}
				}

				if(partName.contains("shieldlower")) {
					this.scale(0.5f, 1, 1);
				}
			}
		}
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		if(!this.isArmorPart(partName)) {
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
	public void onRenderStart(LayerBase layer, String partName, Entity entity, boolean renderAsTrophy) {
		super.onRenderStart(layer, partName, entity, renderAsTrophy);
		if(this.isArmorPart(partName))
			return;
		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
	}

	@Override
	public void onRenderFinish(LayerBase layer, String partName, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, partName, entity, renderAsTrophy);
		if(this.isArmorPart(partName))
			return;
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
	}

	protected boolean isArmorPart(String partName) {
		return "shoulders".equals(partName) || "helm".equals(partName) || partName.contains("shield");
	}
}
