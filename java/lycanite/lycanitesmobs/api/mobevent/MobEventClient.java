package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MobEventClient {

	public MobEventBase mobEvent;
    public PositionedSoundRecord sound;
    
    
	// ==================================================
    //                     Constructor
    // ==================================================
	public MobEventClient(MobEventBase mobEvent) {
		this.mobEvent = mobEvent;
	}
	
	
    // ==================================================
    //                       Start
    // ==================================================
	public void onStart(World world) {
		if(!LycanitesMobs.proxy.getClientPlayer().capabilities.isCreativeMode || MobEventBase.testOnCreative) {
        	if(AssetManager.getSound("mobevent_" + this.mobEvent.name.toLowerCase()) == null)
        			AssetManager.addSound("mobevent_" + this.mobEvent.name.toLowerCase(), this.mobEvent.group, "mobevent." + this.mobEvent.name.toLowerCase());
            this.sound = PositionedSoundRecord.func_147673_a(new ResourceLocation(AssetManager.getSound("mobevent_" + this.mobEvent.name.toLowerCase())));
            Minecraft.getMinecraft().getSoundHandler().playSound(this.sound);
		}
	}
	
	
    // ==================================================
    //                      Finish
    // ==================================================
	public void onFinish() {
	}
}
