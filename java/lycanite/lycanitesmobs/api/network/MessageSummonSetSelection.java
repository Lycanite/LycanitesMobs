package lycanite.lycanitesmobs.api.network;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.ExtendedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class MessageSummonSetSelection implements IMessage, IMessageHandler<MessageSummonSetSelection, IMessage> {
	public byte summonSetID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSummonSetSelection() {}
	public MessageSummonSetSelection(ExtendedPlayer playerExt) {
		this.summonSetID = (byte)playerExt.selectedSummonSet;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageSummonSetSelection message, MessageContext ctx) {
		EntityPlayer player = null;
		if(ctx.side == Side.CLIENT)
			player = Minecraft.getMinecraft().thePlayer;
		else if(ctx.side == Side.SERVER)
			player = ctx.getServerHandler().playerEntity;
		if(player == null) return null;
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) return null;
		playerExt.setSelectedSummonSet(this.summonSetID);
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
		this.summonSetID = packet.readByte();
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
		packet.writeByte(this.summonSetID);
	}
	
}
