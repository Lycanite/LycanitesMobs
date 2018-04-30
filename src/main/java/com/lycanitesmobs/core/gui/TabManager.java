package com.lycanitesmobs.core.gui;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TabManager {
	public static ArrayList<GuiTab> tabList = new ArrayList<GuiTab>();

    public static void registerTab (GuiTab tab) {
        tabList.add(tab);
    }

    public static ArrayList<GuiTab> getTabList () {
        return tabList;
    }
    
    public static void addTabsToInventory (GuiScreen gui) {
    	if(LycanitesMobs.config.getBool("GUI", "Show Inventory Tabs", true, "Set to false to disable the GUI tabs.") && gui.getClass() == GuiInventory.class) {
        	GuiInventorySnooper guiInventorySnooper = new GuiInventorySnooper(mc.player);
        	try {
            	Field field = GuiScreen.class.getDeclaredField(guiInventorySnooper.getButtonListFieldName());
	            field.setAccessible(true);
	            List buttonList = (List)field.get(gui);
	            addTabsToList(buttonList);
	            field.set(gui, buttonList);
			}
        	catch(Exception e) {
				LycanitesMobs.printWarning("", "A problem occured when adding custom inventory tabs:");
				e.printStackTrace();
			}
        }
    }

    private static Minecraft mc = FMLClientHandler.instance().getClient();

    public static void openInventoryGui () {
        GuiInventory inventory = new GuiInventory(mc.player);
        mc.displayGuiScreen(inventory);
        TabManager.addTabsToInventory(inventory);
    }

    public static void updateTabValues (int cornerX, int cornerY, Class<?> selectedButton) {
        int count = 2;
        for(int i = 0; i < tabList.size(); i++) {
        	GuiTab t = tabList.get(i);

            if(t.shouldAddToList()) {
                t.id = count;
                t.x = cornerX + (count - 2) * 28;
                t.y = cornerY - 28;
                t.enabled = !t.getClass().equals(selectedButton);
                count++;
            }
        }
    }

    public static void addTabsToList (List buttonList) {
        for(GuiTab tab : tabList) {
            if(tab.shouldAddToList()) {
                buttonList.add(tab);
            }
        }
    }
}
