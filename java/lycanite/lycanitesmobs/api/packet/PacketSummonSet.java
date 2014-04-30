package lycanite.lycanitesmobs.api.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.SummonSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class PacketSummonSet extends PacketBase {
	public byte summonSetID;
	public String summonType;
	public byte behaviour;
	
	// ==================================================
	//                 Read Creature Set
	// ==================================================
	public void readSummonSetSelection(ExtendedPlayer playerExt) {
		this.summonSetID = (byte)playerExt.selectedSummonSet;
		this.summonType = playerExt.getSelectedSummonSet().summonType;
		this.behaviour = playerExt.getSelectedSummonSet().getBehaviourByte();
	}
	
	
	// ==================================================
	//                      Encode
	// ==================================================
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		try {
			packet.writeByte(this.summonSetID);
			packet.writeStringToBuffer(this.summonType);
			packet.writeByte(this.behaviour);
		} catch (IOException e) {
			LycanitesMobs.printWarning("", "There was a problem encoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
	
	// ==================================================
	//                      Decode
	// ==================================================
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		try {
			this.summonSetID = packet.readByte();
			this.summonType = packet.readStringFromBuffer(256);
			this.behaviour= packet.readByte();
		} catch (IOException e) {
			LycanitesMobs.printWarning("", "There was a problem decoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
	
	// ==================================================
	//                   Handle Server
	// ==================================================
	@Override
	public void handleServerSide(EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
		
		SummonSet summonSet = playerExt.getSummonSet(this.summonSetID);
		summonSet.readFromPacket(this.summonType, this.behaviour);
	}
	
	
	// ==================================================
	//                   Handle Client
	// ==================================================
	@Override
	public void handleClientSide(EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
		
		SummonSet summonSet = playerExt.getSummonSet(this.summonSetID);
		summonSet.readFromPacket(this.summonType, this.behaviour);
	}

}