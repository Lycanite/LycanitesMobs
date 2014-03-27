package lycanite.lycanitesmobs.api;

import java.util.HashMap;
import java.util.Map;


public class MobInfo {
	public static Map<Class, MobInfo> mobClassToInfo = new HashMap<Class, MobInfo>();
	
	/** Mod Class **/
	public ILycaniteMod mod;
	
	/** The name of this mob used by the ObjectManager and Config maps. **/
	public String name = "MobName";
	
	/** The title used by this mob for displaying in game.. **/
	public String title = "Mob Name";
	
	/** Is this mob enabled? If disabled, it will still be registered, etc but wont randomly spawn or have a spawn egg. **/
	public boolean mobEnabled;
	
	/** The class that this mob instantiates with. **/
	public Class entityClass;

	/** The SpawnInfo used by this mob. **/
	public SpawnInfo spawnInfo;
	
	/** The background color of this mob's egg. **/
	public int eggBackColor;	
	
	/** The foreground color of this mob's egg. **/
	public int eggForeColor;
	
	///** The model used by this mob. NOTE: This is currently unused, see AssetManager.getModel() **/
	//public ModelBase model;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobInfo(ILycaniteMod mod, String name, String title, Class entityClass, int eggBack, int eggFore) {
		this.mod = mod;
		
		this.name = name;
		this.title = title;
		
		this.mobEnabled = mod.getConfig().mobsEnabled.containsKey(name) ? mod.getConfig().mobsEnabled.get(name) : false;
		this.spawnInfo = new SpawnInfo(this);
		this.entityClass = entityClass;
		
		this.eggBackColor = eggBack;
		this.eggForeColor = eggFore;
		
		mobClassToInfo.put(entityClass, this);
	}
	
	// For most mobs where the code name and title are the same (no spaces, etc).
	public MobInfo(ILycaniteMod mod, String setName, Class setClass, int setEggBack, int setEggFore) {
		this(mod, setName, setName, setClass, setEggBack, setEggFore);
	}
	
	
	// ==================================================
    //                 Names and Titles
    // ==================================================
	public String getRegistryName() {
		return this.mod.getModID() + "." + this.name;
	}
	
	
    // ==================================================
    //                        Set
    // ==================================================
	/*/ ========== Model ==========
	public void setModel(ModelBase setModel) {
		model = setModel;
	}*/
}
