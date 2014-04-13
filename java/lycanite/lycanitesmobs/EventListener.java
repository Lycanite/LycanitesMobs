package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.api.item.ItemSummoningStaff;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

public class EventListener {
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public EventListener() {}
	
	
	// ==================================================
	//                    Entity Update
	// ==================================================
	@ForgeSubscribe
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null)
			return;
		
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			boolean creative = player.capabilities.isCreativeMode;
			
			// Summoning Focus Stat Update:
			int playerFocus = PlayerControlHandler.getPlayerSummonFocus(player);
			int focusMax = PlayerControlHandler.summonFocusMax;
			if(playerFocus < focusMax) {
				playerFocus++;
				PlayerControlHandler.setPlayerSummonFocus(player, playerFocus);
				if(!creative && !player.worldObj.isRemote && player.worldObj.getWorldTime() % 20 == 0 &&
						(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemSummoningStaff)) {
					Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, PacketHandler.PlayerType.SUMMONFOCUS.id, playerFocus);
					PacketHandler.sendPacketToServer(packet);
				}
			}
			
			// Item Using:
			// This is used to replace Item.onUsingItemTick() as it acted weird. Intended to be used the same.
			if(player.getItemInUse() != null) {
				if(player.getItemInUse().getItem() instanceof ItemScepter) {
					ItemScepter scepter = (ItemScepter)player.getItemInUse().getItem();
					scepter.onPlayerUsing(player.getItemInUse(), player, player.getItemInUseCount());
				}
			}
		}
	}
	
	
    // ==================================================
    //                 Attack Target Event
    // ==================================================
	@ForgeSubscribe
	public void onAttackTarget(LivingSetAttackTargetEvent event) {
		if(event.isCancelable() && event.isCanceled())
	      return;
		
		// Better Invisibility:
		if(event.entityLiving != null) {
			if(event.entityLiving.isPotionActive(Potion.nightVision))
				return;
			if(event.target != null) {
				if(event.target.isInvisible())
					if(event.isCancelable())
						event.setCanceled(true);
			}
		}
	}
	
	
    // ==================================================
    //                 Living Hurt Event
    // ==================================================
	@ForgeSubscribe
	public void onLivingHurt(LivingHurtEvent event) {
		if(event.isCancelable() && event.isCanceled())
	      return;
		
		if(event.entityLiving == null || event.source == null)
			return;
		
		// ========== Mounted Protection ==========
		if(event.entityLiving.ridingEntity != null) {
			if(event.entityLiving.ridingEntity instanceof EntityCreatureRideable) {
				// Prevent Mounted Entities from Suffocating:
				if("inWall".equals(event.source.damageType)) {
					event.setCanceled(true);
					return;
				}
				
				// Copy Mount Immunities to Rider:
				EntityCreatureRideable creatureRideable = (EntityCreatureRideable)event.entityLiving.ridingEntity;
				if(!creatureRideable.isDamageTypeApplicable(event.source.damageType)) {
					event.setCanceled(true);
					return;
				}
			}
		}
	}
}
