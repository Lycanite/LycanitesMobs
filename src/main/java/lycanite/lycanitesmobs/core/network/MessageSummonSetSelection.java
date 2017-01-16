package lycanite.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

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
	public IMessage onMessage(final MessageSummonSetSelection message, final MessageContext ctx) {
        // Server Side:
        if(ctx.side == Side.SERVER) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = ctx.getServerHandler().playerEntity;
                    ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                    playerExt.setSelectedSummonSet(message.summonSetID);
                }
            });
            return null;
        }

        // Client Side:
        EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
        if(playerExt == null) return null;
        playerExt.setSelectedSummonSet(message.summonSetID);
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
