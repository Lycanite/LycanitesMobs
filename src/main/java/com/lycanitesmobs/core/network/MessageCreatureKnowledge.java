package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageCreatureKnowledge implements IMessage, IMessageHandler<MessageCreatureKnowledge, IMessage> {
	public String creatureName;
	public int rank;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageCreatureKnowledge() {}
	public MessageCreatureKnowledge(CreatureKnowledge creatureKnowledge) {
		this.creatureName = creatureKnowledge.creatureName;
		this.rank = creatureKnowledge.rank;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageCreatureKnowledge message, MessageContext ctx) {
		if(ctx.side != Side.CLIENT) return null;
		EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return null;
		
		playerExt.beastiary.addCreatureKnowledge(new CreatureKnowledge(playerExt.beastiary, message.creatureName, message.rank));
		return null;
	}
	
	
	// ==================================================
	//                    From Bytes
	// ==================================================
	/**
	 * Reads the message from bytes.
	 */
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		try {
			this.creatureName = packet.readString(256);
			this.rank = packet.readInt();
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "There was a problem decoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
	
	// ==================================================
	//                     To Bytes
	// ==================================================
	/**
	 * Writes the message into bytes.
	 */
	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		packet.writeString(this.creatureName);
        packet.writeInt(this.rank);
	}
	
}
