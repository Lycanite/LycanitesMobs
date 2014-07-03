package lycanite.lycanitesmobs.api.gui;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

/**
 * A sneaky snooping class that taps into GuiInventory to try and grab stuff from those stupid protected fields!
 *
 */
public class GuiInventorySnooper extends GuiInventory {

	public GuiInventorySnooper(EntityPlayer entityPlayer) {
		super(entityPlayer);
	}
	
	/**
	 * This agent's missing is to infiltrate the buttonList field by comparing it to it's own.
	 * @return The name of the buttonList field before or after obfuscation.
	 */
	public String getButtonListFieldName() {
		try {
			for(Field field : GuiScreen.class.getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(this);
				if(object instanceof List)
					if((List)object == this.buttonList)
						return field.getName();
			}
		} catch(Exception e) {}
		return "";
	}
	
	public int getGUIXSize() {
		return this.xSize;
	}
	
	public int getGUIYSize() {
		return this.ySize;
	}

}
