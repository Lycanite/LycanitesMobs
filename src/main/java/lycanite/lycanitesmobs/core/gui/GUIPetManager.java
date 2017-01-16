package lycanite.lycanitesmobs.core.gui;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.GuiHandler;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.core.network.MessageGUIRequest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GUIPetManager extends GUIBaseManager {

	// ==================================================
	//                      Opener
	// ==================================================
	public static void openToPlayer(EntityPlayer player) {
		if(player != null && player.worldObj != null) {
			player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.PLAYER.id, player.worldObj, GuiHandler.PlayerGuiType.PET_MANAGER.id, 0, 0);
			MessageGUIRequest message = new MessageGUIRequest(GuiHandler.PlayerGuiType.PET_MANAGER.id);
			LycanitesMobs.packetHandler.sendToServer(message);
		}
	}

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIPetManager(EntityPlayer player) {
		super(player, "pet");
	}


	// ==================================================
	//                     Get Texture
	// ==================================================
	@Override
	protected ResourceLocation getTexture() {
		return AssetManager.getTexture("GUIPet");
	}
}
