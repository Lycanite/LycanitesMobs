package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.core.renderer.LayerOverlay;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;

@SideOnly(Side.CLIENT)
public class ModelDjinn extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelDjinn() {
        this(1.0F);
    }

    public ModelDjinn(float shadowSize) {

		// Load Model:
		this.initModel("djinn", ElementalMobs.instance.group, "entity/djinn");

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
		renderer.addLayer(new LayerOverlay(renderer));
	}


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	@Override
	public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
		if(partName.contains("effect")) {
			return super.getBaseTextureOffset(partName, entity, trophy, loop);
		}
		return new Vector2f(loop, 0);
	}
}
