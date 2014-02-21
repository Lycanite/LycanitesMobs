package lycanite.lycanitesmobs;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyBase extends KeyHandler {
	
	// Key Details:
	protected EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
	protected String label;
	protected String id = "Base";
	
	// Static Info:
	public static Map<String, Boolean> keysPressed = new HashMap<String, Boolean>();
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public KeyBase(KeyBinding[] keyBindings, boolean[] repeats, String label, String id) {
		super(keyBindings, repeats);
		this.label = label;
		this.id = id;
	}

	
	// ==================================================
	//                      Details
	// ==================================================
	@Override
	public String getLabel() {
		return this.label;
	}

	
	// ==================================================
	//                      Usage
	// ==================================================
	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if(!tickEnd || Minecraft.getMinecraft().thePlayer == null)
			return;
		if(Minecraft.getMinecraft().currentScreen != null)
			return;
		if(!keyPressed(this.id))
			this.onKeyDown(types, kb, tickEnd, isRepeat);
		this.onKeyHold(types, kb, tickEnd, isRepeat); // Used for repeats only.
		this.keyPressed(this.id, true);
	}
	
	public void onKeyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {}
	
	public void onKeyHold(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if(!tickEnd)
			return;
		if(this.keyPressed(this.id))
			this.onKeyUp(types, kb, tickEnd);
		this.keyPressed(this.id, false);
	}
	
	public void onKeyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}
	
	
	// ==================================================
	//                      Ticks
	// ==================================================
	@Override
	public EnumSet<TickType> ticks() {
		return this.tickTypes;
	}
	
	
	// ==================================================
	//                      Checks
	// ==================================================
	public static boolean keyPressed(String id) {
		if(!keysPressed.containsKey(id))
			return false;
		return keysPressed.get(id);
	}

	public static void keyPressed(String id, boolean pressed) {
		keysPressed.put(id, pressed);
	}
}
