package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerEffect extends LayerBase {
	public String textureSuffix = "effect";

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
}
