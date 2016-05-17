package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GUITabMinion extends GUITab {
	
	public GUITabMinion(int id) {
        super(id, GUITab.startX, GUITab.startY, new ResourceLocation(LycanitesMobs.modid, "textures/items/summoningstaff.png"));
    }

    @Override
    public void onTabClicked () {
    	if(ExtendedPlayer.getForPlayer(Minecraft.getMinecraft().thePlayer) != null)
    		GUIMinion.openToPlayer(Minecraft.getMinecraft().thePlayer, ExtendedPlayer.getForPlayer(Minecraft.getMinecraft().thePlayer).selectedSummonSet);
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
