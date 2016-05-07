package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.AssetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class MobEventClientBoss extends MobEventClient {


	// ==================================================
    //                     Constructor
    // ==================================================
	public MobEventClientBoss(MobEventBase mobEvent, World world) {
        super(mobEvent, world);
	}
	
	
    // ==================================================
    //                       Start
    // ==================================================
    @Override
	public void onStart(EntityPlayer player) {
		String eventMessage = I18n.translateToLocal("event.boss." + (extended ? "extended" : "started"));
		eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
		player.addChatMessage(new TextComponentString(eventMessage));
		
		if(!player.capabilities.isCreativeMode || MobEventServer.testOnCreative || this.mobEvent instanceof MobEventBoss) {
        	if(AssetManager.getSound("mobevent_" + this.mobEvent.name.toLowerCase()) == null)
        			AssetManager.addSound("mobevent_" + this.mobEvent.name.toLowerCase(), this.mobEvent.group, "mobevent." + this.mobEvent.name.toLowerCase());
            this.sound = PositionedSoundRecord.getMusicRecord(AssetManager.getSound("mobevent_" + this.mobEvent.name.toLowerCase()));
            Minecraft.getMinecraft().getSoundHandler().playSound(this.sound);
		}
	}
	
	
    // ==================================================
    //                      Finish
    // ==================================================
    @Override
	public void onFinish(EntityPlayer player) {
		String eventMessage = I18n.translateToLocal("event.boss.finished");
		eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
		player.addChatMessage(new TextComponentString(eventMessage));
	}
}
