package lycanite.lycanitesmobs.api.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

public class PacketEntityGUICommand extends PacketBase {
	int entityID;
	public byte guiCommandID;
	
	// ==================================================
	//                 Read Creature Set
	// ==================================================
	public void readGUICommand(byte guiCommandID, Entity entity) {
		this.entityID = entity.getEntityId();
		this.guiCommandID = guiCommandID;
	}
	
	
	// ==================================================
	//                      Encode
	// ==================================================
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		packet.writeInt(this.entityID);
		packet.writeByte(this.guiCommandID);
	}
	
	
	// ==================================================
	//                      Decode
	// ==================================================
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		this.entityID = packet.readInt();
		this.guiCommandID = packet.readByte();
	}
	
	
	// ==================================================
	//                   Handle Server
	// ==================================================
	@Override
	public void handleServerSide(EntityPlayer player) {
		World world = player.worldObj;
		Entity entity = world.getEntityByID(this.entityID);
		if(entity instanceof EntityCreatureTameable) {
			EntityCreatureTameable pet = (EntityCreatureTameable)entity;
			pet.performGUICommand((EntityPlayer)player, this.guiCommandID);
		}
	}
	
	
	// ==================================================
	//                   Handle Client
	// ==================================================
	@Override
	public void handleClientSide(EntityPlayer player) {
		// Should only be sent from clients to the server.
	}

}