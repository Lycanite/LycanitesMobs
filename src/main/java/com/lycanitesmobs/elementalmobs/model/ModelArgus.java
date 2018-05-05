package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelArgus extends ModelTemplateElemental {

	// ==================================================
  	//                  Constructors
  	// ==================================================
    public ModelArgus() {
        this(1.0F);
    }

    public ModelArgus(float shadowSize) {

		// Load Model:
		this.initModel("argus", ElementalMobs.instance.group, "entity/argus");

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
		renderer.addLayer(new LayerEffect(renderer, "flash", true, LayerEffect.BLEND.ADD.id, true));
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if(layer instanceof LayerEffect && entity instanceof EntityCreatureBase) {
			return ((EntityCreatureBase)entity).isAttackOnCooldown(); // TODO Fix blending!
		}
		return true;
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Arms:
		if(partName.contains("arm")) {
			float rotX = (float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F) * 4;
			float rotY = (float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F);
			if("arm01".equals(partName) || "arm02".equals(partName) || "arm03".equals(partName)) {
				this.rotate(rotX, rotY, loop * 8);
				this.translate(MathHelper.cos(loop) * 0.05F - 0.05F, MathHelper.cos(loop) * 0.05F - 0.05F, 0);
			}
			else if("arm04".equals(partName) || "arm05".equals(partName) || "arm06".equals(partName)) {
				this.rotate(rotX, rotY, -loop * 8);
				this.translate(MathHelper.cos(-loop) * 0.05F - 0.05F, MathHelper.cos(-loop) * 0.05F - 0.05F, 0);
			}
		}

		// Tail:
		if(partName.contains("tail")) {
			float tailSwipeX = 32F;
			float tailSwipeY = 32F;
			this.rotate(
					(MathHelper.cos(loop * 0.1F) * tailSwipeX) - (tailSwipeX * 90),
					(MathHelper.sin(loop * 0.2F) * tailSwipeY) - (tailSwipeY * 90),
					0
			);
		}
	}
}
