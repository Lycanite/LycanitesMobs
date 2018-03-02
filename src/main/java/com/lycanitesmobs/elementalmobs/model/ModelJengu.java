package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.LayerEffect;
import com.lycanitesmobs.core.renderer.LayerScrolling;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelJengu extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelJengu() {
        this(1.0F);
    }

    public ModelJengu(float shadowSize) {
    	// Load Model:
    	this.initModel("jengu", ElementalMobs.instance.group, "entity/jengu");
    	
    	// Trophy:
        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.2F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerEffect(renderer, "", false, LayerEffect.BLEND.NORMAL.id, true));
	}


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		float rotX = 0F;
		float rotY = 0F;
		float rotZ = 0F;

		// Effects:
		if(partName.equals("effectouter"))
			rotX = 15F;
		if(partName.equals("effectinner"))
			rotX = 30F;

		// Apply Animations:
		this.rotate(rotX, rotY, rotZ);

		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
	}


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	@Override
	public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
		if(partName.contains("effect")) {
			return super.getBaseTextureOffset(partName, entity, trophy, loop);
		}
		return new Vector2f(0, loop);
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if(partName.contains("effect")) {
			return layer != null;
		}
		return layer == null;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		if(partName.contains("effect")) {
			return new Vector4f(1, 1, 1, 0.5f);
		}

		return super.getPartColor(partName, entity, layer, trophy, loop);
	}
}
