package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.tileentity.TileEntityBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageTileEntityButton implements IMessage, IMessageHandler<MessageTileEntityButton, IMessage> {
	public byte buttonId;
	public BlockPos tileEntityPos;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageTileEntityButton() {}
	public MessageTileEntityButton(byte buttonId, BlockPos tileEntityPos) {
		this.buttonId = buttonId;
		this.tileEntityPos = tileEntityPos;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(final MessageTileEntityButton message, final MessageContext ctx) {
		if(ctx.side != Side.SERVER)
			return null;
		World world = ctx.getServerHandler().player.getEntityWorld();
		if(world == null)
			return null;
        IThreadListener mainThread = (WorldServer)world;
        mainThread.addScheduledTask(() -> {
			TileEntity tileEntity = world.getTileEntity(message.tileEntityPos);
			if(!(tileEntity instanceof TileEntityBase)) {
				return;
			}
			TileEntityBase tileEntityBase = (TileEntityBase)tileEntity;
			tileEntityBase.onGuiButton(message.buttonId);
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
		this.buttonId = packet.readByte();
		this.tileEntityPos = new BlockPos(packet.readInt(), packet.readInt(), packet.readInt());
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
		packet.writeByte(this.buttonId);
		packet.writeInt(this.tileEntityPos.getX());
		packet.writeInt(this.tileEntityPos.getY());
		packet.writeInt(this.tileEntityPos.getZ());
	}
	
}
