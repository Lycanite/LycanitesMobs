package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class GUITabMain extends GUITab {

	public GUITabMain(int id) {
        super(id, GUITab.startX, GUITab.startY, new ItemStack(ObjectManager.getItem("soulgazer")));
    }

    public GUITabMain(int id, int x, int y) {
        super(id, x, y, new ItemStack(ObjectManager.getItem("soulgazer")));
    }

    @Override
    public void onTabClicked () {
        GUILMMainMenu.openToPlayer(Minecraft.getMinecraft().thePlayer);
    }

    @Override
    public boolean shouldAddToList () {
        return true;
    }
}
