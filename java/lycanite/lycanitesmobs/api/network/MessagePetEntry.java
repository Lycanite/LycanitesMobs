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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class MessagePetEntry implements IMessage, IMessageHandler<MessagePetEntry, IMessage> {
    public String petEntryName;
    public int petEntryID;
    public String petEntryType;
    public boolean spawningActive;
    public boolean teleportEntity;
    public String summonType;
	public byte behaviour;
	public int petEntryEntityID;
	public int respawnTime;
	public int respawnTimeMax;
	public boolean isRespawning;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessagePetEntry() {}
	public MessagePetEntry(ExtendedPlayer playerExt, PetEntry petEntry) {
        this.petEntryName = petEntry.name != null ? petEntry.name : "";
        this.petEntryID = petEntry.petEntryID;
        this.petEntryType = petEntry.getType();
        this.spawningActive = petEntry.spawningActive;
        this.teleportEntity = petEntry.teleportEntity;
        SummonSet summonSet = petEntry.summonSet;
        this.summonType = summonSet.summonType;
		this.behaviour = summonSet.getBehaviourByte();
		this.petEntryEntityID = petEntry.entity != null ? petEntry.entity.getEntityId() : 0;
		this.respawnTime = petEntry.respawnTime;
		this.respawnTimeMax = petEntry.respawnTimeMax;
		this.isRespawning = petEntry.isRespawning;
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
			player = LycanitesMobs.proxy.getClientPlayer(); // Client can only send commands.
		else if(ctx.side == Side.SERVER)
			player = ctx.getServerHandler().playerEntity; // Server can add or remove entries.
		if(player == null) return null;
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) return null;

        PetManager petManager = playerExt.petManager;
        PetEntry petEntry = petManager.getEntry(message.petEntryID);
        if(petEntry == null) {
            if(ctx.side == Side.SERVER) {
                return null; // Client should not be able to tell the server to add a new entry!
            }
            petEntry = new PetEntry(this.petEntryName, message.petEntryType, player, this.summonType);
            petManager.addEntry(petEntry, message.petEntryID);
        }
        petEntry.spawningActive = message.spawningActive;
        petEntry.teleportEntity = message.teleportEntity;
		SummonSet summonSet = petEntry.summonSet;
		summonSet.readFromPacket(message.summonType, message.behaviour);

        if(ctx.side == Side.SERVER)
            petEntry.onBehaviourUpdate();

		if(ctx.side == Side.CLIENT) {
			Entity entity = null;
			if(message.petEntryEntityID != 0) {
				entity = player.worldObj.getEntityByID(message.petEntryEntityID);
			}
			petEntry.entity = entity;
			petEntry.respawnTime = message.respawnTime;
			petEntry.respawnTimeMax = message.respawnTimeMax;
			petEntry.isRespawning = message.isRespawning;
		}
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
            this.petEntryName = packet.readStringFromBuffer(256);
            this.petEntryID = packet.readInt();
            this.petEntryType = packet.readStringFromBuffer(256);
            this.spawningActive = packet.readBoolean();
            this.teleportEntity = packet.readBoolean();
			this.summonType = packet.readStringFromBuffer(256);
			this.behaviour = packet.readByte();
			this.petEntryEntityID = packet.readInt();
			this.respawnTime = packet.readInt();
			this.respawnTimeMax = packet.readInt();
			this.isRespawning = packet.readBoolean();
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
            packet.writeStringToBuffer(this.petEntryName);
			packet.writeInt(this.petEntryID);
            packet.writeStringToBuffer(this.petEntryType);
            packet.writeBoolean(this.spawningActive);
            packet.writeBoolean(this.teleportEntity);
			packet.writeStringToBuffer(this.summonType);
			packet.writeByte(this.behaviour);
			packet.writeInt(this.petEntryEntityID);
			packet.writeInt(this.respawnTime);
			packet.writeInt(this.respawnTimeMax);
			packet.writeBoolean(this.isRespawning);
		} catch (IOException e) {
			LycanitesMobs.printWarning("", "There was a problem encoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
}
