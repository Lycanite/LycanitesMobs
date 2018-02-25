package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelVapula extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelVapula() {
        this(1.0F);
    }

    public ModelVapula(float shadowSize) {

		// Load Model:
		this.initModel("vapula", ElementalMobs.instance.group, "entity/vapula");

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

		// Crystals:
		if(partName.contains("effect")) {
			this.shiftOrigin(partName, "crystals");
			this.rotate(0, 0, loop * 8);
			this.shiftOriginBack(partName, "crystals");
		}

		// Fingers:
		else if(partName.equals("fingerleft01") || partName.equals("fingerright01")) {
			this.rotate(0,(float)Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F), 0);
		}
		else if(partName.equals("fingerleft02") || partName.equals("fingerright02")) {
			this.rotate(0,(float)Math.toDegrees(MathHelper.cos((loop + 20) * 0.2F) * 0.2F - 0.2F), 0);
		}
		else if(partName.equals("fingerleft03") || partName.equals("fingerright03")) {
			this.rotate(0,(float)Math.toDegrees(MathHelper.cos((loop + 40) * 0.2F) * 0.2F - 0.2F), 0);
		}
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	/** Returns true if the part can be rendered on the base layer. **/
	@Override
	public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
		if(entity instanceof EntityCreatureBase && partName.contains("effect")) {
			int attackPhase = ((EntityCreatureBase)entity).getAttackPhase();
			if(((EntityCreatureBase)entity).justAttacked() && attackPhase == 0) {
				return false;
			}
			if("effect01".equals(partName)) {
				return attackPhase <= 7;
			}
			else if("effect02".equals(partName)) {
				return attackPhase <= 6;
			}
			else if("effect03".equals(partName)) {
				return attackPhase <= 5;
			}
			else if("effect04".equals(partName)) {
				return attackPhase <= 4;
			}
			else if("effect05".equals(partName)) {
				return attackPhase <= 3;
			}
			else if("effect06".equals(partName)) {
				return attackPhase <= 2;
			}
			else if("effect07".equals(partName)) {
				return attackPhase <= 1;
			}
			else if("effect08".equals(partName)) {
				return attackPhase <= 0;
			}
		}
		return true;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		if(this.isCrystal(partName)) {
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
		if(!this.isCrystal(partName))
			return;
		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
	}

	@Override
	public void onRenderFinish(LayerBase layer, String partName, Entity entity, boolean renderAsTrophy) {
		super.onRenderFinish(layer, partName, entity, renderAsTrophy);
		if(!this.isCrystal(partName))
			return;
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
	}

	protected boolean isCrystal(String partName) {
		if("eye".equals(partName) || "crystals".equals(partName)) {
			return true;
		}
		return partName.contains("effect") || partName.contains("finger");
	}
}
