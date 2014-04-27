package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.gui.GUIMinion;
import lycanite.lycanitesmobs.api.gui.GUIMinionSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerControlHandler {
	
	// Player packet Types:
    public static enum CONTROL_ID {
		JUMP((byte)1), MOUNT_ABILITY((byte)2), PET_INVENTORY((byte)4), MINION_CONTROLS((byte)8), MINION_SELECT((byte)16);
		public byte id;
		private CONTROL_ID(byte i) { id = i; }
	}
    
    // Control States:
    public static Map<EntityPlayer, Byte> controls = new HashMap<EntityPlayer, Byte>();
    
    // Sync Tracking:
	public static byte lastStateSent = 0;
	public static boolean firstStateSent = false;
    
	
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
    //                   Control Update
    // ==================================================
	/** Called client side to track controls. **/
    public static void updateControls(EntityPlayer player) {
    	byte controlStates = 0;
		
		// Jumping:
		if(Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed())
			controlStates += CONTROL_ID.JUMP.id;
		
		// Mount Ability:
		if(KeyBase.keyPressed("MountAbility"))
			controlStates += CONTROL_ID.MOUNT_ABILITY.id;
		
		// Pet Inventory:
		if(KeyBase.keyPressed("PetInventory"))
			controlStates += CONTROL_ID.PET_INVENTORY.id;
		
		// Minion Manager GUI:
		if(KeyBase.keyPressed("MinionControls"))
			controlStates += CONTROL_ID.MINION_CONTROLS.id;
		
		// Minion Select GUI:
		if(KeyBase.keyPressed("MinionSelect"))
			controlStates += CONTROL_ID.MINION_SELECT.id;
		
		// If Changed, Send Control State To Player Control Handler:
		if(controlStates != lastStateSent || !firstStateSent) {
			// Server Side:
			Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, PacketHandler.PlayerType.CONTROL.id, controlStates);
			PacketHandler.sendPacketToServer(packet);
			
			// Client Side:
			PlayerControlHandler.updateStates(player, controlStates);
			
			lastStateSent = controlStates;
			firstStateSent = true;
		}
    }
    
	
    // ==================================================
    //                   Update States
    // ==================================================
    /** Called when the control states change. **/
    public static void updateStates(EntityPlayer player, byte states) {
    	controls.put(player, states);
    	
    	// Open Minion Controls:
    	if(player != null && player.worldObj != null) {
	    	if(playerInputMinionControls(player)) {
	    		if(!player.worldObj.isRemote) {
	    			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)player);
	    			if(playerExt != null)
	    				playerExt.sendAllSummonSetsToPlayer();
	    		}
	    		if(player.worldObj.isRemote)
	    			GUIMinion.openToPlayer(player, 1);
	    	}
	    	
	    	if(player.worldObj.isRemote) {
		    	if(playerInputMinionSelection(player)) {
		    		GUIMinionSelection.openToPlayer(player);
		    	}
		    	else if(Minecraft.getMinecraft().currentScreen instanceof GUIMinionSelection) {
		    		player.closeScreen();
		    	}
	    	}
    	}
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

    public static boolean playerInputMinionControls(EntityPlayer player) {
    	return controlActive(player, PlayerControlHandler.CONTROL_ID.MINION_CONTROLS);
    }

    public static boolean playerInputMinionSelection(EntityPlayer player) {
    	return controlActive(player, PlayerControlHandler.CONTROL_ID.MINION_SELECT);
    }
}
