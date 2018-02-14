package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class MessageEntityVelocity implements IMessage, IMessageHandler<MessageEntityVelocity, IMessage> {
    public int entityID;
    public int motionX;
    public int motionY;
    public int motionZ;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageEntityVelocity() {}
	public MessageEntityVelocity(Entity entity, double motionX, double motionY, double motionZ) {
		this.entityID = entity.getEntityId();
		double d0 = 3.9D;

		if (motionX < -3.9D) {
			motionX = -3.9D;
		}

		if (motionY < -3.9D) {
			motionY = -3.9D;
		}

		if (motionZ < -3.9D) {
			motionZ = -3.9D;
		}

		if (motionX > 3.9D) {
			motionX = 3.9D;
		}

		if (motionY > 3.9D) {
			motionY = 3.9D;
		}

		if (motionZ > 3.9D) {
			motionZ = 3.9D;
		}

		this.motionX = (int)(motionX * 8000.0D);
		this.motionY = (int)(motionY * 8000.0D);
		this.motionZ = (int)(motionZ * 8000.0D);
	}


	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageEntityVelocity message, MessageContext ctx) {
		if(ctx.side != Side.CLIENT)
			return null;

		EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
		World world = player.getEntityWorld();
		Entity entity = world.getEntityByID(message.entityID);
		entity.motionX += (double)message.motionX / 8000.0D;
		entity.motionY += (double)message.motionY / 8000.0D;
		entity.motionZ += (double)message.motionZ / 8000.0D;

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
		this.entityID = packet.readVarInt();
		this.motionX = packet.readShort();
		this.motionY = packet.readShort();
		this.motionZ = packet.readShort();
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
		packet.writeVarInt(this.entityID);
		packet.writeShort(this.motionX);
		packet.writeShort(this.motionY);
		packet.writeShort(this.motionZ);
	}
}