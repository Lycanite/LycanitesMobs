package com.lycanitesmobs.shadowmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateBiped;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWraamon extends ModelTemplateBiped {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelWraamon() {
        this(1.0F);
    }

    public ModelWraamon(float shadowSize) {
    	// Load Model:
    	this.initModel("wraamon", ShadowMobs.instance.group, "entity/wraamon");

    	// Looking:
		this.lookHeadScaleX = 0.8F;
		this.lookHeadScaleY = 0.8F;
		this.lookNeckScaleX = 0.2F;
		this.lookNeckScaleY = 0.2F;

        // Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, -0.2F, 0.0F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerEffect(renderer, "glow", true, LayerEffect.BLEND.ADD.id, true));
	}
    
    
    // ==================================================
   	//                    Animate Part
   	// ==================================================
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	// Standing:
		if(this.currentModelState != null) {
			if (this.currentModelState.getBoolean("standing")) {
				if (partName.equals("body")) {
					this.rotate(-45, 0, 0);
				}
				else if (partName.equals("head")) {
					this.rotate(30, 0, 0);
				}
				else if (partName.contains("leg")) {
					this.rotate(45, 0, 0);
				}
				else if (partName.contains("tail")) {
					this.rotate(50, 0, 0);
				}
			}
			if (this.currentModelState.getFloat("standingChangeTime") <= 0) {
				this.currentModelState.setBoolean("standing", !this.currentModelState.getBoolean("standing"));
				this.currentModelState.setFloat("standingChangeTime", (2 * 20) + (8 * 20 * entity.getRNG().nextFloat()));
			}
			if (partName.equals("body")) {
				this.currentModelState.setFloat("standingChangeTime", this.currentModelState.getFloat("standingChangeTime") - 1);
			}
		}

		// Super:
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

    	// Idle:
		if(partName.equals("nose")) {
			this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.1F), (float)-Math.toDegrees(MathHelper.cos(loop * 0.4F) * 0.1F), 0.0F);
		}
		else if(partName.equals("tailleft")) {
			float rotX = (float)-Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
			float rotY = (float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
			this.rotate(rotX, rotY, 0);
		}
		else if(partName.equals("tailright")) {
			float rotX = (float)Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F - 0.05F);
			float rotY = (float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
			this.rotate(rotX, rotY, 0);
		}
    }
}
