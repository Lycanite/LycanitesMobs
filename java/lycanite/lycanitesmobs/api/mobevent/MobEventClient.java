package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.AssetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
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
	public void onStart(World world, EntityPlayer player) {
		String eventMessage = StatCollector.translateToLocal("event.started");
		eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
		player.addChatMessage(new ChatComponentText(eventMessage));
		
		if(!player.capabilities.isCreativeMode || MobEventBase.testOnCreative) {
        	if(AssetManager.getSound("mobevent_" + this.mobEvent.name.toLowerCase()) == null)
        			AssetManager.addSound("mobevent_" + this.mobEvent.name.toLowerCase(), this.mobEvent.group, "mobevent." + this.mobEvent.name.toLowerCase());
            this.sound = PositionedSoundRecord.func_147673_a(new ResourceLocation(AssetManager.getSound("mobevent_" + this.mobEvent.name.toLowerCase())));
            Minecraft.getMinecraft().getSoundHandler().playSound(this.sound);
		}
	}
	
	
    // ==================================================
    //                      Finish
    // ==================================================
	public void onFinish(EntityPlayer player) {
		String eventMessage = StatCollector.translateToLocal("event.finished");
		eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
		player.addChatMessage(new ChatComponentText(eventMessage));
	}
}
