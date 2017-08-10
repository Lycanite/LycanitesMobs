package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.pets.SummonSet;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSummoningPedestalSummonSet implements IMessage, IMessageHandler<MessageSummoningPedestalSummonSet, IMessage> {
	public String summonType;
	public byte behaviour;
    public int x;
    public int y;
    public int z;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSummoningPedestalSummonSet() {}
	public MessageSummoningPedestalSummonSet(SummonSet summonSet, int x, int y, int z) {
		this.summonType = summonSet.summonType;
		this.behaviour = summonSet.getBehaviourByte();
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
	public IMessage onMessage(final MessageSummoningPedestalSummonSet message, final MessageContext ctx) {
        // Server Side:
        if(ctx.side != Side.SERVER)
            return null;
        IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = ctx.getServerHandler().playerEntity;
                TileEntity tileEntity = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
                TileEntitySummoningPedestal summoningPedestal = null;
                if(tileEntity instanceof TileEntitySummoningPedestal)
                    summoningPedestal = (TileEntitySummoningPedestal)tileEntity;
                if(summoningPedestal == null)
                    return;
                if(summoningPedestal.summonSet == null)
                    summoningPedestal.summonSet = new SummonSet(null);
                summoningPedestal.summonSet.readFromPacket(message.summonType, message.behaviour);
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
        this.x = packet.readInt();
        this.y = packet.readInt();
        this.z = packet.readInt();
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
        packet.writeInt(this.x);
        packet.writeInt(this.y);
        packet.writeInt(this.z);
        packet.writeString(this.summonType);
        packet.writeByte(this.behaviour);
	}
	
}
