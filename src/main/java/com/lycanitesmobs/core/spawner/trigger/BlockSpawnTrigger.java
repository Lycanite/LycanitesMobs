package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.List;

public class BlockSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering when certain blocks are broken by the player. **/

	/** If true, only players can activate this Trigger, fake players are not counted. **/
	public boolean playerOnly = true;

	/** If true, the Block Break event will activate this trigger. **/
	public boolean onBreak = true;

	/** If true, the Block Harvest event will activate this trigger. **/
	public boolean onHarvest = true;

	/** If true, this Block Place event will activate this trigger. **/
	public boolean onPlace = false;

	/** A list of Blocks that match this Trigger. **/
	public List<Block> blocks = new ArrayList<>();

	/** Determines if the blocks list is a blacklist or whitelist. **/
	public String blocksListType = "whitelist";

	/** A list of Block Materials that match this Trigger. **/
	public List<Material> blockMaterials = new ArrayList<>();

	/** Determines if the block materials list is a blacklist or whitelist. **/
	public String blockMaterialsListType = "whitelist";

	/** How to react to blocks broken with or without silk touch. Can be: "ignore", "require" or "deny". Default: "ignore". **/
	public String silkTouch = "ignore";

	/** The minimum fortune that the harvester must have. Default: -1 (ignore) **/
	public double fortuneMin = -1;

	/** The maximum fortune that the harvester must have. Default: -1 (ignore) **/
	public double fortuneMax = -1;


	/** Constructor **/
	public BlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}

	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("playerOnly"))
			this.playerOnly = json.get("playerOnly").getAsBoolean();

		if(json.has("onBreak"))
			this.onBreak = json.get("onBreak").getAsBoolean();

		if(json.has("onHarvest"))
			this.onHarvest = json.get("onHarvest").getAsBoolean();

		if(json.has("onPlace"))
			this.onPlace = json.get("onPlace").getAsBoolean();

		this.blocks = JSONHelper.getJsonBlocks(json);

		if(json.has("blocksListType"))
			this.blocksListType = json.get("blocksListType").getAsString();

		this.blockMaterials = JSONHelper.getJsonMaterials(json);

		if(json.has("blockMaterialsListType"))
			this.blockMaterialsListType = json.get("blockMaterialsListType").getAsString();

		if(json.has("silkTouch"))
			this.silkTouch = json.get("silkTouch").getAsString();

		if(json.has("fortuneMin"))
			this.fortuneMin = json.get("fortuneMin").getAsInt();

		if(json.has("fortuneMax"))
			this.fortuneMax = json.get("fortuneMax").getAsInt();

		super.loadFromJSON(json);
	}


	/** Called every time a block breaks. **/
	public void onBlockBreak(World world, EntityPlayer player, BlockPos breakPos, IBlockState blockState, int chain) {
		if(!this.onBreak) {
			return;
		}
		this.onBlockTriggered(world, player, breakPos, blockState, chain, 0);
	}


	/** Called every time a block is harvested. **/
	public void onBlockHarvest(World world, EntityPlayer player, BlockPos breakPos, IBlockState blockState, int chain, int fortune, boolean silkTouch) {
		if(!this.onHarvest) {
			return;
		}

		if(this.fortuneMin >= 0 && fortune < this.fortuneMin) {
			return;
		}
		if(this.fortuneMax > this.fortuneMin && fortune > this.fortuneMax) {
			return;
		}

		if(silkTouch && "deny".equals(this.silkTouch)) {
			return;
		}
		if(!silkTouch && "require".equals(this.silkTouch)) {
			return;
		}

		this.onBlockTriggered(world, player, breakPos, blockState, chain, fortune);
	}


	/** Called every time a block is placed. **/
	public void onBlockPlace(World world, EntityPlayer player, BlockPos breakPos, IBlockState blockState, int chain) {
		if(!this.onPlace) {
			return;
		}
		this.onBlockTriggered(world, player, breakPos, blockState, chain, 0);
	}


	/** Called every time a player breaks a block. **/
	public void onBlockTriggered(World world, EntityPlayer player, BlockPos breakPos, IBlockState blockState, int chain, int fortune) {
		// Check Player:
		if(this.playerOnly && (player == null || player instanceof FakePlayer)) {
			return;
		}

		// Check Block:
		if(!this.isTriggerBlock(blockState, world, breakPos, fortune)) {
			return;
		}

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		this.trigger(world, player, breakPos, this.getBlockLevel(blockState, world, breakPos), chain);
	}

	/** Returns true if the provided block is a match for this trigger. **/
	public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos, int fortune) {
		if(this.blocks.size() > 0) {
			Block block = blockState.getBlock();
			if (this.blocks.contains(block)) {
				return !"blacklist".equalsIgnoreCase(this.blocksListType);
			}
		}

		if(this.blockMaterials.size() > 0) {
			Material material = blockState.getMaterial();
			if (this.blockMaterials.contains(material)) {
				return !"blacklist".equalsIgnoreCase(this.blockMaterialsListType);
			}
		}

		return "blacklist".equalsIgnoreCase(this.blocksListType) && "blacklist".equalsIgnoreCase(this.blockMaterialsListType);
	}

	/** Returns a value to represent the block's rarity for higher level spawns with increased chances of tougher mobs, etc. **/
	public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
		return 0;
	}
}
