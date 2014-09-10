package lycanite.lycanitesmobs.api.mobevent;

import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.gui.GuiOverlay;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MobEventBase {
	
	// Properties:
	public String name = "mobevent";
	public int weight = 8;
    public List<SpawnTypeBase> spawners = new ArrayList<SpawnTypeBase>();
    public GroupInfo group;

    // Properties:
    public int duration = 60 * 20;
    public int serverTicks = 0;
    public int clientTicks = 0;
    public World world;
    
    // Client:
    public PositionedSoundRecord sound;
    
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBase(String name, GroupInfo group) {
		this.name = name;
		this.group = group;
	}
    
	
    // ==================================================
    //                       Names
    // ==================================================
    /** Returns the translated name of this event. **/
	public String getTitle() {
		return StatCollector.translateToLocal("mobevent." + this.name + ".name");
	}

    /** Returns a translated string to overlay the event image, this returns an empty string for english as the image itself has the title in english. **/
    public String getDisplayTitle() {
        String title = this.getTitle().replaceAll(" ", "").toLowerCase();
        return title.equalsIgnoreCase(this.name) ? "" : title;
    }
	
	
    // ==================================================
    //                      Enabled
    // ==================================================
	public boolean isEnabled() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
		return config.getBool("Events Enabled", this.name, true);
	}


    // ==================================================
    //                    Event Stats
    // ==================================================
    /* Returns the rate that event mobs are spawned at in ticks. This changes based on the difficulty of the provided world. */
    public int getRate(World world) {
        int base = MobEventManager.instance.baseRate;
        if(world.difficultySetting.getDifficultyId() <= 1)
            return Math.round(base * 1.5F);
        else if(world.difficultySetting.getDifficultyId() == 2)
            return base;
        else
            return Math.round(base * 0.5F);
    }

    /* Returns the distance from the player that event mobs are spawned from. This changes based on the difficulty of the provided world. */
    public int getRange(World world) {
        int base = MobEventManager.instance.baseRange;
        if(world.difficultySetting.getDifficultyId() <= 1)
            return Math.round(base * 1.5F);
        else if(world.difficultySetting.getDifficultyId() == 2)
            return base;
        else
            return Math.round(base * 0.5F);
    }


    // ==================================================
    //                     Spawners
    // ==================================================
    public MobEventBase addSpawner(SpawnTypeBase spawner) {
        if(!this.spawners.contains(spawner)) {
            this.spawners.add(spawner);
            spawner.setMobEvent(this);
        }
        return this;
    }
	
	
    // ==================================================
    //                       Start
    // ==================================================
	public void onStart(World world) {
		LycanitesMobs.printInfo("", "Mob Event Started: " + this.getTitle());
		this.serverTicks = 0;
		this.clientTicks = 0;
		this.world = world;
		
		// Client Side:
        if(this.world.isRemote) {
    		String eventMessage = StatCollector.translateToLocal("event.started");
			eventMessage = eventMessage.replace("%event%", this.getTitle());
			LycanitesMobs.proxy.getClientPlayer().addChatMessage(new ChatComponentText(eventMessage));
			
			if(!LycanitesMobs.proxy.getClientPlayer().capabilities.isCreativeMode) {
	        	if(AssetManager.getSound("mobevent_" + this.name.toLowerCase()) == null)
	        			AssetManager.addSound("mobevent_" + this.name.toLowerCase(), this.group, "mobevent." + this.name.toLowerCase());
	            this.sound = PositionedSoundRecord.func_147673_a(new ResourceLocation(AssetManager.getSound("mobevent_" + this.name.toLowerCase())));
	            Minecraft.getMinecraft().getSoundHandler().playSound(this.sound);
			}
        }
	}
	
	
    // ==================================================
    //                      Finish
    // ==================================================
	public void onFinish() {
		if(this.world.isRemote) {
	        String eventMessage = StatCollector.translateToLocal("event.finished");
			eventMessage = eventMessage.replace("%event%", this.getTitle());
			LycanitesMobs.proxy.getClientPlayer().addChatMessage(new ChatComponentText(eventMessage));
		}
		LycanitesMobs.printInfo("", "Mob Event Finished: " + this.getTitle());
	}
	
	
    // ==================================================
    //                      Update
    // ==================================================
	public void onServerUpdate(World serverWorld) {
		if(serverWorld == null) {
			LycanitesMobs.printWarning("", "Mob Event is trying to update without a world object!");
			return;
		}

        // Spawn Near Players:
        for(Object playerObj : serverWorld.playerEntities) {
            if(playerObj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)playerObj;
                if(!player.capabilities.isCreativeMode) {
	                int x = (int)player.posX;
	                int y = (int)player.posY;
	                int z = (int)player.posZ;
	
	                // Event Mob Spawning:
	                int tickOffset = 0;
	                for(SpawnTypeBase spawnType : this.spawners) {
	                    spawnType.spawnMobs(this.serverTicks - tickOffset, serverWorld, x, y, z);
	                    tickOffset += 7;
	                }
                }
            }
        }

        this.serverTicks++;

        // Stop Event When Time Runs Out:
        if(this.serverTicks > this.duration) {
        	MobEventManager.instance.stopMobEvent();
        }
	}
	
	
    // ==================================================
    //                  Client Update
    // ==================================================
	public void onClientUpdate() {
        this.clientTicks++;
	}
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
	public void onSpawn(EntityLiving entity) {}
	
	
    // ==================================================
    //                       GUI
    // ==================================================
    @SideOnly(Side.CLIENT)
	public void onGUIUpdate(GuiOverlay gui, int sWidth, int sHeight) {
    	if(LycanitesMobs.proxy.getClientPlayer().capabilities.isCreativeMode) return;
    	if(this.world == null) return;
    	if(!this.world.isRemote) return;
    	
		int introTime = 12 * 20;
        if(this.clientTicks > introTime) return;
        int startTime = 2 * 20;
        int stopTime = 4 * 20;
        float animation = 1.0F;

        if(this.clientTicks < startTime)
            animation = (float)this.clientTicks / (float)startTime;
        else if(this.clientTicks > introTime - stopTime)
            animation = ((float)(introTime - this.clientTicks) / (float)stopTime);

        int width = 256;
        int height = 256;
        int x = (sWidth / 2) - (width / 2);
        int y = (sHeight / 2) - (height / 2);
        int u = width;
        int v = height;
        x += 3 - (this.clientTicks % 6); 
        y += 2 - (this.clientTicks % 4); 
        
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, animation);
        gui.mc.getTextureManager().bindTexture(this.getTexture());
        gui.drawTexturedModalRect(x, y, u, v, width, height);
        GL11.glPopMatrix();
	}

    @SideOnly(Side.CLIENT)
    public ResourceLocation getTexture() {
        if(AssetManager.getTexture("guimobevent" + this.name) == null)
            AssetManager.addTexture("guimobevent" + this.name, this.group, "textures/mobevents/" + this.name.toLowerCase() + ".png");
        return AssetManager.getTexture("guimobevent" + this.name);
    }
}
