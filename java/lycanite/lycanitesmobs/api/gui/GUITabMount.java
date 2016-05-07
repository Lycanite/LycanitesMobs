package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.KeyHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GUITabMount extends GUITab {
	
	public GUITabMount(int id) {
        super(id, GUITab.startX, GUITab.startY, new ItemStack(Items.saddle));
    }

    @Override
    public void onTabClicked () {
        KeyBinding.setKeyBindState(KeyHandler.instance.mountInventory.getKeyCode(), true); //TODO Add a better way that works!
    }

    @Override
    public boolean shouldAddToList () {
    	return false;
        /*boolean ridingMount = Minecraft.getMinecraft().thePlayer.ridingEntity instanceof EntityCreatureRideable;
        if(ridingMount)
        	this.icon = ((EntityCreatureRideable)Minecraft.getMinecraft().thePlayer.ridingEntity).mobInfo.getSprite();
        return ridingMount;*/
    }
}
