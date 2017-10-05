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


    @Override
    public boolean isMet(World world, EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);

    	// Check Username:
        if(!"".equals(this.username) && this.username.equalsIgnoreCase(player.getName())) {
        	return false;
		}

		// Check Level:
		if(this.levelMin > 0 && player.experienceLevel < this.levelMin) {
			return false;
		}
		if(this.levelMax > 0 && player.experienceLevel > this.levelMax) {
			return false;
		}

		// Check Time:
		if(playerExt != null) {
			if ( this.timeMin > 0 && playerExt.timePlayed < this.timeMin){
				return false;
			}
			if (this.timeMax > 0 && playerExt.timePlayed > this.timeMax) {
				return false;
			}
		}

		// Check Light Level:
		int lightLevel = world.getLight(player.getPosition());
		if(this.lightLevelMin > 0 && lightLevel < this.lightLevelMin) {
			return false;
		}
		if(this.lightLevelMax > 0 && lightLevel > this.lightLevelMax) {
			return false;
		}

        return super.isMet(world, player);
    }

    @Override
    public void fromJSON(JsonObject json) {
        super.fromJSON(json);
    }
}
