package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class GUITabMinion extends GUITab {
	
	public GUITabMinion(int id) {
        super(id, GUITab.startX, GUITab.startY, new ItemStack(ObjectManager.getItem("summoningstaff")));
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
