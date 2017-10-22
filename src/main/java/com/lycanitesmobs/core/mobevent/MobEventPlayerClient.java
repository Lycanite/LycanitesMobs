package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.gui.GuiOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class MobEventPlayerClient {

    // Properties:
	public MobEvent mobEvent;

    // Properties:
    public int ticks = 0;
    public World world;
    public PositionedSoundRecord sound;

    /** True if the started event was already running and show display as 'Event Extended' in chat. **/
    public boolean extended = false;
    
    
	// ==================================================
    //                     Constructor
    // ==================================================
	public MobEventPlayerClient(MobEvent mobEvent, World world) {
		this.mobEvent = mobEvent;
        this.world = world;
        if(!world.isRemote)
            LycanitesMobs.printWarning("", "Created a MobEventClient with a server side world, this shouldn't happen, things are going to get weird!");
	}
	
	
    // ==================================================
    //                       Start
    // ==================================================
	public void onStart(EntityPlayer player) {
		if(!this.extended) {
			this.ticks = 0;
		}
		String eventMessage = I18n.translateToLocal("event." + (extended ? "extended" : "started"));
		eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
		player.sendMessage(new TextComponentString(eventMessage));

		if(player.capabilities.isCreativeMode && !MobEventPlayerServer.testOnCreative && "world".equalsIgnoreCase(this.mobEvent.channel)) {
			return;
		}

		this.playSound();
	}

    public void playSound() {
        if(AssetManager.getSound("mobevent_" + this.mobEvent.title.toLowerCase()) == null) {
            LycanitesMobs.printWarning("MobEvent", "Sound missing for: " + this.mobEvent.getTitle());
            return;
        }
        this.sound = new PositionedSoundRecord(AssetManager.getSound("mobevent_" + this.mobEvent.title.toLowerCase()).getSoundName(), SoundCategory.RECORDS, 1.0F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
        Minecraft.getMinecraft().getSoundHandler().playSound(this.sound);
    }
	
	
    // ==================================================
    //                      Finish
    // ==================================================
	public void onFinish(EntityPlayer player) {
		String eventMessage = I18n.translateToLocal("event.finished");
		eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
		player.sendMessage(new TextComponentString(eventMessage));
	}


    // ==================================================
    //                      Update
    // ==================================================
    public void onUpdate() {
        this.ticks++;
    }


    // ==================================================
    //                       GUI
    // ==================================================
    @SideOnly(Side.CLIENT)
    public void onGUIUpdate(GuiOverlay gui, int sWidth, int sHeight) {
    	EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
        if(player.capabilities.isCreativeMode && !MobEventPlayerServer.testOnCreative && "world".equalsIgnoreCase(this.mobEvent.channel)) {
			return;
		}
        if(this.world == null || this.world != player.getEntityWorld()) return;
        if(!this.world.isRemote) return;

        int introTime = 12 * 20;
        if(this.ticks > introTime) return;
        int startTime = 2 * 20;
        int stopTime = 4 * 20;
        float animation = 1.0F;

        if(this.ticks < startTime)
            animation = (float)this.ticks / (float)startTime;
        else if(this.ticks > introTime - stopTime)
            animation = ((float)(introTime - this.ticks) / (float)stopTime);

        int width = 256;
        int height = 256;
        int x = (sWidth / 2) - (width / 2);
        int y = (sHeight / 2) - (height / 2);
        int u = width;
        int v = height;
        x += 3 - (this.ticks % 6);
        y += 2 - (this.ticks % 4);

        gui.mc.getTextureManager().bindTexture(this.getTexture());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, animation);
        if(animation > 0) {
			gui.drawTexturedModalRect(x, y, u, v, width, height);
		}
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getTexture() {
        if(AssetManager.getTexture("guimobevent" + this.mobEvent.title) == null)
            AssetManager.addTexture("guimobevent" + this.mobEvent.title, LycanitesMobs.group, "textures/mobevents/" + this.mobEvent.title.toLowerCase() + ".png");
        return AssetManager.getTexture("guimobevent" + this.mobEvent.title);
    }
}
