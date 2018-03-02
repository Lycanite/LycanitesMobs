package com.lycanitesmobs.elementalmobs.model;

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
public class ModelGrue extends ModelTemplateElemental {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelGrue() {
        this(1.0F);
    }
    
    public ModelGrue(float shadowSize) {
    	// Load Model:
    	this.initModel("grue", ElementalMobs.instance.group, "entity/grue");
    	
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
		renderer.addLayer(new LayerScrolling(renderer, "cloak", false, LayerEffect.BLEND.SUB.id, true, new Vec2f(0, 1)));
	}
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

    	// Mouth:
		if(partName.equals("mouth")) {
			this.rotate(10F, 0, 0);
		}
		
    	// Fingers:
		else if(partName.equals("fingerleft01") || partName.equals("fingerright01")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F), 0, 0);
    	}
		else if(partName.equals("fingerleft02") || partName.equals("fingerright02")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 20) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
		else if(partName.equals("fingerleft03") || partName.equals("fingerright03")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 40) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
		else if(partName.equals("fingerleft04") || partName.equals("fingerright04")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 60) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
    }


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if("cloak".equals(partName)) {
			return layer != null && "cloak".equals(layer.name);
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
