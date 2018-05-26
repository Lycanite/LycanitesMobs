package com.lycanitesmobs.shadowmobs.model;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.template.ModelTemplateBiped;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.shadowmobs.ShadowMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelShade extends ModelTemplateBiped {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelShade() {
        this(1.0F);
    }

    public ModelShade(float shadowSize) {
    	// Load Model:
    	this.initModel("shade", ShadowMobs.instance.group, "entity/shade");

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
		renderer.addLayer(new LayerEffect(renderer, "eyes", true, LayerEffect.BLEND.ADD.id, true));
	}
    
    
    // ==================================================
   	//                    Animate Part
   	// ==================================================
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	if(entity instanceof EntityCreatureBase && entity.getControllingPassenger() != null) {
			time = time * 0.25F;
			distance = distance * 0.4F;
		}
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		if(partName.equals("mouth")) {
			this.rotate((float)-Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.1F), 0.0F, 0.0F);
		}

		else if(partName.contains("tail")) {
			float sine = 0;
			if(partName.equals("tail.002")) {
				sine = 1;
			}
			else if(partName.equals("tail.003")) {
				sine = 2;
			}
			else if(partName.equals("tail.004")) {
				sine = 3;
			}
			else if(partName.equals("tail.005")) {
				sine = 4;
			}
			else if(partName.equals("tail.006")) {
				sine = 5;
			}
			else if(partName.equals("tail.007")) {
				sine = 6;
			}
			sine = (MathHelper.sin(sine / 6) - 0.5F);
			float rotX = (float)-Math.toDegrees(MathHelper.cos((loop + time) * 0.1F) * 0.05F - 0.05F);
			float rotY = (float)-Math.toDegrees(MathHelper.cos((loop + time) * sine * 0.1F) * 0.4F);
			rotY += Math.toDegrees(MathHelper.cos(time * 0.25F) * distance);
			this.rotate(rotX, rotY, 0);
		}
    }
}
