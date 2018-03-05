package com.lycanitesmobs.core.config;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ItemDrop;
import jline.internal.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigBase {
    // ========== Version Check ==========
    public static void versionCheck(String minVersion, String currentVersion) {
        // Get Config Version:
        ConfigBase versionConfig = getConfig(LycanitesMobs.group, "version");
        String configVersion = versionConfig.getString("Version", "Config Version", "0.0.0.0", "The version that this config was last read from, manually update this if you do not want your config to be cleared, although it is recommended not to unless you are aware of the changes.");
        
        // Test Config Version:
        String[] minVersions = minVersion.split("\\.");
        String[] configVersions = configVersion.split("\\.");
        if(configVersions.length != 4)
            configVersions = "0.0.0.0".split("\\.");
        boolean oldVersion = false;
        for(int i = 0; i < 4; i++) {
            int minVerNum = NumberUtils.isNumber(minVersions[i].replaceAll("[^\\d.]", "")) ? Integer.parseInt(minVersions[i].replaceAll("[^\\d.]", "")) : 0;
            int currentVerNum = NumberUtils.isNumber(configVersions[i].replaceAll("[^\\d.]", "")) ? Integer.parseInt(configVersions[i].replaceAll("[^\\d.]", "")) : 0;
            if(currentVerNum < minVerNum) {
                oldVersion = true;
                break;
            }
            if(currentVerNum > minVerNum)
                break;
        }

        // Clear Old Configs:
        if(oldVersion) {
            String configDirPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid;
            File configDir = new File(configDirPath);
            configDir.mkdir();
            LycanitesMobs.printWarning("", "[Config] The current config is too old, resetting config now...");
            try {
                FileUtils.cleanDirectory(configDir);
            } catch (IOException e) {
                LycanitesMobs.printWarning("", "[Config] Unable to clear the config directory! This could be a permissions/read-only issue!");
                e.printStackTrace();
            }
        }

        // Update Config Version:
        currentVersion = currentVersion.replace(" ", "");
        currentVersion = currentVersion.split("-")[0];
        if(!configVersion.equals(currentVersion))
            versionConfig.setString("Version", "Config Version", currentVersion);
    }


	// ========== Config Collections ==========
	// Configurations:
	public static Map<String, ConfigBase> configs = new HashMap<String, ConfigBase>();
	
	// Register Config:
	public static void registerConfig(ConfigBase config) {
		if(config == null)
			return;
		String configFileName = config.group.filename + "-" + config.fileName.toLowerCase();
		configs.put(configFileName, config);
	}

    // Get Config:
    public static ConfigBase getConfig(GroupInfo group, String configName) {
        String configFileName = group.filename + "-" + configName.toLowerCase();
        if(!configs.containsKey(configFileName))
            registerConfig(new ConfigBase(group, configName));
        return configs.get(configFileName);
    }
	
	
	// ========== Config ==========
	// Configuration:
	public Configuration config;

    public GroupInfo group;
    public String configName;
    public String fileName;
    public String configFileName;
	public List<IConfigListener> updateListeners = new ArrayList<IConfigListener>();
	
	
	// ========== Constructor ==========
    public ConfigBase(GroupInfo group, String name) {
        this.group = group;
        this.configName = name;
        this.fileName = this.configName.toLowerCase();
        this.configFileName = group.filename + "-" + this.fileName;
        this.init();
    }
	
	
	// ========== Pre-Init ==========
	public void init() {
		// Create/Load Config File:
		String configDirPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid;
		File configDir = new File(configDirPath);
		configDir.mkdir();
		File configFile = new File(configDirPath + "/" + this.configFileName + ".cfg");
	    try {
	    	configFile.createNewFile();
	    	LycanitesMobs.printInfo("", "Config " + this.configFileName + " created successfully.");
	    }
		catch (IOException e) {
            LycanitesMobs.printWarning("", "Config " + this.configFileName + " could not be created:");
	    	System.out.println(e);
            LycanitesMobs.printWarning("", "Make sure the config folder has write permissions or (if using Windows) isn't read only and that Minecraft is not in Program Files on a non-administrator account.");
		}
	    
	    // Read Config File:
		this.config = new Configuration(configFile);
		this.config.load();
	}
	
	
	// ========== Update ==========
	public void update() {
		this.config.save();
		for(IConfigListener updateListener : this.updateListeners) {
			if(updateListener != null)
				updateListener.onConfigUpdate(this);
		}
	}


    // ========== Add Listener ==========
    public void addListener(IConfigListener listener) {
        if(!this.updateListeners.contains(listener))
            this.updateListeners.add(listener);
    }


    // ========================================
    //		      Category Comments
    // ========================================
    public void setCategoryComment(String category, String comment) {
        category = category.toLowerCase();
        this.config.addCustomCategoryComment(category, comment);
    }
	
	
	// ========================================
	//				 Get Values
	// ========================================
	
	// ========== Get Boolean ==========
	public boolean getBool(String category, String key) {
		return this.getBool(category, key, false);
	}
	
	public boolean getBool(String category, String key, boolean defaultValue) {
		return this.getBool(category, key, defaultValue, null);
	}
	
	public boolean getBool(String category, String key, boolean defaultValue, String comment) {
        category = category.toLowerCase();
        boolean newEntry = !this.config.getCategory(category).containsKey(key);
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.setComment(comment);
        if(newEntry) this.config.save();
		return property.getBoolean(defaultValue);
	}
	
	// ========== Get Int ==========
	public int getInt(String category, String key) {
		return this.getInt(category, key, 0);
	}
	
	public int getInt(String category, String key, int defaultValue) {
		return this.getInt(category, key, defaultValue, null);
	}
	
	public int getInt(String category, String key, int defaultValue, String comment) {
        category = category.toLowerCase();
        boolean newEntry = !this.config.getCategory(category).containsKey(key);
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.setComment(comment);
        if(newEntry) this.config.save();
		return property.getInt(defaultValue);
	}
	
	// ========== Get Double ==========
	public double getDouble(String category, String key) {
		return this.getDouble(category, key, 0);
	}
	
	public double getDouble(String category, String key, double defaultValue) {
		return this.getDouble(category, key, defaultValue, null);
	}
	
	public double getDouble(String category, String key, double defaultValue, String comment) {
        category = category.toLowerCase();
        boolean newEntry = !this.config.getCategory(category).containsKey(key);
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.setComment(comment);
        if(newEntry) this.config.save();
		return property.getDouble(defaultValue);
	}
	
	// ========== Get String ==========
	public String getString(String category, String key) {
		return this.getString(category, key, "");
	}
	
	public String getString(String category, String key, String defaultValue) {
		return this.getString(category, key, defaultValue, null);
	}
	
	public String getString(String category, String key, String defaultValue, String comment) {
        category = category.toLowerCase();
        boolean newEntry = !this.config.getCategory(category).containsKey(key);
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.setComment(comment);
        if(newEntry) this.config.save();
		return property.getString();
	}

	// ========== Get String ==========
	@Nullable
	public ItemDrop getItemDrop(String category, String key) {
		return this.getItemDrop(category, key, null);
	}

	@Nullable
	public ItemDrop getItemDrop(String category, String key, ItemDrop defaultValue) {
		return this.getItemDrop(category, key, defaultValue, null);
	}

	@Nullable
	public ItemDrop getItemDrop(String category, String key, ItemDrop defaultValue, String comment) {
    	String defaultValueString = "";
    	if(defaultValue != null) {
    		defaultValueString = defaultValue.toConfigString();
		}
		String itemDropString = this.getString(category, key, defaultValueString, comment).replace(" ", "");
		if(itemDropString != null && itemDropString.length() > 0) {
			String[] customDropValues = itemDropString.split(",");
			String itemId = customDropValues[0];
			int itemMetadata = 0;
			if (customDropValues.length > 1) {
				itemMetadata = Integer.parseInt(customDropValues[1]);
			}
			int amountMin = 1;
			if (customDropValues.length > 2) {
				amountMin = Integer.parseInt(customDropValues[2]);
			}
			int amountMax = 1;
			if (customDropValues.length > 3) {
				amountMax = Integer.parseInt(customDropValues[3]);
			}
			float chance = 1;
			if (customDropValues.length > 4) {
				chance = Float.parseFloat(customDropValues[4]);
			}

			ItemDrop itemDrop = null;
			Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(itemId));
			if(item != null) {
				itemDrop = new ItemDrop(new ItemStack(item, amountMin, itemMetadata), chance);
				itemDrop.setMinAmount(amountMin);
				itemDrop.setMaxAmount(amountMax);
			}
			return itemDrop;
		}
		return null;
	}
	
	
	// ========================================
	//		       Get Value Lists
	// ========================================
	
	// ========== Get Boolean List ==========
	public boolean[] getBoolList(String category, String key) {
		return this.getBoolList(category, key, new boolean[]{false});
	}
	
	public boolean[] getBoolList(String category, String key, boolean[] defaultValue) {
		return this.getBoolList(category, key, defaultValue, null);
	}
	
	public boolean[] getBoolList(String category, String key, boolean[] defaultValue, String comment) {
        category = category.toLowerCase();
        boolean newEntry = !this.config.getCategory(category).containsKey(key);
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.setComment(comment);
        if(newEntry) this.config.save();
		return property.getBooleanList();
	}
	
	// ========== Get Int List ==========
	public int[] getIntList(String category, String key) {
		return this.getIntList(category, key, new int[]{0});
	}
	
	public int[] getIntList(String category, String key, int[] defaultValue) {
		return this.getIntList(category, key, defaultValue, null);
	}
	
	public int[] getIntList(String category, String key, int[] defaultValue, String comment) {
        category = category.toLowerCase();
        boolean newEntry = !this.config.getCategory(category).containsKey(key);
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.setComment(comment);
        if(newEntry) this.config.save();
		return property.getIntList();
	}
	
	// ========== Get Double List ==========
	public double[] getDoubleList(String category, String key) {
		return this.getDoubleList(category, key, new double[]{0});
	}
	
	public double[] getDoubleList(String category, String key, double[] defaultValue) {
		return this.getDoubleList(category, key, defaultValue, null);
	}
	
	public double[] getDoubleList(String category, String key, double[] defaultValue, String comment) {
        category = category.toLowerCase();
        boolean newEntry = !this.config.getCategory(category).containsKey(key);
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.setComment(comment);
        if(newEntry) this.config.save();
		return property.getDoubleList();
	}
	
	// ========== Get String List ==========
	public String[] getStringList(String category, String key) {
		return this.getStringList(category, key, new String[]{""});
	}
	
	public String[] getStringList(String category, String key, String[] defaultValue) {
		return this.getStringList(category, key, defaultValue, null);
	}
	
	public String[] getStringList(String category, String key, String[] defaultValue, String comment) {
        category = category.toLowerCase();
        boolean newEntry = !this.config.getCategory(category).containsKey(key);
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.setComment(comment);
        if(newEntry) this.config.save();
		return property.getStringList();
	}
	
	

	
	
	// ========================================
	//				 Set Values
	// ========================================
	
	// ========== Set Boolean ==========
	public void setBool(String category, String key, boolean value) {
		this.setBool(category, key, value, null);
	}
	public void setBool(String category, String key, boolean value, String comment) {
        category = category.toLowerCase();
		Property property = this.config.get(category, key, value);
		if(comment != null) property.setComment(comment);
		property.set(value);
		this.update();
	}
	
	// ========== Set Int ==========
	public void setInt(String category, String key, int value) {
		this.setInt(category, key, value, null);
	}
	public void setInt(String category, String key, int value, String comment) {
        category = category.toLowerCase();
		Property property = this.config.get(category, key, value);
		if(comment != null) property.setComment(comment);
		property.set(value);
		this.update();
	}
	
	// ========== Set Double ==========
	public void setDouble(String category, String key, double value) {
		this.setDouble(category, key, value, null);
	}
	public void setDouble(String category, String key, double value, String comment) {
        category = category.toLowerCase();
		Property property = this.config.get(category, key, value);
		if(comment != null) property.setComment(comment);
		property.set(value);
		this.update();
	}
	
	// ========== Set String ==========
	public void setString(String category, String key, String value) {
		this.setString(category, key, value, null);
	}
	public void setString(String category, String key, String value, String comment) {
        category = category.toLowerCase();
		Property property = this.config.get(category, key, value);
		if(comment != null) property.setComment(comment);
		property.set(value);
		this.update();
	}
	
	
	// ========================================
	//			   Set Value Lists
	// ========================================
	
	// ========== Set List ==========
	public void setList(String category, String key, Object[] objValues) {
		this.setList(category, key, objValues, null);
	}
	public void setList(String category, String key, Object[] objValues, String comment) {
        category = category.toLowerCase();
        List<String> valuesList = new ArrayList<String>();
        for(Object objValue : objValues)
            valuesList.add(objValue.toString());
        String[] values = valuesList.toArray(new String[valuesList.size()]);
		Property property = this.config.get(category, key, values);
		if(comment != null) property.setComment(comment);
		property.set(values);
		this.update();
	}
}
