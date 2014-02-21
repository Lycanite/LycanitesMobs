package lycanite.lycanitesmobs;

import java.io.BufferedReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

public class Hooks {
	private Map<String, Class> classHooks = new HashMap<String, Class>();
	private Map<String, Method> methodHooks = new HashMap<String, Method>();
	private Map<String, String> classPackages = new HashMap<String, String>();
	
	// Get Class Packages:
	private void checkClassPackages() {
		if(classPackages.size() < 1) {
			
			// Metallurgy Classes:
			classPackages.put("MetalInfoDatabase", "rebelkeithy.mods.metallurgy.core");
			classPackages.put("MetalSet", "rebelkeithy.mods.metallurgy.core.metalsets");
			classPackages.put("OreInfo", "rebelkeithy.mods.metallurgy.core.metalsets");
			classPackages.put("CrusherRecipes", "rebelkeithy.mods.metallurgy.machines.crusher");
			classPackages.put("MetallurgyMetals", "rebelkeithy.mods.metallurgy.metals");
			classPackages.put("MetallurgyMachines", "rebelkeithy.mods.metallurgy.machines");
			
			// Universal Electricity Classes:
			classPackages.put("BasicComponents", "basiccomponents.common");
			classPackages.put("Mekanism", "mekanism.common");
			classPackages.put("ElectricExpansionItems", "electricexpansion.api");
			classPackages.put("FluidMech", "fluidmech.common");
			
			// Other Mods:
			classPackages.put("BOPBlocks", "tdwp_ftw.biomesop.configuration");
			classPackages.put("BOPItems", "tdwp_ftw.biomesop.configuration");
		}
	}
	
	// Get Class from Hook:
	private Class hookClass(String className) throws ClassNotFoundException {
		checkClassPackages();
		if(classHooks.get(className) == null) classHooks.put(className, Class.forName(classPackages.get(className) + "." + className));
		else if(classHooks.get(className) == null) classHooks.put(className, Class.forName("net.minecraft.src." + classPackages.get(className) + "." + className));
		return classHooks.get(className);
	}
	
	// Get Method from Hook:
	private Method hookMethod(String methodName) {
		return methodHooks.get(methodName);
	}
	
	// Hook External Methods:
	private void checkMethodHook(String methodName, String className, Class... methodParameters) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class classRef = hookClass(className);
		if(methodHooks.get(methodName) == null) methodHooks.put(methodName, classRef.getDeclaredMethod(methodName, methodParameters));
	}
	private void checkMethodHook(String methodName, String className) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class classRef = hookClass(className);
		if(methodHooks.get(methodName) == null) methodHooks.put(methodName, classRef.getDeclaredMethod(methodName));
	}
	
	//=============================================//
	//               Metallurgy Ores               //
	//=============================================//
	
	// Get Metal Set Data:
	public Map<String, Map<String, String>> metallurgyGetMetalsetData(String setName, String path) {
		try {
			// Methods
			checkMethodHook("readMetalDataFromJar", "MetalInfoDatabase", String.class, String.class);
			
			// Invoke
			Object output = hookMethod("readMetalDataFromJar").invoke(hookClass("MetalInfoDatabase"), new Object[] { setName + ".csv", path });
			return (Map<String, Map<String, String>>)output;
		}
		catch(Exception e) {
			System.out.println("[MetalMech] There was a problem with the Metalset Data Info hook:");
			System.out.println("The MetalMech Metal Set Database (csv file) or Metallurgy 3 Core may be missing.");
			e.printStackTrace();
			return null;
		}
	}
	
	// Create Metal Set:
	public Object metallurgyNewMetalset(String setName, CreativeTabs creativeTab) {
		try {
			// Methods
			checkMethodHook("getSpreadsheetDataForSet", "MetalInfoDatabase", String.class);
						
			// Get Metalset Data
			Map<String, Map<String, String>> setData = (Map<String, Map<String, String>>)hookMethod("getSpreadsheetDataForSet").invoke(hookClass("MetalInfoDatabase"), new Object[] { setName });
			
			// Create Metalset
			Constructor newMetalSet = hookClass("MetalSet").getDeclaredConstructor(new Class[] { String.class, Map.class, CreativeTabs.class });
			newMetalSet.setAccessible(true);
			return newMetalSet.newInstance(new Object[] { setName, setData, creativeTab });
		}
		catch(Exception e) {
			System.out.println("[MetalMech] There was a problem with the Metalset hook:");
			System.out.println("The MetalMech Metal Set Database (csv file) or Metallurgy 3 Core may be missing.");
			e.printStackTrace();
			return null;
		}
	}
	
	// Get Crushing Result:
	public ItemStack metallurgyGetCrushingResult(ItemStack item) {
		try {
			// Methods
			checkMethodHook("smelting", "CrusherRecipes");
			checkMethodHook("getCrushingResult", "CrusherRecipes", ItemStack.class);
			
			// Get Crusher Recipes
			Object instance = hookMethod("smelting").invoke(hookClass("CrusherRecipes"));
			Object result = hookMethod("getCrushingResult").invoke(instance, new Object[] { item });
			if(result == null) return null;
			else return (ItemStack)result;
		}
		catch(Exception e) {
			System.out.println("[MetalMech] There was a problem with the Metallurgy Crusher Recipes hook:");
			e.printStackTrace();
			return null;
		}
	}
	
	
	//=============================================//
	//                  Hook Item                  //
	//=============================================//
	
	// Get ItemStack from Class Field:
	public ItemStack getItemStack(String name, int amount, int metadata, String className) {
		try {
			Field field = hookClass(className).getDeclaredField(name);
			field.setAccessible(true);
			Object result = field.get(null);
	
			if(result instanceof ItemStack) return (ItemStack)result;
			if(result instanceof Item) return new ItemStack((Item)result, amount, metadata);
			if(result instanceof Block) return new ItemStack((Block)result, amount, metadata);
			throw new Exception("An error occurred when trying to get " + name + " from Basic Components (probably a bad name).");
		}
		catch(Exception e) {
			if(className == "BasicComponents") {
				System.out.println("[MetalMech] Unable to get "+ name + " from " + className + ":");
				System.out.println("This will disable some recipes, make sure you have " + className + " installed.");
				e.printStackTrace();
			}
			return null;
		}
	}
	public ItemStack getItemStack(String name, String className) {
		return getItemStack(name, 1, 0, className);
	}
}
