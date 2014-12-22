package lycanite.lycanitesmobs.api.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.pets.PetEntry;
import lycanite.lycanitesmobs.api.pets.PetManager;
import lycanite.lycanitesmobs.api.pets.SummonSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class MessagePetEntry implements IMessage, IMessageHandler<MessagePetEntry, IMessage> {
    public String petEntryType;
	public int petEntryID;
    public String summonType;
	public byte behaviour;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessagePetEntry() {}
	public MessagePetEntry(ExtendedPlayer playerExt, int petEntryID, String petEntryType) {
        this.petEntryType = petEntryType;
		this.petEntryID = petEntryID;
        SummonSet summonSet = playerExt.petManager.getEntry(petEntryID).summonSet;
        this.summonType = summonSet.summonType;
		this.behaviour = summonSet.getBehaviourByte();
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessagePetEntry message, MessageContext ctx) {
		EntityPlayer player = null;
		if(ctx.side == Side.CLIENT)
			player = LycanitesMobs.proxy.getClientPlayer();
		else if(ctx.side == Side.SERVER)
			player = ctx.getServerHandler().playerEntity;
		if(player == null) return null;
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) return null;

        PetManager petManager = playerExt.petManager;
        PetEntry petEntry = petManager.getEntry(message.petEntryID);
        if(petEntry == null) {
            if(ctx.side == Side.SERVER)
                return null; //Client should not be able to tell the server to add a new entry!
            petEntry = new PetEntry(message.petEntryType, player, this.summonType);
            petManager.addEntry(petEntry, message.petEntryID);
        }
		SummonSet summonSet = petEntry.summonSet;
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
		try {
            this.petEntryType = packet.readStringFromBuffer(256);
			this.petEntryID = packet.readInt();
			this.summonType = packet.readStringFromBuffer(256);
			this.behaviour = packet.readByte();
		} catch (IOException e) {
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
            packet.writeStringToBuffer(this.petEntryType);
			packet.writeInt(this.petEntryID);
			packet.writeStringToBuffer(this.summonType);
			packet.writeByte(this.behaviour);
		} catch (IOException e) {
			LycanitesMobs.printWarning("", "There was a problem encoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
}
