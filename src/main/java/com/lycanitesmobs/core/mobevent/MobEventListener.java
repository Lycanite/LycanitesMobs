package com.lycanitesmobs.core.mobevent;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.mobevent.trigger.AltarMobEventTrigger;
import com.lycanitesmobs.core.mobevent.trigger.MobEventTrigger;
import com.lycanitesmobs.core.mobevent.trigger.RandomMobEventTrigger;
import com.lycanitesmobs.core.mobevent.trigger.TickMobEventTrigger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.util.ArrayList;
import java.util.List;


public class MobEventListener {
	// Global:
    protected static MobEventListener INSTANCE;

	public List<RandomMobEventTrigger> randomMobEventTriggers = new ArrayList<>();
	public List<TickMobEventTrigger> tickMobEventTriggers = new ArrayList<>();

	protected long lastEventUpdateTime = 0;


	/** Returns the main Mob Event Listener instance. **/
	public static MobEventListener getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MobEventListener();
		}
		return INSTANCE;
	}


	/**
	 * Adds a new Mob Event Trigger.
	 * @return True on success, false if it failed to add (could happen if the Trigger type has no matching list created yet).
	 */
	public boolean addTrigger(MobEventTrigger mobEventTrigger) {
		if(mobEventTrigger instanceof RandomMobEventTrigger && !this.randomMobEventTriggers.contains(mobEventTrigger)) {
			this.randomMobEventTriggers.add((RandomMobEventTrigger)mobEventTrigger);
			return true;
		}
		if(mobEventTrigger instanceof TickMobEventTrigger && !this.tickMobEventTriggers.contains(mobEventTrigger)) {
			this.tickMobEventTriggers.add((TickMobEventTrigger)mobEventTrigger);
			return true;
		}
		return false;
	}

	/**
	 * Removes a Mob Event Trigger.
	 */
	public void removeTrigger(MobEventTrigger mobEventTrigger) {
		if(this.randomMobEventTriggers.contains(mobEventTrigger)) {
			this.randomMobEventTriggers.remove(mobEventTrigger);
		}
		if(this.tickMobEventTriggers.contains(mobEventTrigger)) {
			this.tickMobEventTriggers.remove(mobEventTrigger);
		}
		if(mobEventTrigger instanceof AltarMobEventTrigger) {
			AltarMobEventTrigger altarMobEventTrigger = (AltarMobEventTrigger)mobEventTrigger;
			altarMobEventTrigger.altar.mobEventTrigger = null;
		}
	}


	/** Called every tick in a world and counts down to the next event then fires it! The countdown is paused during an event. **/
	@SubscribeEvent
	public void onWorldUpdate(WorldTickEvent event) {
		World world = event.world;
		if(world.isRemote)
			return;
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		if(worldExt == null)
			return;

		// Check If Events Are Completely Disabled:
		if(!MobEventManager.getInstance().mobEventsEnabled || world.getDifficulty() == EnumDifficulty.PEACEFUL) {
			if(worldExt.serverWorldEventPlayer != null)
				worldExt.stopWorldEvent();
			return;
		}

        // Only Tick On World Time Ticks:
        if(this.lastEventUpdateTime == world.getTotalWorldTime())
        	return;
		this.lastEventUpdateTime = world.getTotalWorldTime();
		
		// Only Run If Players Are Present:
		if(world.playerEntities.size() < 1) {
			return;
		}

		// Scheduled Mob Events:
		for(MobEventSchedule mobEventSchedule : MobEventManager.getInstance().mobEventSchedules) {
			if(mobEventSchedule.canStart(world)) {
				mobEventSchedule.start(worldExt);
			}
		}

		// Tick Mob Events:
		for(TickMobEventTrigger mobEventTrigger : this.tickMobEventTriggers) {
			mobEventTrigger.onTick(world, this.lastEventUpdateTime);
		}

        // Random Mob Events:
        if(MobEventManager.getInstance().mobEventsRandom) {
			if (MobEventManager.getInstance().minEventsRandomDay > 0 && Math.floor((worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 24000D) < MobEventManager.getInstance().minEventsRandomDay) {
				return;
			}
			if (worldExt.getWorldEventStartTargetTime() <= 0 || worldExt.getWorldEventStartTargetTime() > world.getTotalWorldTime() + MobEventManager.getInstance().maxTicksUntilEvent) {
				worldExt.setWorldEventStartTargetTime(world.getTotalWorldTime() + worldExt.getRandomEventDelay(world.rand));
			}
			if (world.getTotalWorldTime() >= worldExt.getWorldEventStartTargetTime()) {
				this.triggerRandomMobEvent(world, worldExt);
			}
		}
    }


	/**
	 * Triggers a Random Mob Event Trigger if one is available.
	 *  **/
	public void triggerRandomMobEvent(World world, ExtendedWorld worldExt) {
        // Get Triggers and Total Weight:
		List<RandomMobEventTrigger> validTriggers = new ArrayList<>();
		int totalWeights = 0;
		int highestPriority = 0;
		for(RandomMobEventTrigger mobEventTrigger : this.randomMobEventTriggers) {
			if(mobEventTrigger.priority >= highestPriority && mobEventTrigger.canTrigger(world, null)) {
				if(mobEventTrigger.priority > highestPriority) {
					totalWeights = 0;
					validTriggers.clear();
				}
				totalWeights += mobEventTrigger.weight;
				highestPriority = mobEventTrigger.priority;
				validTriggers.add(mobEventTrigger);
			}
		}
		if(totalWeights <= 0) {
			return;
		}

		// Fire Random Trigger Using Weights:
		int randomWeight = 1;
		if(totalWeights > 1) {
			randomWeight = world.rand.nextInt(totalWeights - 1) + 1;
		}
		int searchWeight = 0;
		for(RandomMobEventTrigger mobEventTrigger : validTriggers) {
			if(mobEventTrigger.weight + searchWeight > randomWeight) {
				mobEventTrigger.trigger(world, null, new BlockPos(0, 0, 0), 1);
				return;
			}
			searchWeight += mobEventTrigger.weight;
		}
	}
}
