package lycanite.lycanitesmobs;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.packet.Packet;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {
	public static byte lastStateSent = 0;
	public static boolean firstStateSent = false;
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public TickHandler() {}

	
	// ==================================================
	//                      Details
	// ==================================================
	@Override
	public String getLabel() {
		return "LycanitesMobsTickHandler";
	}
	
	
	// ==================================================
	//                      Ticks
	// ==================================================
	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}
	

	// ==================================================
	//                   Tick Handling
	// ==================================================
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.PLAYER)) {
			playerTick(tickData);
		}
		if(type.contains(TickType.CLIENT)) {
			clientTick();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}
	

	// ==================================================
	//                 Player Ticks
	// ==================================================
	public static void playerTick(Object... tickData) {
	}
	

	// ==================================================
	//                 Client Ticks
	// ==================================================
	public static void clientTick() {
		byte controlStates = 0;
		
		// Jumping:
		if(Minecraft.getMinecraft().gameSettings.keyBindJump.pressed)
			controlStates += PlayerControlHandler.CONTROL_ID.JUMP.id;
		
		// Mount Ability:
		if(KeyBase.keyPressed("MountAbility"))
			controlStates += PlayerControlHandler.CONTROL_ID.MOUNT_ABILITY.id;
		
		// Pet Inventory:
		if(KeyBase.keyPressed("PetInventory"))
			controlStates += PlayerControlHandler.CONTROL_ID.PET_INVENTORY.id;
		
		// Minion GUI:
		if(KeyBase.keyPressed("MinionControls"))
			controlStates += PlayerControlHandler.CONTROL_ID.MINION_CONTROLS.id;
		
		// If Changed, Send Control State To Player Control Handler:
		if(controlStates != lastStateSent || !firstStateSent) {
			// Client Side:
			PlayerControlHandler.updateStates(Minecraft.getMinecraft().thePlayer, controlStates);
			
			// Server Side:
			Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, PacketHandler.PlayerType.CONTROL.id, controlStates);
			PacketHandler.sendPacketToServer(packet);
			lastStateSent = controlStates;
			firstStateSent = true;
		}
	}
}
