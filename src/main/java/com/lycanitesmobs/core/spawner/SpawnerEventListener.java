package com.lycanitesmobs.core.spawner;

import com.lycanitesmobs.core.spawner.trigger.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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

public class SpawnerEventListener {
    public static SpawnerEventListener instance;

    // ==================================================
    //                     Constructor
    // ==================================================
	public SpawnerEventListener() {
		instance = this;
	}
	
	
	// ==================================================
	//               Entity Update Event
	// ==================================================
    public List<TickSpawnTrigger> tickSpawnTriggers = new ArrayList<>();
	public Map<EntityPlayer, Long> playerUpdateTicks = new HashMap<>();
	
	/** This uses the player update events to update Tick Spawn Triggers. **/
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null || !(entity instanceof EntityPlayer) || entity.getEntityWorld() == null || entity.getEntityWorld().isRemote || event.isCanceled())
			return;
		
		// ========== Spawn Near Players ==========
		EntityPlayer player = (EntityPlayer)entity;
		
		if(!playerUpdateTicks.containsKey(player))
			playerUpdateTicks.put(player, (long)0);
		long entityUpdateTick = playerUpdateTicks.get(player);
		
		// Custom Mob Spawning:
		int tickOffset = 0;
		for(TickSpawnTrigger spawnTrigger : this.tickSpawnTriggers) {
			spawnTrigger.onTick(player, entityUpdateTick - tickOffset);
			tickOffset += 105;
		}

		playerUpdateTicks.put(player, entityUpdateTick + 1);
	}
	
	
	// ==================================================
	//                 Entity Death Event
	// ==================================================
    public List<KillSpawnTrigger> killSpawnTriggers = new ArrayList<>();
	
	/** This uses the entity death events to update Kill Spawn Triggers. **/
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		// Get Killed:
		EntityLivingBase killedEntity = event.getEntityLiving();
		if(killedEntity == null || killedEntity.getEntityWorld() == null || killedEntity.getEntityWorld().isRemote || event.isCanceled() || !(killedEntity instanceof EntityLiving)) {
			return;
		}
		
		// Get Killer:
		Entity killerEntity = event.getSource().getTrueSource();
		if(!(killerEntity instanceof EntityPlayer)) {
			return;
		}

		// Call Triggers:
		for(KillSpawnTrigger spawnTrigger : this.killSpawnTriggers) {
			spawnTrigger.onKill((EntityPlayer)killerEntity, (EntityLiving)killedEntity);
		}
	}

	
	// ==================================================
	//                 Harvest Drops Event
	// ==================================================
    public List<BlockSpawnTrigger> blockSpawnTriggers = new ArrayList<>();
	/** This uses the block harvest drops events to update Block Spawn Triggers. **/
	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isRemote || event.isCanceled()) {
			return;
		}
		if(player != null || (player != null && player.capabilities.isCreativeMode)) { // No Spawning for Creative Players
			return;
		}
		
		// Spawn On Block Harvest:
		World world = event.getWorld();
        BlockPos blockPos = event.getPos().add(0, 0, 1);
		IBlockState blockState = event.getState();

        for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
            spawnTrigger.onBlockBreak(world, player, blockPos, blockState);
        }
	}


    // ==================================================
    //                 Break Block Event
    // ==================================================
	/** This uses the block break events to update Block Spawn Triggers. **/
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isRemote || event.isCanceled()) {
			return;
		}
		if(player != null || (player != null && player.capabilities.isCreativeMode)) { // No Spawning for Creative Players
			return;
		}

		// Spawn On Block Harvest:
		World world = event.getWorld();
		BlockPos blockPos = event.getPos().add(0, 0, 1);
		IBlockState blockState = event.getState();

		for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
			spawnTrigger.onBlockBreak(world, player, blockPos, blockState);
		}
    }

	
	// ==================================================
	//                Player Use Bed Event
	// ==================================================
    public List<SleepSpawnTrigger> sleepSpawnTriggers = new ArrayList<>();
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
		for(SleepSpawnTrigger spawnTrigger : this.sleepSpawnTriggers) {
			if(spawnTrigger.onSleep(world, player, spawnPos)) {
				interrupted = true;
			}
		}
		
		// Interrupt:
		if(interrupted) {
			event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
		}
	}


	// ==================================================
	//                  Fished Event
	// ==================================================
	public List<FishingSpawnTrigger> fishingSpawnTriggers = new ArrayList<>();
	/** This uses the lightning strike event to spawn mobs. **/
	public void onFished(ItemFishedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player == null || event.isCanceled())
			return;

		World world = player.getEntityWorld();
		Entity hookEntity = event.getHookEntity();
		for(FishingSpawnTrigger spawnTrigger : this.fishingSpawnTriggers) {
			spawnTrigger.onFished(world, player, hookEntity);
		}
	}
}
