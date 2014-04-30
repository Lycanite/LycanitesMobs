package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PacketHandler {
	
	// Packet Types:
    public static enum PacketType {
		NULL((byte)-1), TILEENTITY((byte)0), ENTITY((byte)1), PLAYER((byte)2), GUI((byte)3);
		public byte id;
		private PacketType(byte i) { id = i; }
	}
	
	// Player packet Types:
    public static enum PlayerType {
		CONTROL((byte)0), SUMMONFOCUS((byte)1), MINION((byte)2), MINION_SELECT((byte)3), BEASTIARY((byte)4), BEASTIARY_ALL((byte)5);
		public byte id;
		private PlayerType(byte i) { id = i; }
	}
	
	// Player packet Types:
    public static enum ButtonType {
		MINION((byte)0);
		public byte id;
		private ButtonType(byte i) { id = i; }
	}
	
	
    // ==================================================
    //                   Receive Packet
    // ==================================================
	public void onPacketData(EntityPlayer playerEntity) {
		try {
			World world = playerEntity.worldObj;
			boolean server = !world.isRemote;
			boolean client = world.isRemote;
			
			byte packetType = data.readByte();
			
			// ========== GUI Packet ==========
			else if(packetType == PacketType.GUI.id) {
				if(!world.isRemote) {
					Entity entity = world.getEntityByID(data.readInt());
					if(entity instanceof EntityCreatureTameable) {
						EntityCreatureTameable pet = (EntityCreatureTameable)entity;
						byte guiCommandID = data.readByte();
						pet.performGUICommand((EntityPlayer)player, guiCommandID);
					}//TODO GUI Packet Class
				}
			}
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "Invalid Packet Type was passed.");
			e.printStackTrace();
		}
	}
}
