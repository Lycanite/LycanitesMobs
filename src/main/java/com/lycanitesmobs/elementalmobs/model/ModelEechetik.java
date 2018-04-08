package com.lycanitesmobs.elementalmobs.model;

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
public class ModelEechetik extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelEechetik() {
        this(1.0F);
    }

    public ModelEechetik(float shadowSize) {

		// Load Model:
		this.initModel("eechetik", ElementalMobs.instance.group, "entity/eechetik");

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
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	float maxLeg = 0F;
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Loop Offset:
		float loopOffset = 0;
		if(partName.contains("01")) {
			loopOffset += 10;
		}
		else if(partName.contains("02")) {
			loopOffset += 20;
		}
		else if(partName.contains("03")) {
			loopOffset += 30;
		}
		else if(partName.contains("04")) {
			loopOffset += 40;
		}

		// Idle:
		if (partName.equals("wingleftbottom") || partName.equals("winglefttop")) {
			this.rotate(0, 10 + (float)Math.toDegrees(MathHelper.sin(loop * 2F) * 0.4F), 0);
		}
		else if (partName.equals("wingrightbottom") || partName.equals("wingrighttop")) {
			this.rotate(0, -10 + (float)Math.toDegrees(MathHelper.sin(loop * 2F + (float)Math.PI) * 0.4F), 0);
		}
		else if(partName.contains("armleft")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F),
					(float)Math.toDegrees(MathHelper.sin((loop + (loopOffset / 2)) * 0.2F) * 0.25F) - 10,
					(float)-Math.toDegrees(MathHelper.cos((loop + (loopOffset / 2)) * 0.09F) * 0.1F)
					);
		}
		else if(partName.contains("armright")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F),
					(float)-Math.toDegrees(MathHelper.sin((loop + (loopOffset / 2)) * 0.2F) * 0.25F) + 10,
					(float)Math.toDegrees(MathHelper.cos((loop + (loopOffset / 2)) * 0.09F) * 0.1F)
			);
		}
		else if(partName.contains("mouthleft")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.1F),
					0,
					(float)-Math.toDegrees(MathHelper.cos((loop + loopOffset) * 0.2F) * 0.1F)
			);
		}
		else if(partName.contains("mouthright")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.1F),
					0,
					(float)Math.toDegrees(MathHelper.cos((loop + loopOffset) * 0.2F) * 0.1F)
			);
		}
		else if(partName.contains("antennaleft")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.1F),
					0,
					(float)-Math.toDegrees(MathHelper.cos((loop + loopOffset) * 0.2F) * 0.1F)
			);
		}
		else if(partName.contains("antennarights")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.1F),
					0,
					(float)Math.toDegrees(MathHelper.cos((loop + loopOffset) * 0.2F) * 0.1F)
			);
		}
	}
}
