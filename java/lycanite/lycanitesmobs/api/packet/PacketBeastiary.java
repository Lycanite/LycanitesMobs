package lycanite.lycanitesmobs.api.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.Beastiary;
import lycanite.lycanitesmobs.api.info.CreatureKnowledge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public class PacketBeastiary extends PacketBase {
	public int entryAmount = 0;
	public String[] creatureNames;
	public double[] completions;
	
	// ==================================================
	//                 Read Creature Set
	// ==================================================
	public void readBeastiary(Beastiary beastiary) {
		this.entryAmount = beastiary.creatureKnowledgeList.size();
		if(this.entryAmount > 0) {
			this.creatureNames = new String[this.entryAmount];
			this.completions = new double[this.entryAmount];
			int i = 0;
			for(CreatureKnowledge creatureKnowledge : beastiary.creatureKnowledgeList.values()) {
				this.creatureNames[i] = creatureKnowledge.creatureName;
				this.completions[i] = creatureKnowledge.completion;
				i++;
			}
		}
	}
	
	
	// ==================================================
	//                      Encode
	// ==================================================
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		try {
			packet.writeInt(this.entryAmount);
			if(this.entryAmount > 0) {
				for(int i = 0; i < this.entryAmount; i++) {
					packet.writeStringToBuffer(this.creatureNames[i]);
					packet.writeDouble(this.completions[i]);
				}
			}
		} catch (IOException e) {
			LycanitesMobs.printWarning("", "There was a problem encoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
	
	// ==================================================
	//                      Decode
	// ==================================================
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		PacketBuffer packet = new PacketBuffer(buffer);
		try {
			this.entryAmount = packet.readInt();
			if(this.entryAmount > 0) {
				this.creatureNames = new String[this.entryAmount];
				this.completions = new double[this.entryAmount];
				for(int i = 0; i < this.entryAmount; i++) {
					this.creatureNames[i] = packet.readStringFromBuffer(256);
					this.completions[i] = packet.readDouble();
				}
			}
		} catch (IOException e) {
			LycanitesMobs.printWarning("", "There was a problem decoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
	
	// ==================================================
	//                   Handle Server
	// ==================================================
	@Override
	public void handleServerSide(EntityPlayer player) {
		// Should only be sent from the server to clients.
	}
	
	
	// ==================================================
	//                   Handle Client
	// ==================================================
	@Override
	public void handleClientSide(EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
		
		if(this.entryAmount < 0)
			return;
		
		Map<String, CreatureKnowledge> newKnowledgeList = new HashMap<String, CreatureKnowledge>();
		for(int i = 0; i < this.entryAmount; i++) {
			String creatureName = this.creatureNames[i];
			double completion = this.completions[i];
			CreatureKnowledge creatureKnowledge = new CreatureKnowledge(player, creatureName, completion);
			newKnowledgeList.put(creatureKnowledge.creatureName, creatureKnowledge);
		}
		playerExt.beastiary.newKnowledgeList(newKnowledgeList);
	}
}
