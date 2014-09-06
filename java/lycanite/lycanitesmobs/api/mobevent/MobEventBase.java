package lycanite.lycanitesmobs.api.mobevent;

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
}
