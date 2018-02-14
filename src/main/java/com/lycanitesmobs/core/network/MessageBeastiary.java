package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.Beastiary;
import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public class MessageBeastiary implements IMessage, IMessageHandler<MessageBeastiary, IMessage> {
	public int entryAmount = 0;
	public String[] creatureNames;
	public double[] completions;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageBeastiary() {}
	public MessageBeastiary(Beastiary beastiary) {
		this.entryAmount = Math.min(201, beastiary.creatureKnowledgeList.size());
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
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageBeastiary message, MessageContext ctx) {
		if(ctx.side != Side.CLIENT) return null;
		EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) return null;
		if(message.entryAmount < 0) return null;
		
		Map<String, CreatureKnowledge> newKnowledgeList = new HashMap<String, CreatureKnowledge>();
		for(int i = 0; i < message.entryAmount; i++) {
			String creatureName = message.creatureNames[i];
			double completion = message.completions[i];
			CreatureKnowledge creatureKnowledge = new CreatureKnowledge(playerExt.getBeastiary(), creatureName, completion);
			newKnowledgeList.put(creatureKnowledge.creatureName, creatureKnowledge);
		}
		playerExt.getBeastiary().newKnowledgeList(newKnowledgeList);
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
        this.entryAmount = Math.min(200, packet.readInt());
        if(this.entryAmount == 200) {
        	LycanitesMobs.printWarning("", "Received 200 or more creature entries, something went wrong with the Beastiary packet! Addition entries will be skipped to prevent OOM!");
		}
        if(this.entryAmount > 0) {
            this.creatureNames = new String[this.entryAmount];
            this.completions = new double[this.entryAmount];
            for(int i = 0; i < this.entryAmount; i++) {
                this.creatureNames[i] = packet.readString(32767);
                this.completions[i] = packet.readDouble();
            }
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
        packet.writeInt(this.entryAmount);
        if(this.entryAmount > 0) {
            for(int i = 0; i < this.entryAmount; i++) {
                packet.writeString(this.creatureNames[i]);
                packet.writeDouble(this.completions[i]);
            }
        }
	}
	
}
