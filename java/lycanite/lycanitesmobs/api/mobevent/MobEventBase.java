package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import net.minecraft.util.StatCollector;

public class MobEventBase {
	
	// Properties:
	public String name = "mobevent";
	public int weight = 8;
    
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBase(String name) {
		this.name = name;
	}
    
	
    // ==================================================
    //                       Names
    // ==================================================
	public String getTitle() {
		return StatCollector.translateToLocal("mobevent." + this.name + ".name");
	}
	
	
    // ==================================================
    //                      Enabled
    // ==================================================
	public boolean isEnabled() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
		return config.getBool("Events Enabled", "Cinderfall", true);
	}
	
	
    // ==================================================
    //                       Start
    // ==================================================
	public void onStart() {
		
	}
	
	
    // ==================================================
    //                      Update
    // ==================================================
	public void onUpdate() {
		
	}
	
	
    // ==================================================
    //                      Finish
    // ==================================================
	public void onFinish() {
		
	}
	
	
    // ==================================================
    //                       GUI
    // ==================================================
	public void onGUIUpdate() {
		
	}
}
