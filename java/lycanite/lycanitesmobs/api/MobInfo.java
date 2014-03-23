package lycanite.lycanitesmobs.api;

import net.minecraft.client.model.ModelBase;

public class MobInfo {
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
	
	/** The model used by this mob. **/
	public ModelBase model;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobInfo(String setName, String setTitle, Class setClass, int setEggBack, int setEggFore) {
		this.name = setName;
		this.title = setTitle;
		this.entityClass = setClass;
		this.eggBackColor = setEggBack;
		this.eggForeColor = setEggFore;
	}
	
	
    // ==================================================
    //                        Set
    // ==================================================
	// ========== Model ==========
	public void setModel(ModelBase setModel) {
		model = setModel;
	}
}
