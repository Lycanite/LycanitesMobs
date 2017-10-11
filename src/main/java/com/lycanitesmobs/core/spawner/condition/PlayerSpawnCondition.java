package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PlayerSpawnCondition extends SpawnCondition {

    /** The username of the player. **/
    public String username = "";

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


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("username"))
			this.username = json.get("username").getAsString();

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

		super.loadFromJSON(json);
	}


    @Override
    public boolean isMet(World world, EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);

    	// Check Username:
        if(!"".equals(this.username) && !this.username.equalsIgnoreCase(player.getName())) {
        	return false;
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

        return super.isMet(world, player);
    }
}
