package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class EventListener {
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public EventListener() {}
	
	
	// ==================================================
    //                Entity Constructing
    // ==================================================
	@ForgeSubscribe
	public void onEntityConstructing(EntityConstructing event) {
		// ========== Extended Player ==========
		if(event.entity instanceof EntityPlayer)
			ExtendedPlayer.getForPlayer((EntityPlayer)event.entity);
	}
	
	
	// ==================================================
    //                  Entity Join World
    // ==================================================
	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		// ========== Extended Player ==========
		if(event.entity instanceof EntityPlayer)
			ExtendedPlayer.getForPlayer((EntityPlayer)event.entity);
	}
	
	
	// ==================================================
    //                 Living Death Event
    // ==================================================
	@ForgeSubscribe
	public void onLivingDeathEvent(LivingDeathEvent event) {
		// ========== Extended Player Data Backup ==========
		if(event.entity instanceof EntityPlayer)
			ExtendedPlayer.getForPlayer((EntityPlayer)event.entity).onDeath();
	}
	
	
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
			ExtendedPlayer extPlayer = ExtendedPlayer.getForPlayer(player);
			boolean creative = player.capabilities.isCreativeMode;
			
			// Summoning Focus Stat Update:
			if(extPlayer != null) {
				if(extPlayer.summonFocus < extPlayer.summonFocusMax) {
					extPlayer.summonFocus++;
					if(!creative && !player.worldObj.isRemote && player.worldObj.getWorldTime() % 20 == 0 &&
							(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemStaffSummoning)) {
						Packet packet = PacketHandler.createPacket(PacketHandler.PacketType.PLAYER, PacketHandler.PlayerType.SUMMONFOCUS.id, extPlayer.summonFocus);
						PacketHandler.sendPacketToServer(packet);
					}
				}
			}
			
			// Item Using:
			// This is used to replace Item.onUsingItemTick() as it acted weird. Intended to be used the same.
			/*if(player.getItemInUse() != null) {
				if(player.getItemInUse().getItem() instanceof ItemScepter) {
					ItemScepter scepter = (ItemScepter)player.getItemInUse().getItem();
					scepter.onPlayerUsing(player.getItemInUse(), player, player.getItemInUseCount());
				}
			}*/
		}
	}
	
	
    // ==================================================
    //                Entity Interact Event
    // ==================================================
	@ForgeSubscribe
	public void onEntityInteract(EntityInteractEvent event) {
		EntityPlayer player = event.entityPlayer;
		Entity entity = event.target;
		if(player == null || entity == null)
			return;
		
		if(player.getHeldItem() != null) {
			Item item = player.getHeldItem().getItem();
			if(item instanceof ItemBase)
				if(((ItemBase)item).onItemRightClickOnEntity(player, entity)) {
					if(event.isCancelable())
						event.setCanceled(true);
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
