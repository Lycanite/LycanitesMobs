package lycanite.lycanitesmobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lycanite.lycanitesmobs.api.ILycanEventListener;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.EntityItemCustom;
import lycanite.lycanitesmobs.api.item.ItemBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventListener {
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public EventListener() {}
	
	
    // ==================================================
    //                    World Load
    // ==================================================
	@SubscribeEvent
	public void onWorldLoading(WorldEvent.Load event) {
		if(event.world == null)
			return;
		
		// ========== Extended World ==========
		ExtendedWorld.getForWorld(event.world);
	}
	
	
	// ==================================================
    //                Entity Constructing
    // ==================================================
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if(event.entity == null)
			return;
		
		// ========== Extended Entity ==========
		if(event.entity != null)
			ExtendedEntity.getForEntity(event.entity);
		
		// ========== Extended Player ==========
		if(event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			ExtendedPlayer.getForPlayer(player);
		}
	}
	
	
	// ==================================================
    //                  Entity Join World
    // ==================================================
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		// ========== Extended Player ==========
		if(event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			ExtendedPlayer playerExtended = ExtendedPlayer.getForPlayer(player);
			playerExtended.onJoinWorld();
		}
	}
	
	
	// ==================================================
    //                 Living Death Event
    // ==================================================
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null) return;
		
		// ========== Extended Entity ==========
		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
		if(extendedEntity != null)
			extendedEntity.onDeath();
		
		// ========== Extended Player ==========
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			ExtendedPlayer.getForPlayer(player).onDeath();
		}
	}
	
	
	// ==================================================
	//                    Entity Update
	// ==================================================
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null) return;

		// ========== Extended Entity ==========
		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
		if(extendedEntity != null)
			extendedEntity.update();

		// ========== Extended Player ==========
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if(playerExt != null)
				playerExt.onUpdate();
		}
	}
	
	
    // ==================================================
    //                Entity Interact Event
    // ==================================================
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		EntityPlayer player = event.entityPlayer;
		Entity entity = event.target;
		if(player == null || entity == null)
			return;
		
		// Item onItemRightClickOnEntity():
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
	@SubscribeEvent
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
	public static List<ILycanEventListener> onLivingHurtListeners = new ArrayList<ILycanEventListener>();
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		if(event.isCancelable() && event.isCanceled())
	      return;
		
		if(event.entityLiving == null || event.source == null)
			return;
		
		/*/ ========== Minimum Armor Damage ========== Moved to EntityCreatureBase as it doesn't work here. :/
		float damage = event.ammount;
		if(damage > 0 && !event.source.isUnblockable() && !event.source.isDamageAbsolute() && event.source.getEntity() != null && event.source.getEntity() instanceof EntityCreatureBase) {
			float minDamage = 1 + (float)Math.floor(damage / 5.0D);
			if(damage <= minDamage) {
				event.source.setDamageBypassesArmor().setDamageIsAbsolute();
			}
			else {
				event.ammount = damage - minDamage;
				DamageSource unblockableDamage = DamageSource.causeMobDamage((EntityLivingBase)event.source.getEntity()).setDamageBypassesArmor().setDamageIsAbsolute();
				event.entityLiving.attackEntityFrom(unblockableDamage, minDamage);
				//event.entityLiving.setHealth(event.entityLiving.getHealth() - minDamage);
			}
		}*/
		
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
	
	
	// ==================================================
    //                 Living Drops Event
    // ==================================================
	@SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
		World world = event.entityLiving.worldObj;
		
		// Halloween Treats:
		Calendar calendar = Calendar.getInstance();
		if(Utilities.isHalloween()) {
			boolean noHalloweenTreat = false;
			boolean alwaysDrop = false;
			if(event.entityLiving instanceof EntityCreatureBase) {
				if(((EntityCreatureBase)event.entityLiving).isMinion())
					noHalloweenTreat = true;
				if(((EntityCreatureBase)event.entityLiving).getSubspecies() != null)
					alwaysDrop = true;
			}
			if(ObjectManager.getItem("halloweentreat") != null && !noHalloweenTreat && (alwaysDrop || event.entityLiving.getRNG().nextFloat() >= 0.6F)) {
				ItemStack dropStack = new ItemStack(ObjectManager.getItem("halloweentreat"), 1);
				EntityItemCustom entityItem = new EntityItemCustom(world, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, dropStack);
				entityItem.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(entityItem);
			}
		}
	}
	
	
    // ==================================================
    //                 Bucket Fill Event
    // ==================================================
	@SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        World world = event.world;
        MovingObjectPosition pos = event.target;
        Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
        Item bucket = ObjectManager.buckets.get(block);
        ItemStack result = null;
        if(bucket != null && world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0) {
            world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
            result = new ItemStack(bucket);
        }
        
        if(result == null)
        	return;

        event.result = result;
        event.setResult(Result.ALLOW);
    }
}
