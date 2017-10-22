package com.lycanitesmobs.core.mobevent.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.mobevent.MobEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AltarMobEventTrigger extends MobEventTrigger {
	/** The Altar to trigger from. TODO Altars should be created via JSON here instead. **/
	public AltarInfo altar;


	/** Constructor **/
	public AltarMobEventTrigger(MobEvent mobEvent) {
		super(mobEvent);
	}


	/** Loads this Mob Event Trigger from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		this.altar = AltarInfo.getAltar(json.get("altarName").getAsString());
		this.altar.mobEventTrigger = this;

		super.loadFromJSON(json);
	}


	/**
	 * Called when the associated altar has been activated for this trigger.
	 * @param entity
	 * @param world The world that the Altar was activated in.
	 * @param pos The central position of the Altar.
	 * @param level The level of the activated the Altar.
	 * @return
	 */
	public boolean onActivate(Entity entity, World world, BlockPos pos, int level) {
		EntityPlayer player = null;
		if(entity instanceof EntityPlayer) {
			player = (EntityPlayer)entity;
		}
		if(!this.canTrigger(world, player)) {
			return false;
		}
		return this.trigger(world, player, pos, level);
	}
}
