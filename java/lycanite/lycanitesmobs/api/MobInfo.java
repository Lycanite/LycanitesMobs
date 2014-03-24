package lycanite.lycanitesmobs.api;


public class MobInfo {
	/** Mod Class **/
	public ILycaniteMod mod;
	
	/** The name of this mob used by the ObjectManager and Config maps. **/
	public String name = "MobName";
	
	/** The title used by this mob for displaying in game.. **/
	public String title = "Mob Name";
	
	/** The class that this mob instantiates with. **/
	public Class entityClass;

	/** A lsit of SpawnInfo used by this mob, multiple SpawnInfos can be added for multiple spawn methods. **/
	public SpawnInfo[] spawnInfo;
	
	/** The background color of this mob's egg. **/
	public int eggBackColor;	
	
	/** The foreground color of this mob's egg. **/
	public int eggForeColor;
	
	///** The model used by this mob. NOTE: This is currently unused, see AssetManager.getModel() **/
	//public ModelBase model;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobInfo(ILycaniteMod mod, String setName, String setTitle, Class setClass, int setEggBack, int setEggFore) {
		this.name = setName;
		this.title = setTitle;
		this.entityClass = setClass;
		this.eggBackColor = setEggBack;
		this.eggForeColor = setEggFore;
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
