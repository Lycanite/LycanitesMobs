package lycanite.lycanitesmobs.api.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.CreatureKnowledge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class PacketCreatureKnowledge extends PacketBase {
	public String creatureName;
	public double completion;
	
	// ==================================================
	//                 Read Creature Set
	// ==================================================
	public void readCreatureKnowledge(CreatureKnowledge creatureKnowledge) {
		this.creatureName = creatureKnowledge.creatureName;
		this.completion = creatureKnowledge.completion;
	}
	
	
	// ==================================================
	//                      Encode
	// ==================================================
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		try {
			packet.writeStringToBuffer(this.creatureName);
			packet.writeDouble(this.completion);
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
			this.creatureName = packet.readStringFromBuffer(256);
			this.completion = packet.readDouble();
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
		// Should only be sent from the server to clients.
	}
	
	
	// ==================================================
	//                   Handle Client
	// ==================================================
	@Override
	public void handleClientSide(EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
		
		playerExt.beastiary.addToKnowledgeList(new CreatureKnowledge(player, this.creatureName, this.completion));
	}

}