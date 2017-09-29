package com.lycanitesmobs.RecipeExporter;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.*;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemSlabCustom;
import com.lycanitesmobs.core.item.ItemSwordBase;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RecipeExporter {
	public static boolean EXPORT_RECIPES = false;
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Set<String> USED_OD_NAMES = new TreeSet<>();

	private static File setupDir(ItemStack result) {
		String path = LycanitesMobs.proxy.getMinecraftDir() + "/recipes/";

		if(result.getItem() instanceof ItemBase) {
			path += ((ItemBase)result.getItem()).group.filename + "/recipes/";
		}
		else if(result.getItem() instanceof ItemCustomFood) {
			path += ((ItemCustomFood)result.getItem()).group.filename + "/recipes/";
		}
		else if(result.getItem() instanceof ItemSwordBase) {
			path += ((ItemSwordBase)result.getItem()).group.filename + "/recipes/";
		}
		/*else if(result.getItem() instanceof ItemBucketOoze) {
			path += ((ItemBucketOoze)result.getItem()).group.filename + "/recipes/";
		}
		else if(result.getItem() instanceof ItemBucketPureLava) {
			path += ((ItemBucketPureLava)result.getItem()).group.filename + "/recipes/";
		}*/
		else if(result.getItem() instanceof ItemSlabCustom && ((ItemSlabCustom)result.getItem()).group != null) {
			path += ((ItemSlabCustom)result.getItem()).group.filename + "/recipes/";
		}
		else if(result.getItem() instanceof ItemBlock) {
			ItemBlock itemBlock = (ItemBlock)result.getItem();
			if(itemBlock.getBlock() instanceof BlockBase) {
				path += ((BlockBase)itemBlock.getBlock()).group.filename + "/recipes/";
			}
			else if(itemBlock.getBlock() instanceof BlockFluidBase) {
				path += ((BlockFluidBase)itemBlock.getBlock()).group.filename + "/recipes/";
			}
			else if(itemBlock.getBlock() instanceof BlockStairsCustom) {
				path += ((BlockStairsCustom)itemBlock.getBlock()).group.filename + "/recipes/";
			}
			else if(itemBlock.getBlock() instanceof BlockSlabCustom) {
				path += ((BlockSlabCustom)itemBlock.getBlock()).group.filename + "/recipes/";
			}
			else if(itemBlock.getBlock() instanceof BlockWallCustom) {
				path += ((BlockWallCustom)itemBlock.getBlock()).group.filename + "/recipes/";
			}
			else if(itemBlock.getBlock() instanceof BlockFenceCustom) {
				path += ((BlockFenceCustom)itemBlock.getBlock()).group.filename + "/recipes/";
			}
		}

		File recipeDir = new File(path);
		if (!recipeDir.exists()) {
			recipeDir.mkdirs();
		}
		return recipeDir;
	}

	public static void addShapedRecipe(ItemStack result, Object... components) {
		GameRegistry.addRecipe(new ShapedOreRecipe(result, components));

		if(!EXPORT_RECIPES)
			return;

		File recipeDir = setupDir(result);

		Map<String, Object> json = new HashMap<>();

		List<String> pattern = new ArrayList<>();
		int i = 0;
		while (i < components.length && components[i] instanceof String) {
			pattern.add((String) components[i]);
			i++;
		}
		json.put("pattern", pattern);

		boolean isOreDict = true;
		Map<String, Map<String, Object>> key = new HashMap<>();
		Character curKey = null;
		for (; i < components.length; i++) {
			Object o = components[i];
			if (o instanceof Character) {
				if (curKey != null)
					throw new IllegalArgumentException("Provided two char keys in a row");
				curKey = (Character) o;
			} else {
				if (curKey == null)
					throw new IllegalArgumentException("Providing object without a char key");
				if (o instanceof String)
					isOreDict = true;
				key.put(Character.toString(curKey), serializeItem(o));
				curKey = null;
			}
		}
		json.put("key", key);
		json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(recipeDir, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(recipeDir, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}

		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addShapelessRecipe(ItemStack result, Object... components) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(result, components));

		if(!EXPORT_RECIPES)
			return;

		File recipeDir = setupDir(result);

		Map<String, Object> json = new HashMap<>();

		boolean isOreDict = true;
		List<Map<String, Object>> ingredients = new ArrayList<>();
		for (Object o : components) {
			if (o instanceof String)
				isOreDict = true;
			ingredients.add(serializeItem(o));
		}
		json.put("ingredients", ingredients);
		json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(recipeDir, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(recipeDir, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}


		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<String, Object> serializeItem(Object thing) {
		if (thing instanceof Item) {
			return serializeItem(new ItemStack((Item) thing));
		}
		if (thing instanceof Block) {
			return serializeItem(new ItemStack((Block) thing));
		}
		if (thing instanceof ItemStack) {
			ItemStack stack = (ItemStack) thing;
			Map<String, Object> ret = new HashMap<>();
			ret.put("item", stack.getItem().getRegistryName().toString());
			if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
				ret.put("data", stack.getItemDamage());
			}
			if (stack.getCount() > 1) {
				ret.put("count", stack.getCount());
			}

			if (stack.hasTagCompound()) {
				ret.put("type", "minecraft:item_nbt");
				ret.put("nbt", stack.getTagCompound().toString());
			}

			return ret;
		}
		if (thing instanceof String) {
			Map<String, Object> ret = new HashMap<>();
			USED_OD_NAMES.add((String) thing);
			ret.put("item", "#" + ((String) thing).toUpperCase(Locale.ROOT));
			return ret;
		}

		throw new IllegalArgumentException("Not a block, item, stack, or od name");
	}

	// Call this after you are done generating
	private static void generateConstants() {
		List<Map<String, Object>> json = new ArrayList<>();
		for (String s : USED_OD_NAMES) {
			Map<String, Object> entry = new HashMap<>();
			entry.put("name", s.toUpperCase(Locale.ROOT));
			entry.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", s));
			json.add(entry);
		}

		File recipeDir = setupDir(null);
		try (FileWriter w = new FileWriter(new File(recipeDir, "_constants.json"))) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
