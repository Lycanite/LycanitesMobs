package lycanite.lycanitesmobs.api.network;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.pets.SummonSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSummonSet implements IMessage, IMessageHandler<MessageSummonSet, IMessage> {
	public byte summonSetID;
	public String summonType;
	public byte behaviour;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSummonSet() {}
	public MessageSummonSet(ExtendedPlayer playerExt, byte summonSetID) {
		this.summonSetID = summonSetID;
		this.summonType = playerExt.getSummonSet(summonSetID).summonType;
		this.behaviour = playerExt.getSummonSet(summonSetID).getBehaviourByte();
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageSummonSet message, MessageContext ctx) {
		EntityPlayer player = null;
		if(ctx.side == Side.CLIENT)
			player = LycanitesMobs.proxy.getClientPlayer();
		else if(ctx.side == Side.SERVER)
			player = ctx.getServerHandler().playerEntity;
		if(player == null) return null;
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) return null;
		
		SummonSet summonSet = playerExt.getSummonSet(message.summonSetID);
		summonSet.readFromPacket(message.summonType, message.behaviour);
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
        this.summonType = packet.readStringFromBuffer(256);
        this.behaviour = packet.readByte();
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
        packet.writeString(this.summonType);
        packet.writeByte(this.behaviour);
	}
	
}
