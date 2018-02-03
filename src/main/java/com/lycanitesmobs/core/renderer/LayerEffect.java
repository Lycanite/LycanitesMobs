package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerEffect extends LayerBase {

	public String textureSuffix = "effect";
	public boolean glow = false;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerEffect(RenderCreature renderer, String textureSuffix) {
        super(renderer);
        this.textureSuffix = textureSuffix;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
		String textureName = entity.getTextureName();
		if(entity.getSubspecies() != null) {
			textureName += "_" + entity.getSubspecies().name;
		}
		textureName += "_" + this.textureSuffix;
		if(AssetManager.getTexture(textureName) == null)
			AssetManager.addTexture(textureName, entity.group, "textures/entity/" + textureName.toLowerCase() + ".png");
		return AssetManager.getTexture(textureName);
    }

	@Override
	public void onRenderStart(String partName, Entity entity, boolean trophy) {
    	if(this.glow) {
			int i = 15728880;
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
		}
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_ALPHA, 1, 0);
	}

	@Override
	public void onRenderFinish(String partName, Entity entity, boolean trophy) {
		if(this.glow) {
			int i = entity.getBrightnessForRender();
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
}
