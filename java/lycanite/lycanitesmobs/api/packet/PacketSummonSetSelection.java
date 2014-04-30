package lycanite.lycanitesmobs.api.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lycanite.lycanitesmobs.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class PacketSummonSetSelection extends PacketBase {
	public byte summonSetID;
	
	// ==================================================
	//                 Read Creature Set
	// ==================================================
	public void readSummonSetSelection(ExtendedPlayer playerExt) {
		this.summonSetID = (byte)playerExt.selectedSummonSet;
	}
	
	
	// ==================================================
	//                      Encode
	// ==================================================
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		packet.writeByte(this.summonSetID);
	}
	
	
	// ==================================================
	//                      Decode
	// ==================================================
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		this.summonSetID = packet.readByte();
	}
	
	
	// ==================================================
	//                   Handle Server
	// ==================================================
	@Override
	public void handleServerSide(EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
		
		playerExt.setSelectedSummonSet(this.summonSetID);
	}
	
	
	// ==================================================
	//                   Handle Client
	// ==================================================
	@Override
	public void handleClientSide(EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
		
		playerExt.setSelectedSummonSet(this.summonSetID);
	}

}