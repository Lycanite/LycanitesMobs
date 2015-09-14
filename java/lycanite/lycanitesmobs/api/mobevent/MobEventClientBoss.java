package lycanite.lycanitesmobs.api.mobevent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.gui.GuiOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

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
		String eventMessage = StatCollector.translateToLocal("event.boss." + (extended ? "extended" : "started"));
		eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
		player.addChatMessage(new ChatComponentText(eventMessage));
		
		if(!player.capabilities.isCreativeMode || MobEventServer.testOnCreative || this.mobEvent instanceof MobEventBoss) {
        	if(AssetManager.getSound("mobevent_" + this.mobEvent.name.toLowerCase()) == null)
        			AssetManager.addSound("mobevent_" + this.mobEvent.name.toLowerCase(), this.mobEvent.group, "mobevent." + this.mobEvent.name.toLowerCase());
            this.sound = PositionedSoundRecord.func_147673_a(new ResourceLocation(AssetManager.getSound("mobevent_" + this.mobEvent.name.toLowerCase())));
            Minecraft.getMinecraft().getSoundHandler().playSound(this.sound);
		}
	}
	
	
    // ==================================================
    //                      Finish
    // ==================================================
    @Override
	public void onFinish(EntityPlayer player) {
		String eventMessage = StatCollector.translateToLocal("event.boss.finished");
		eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
		player.addChatMessage(new ChatComponentText(eventMessage));
	}
}
