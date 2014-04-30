package lycanite.lycanitesmobs.api.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ChannelHandler.Sharable
public class PacketPipeline extends MessageToMessageCodec<FMLProxyPacket, PacketBase> {
	private EnumMap<Side, FMLEmbeddedChannel> channels;
	private LinkedList<Class<? extends PacketBase>> packets = new LinkedList<Class<? extends PacketBase>>();
	private boolean isPostInitialized = false;
	
	// ==================================================
	//                  Register Packet
	// ==================================================
	/**
	 * Registers a new packet class. Will return false is there was a problem when registering. There is a 256 packet type limit.
	 * @param packet
	 * @return
	 */
	public boolean registerPacket(Class<? extends PacketBase> packet) {
		if(this.packets.size() > 256) {
			LycanitesMobs.printWarning("", "256 packets have already been registered! When registering: " + packet);
			return false;
    	}
		
		if(this.packets.contains(packet)) {
			LycanitesMobs.printWarning("", "Tried to register the packet: " + packet + " when it is already registered.");
			return false;
    	}
		
		if(this.isPostInitialized) {
			LycanitesMobs.printWarning("", "Tried to register a packet: " + packet + " after the Post Initialization event.");
			return false;
    	}
		
		this.packets.add(packet);
		return true;
	}
	
	
	// ==================================================
	//                  Encode Packet
	// ==================================================
	/**
	 * Encodes a packet.
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, PacketBase msg, List<Object> out) throws Exception {
		ByteBuf buffer = Unpooled.buffer();
		Class<? extends PacketBase> packetClass = msg.getClass();
		if (!this.packets.contains(msg.getClass())) {
			throw new NullPointerException("No Packet Registered for: " + msg.getClass().getCanonicalName());
		}
		
		byte discriminator = (byte) this.packets.indexOf(packetClass);
		buffer.writeByte(discriminator);
		msg.encodeInto(ctx, buffer);
		FMLProxyPacket proxyPacket = new FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
		out.add(proxyPacket);
	}
	
	
	// ==================================================
	//                  Decode Packet
	// ==================================================
	/**
	 * Decodes a packet.
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
		ByteBuf payload = msg.payload();
		byte discriminator = payload.readByte();
		Class<? extends PacketBase> packetClass = this.packets.get(discriminator);
		if(packetClass == null) {
			throw new NullPointerException("No packet registered for discriminator: " + discriminator);
		}
		
		PacketBase pkt = packetClass.newInstance();
		pkt.decodeInto(ctx, payload.slice());
		
		EntityPlayer player;
		switch (FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT:
				player = this.getClientPlayer();
				pkt.handleClientSide(player);
			break;
			
			case SERVER:
				INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				player = ((NetHandlerPlayServer)netHandler).playerEntity;
				pkt.handleServerSide(player);
			break;
			
			default:
		}
		
		out.add(pkt);
	}
	
	
	// ==================================================
	//                    Initialize
	// ==================================================
	/**
	 * Initializes the Packet Pipeline, should be called by an @Mod init event.
	 */
	public void initialize() {
		this.channels = NetworkRegistry.INSTANCE.newChannel("LYCANITESMOBS", this);
		
		// Register Common Packets:
		
	}
	
	
	// ==================================================
	//                  Post Initialize
	// ==================================================
	/**
	 * Post initializes the Packet Pipeline, should be called by an @Mod post init event.
	 */
	public void postInitialize() {
		if(this.isPostInitialized)
			return;
		this.isPostInitialized = true;
		
		Collections.sort(this.packets, new Comparator<Class<? extends PacketBase>>() {
			@Override
			public int compare(Class<? extends PacketBase> packet1, Class<? extends PacketBase> packet2) {
				int com = String.CASE_INSENSITIVE_ORDER.compare(packet1.getCanonicalName(), packet2.getCanonicalName());
				if(com == 0) {
					com = packet1.getCanonicalName().compareTo(packet2.getCanonicalName());
				}
				return com;
			}
		});
	}
	
	
	// ==================================================
	//                 Get Client Player
	// ==================================================
	/**
	 * Returns the client side player entity.
	 * @return Client side player entity.
	 */
	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	
	// ==================================================
	//                    Send To All
	// ==================================================
	/**
	 * Sends a packet from the server to all players.
	 * @param packet
	 */
	public void sendToAll(PacketBase packet) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		this.channels.get(Side.SERVER).writeAndFlush(packet);
	}
	
	
	// ==================================================
	//                   Send To Player
	// ==================================================
	/**
	 * Sends a packet from the server to the specified player.
	 * @param packet
	 * @param player
	 */
	public void sendToPlayer(PacketBase packet, EntityPlayerMP player) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		this.channels.get(Side.SERVER).writeAndFlush(packet);
	}
	
	
	// ==================================================
	//                 Send To All Around
	// ==================================================
	/**
	 * Sends a packet from the server to all players near the specified target point.
	 * @param packet
	 * @param point
	 */
	public void sendToAllAround(PacketBase packet, NetworkRegistry.TargetPoint point) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		this.channels.get(Side.SERVER).writeAndFlush(packet);
	}
	
	
	// ==================================================
	//                 Send To Dimension
	// ==================================================
	/**
	 * Sends a packet to all players within the specified dimension.
	 * @param packet
	 * @param dimensionID The ID of the dimension to use.
	 */
	public void sendToDimension(PacketBase packet, int dimensionID) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionID);
		this.channels.get(Side.SERVER).writeAndFlush(packet);
	}
	
	
	// ==================================================
	//                   Send To Server
	// ==================================================
	/**
	 * Sends a packet from the client player to the server.
	 * @param packet
	 */
	public void sendToServer(PacketBase packet) {
		this.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		this.channels.get(Side.CLIENT).writeAndFlush(packet);
	}
}
