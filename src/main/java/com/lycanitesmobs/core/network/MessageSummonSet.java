package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
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
	public IMessage onMessage(final MessageSummonSet message, final MessageContext ctx) {
        // Server Side:
        if(ctx.side == Side.SERVER) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.getEntityWorld();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = ctx.getServerHandler().player;
                    ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);

                    SummonSet summonSet = playerExt.getSummonSet(message.summonSetID);
                    summonSet.readFromPacket(message.summonType, message.behaviour);
                }
            });
            return null;
        }

        // Client Side:
        EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
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
        this.summonType = packet.readString(256);
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
