package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageEntityGUICommand implements IMessage, IMessageHandler<MessageEntityGUICommand, IMessage> {
	int entityID;
	public byte guiCommandID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageEntityGUICommand() {}
	public MessageEntityGUICommand(byte guiCommandID, Entity entity) {
		this.entityID = entity.getEntityId();
		this.guiCommandID = guiCommandID;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(final MessageEntityGUICommand message, final MessageContext ctx) {
		if(ctx.side != Side.SERVER) return null;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.getEntityWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = ctx.getServerHandler().player;
                World world = player.getEntityWorld();
                Entity entity = world.getEntityByID(message.entityID);
                if (entity instanceof EntityCreatureTameable) {
                    EntityCreatureTameable pet = (EntityCreatureTameable) entity;
                    pet.performGUICommand(player, message.guiCommandID);
                }
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
		this.entityID = packet.readInt();
		this.guiCommandID = packet.readByte();
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
		packet.writeInt(this.entityID);
		packet.writeByte(this.guiCommandID);
	}
	
}
