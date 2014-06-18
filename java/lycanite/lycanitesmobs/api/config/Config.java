package lycanite.lycanitesmobs.api.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
	// ========== Config Collections ==========
	// Configurations:
	public static Map<String, Config> configs = new HashMap<String, Config>();
	
	// Get Config:
	public static Config getConfig(String configName) {
		if(!configs.containsKey(configName))
			configs.put(configName, new Config(configName));
		return configs.get(configName);
	}
	
	
	// ========== Config ==========
	// Configuration:
	public Configuration config;
	
	public String configName;
	public List<IConfigListener> updateListeners = new ArrayList<IConfigListener>();
	
	public Map<String, Boolean> bools = new HashMap<String, Boolean>();
	public Map<String, Integer> ints = new HashMap<String, Integer>();
	public Map<String, Double> doubles = new HashMap<String, Double>();
	public Map<String, String> strings = new HashMap<String, String>();
	public Map<String, String[]> lists = new HashMap<String, String[]>();
	
	
	// ========== Constructor ==========
	public Config(String name) {
		this.configName = name;
		this.init();
	}
	
	
	// ========== Pre-Init ==========
	public void init() {
		
		// ========== Create/Load Config File ==========
		String configDirPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid;
		File configDir = new File(configDirPath);
		configDir.mkdir();
		File configFile = new File(configDirPath + "/" + this.configName + ".cfg");
	    try {
	    	configFile.createNewFile();
	    	System.out.println("[INFO] [LycanitesMobs-" + this.configName + "] Successfully created/read configuration file.");
	    }
		catch (IOException e) {
	    	System.out.println("[SEVERE] [LycanitesMobs-" + this.configName + "] Could not create configuration file:");
	    	System.out.println(e);
	    	System.out.println("Make sure the config folder has write permissions or (if using Windows) isn't read only and that Minecraft is not in Program Files on a non-administrator account.");
		}
	    
	    // ========== Run Config File ==========
		this.config = new Configuration(configFile);
		this.config.load();
	}
	
	
	// ========== Save ==========
	public void update() {
		this.config.save();
		for(IConfigListener updateListener : this.updateListeners) {
			if(updateListener != null)
				updateListener.onConfigUpdate(this);
		}
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
		Property property = config.get(category, key, defaultValue);
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
		Property property = config.get(category, key, defaultValue);
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
		Property property = config.get(category, key, defaultValue);
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
		Property property = config.get(category, key, defaultValue);
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
		Property property = config.get(category, key, defaultValue);
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
		Property property = config.get(category, key, defaultValue);
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
		Property property = config.get(category, key, defaultValue);
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
		Property property = config.get(category, key, defaultValue);
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
		Property property = config.get(category, key, value);
		if(comment != null) property.comment = comment;
		property.set(value);
		this.update();
	}
	
	// ========== Set Int ==========
	public void setInt(String category, String key, int value) {
		this.setInt(category, key, value, null);
	}
	public void setInt(String category, String key, int value, String comment) {
		Property property = config.get(category, key, value);
		if(comment != null) property.comment = comment;
		property.set(value);
		this.update();
	}
	
	// ========== Set Double ==========
	public void setDouble(String category, String key, double value) {
		this.setDouble(category, key, value, null);
	}
	public void setDouble(String category, String key, double value, String comment) {
		Property property = config.get(category, key, value);
		if(comment != null) property.comment = comment;
		property.set(value);
		this.update();
	}
	
	// ========== Set String ==========
	public void setString(String category, String key, String value) {
		this.setString(category, key, value, null);
	}
	public void setString(String category, String key, String value, String comment) {
		Property property = config.get(category, key, value);
		if(comment != null) property.comment = comment;
		property.set(value);
		this.update();
	}
	
	
	// ========================================
	//			   Set Value Lists
	// ========================================
	
	// ========== Set String ==========
	public void setStringList(String category, String key, String[] values) {
		this.setStringList(category, key, values, null);
	}
	public void setStringList(String category, String key, String[] values, String comment) {
		Property property = config.get(category, key, values);
		if(comment != null) property.comment = comment;
		property.set(values);
		this.update();
	}
}
