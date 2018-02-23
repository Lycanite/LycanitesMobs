package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class VillageSpawnLocation extends RandomSpawnLocation {

	/** How close to the player (in blocks) Villages must be. Default: 128. **/
	public int villageRange = 128;


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		if(json.has("villageRange"))
			this.villageRange = json.get("villageRange").getAsInt();
	}


	@Override
	public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
		LycanitesMobs.printDebug("JSONSpawner", "Getting Nearest Village Within Range");

		Village village = world.getVillageCollection().getNearestVillage(triggerPos, this.villageRange);

		// No Village:
		if(village == null) {
			LycanitesMobs.printDebug("JSONSpawner", "No Village within range found.");
			return new ArrayList<>();
		}

		// Too Far:
		double villageDistance = Math.sqrt(village.getCenter().distanceSq(triggerPos));
		if(villageDistance > this.villageRange) {
			LycanitesMobs.printDebug("JSONSpawner", "No Village within range, nearest was: " + villageDistance);
			return new ArrayList<>();
		}

		// Village Found:
		LycanitesMobs.printDebug("JSONSpawner", "Found a Village within range, at: " + village.getCenter());
		return super.getSpawnPositions(world, player, village.getCenter());
	}

}
