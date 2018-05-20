package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

public class StructureSpawnLocation extends RandomSpawnLocation {

	/** The name of the Structure Type. Vanilla offers: Stronghold, Monument, Village, Mansion, EndCity, Fortress, Temple and Mineshaft, though Mineshaft is buggy, see the mineshaft.json spawner for a better way. Default: Stronghold. **/
	public String structureType = "Stronghold";

	/** How close to the player (in blocks) Structures must be. Default: 128. **/
	public int structureRange = 128;


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		if(json.has("structureType"))
			this.structureType = json.get("structureType").getAsString();

		if(json.has("structureRange"))
			this.structureRange = json.get("structureRange").getAsInt();
	}


	@Override
	public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
		LycanitesMobs.printDebug("JSONSpawner", "Getting Nearest Structures Within Range");
		if(!(world instanceof WorldServer)) {
			LycanitesMobs.printWarning("", "[JSONSpawner] Structure spawn location was called with a non ServerWorld World instance.");
			return new ArrayList<>();
		}

		BlockPos structurePos = null;
		try {
			structurePos = ((WorldServer) world).getChunkProvider().getNearestStructurePos(world, this.structureType, triggerPos, false);
		}
		catch (Exception e) {}

		// No Structure:
		if(structurePos == null) {
			LycanitesMobs.printDebug("JSONSpawner", "No Structures found.");
			return new ArrayList<>();
		}

		// Too Far:
		double structureDistance = Math.sqrt(structurePos.distanceSq(triggerPos));
		if(structureDistance > this.structureRange) {
			LycanitesMobs.printDebug("JSONSpawner", "No Structures within range, nearest was: " + structureDistance + " at: " + structurePos);
			return new ArrayList<>();
		}

		// Village Found:
		LycanitesMobs.printDebug("JSONSpawner", "Found a Structures within range, at: " + structurePos);
		return super.getSpawnPositions(world, player, structurePos);
	}

}
