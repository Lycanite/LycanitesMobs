package lycanite.lycanitesmobs;

import java.util.EnumSet;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {
	// This will be removed soon if the EventListener can do a better job.
	
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
			clientTick(tickData);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}
	

	// ==================================================
	//                 Player Ticks
	// ==================================================
	public static void playerTick(Object... tickData) {}
	

	// ==================================================
	//                 Client Ticks
	// ==================================================
	public static void clientTick(Object... tickData) {}
}
