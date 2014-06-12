package lycanite.lycanitesmobs.api.network;

import io.netty.buffer.ByteBuf;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

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
	public IMessage onMessage(MessageEntityGUICommand message, MessageContext ctx) {
		if(ctx.side != Side.SERVER) return null;
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		Entity entity = world.getEntityByID(message.entityID);
		if(entity instanceof EntityCreatureTameable) {
			EntityCreatureTameable pet = (EntityCreatureTameable)entity;
			pet.performGUICommand((EntityPlayer)player, message.guiCommandID);
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
