package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerGlow extends LayerBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerGlow(RenderCreature renderer) {
        super(renderer);
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
		textureName += "_glow";
		if(AssetManager.getTexture(textureName) == null)
			AssetManager.addTexture(textureName, entity.creatureInfo.group, "textures/entity/" + textureName.toLowerCase() + ".png");
		return AssetManager.getTexture(textureName);
    }

    @Override
    public void onRenderStart(String partName, Entity entity, boolean trophy) {
		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
    }

    @Override
    public void onRenderFinish(String partName, Entity entity, boolean trophy) {
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
    }
}
