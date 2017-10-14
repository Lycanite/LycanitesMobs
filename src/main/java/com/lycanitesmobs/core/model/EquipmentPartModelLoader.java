package com.lycanitesmobs.core.model;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class EquipmentPartModelLoader implements ICustomModelLoader {
	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if(LycanitesMobs.modid.equals(modelLocation.getResourceDomain()) && "equipmentpart".equals(modelLocation.getResourcePath())) {
			return true;
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		return (state, format, bakedTextureGetter) -> new EquipmentPartModel();
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {

	}
}
