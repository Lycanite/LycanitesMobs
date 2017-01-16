package lycanite.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePlayerStats implements IMessage, IMessageHandler<MessagePlayerStats, IMessage> {
	public int spirit;
	public int summonFocus;
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessagePlayerStats() {}
	public MessagePlayerStats(ExtendedPlayer playerExt) {
		this.spirit = playerExt.spirit;
		this.summonFocus = playerExt.summonFocus;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessagePlayerStats message, MessageContext ctx) {
		if(ctx.side != Side.CLIENT) return null;
		EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) return null;

		playerExt.spirit = message.spirit;
		playerExt.summonFocus = message.summonFocus;
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
		this.spirit = packet.readInt();
		this.summonFocus = packet.readInt();
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
		packet.writeInt(this.spirit);
		packet.writeInt(this.summonFocus);
	}
	
}
