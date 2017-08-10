package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedWorld;
import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageMobEvent implements IMessage, IMessageHandler<MessageMobEvent, IMessage> {
	public String mobEventName;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageMobEvent() {}
	public MessageMobEvent(String mobEventName) {
        this.mobEventName = mobEventName;
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
                World world = player.worldObj;
                ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);

                if ("".equals(message.mobEventName))
                    worldExt.stopMobEvent(message.mobEventName);
                else {
                    worldExt.startMobEvent(message.mobEventName, 0, 0, 0);
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
        this.mobEventName = packet.readStringFromBuffer(256);
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
	}
	
}
