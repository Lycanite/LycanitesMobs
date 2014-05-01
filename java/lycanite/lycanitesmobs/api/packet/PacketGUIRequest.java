package lycanite.lycanitesmobs.api.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lycanite.lycanitesmobs.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class PacketGUIRequest extends PacketBase {
	public byte guiID;
	
	// ==================================================
	//                   Read GUI ID
	// ==================================================
	public void readGUI(byte guiID) {
		this.guiID = guiID;
	}
	
	
	// ==================================================
	//                      Encode
	// ==================================================
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		packet.writeByte(this.guiID);
	}
	
	
	// ==================================================
	//                      Decode
	// ==================================================
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		this.guiID = packet.readByte();
	}
	
	
	// ==================================================
	//                   Handle Server
	// ==================================================
	@Override
	public void handleServerSide(EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		playerExt.requestGUI(this.guiID);
	}
	
	
	// ==================================================
	//                   Handle Client
	// ==================================================
	@Override
	public void handleClientSide(EntityPlayer player) {
		// Should only be sent from clients to the server.
	}

}