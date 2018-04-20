package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class PlayerSpawnCondition extends SpawnCondition {

    /** The username of the player. **/
    public String username = "";

	/** The minimum local area difficulty level. **/
	public float difficultyMin = -1;

	/** The maximum local area difficulty level. **/
	public float difficultyMax = -1;

    /** The minimum level of the player. **/
    public int levelMin = -1;

    /** The maximum level of the player. **/
    public int levelMax = -1;

    /** The minimum time that the player has been playing. **/
    public int timeMin = -1;

    /** The maximum time that the player has been playing. **/
    public int timeMax = -1;

	/** The minimum light level that the player must be in. **/
	public int lightLevelMin = -1;

	/** The maximum light level that the player must be in. **/
	public int lightLevelMax = -1;

	/** If true, the player must be on the ground. **/
	public boolean grounded = false;

	/** If true, the player must not be on the ground. **/
	public boolean notGrounded = false;

	/** If true, the player must be in the water. **/
	public boolean inWater = false;

	/** If true, the player must not be in the water. **/
	public boolean notInWater = false;

	/** A list of Blocks that match this Trigger. **/
	public List<Item> heldItems = new ArrayList<>();

	/** Determines if the blocks list is a blacklist or whitelist. **/
	public String heldItemsListType = "blacklist";


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("username"))
			this.username = json.get("username").getAsString();

		if(json.has("difficultyMin"))
			this.difficultyMin = json.get("difficultyMin").getAsFloat();

		if(json.has("difficultyMax"))
			this.difficultyMax = json.get("difficultyMax").getAsFloat();

		if(json.has("levelMin"))
			this.levelMin = json.get("levelMin").getAsInt();

		if(json.has("levelMax"))
			this.levelMax = json.get("levelMax").getAsInt();

		if(json.has("timeMin"))
			this.timeMin = json.get("timeMin").getAsInt();

		if(json.has("timeMax"))
			this.timeMax = json.get("timeMax").getAsInt();

		if(json.has("lightLevelMin"))
			this.lightLevelMin = json.get("lightLevelMin").getAsInt();

		if(json.has("lightLevelMax"))
			this.lightLevelMax = json.get("lightLevelMax").getAsInt();

		if(json.has("grounded"))
			this.grounded = json.get("grounded").getAsBoolean();

		if(json.has("notGrounded"))
			this.notGrounded = json.get("notGrounded").getAsBoolean();

		if(json.has("inWater"))
			this.inWater = json.get("inWater").getAsBoolean();

		if(json.has("notInWater"))
			this.notInWater = json.get("notInWater").getAsBoolean();

		if(json.has("heldItems")) {
			this.heldItems = JSONHelper.getJsonItems(json.get("heldItems").getAsJsonArray());
		}

		if(json.has("heldItemsListType"))
			this.heldItemsListType = json.get("heldItemsListType").getAsString();

		super.loadFromJSON(json);
	}


    @Override
    public boolean isMet(World world, EntityPlayer player, BlockPos position) {
		if(player == null) {
			return false;
		}

		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);

    	// Check Username:
        if(!"".equals(this.username) && !this.username.equalsIgnoreCase(player.getName())) {
        	return false;
		}

		// Check Local Area Difficulty:
		if(this.difficultyMin >= 0 || this.difficultyMax >= 0) {
			DifficultyInstance difficultyInstance = world.getDifficultyForLocation(player.getPosition());
			float difficulty = difficultyInstance.getAdditionalDifficulty();
			if (this.difficultyMin >= 0 && difficulty < this.difficultyMin) {
				return false;
			}
			if (this.difficultyMax >= 0 && difficulty > this.difficultyMax) {
				return false;
			}
		}

		// Check Level:
		if(this.levelMin >= 0 && player.experienceLevel < this.levelMin) {
			return false;
		}
		if(this.levelMax >= 0 && player.experienceLevel > this.levelMax) {
			return false;
		}

		// Check States:
		if(this.grounded && !player.onGround) {
			return false;
		}
		if(this.notGrounded && player.onGround) {
			return false;
		}
		if(this.inWater && !player.isInWater()) {
			return false;
		}
		if(this.notInWater && player.isInWater()) {
			return false;
		}

		// Check Time:
		if(playerExt != null) {
			if ( this.timeMin >= 0 && playerExt.timePlayed < this.timeMin){
				return false;
			}
			if (this.timeMax >= 0 && playerExt.timePlayed > this.timeMax) {
				return false;
			}
		}

		// Check Light Level:
		int lightLevel = world.getLight(player.getPosition());
		if(this.lightLevelMin >= 0 && lightLevel < this.lightLevelMin) {
			return false;
		}
		if(this.lightLevelMax >= 0 && lightLevel > this.lightLevelMax) {
			return false;
		}

		// Check Held Item:
		if(this.heldItems.size() > 0) {
			boolean holdingItem = false;

			// Main Hand:
			if(!player.getHeldItemMainhand().isEmpty()) {
				if (this.heldItems.contains(player.getHeldItemMainhand().getItem())) {
					holdingItem = true;
					if("blacklist".equalsIgnoreCase(this.heldItemsListType)) {
						return false;
					}
				}
			}

			// Off Hand:
			if(!player.getHeldItemOffhand().isEmpty()) {
				if (this.heldItems.contains(player.getHeldItemOffhand().getItem())) {
					holdingItem = true;
					if("blacklist".equalsIgnoreCase(this.heldItemsListType)) {
						return false;
					}
				}
			}

			// Whitelist:
			if(!holdingItem && "whitelist".equalsIgnoreCase(this.heldItemsListType)) {
				return false;
			}
		}
		else if("whitelist".equalsIgnoreCase(this.heldItemsListType)) {
			return false;
		}

        return super.isMet(world, player, position);
    }
}
