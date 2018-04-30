package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageGUIRequest implements IMessage, IMessageHandler<MessageGUIRequest, IMessage> {
	public byte guiID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageGUIRequest() {}
	public MessageGUIRequest(byte guiID) {
		this.guiID = guiID;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(final MessageGUIRequest message, final MessageContext ctx) {
		if(ctx.side != Side.SERVER) return null;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.getEntityWorld();
        mainThread.addScheduledTask(() -> {
			EntityPlayer player = ctx.getServerHandler().player;
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			playerExt.requestGUI(message.guiID);
		});
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
		this.guiID = packet.readByte();
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
		packet.writeByte(this.guiID);
	}
	
}
