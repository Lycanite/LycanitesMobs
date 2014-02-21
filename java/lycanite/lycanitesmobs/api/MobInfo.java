package lycanite.lycanitesmobs.api;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EnumCreatureType;

public class MobInfo {
	public String name = "MobName";
	public String title = "Mob Name";
	public Class entityClass;
	//public SpawnInfo[] spawnInfo;
	public EnumCreatureType spawnType;
	public int eggBackColor;
	public int eggForeColor;
	//public Item egg;
	
	public ModelBase model;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobInfo(String setName, Class setClass, EnumCreatureType setType, int setEggBack, int setEggFore) {
		name = setName;
		entityClass = setClass;
		spawnType = setType;
		eggBackColor = setEggBack;
		eggForeColor = setEggFore;
	}
	
	
    // ==================================================
    //                        Set
    // ==================================================
	// ========== Model ==========
	public void setModel(ModelBase setModel) {
		model = setModel;
	}
}
