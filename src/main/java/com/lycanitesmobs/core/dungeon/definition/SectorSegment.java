package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class SectorSegment {
    /** Sector Segments are the floors, walls and ceilings of a sector. **/

	/** A list of layers that make up this segment. **/
	public Map<Integer, SectorLayer> layers = new HashMap<>();


    /** Loads this Dungeon Sector Segment from the provided JSON data. **/
	public void loadFromJSON(JsonElement json) {
		for(JsonElement jsonElement : json.getAsJsonArray()) {
			JsonObject layerJson = jsonElement.getAsJsonObject();
			if(!layerJson.has("layer") || !layerJson.has("pattern"))
				continue;
			String layerNumber = layerJson.get("layer").getAsString();
			if(!NumberUtils.isCreatable(layerNumber))
				continue;
			int layerIndex = NumberUtils.createInteger(layerNumber);

			SectorLayer layer = new SectorLayer();
			layer.loadFromJSON(layerJson);

			this.layers.put(layerIndex, layer);
		}
	}
}
