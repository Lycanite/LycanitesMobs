package lycanite.lycanitesmobs.api.network;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.ExtendedPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePlayerAttack implements IMessage, IMessageHandler<MessagePlayerAttack, IMessage> {
    public int attackEntityID = 0;

	// ==================================================
	//                    Constructors
	// ==================================================
	public MessagePlayerAttack() {}
	public MessagePlayerAttack(Entity attackEntity) {
        if(attackEntity != null)
            this.attackEntityID = attackEntity.getEntityId();
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(final MessagePlayerAttack message, final MessageContext ctx) {
		if(ctx.side != Side.SERVER) return null;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = ctx.getServerHandler().playerEntity;
                ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                if(message.attackEntityID != 0)
                    playerExt.meleeAttack(player.worldObj.getEntityByID(message.attackEntityID));
            }
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
        this.attackEntityID = packet.readInt();
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
        packet.writeInt(this.attackEntityID);
	}
	
}
