package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelReiver extends ModelTemplateElemental {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelReiver() {
        this(1.0F);
    }
    
    public ModelReiver(float shadowSize) {

    	// Load Model:
    	this.initModel("reiver", ElementalMobs.instance.group, "entity/reiver");

        // Tropy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.2F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerEffect(renderer, "pulse01", false, LayerEffect.BLEND.NORMAL.id, false));
		renderer.addLayer(new LayerEffect(renderer, "pulse02", false, LayerEffect.BLEND.NORMAL.id, false));
		renderer.addLayer(new LayerEffect(renderer, "pulse03", false, LayerEffect.BLEND.NORMAL.id, false));
	}
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		// Effect:
		if(partName.contains("effect")) {
			this.rotate(25, 0, 0);
		}

    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
    }


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		if(layer == null) {
			return super.getPartColor(partName, entity, layer, trophy, loop);
		}

		float alphaSpeed = 10;
		if("pulse02".equals(layer.name)) {
			alphaSpeed = 9.5f;
			loop += 100;
		}
		if("pulse03".equals(layer.name)) {
			alphaSpeed = 9;
			loop += 200;
		}
		float alpha = loop * alphaSpeed % 360;
		return new Vector4f(1, 1, 1, ((float)Math.cos(Math.toRadians(alpha)) / 2) + 0.5f);
	}
}
