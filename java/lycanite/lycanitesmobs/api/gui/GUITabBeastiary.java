package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class GUITabBeastiary extends GUITab {
	
	public GUITabBeastiary(int id) {
        super(id, GUITab.startX, GUITab.startY, new ItemStack(ObjectManager.getItem("soulgazer")));
    }

    @Override
    public void onTabClicked () {
        GUIBeastiary.openToPlayer(Minecraft.getMinecraft().thePlayer);
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
