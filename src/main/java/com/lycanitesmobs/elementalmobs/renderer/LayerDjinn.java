package com.lycanitesmobs.elementalmobs.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerDjinn extends LayerBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerDjinn(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public boolean canRenderPart(String partName, EntityCreatureBase entity, boolean trophy) {
        return partName.contains("ribbon");
    }

    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
		String textureName = entity.getTextureName();
		if(entity.getSubspecies() != null) {
			textureName += "_" + entity.getSubspecies().name;
		}
		textureName += "_ribbon";
		if(AssetManager.getTexture(textureName) == null)
			AssetManager.addTexture(textureName, entity.group, "textures/entity/" + textureName.toLowerCase() + ".png");
		return AssetManager.getTexture(textureName);
    }

	@Override
	public Vector2f getTextureOffset(String partName, EntityCreatureBase entity, boolean trophy, float loop) {
		return new Vector2f(-loop * 25, 0);
	}

	@Override
	public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
		return new Vector4f(1, 1, 1, 0.75f);
	}

	@Override
	public void onRenderStart(String partName, Entity entity, boolean trophy) {
		GL11.glEnable(GL11.GL_BLEND);
	}

	@Override
	public void onRenderFinish(String partName, Entity entity, boolean trophy) {
		GL11.glDisable(GL11.GL_BLEND);
	}
}
