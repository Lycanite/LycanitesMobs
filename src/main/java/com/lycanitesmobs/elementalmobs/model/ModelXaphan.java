package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.LayerScrolling;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelXaphan extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelXaphan() {
        this(1.0F);
    }

    public ModelXaphan(float shadowSize) {
    	// Load Model:
    	this.initModel("xaphan", ElementalMobs.instance.group, "entity/xaphan");
    	
    	// Trophy:
        this.trophyScale = 0.8F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerScrolling(renderer, "ball", true, LayerEffect.BLEND.NORMAL.id, true, new Vec2f(0, 4)));
		renderer.addLayer(new LayerEffect(renderer, "ring", true, LayerEffect.BLEND.ADD.id, true));
	}
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
		
    	// Fingers:
		if(partName.equals("fingerleft01") || partName.equals("fingerright01")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F), 0, 0);
    	}
		else if(partName.equals("fingerleft02") || partName.equals("fingerright02")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 20) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
		else if(partName.equals("fingerleft03") || partName.equals("fingerright03")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 40) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}

		// Shoulders:
		else if(partName.equals("shoulderleft")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F),
					0,
					(float)Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F)
			);
		}
		else if(partName.equals("shoulderright")) {
			this.rotate(
					(float)Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F),
					0,
					(float)-Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.1F)
			);
		}

		// Fingers:
		else if(partName.equals("effect01")) {
			this.rotate(0, loop * 16, 0);
		}

		// Balls:
		else if(partName.contains("ball")) {
			this.shiftOrigin(partName, "body");
			this.rotate(0, loop * -8, 0);
			this.shiftOriginBack(partName, "body");
			float ballScale = (float)(1 +  0.2 * (MathHelper.cos(loop)));
			if(partName.equals("ball02")) {
				ballScale = (float)(1 +  0.2 * (MathHelper.cos(loop + 20)));
			}
			else if(partName.equals("ball03")) {
				ballScale = (float)(1 +  0.2 * (MathHelper.cos(loop + 40)));
			}
			else if(partName.equals("ball04")) {
				ballScale = (float)(1 +  0.2 * (MathHelper.cos(loop + 60)));
			}
			this.scale(ballScale, ballScale, ballScale);
		}

		// Balls:
		else if(partName.contains("spine")) {
			float coil = 8F;
			this.rotate((MathHelper.cos(loop * 0.1F) * coil) - (coil * 90), 0, 0);
		}
    }


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if("effect01".equals(partName)) {
			return layer != null && "ring".equals(layer.name);
		}
		if(partName.contains("ball")) {
			return layer != null && "ball".equals(layer.name);
		}
		return layer == null;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	@Override
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		return super.getPartColor(partName, entity, layer, trophy, loop);
	}
}
