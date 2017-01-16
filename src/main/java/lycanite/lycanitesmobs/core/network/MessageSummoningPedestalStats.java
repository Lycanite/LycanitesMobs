package lycanite.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSummoningPedestalStats implements IMessage, IMessageHandler<MessageSummoningPedestalStats, IMessage> {
	public int capacity;
	public int progress;
    public int x;
    public int y;
    public int z;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSummoningPedestalStats() {}
	public MessageSummoningPedestalStats(int capacity, int progress, int x, int y, int z) {
		this.capacity = capacity;
        this.progress = progress;
        this.x = x;
        this.y = y;
        this.z = z;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageSummoningPedestalStats message, MessageContext ctx) {
		EntityPlayer player = null;
		if(ctx.side == Side.SERVER)
			return null;

        player = LycanitesMobs.proxy.getClientPlayer();
        TileEntity tileEntity = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
        TileEntitySummoningPedestal summoningPedestal = null;
        if(tileEntity instanceof TileEntitySummoningPedestal)
            summoningPedestal = (TileEntitySummoningPedestal)tileEntity;
        if(summoningPedestal == null)
            return null;
        summoningPedestal.capacity = message.capacity;
        summoningPedestal.summonProgress = message.progress;
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
        this.x = packet.readInt();
        this.y = packet.readInt();
        this.z = packet.readInt();
        this.capacity = packet.readInt();
        this.progress = packet.readInt();
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
        packet.writeInt(this.x);
        packet.writeInt(this.y);
        packet.writeInt(this.z);
        packet.writeInt(this.capacity);
        packet.writeInt(this.progress);
	}
	
}
