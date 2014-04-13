package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerControlHandler {
	
	// Player packet Types:
    public static enum CONTROL_ID {
		JUMP((byte)1), MOUNT_ABILITY((byte)2), PET_INVENTORY((byte)4);
		public byte id;
		private CONTROL_ID(byte i) { id = i; }
	}
    
    // Control States:
    public static Map<EntityPlayer, Byte> controls = new HashMap<EntityPlayer, Byte>();
    
    // Custom Stats:
    public static Map<EntityPlayer, Integer> summonFocus = new HashMap<EntityPlayer, Integer>();
    public static Map<EntityPlayer, Integer> summonAmount = new HashMap<EntityPlayer, Integer>();
    public static int summonFocusCharge = 6000;
    public static int summonFocusMax = summonFocusCharge * 10;
	
	
    // ==================================================
    //                 Get Control State
    // ==================================================
    public static boolean controlActive(EntityPlayer player, CONTROL_ID control) {
    	if(!controls.containsKey(player))
    		return false;
    	byte states = controls.get(player);
    	return (states & control.id) > 0;
    }
	
	
    // ==================================================
    //                   Update States
    // ==================================================
    public static void updateStates(EntityPlayer player, byte states) {
    	controls.put(player, states);
    }
	
	
    // ==================================================
    //                   Get Controls
    // ==================================================
    public static boolean playerInputJumping(EntityPlayer player) {
    	return controlActive(player, PlayerControlHandler.CONTROL_ID.JUMP);
    }

    public static boolean playerInputMountAbility(EntityPlayer player) {
    	return controlActive(player, PlayerControlHandler.CONTROL_ID.MOUNT_ABILITY);
    }

    public static boolean playerInputInventory(EntityPlayer player) {
    	return controlActive(player, PlayerControlHandler.CONTROL_ID.PET_INVENTORY);
    }
	
	
    // ==================================================
    //                   Set Stats
    // ==================================================
    public static void setPlayerSummonFocus(EntityPlayer player, int focus) {
    	summonFocus.put(player, focus);
    }
    public static void setPlayerSummonAmount(EntityPlayer player, int amount) {
    	if(player instanceof EntityClientPlayerMP)
    		return;
    	summonAmount.put(player, amount);
    }
	
	
    // ==================================================
    //                   Get Stats
    // ==================================================
    public static int getPlayerSummonFocus(EntityPlayer player) {
    	if(!summonFocus.containsKey(player))
    		setPlayerSummonFocus(player, summonFocusMax);
    	return summonFocus.get(player);
    }
    public static int getPlayerSummonAmount(EntityPlayer player) {
    	if(player instanceof EntityClientPlayerMP)
    		return 0;
    	if(!summonAmount.containsKey(player)) {
    		setPlayerSummonAmount(player, 0);
    	}
    	return summonAmount.get(player);
    }
}
