package com.lycanitesmobs.core.mobevent.effects;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureMobEventEffect extends MobEventEffect {

	/** The Structure Builder to activate. TODO Replace with JSON Structures. **/
	StructureBuilder structureBuilder;


	@Override
	public void loadFromJSON(JsonObject json) {
		this.structureBuilder = StructureBuilder.getStructureBuilder(json.get("structureBuilderName").getAsString());

		super.loadFromJSON(json);
	}


	@Override
	public void onUpdate(World world, EntityPlayer player, BlockPos pos, int level, int ticks) {
		if(this.structureBuilder != null) {
			this.structureBuilder.build(world, player, pos, level, ticks);
		}
	}
}
