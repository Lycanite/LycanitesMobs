package lycanite.lycanitesmobs.api.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfig {
	// ========== Config Collections ==========
	// Configurations:
	public static Map<String, ModConfig> configs = new HashMap<String, ModConfig>();
	
	// Register Config:
	public static void registerConfig(ModConfig config) {
		if(config != null)
			configs.put(config.fileName, config);
	}

    // Get Config:
    public static ModConfig getConfig(GroupInfo group, String configName) {
        String configFileName = group.filename + "-" + configName.toLowerCase();
        if(!configs.containsKey(configFileName))
            registerConfig(new ModConfig(group, configName));
        return configs.get(configFileName);
    }
	
	
	// ========== Config ==========
	// Configuration:
	public Configuration config;

    public GroupInfo group;
    public String configName;
    public String fileName;
	public List<IConfigListener> updateListeners = new ArrayList<IConfigListener>();
	
	
	// ========== Constructor ==========
    public ModConfig(GroupInfo group, String name) {
        this.group = group;
        this.configName = name;
        this.fileName = group.filename + "-" + name.toLowerCase();
        this.init();
    }
	
	
	// ========== Pre-Init ==========
	public void init() {
		
		// Create/Load Config File:
		String configDirPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid;
		File configDir = new File(configDirPath);
		configDir.mkdir();
		File configFile = new File(configDirPath + "/" + this.fileName + ".cfg");
	    try {
	    	configFile.createNewFile();
	    	LycanitesMobs.printInfo("", "Config " + this.fileName + " created successfully.");
	    }
		catch (IOException e) {
            LycanitesMobs.printWarning("", "Config " + this.fileName + " could not be created:");
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
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.comment = comment;
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
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.comment = comment;
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
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.comment = comment;
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
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.comment = comment;
		return property.getString();
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
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.comment = comment;
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
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.comment = comment;
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
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.comment = comment;
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
		Property property = this.config.get(category, key, defaultValue);
		if(comment != null) property.comment = comment;
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
		Property property = this.config.get(category, key, value);
		if(comment != null) property.comment = comment;
		property.set(value);
		this.update();
	}
	
	// ========== Set Int ==========
	public void setInt(String category, String key, int value) {
		this.setInt(category, key, value, null);
	}
	public void setInt(String category, String key, int value, String comment) {
		Property property = this.config.get(category, key, value);
		if(comment != null) property.comment = comment;
		property.set(value);
		this.update();
	}
	
	// ========== Set Double ==========
	public void setDouble(String category, String key, double value) {
		this.setDouble(category, key, value, null);
	}
	public void setDouble(String category, String key, double value, String comment) {
		Property property = this.config.get(category, key, value);
		if(comment != null) property.comment = comment;
		property.set(value);
		this.update();
	}
	
	// ========== Set String ==========
	public void setString(String category, String key, String value) {
		this.setString(category, key, value, null);
	}
	public void setString(String category, String key, String value, String comment) {
		Property property = this.config.get(category, key, value);
		if(comment != null) property.comment = comment;
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
        List<String> valuesList = new ArrayList<String>();
        for(Object objValue : objValues)
            valuesList.add(objValue.toString());
        String[] values = valuesList.toArray(new String[valuesList.size()]);
		Property property = this.config.get(category, key, values);
		if(comment != null) property.comment = comment;
		property.set(values);
		this.update();
	}
}
