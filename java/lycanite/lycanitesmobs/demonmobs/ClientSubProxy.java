package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.demonmobs.entity.EntityBehemoth;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireball;
import lycanite.lycanitesmobs.demonmobs.model.ModelAsmodi;
import lycanite.lycanitesmobs.demonmobs.model.ModelBehemoth;
import lycanite.lycanitesmobs.demonmobs.model.ModelBelph;
import lycanite.lycanitesmobs.demonmobs.model.ModelCacodemon;
import lycanite.lycanitesmobs.demonmobs.model.ModelNetherSoul;
import lycanite.lycanitesmobs.demonmobs.model.ModelPinky;
import lycanite.lycanitesmobs.demonmobs.model.ModelTrite;
import lycanite.lycanitesmobs.render.RenderCreature;
import lycanite.lycanitesmobs.render.RenderProjectile;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFireball;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientSubProxy extends CommonSubProxy {
	
	// ========== Render ID ==========
	public static int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	
	
	// ========== Register Models ==========
	@Override
    public void registerModels() {
		AssetManager.addModel("Belph", new ModelBelph());
		AssetManager.addModel("Behemoth", new ModelBehemoth());
		AssetManager.addModel("Pinky", new ModelPinky());
		AssetManager.addModel("Trite", new ModelTrite());
		AssetManager.addModel("Asmodi", new ModelAsmodi());
		AssetManager.addModel("NetherSoul", new ModelNetherSoul());
		AssetManager.addModel("Cacodemon", new ModelCacodemon());
	}
}