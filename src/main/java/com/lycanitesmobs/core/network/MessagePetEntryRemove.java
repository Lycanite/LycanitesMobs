package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.pets.PetEntry;
import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.pets.PetManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePetEntryRemove implements IMessage, IMessageHandler<MessagePetEntryRemove, IMessage> {
    public int petEntryID;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessagePetEntryRemove() {}
	public MessagePetEntryRemove(ExtendedPlayer playerExt, PetEntry petEntry) {
        this.petEntryID = petEntry.petEntryID;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(final MessagePetEntryRemove message, final MessageContext ctx) {
        // Server Side:
        if(ctx.side == Side.SERVER) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.getEntityWorld();
            mainThread.addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = ctx.getServerHandler().player;
                    ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);

                    PetManager petManager = playerExt.petManager;
                    PetEntry petEntry = petManager.getEntry(message.petEntryID);
                    if(petEntry == null) {
                        LycanitesMobs.printWarning("", "Tried to remove a null PetEntry from server!");
                        return; // Nothing to remove!
                    }
                    petEntry.remove();
                }
            });
            return null;
        }

        // Client Side:
        EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) return null;

        PetManager petManager = playerExt.petManager;
        PetEntry petEntry = petManager.getEntry(message.petEntryID);
        if(petEntry == null) {
			LycanitesMobs.printWarning("", "Tried to remove a null PetEntry from client!");
            return null; // Nothing to remove!
        }
        petEntry.remove();
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
		try {
            this.petEntryID = packet.readInt();
		} catch (Exception e) {
			LycanitesMobs.printWarning("", "There was a problem decoding the packet: " + packet + ".");
			e.printStackTrace();
		}
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
		try {
			packet.writeInt(this.petEntryID);
		} catch (Exception e) {
			LycanitesMobs.printWarning("", "There was a problem encoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
}
