package lycanite.lycanitesmobs.api.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public abstract class PacketBase {
	
	// ==================================================
	//                      Player
	// ==================================================
	private EntityPlayer player;
	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	public EntityPlayer getPlayer() {
		return this.player;
	}
	
	
	// ==================================================
	//                      Encode
	// ==================================================
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer);
	

	// ==================================================
	//                      Decode
	// ==================================================
	public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer);
	

	// ==================================================
	//                   Handle Server
	// ==================================================
	public abstract void handleServerSide(EntityPlayer player);
	

	// ==================================================
	//                   Handle Client
	// ==================================================
	public abstract void handleClientSide(EntityPlayer player);
}
