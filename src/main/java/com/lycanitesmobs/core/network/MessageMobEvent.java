package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageMobEvent implements IMessage, IMessageHandler<MessageMobEvent, IMessage> {
	public String mobEventName;
	public BlockPos pos;
	public int level = 1;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageMobEvent() {}
	public MessageMobEvent(String mobEventName, BlockPos pos, int level) {
        this.mobEventName = mobEventName;
        this.pos = pos;
        this.level = level;
    }
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(final MessageMobEvent message, final MessageContext ctx) {
		if(ctx.side != Side.CLIENT) return null;
        IThreadListener mainThread = Minecraft.getMinecraft();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
                World world = player.getEntityWorld();
                ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);

                if ("".equals(message.mobEventName))
                    worldExt.stopMobEvent(message.mobEventName);
                else {
                    worldExt.startMobEvent(message.mobEventName, null, message.pos, message.level);
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
        this.mobEventName = packet.readString(256);
        this.pos = packet.readBlockPos();
        this.level = packet.readInt();
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
        packet.writeString(this.mobEventName);
        packet.writeBlockPos(this.pos);
        packet.writeInt(this.level);
	}
	
}
