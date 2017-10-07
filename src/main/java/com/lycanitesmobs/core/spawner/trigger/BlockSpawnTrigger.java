package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.spawner.SpawnerJSONUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering when certain blocks are broken by the player. **/

	/** Whether fake players (such as BuildCraft quarries) should trigger this also. **/
	public boolean ignoreFakePlayers = true;

	/** A list of Blocks that match this Trigger. **/
	public List<Block> blocks = new ArrayList<>();

	/** A list of Block Materials that match this Trigger. **/
	public List<Material> blockMaterials = new ArrayList<>();

	/** Determines if the block/material lists are a blacklist or whitelist. **/
	public String listType = "whitelist";


	/** Constructor **/
	public BlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}

	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("ignoreFakePlayers"))
			this.ignoreFakePlayers = json.get("ignoreFakePlayers").getAsBoolean();

		this.blocks = SpawnerJSONUtilities.getJsonBlocks(json);

		this.blockMaterials = SpawnerJSONUtilities.getJsonMaterials(json);

		if(json.has("listType"))
			this.listType = json.get("listType").getAsString();

		super.loadFromJSON(json);
	}


	/** Called every time a player breaks a block. **/
	public void onBlockBreak(World world, EntityPlayer player, BlockPos breakPos, IBlockState blockState) {
		// Check Player:
		if(this.ignoreFakePlayers && player instanceof FakePlayer) {
			return;
		}

		// Check Block:
		if(!this.isTriggerBlock(blockState, world, breakPos)) {
			return;
		}

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		this.trigger(world, player, player.getPosition(), this.getBlockLevel(blockState, world, breakPos));
	}

	/** Returns true if the provided block is a match for this trigger. **/
	public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos) {
		Block block = blockState.getBlock();
		if(this.blocks.contains(block)) {
			return !"blacklist".equalsIgnoreCase(this.listType);
		}

		Material material = blockState.getMaterial();
		if(this.blockMaterials.contains(material)) {
			return !"blacklist".equalsIgnoreCase(this.listType);
		}

		return "blacklist".equalsIgnoreCase(this.listType);
	}

	/** Returns a value to represent the block's rarity for higher level spawns with increased chances of tougher mobs, etc. **/
	public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
		return 0;
	}
}
