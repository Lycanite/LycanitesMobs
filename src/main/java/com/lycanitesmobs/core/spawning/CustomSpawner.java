package com.lycanitesmobs.core.spawning;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSpawner {
    public static CustomSpawner instance;

    // ==================================================
    //                     Constructor
    // ==================================================
	public CustomSpawner() {
		instance = this;
	}
	
	
	// ==================================================
	//                 Entity Update Event
	// ==================================================
    public List<SpawnTypeBase> updateSpawnTypes = new ArrayList<>();
	public Map<EntityPlayer, Long> entityUpdateTicks = new HashMap<>();
	
	/** This uses the player update events to spawn mobs around each player randomly over time. **/
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null || !(entity instanceof EntityPlayer) || entity.getEntityWorld() == null || entity.getEntityWorld().isRemote || event.isCanceled())
			return;
		
		// ========== Spawn Near Players ==========
		EntityPlayer player = (EntityPlayer)entity;
		World world = player.getEntityWorld();
		
		if(!entityUpdateTicks.containsKey(player))
			entityUpdateTicks.put(player, (long)0);
		long entityUpdateTick = entityUpdateTicks.get(player);
		
		// Custom Mob Spawning:
		int tickOffset = 0;
		for(SpawnTypeBase spawnType : this.updateSpawnTypes) {
			spawnType.spawnMobs(entityUpdateTick - tickOffset, world, player.getPosition(), player);
			tickOffset += 105;
		}
		
		entityUpdateTicks.put(player, entityUpdateTick + 1);
	}
	
	
	// ==================================================
	//                 Entity Death Event
	// ==================================================
    public List<SpawnTypeDeath> deathSpawnTypes = new ArrayList<>();
	
	/** This uses the entity death events to spawn mobs when other mobs/players die. **/
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null || entity.getEntityWorld() == null || entity.getEntityWorld().isRemote || event.isCanceled())
			return;
		
		// ========== Get Killer ==========
		Entity killerEntity = event.getSource().getTrueSource();
		if(!(killerEntity instanceof EntityLivingBase))
			return;
		EntityLivingBase killer = (EntityLivingBase)killerEntity;
		if(!(killer instanceof EntityPlayer) && !(entity instanceof EntityPlayer))
			return;
		
		// ========== Get Coords ==========
		World world = entity.getEntityWorld();
		
		// ========== Pass To Spawners ==========
		for(SpawnTypeDeath spawnType : this.deathSpawnTypes) {
			if(spawnType.isValidKill(entity, killer))
				spawnType.spawnMobs(0, world, entity.getPosition(), null);
		}
	}

	
	// ==================================================
	//                 Harvest Drops Event
	// ==================================================
    public List<SpawnTypeBlockBreak> blockSpawnTypes = new ArrayList<SpawnTypeBlockBreak>();
	/** This uses the block harvest drops events to spawn mobs around blocks when they are destroyed. **/
	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isRemote || event.isCanceled())
			return;
		if(player != null || (player != null && player.capabilities.isCreativeMode)) // No Spawning for Creative Players
			return;
		
		// Spawn On Block Harvest:
		World world = event.getWorld();
        BlockPos spawnPos = event.getPos().add(0, 0, 1);

        for(SpawnTypeBlockBreak spawnType : this.blockSpawnTypes) {
            if(spawnType.validBlockHarvest(event.getState().getBlock(), world, spawnPos, player))
                spawnType.spawnMobs(0, world, spawnPos, player, event.getState().getBlock());
        }
	}


    // ==================================================
    //                 Break Block Event
    // ==================================================
    /** This uses the block break events to spawn mobs around blocks when they are destroyed. **/
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if(event.getState().getBlock() == null || event.getWorld() == null || event.getWorld().isRemote || event.isCanceled())
            return;
        if(player == null || (player != null && player.capabilities.isCreativeMode)) // No Spawning for Creative Players
            return;

        // Spawn On Block Harvest:
        World world = event.getWorld();
        BlockPos spawnPos = event.getPos().add(0, 0, 1);

        for(SpawnTypeBlockBreak spawnType : this.blockSpawnTypes) {
            if(spawnType.validBlockBreak(event.getState().getBlock(), world, spawnPos, player))
                spawnType.spawnMobs(0, world, spawnPos, player);
        }
    }

	
	// ==================================================
	//                Player Use Bed Event
	// ==================================================
    public List<SpawnTypeSleep> sleepSpawnTypes = new ArrayList<SpawnTypeSleep>();
	/** This uses the player sleep in bed event to spawn mobs. **/
	@SubscribeEvent
	public void onSleep(PlayerSleepInBedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player == null || event.isCanceled())
			return;
		
		// Get Coords:
		World world = player.getEntityWorld();
        BlockPos spawnPos = event.getPos().add(0, 0, 1);
		
		if(world == null || world.isRemote || world.provider.isDaytime())
			return;
		
		// Run Spawners:
		boolean interrupted = false;
		for(SpawnTypeBase spawnType : this.sleepSpawnTypes) {
			if(spawnType.spawnMobs(0, world, spawnPos, player))
				interrupted = true;
		}
		
		// Possible Interrupt:
		if(interrupted)
			event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
	}


	// ==================================================
	//                  Fished Event
	// ==================================================
	public List<SpawnTypeFishing> fishingTypes = new ArrayList<>();
	/** This uses the lightning strike event to spawn mobs. **/
	public void onFished(ItemFishedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player == null || event.isCanceled())
			return;

		// Get Coords:
		World world = player.getEntityWorld();
		BlockPos spawnPos = player.getPosition().add(0, 0, 1);
		Entity hookEntity = event.getHookEntity();
		if(hookEntity != null)
			spawnPos = hookEntity.getPosition();

		for(SpawnTypeFishing spawnType : this.fishingTypes) {
			spawnType.setHookEntity(hookEntity);
			spawnType.spawnMobs(0, world, spawnPos, player);
		}
	}

	
	// ==================================================
	//              Lightning Strike Event
	// ==================================================
    public List<SpawnTypeStorm> lightningStrikeTypes = new ArrayList<SpawnTypeStorm>();
	/** This uses the lightning strike event to spawn mobs. **/
	public void onLightningStrike() {
		//TODO Lightning strike detection.
	}
}
