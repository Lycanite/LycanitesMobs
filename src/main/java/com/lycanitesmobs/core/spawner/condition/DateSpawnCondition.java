package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Calendar;

public class DateSpawnCondition extends SpawnCondition {

    /** The minimum month that is must be. **/
    public int monthMin = -1;

	/** The maximum month that is must be. **/
	public int monthMax = -1;

    /** The minimum day of the month it must be. **/
    public int dayMin = -1;

    /** The maximum day of the month it must be. **/
    public int dayMax = -1;

    /** A season to check for. Can be "valentines", "easter", "midsummer", "halloween", "yuletide"/"christmas" or "newyear". **/
    public String season = "";


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("monthMin"))
			this.monthMin = json.get("monthMin").getAsInt();

		if(json.has("monthMax"))
			this.monthMax = json.get("monthMax").getAsInt();

		if(json.has("dayMin"))
			this.dayMin = json.get("dayMin").getAsInt();

		if(json.has("dayMax"))
			this.dayMax = json.get("dayMax").getAsInt();

		if(json.has("season"))
			this.season = json.get("season").getAsString();

		super.loadFromJSON(json);
	}


    @Override
    public boolean isMet(World world, EntityPlayer player, BlockPos position) {
		Calendar calendar = Calendar.getInstance();

    	// Check Month:
		int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0-11!
		if(this.monthMin >= 0 && month < this.monthMin) {
			return false;
		}
		if(this.monthMax >= 0 && month > this.monthMax) {
			return false;
		}

		// Check Day of Month:
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if(this.dayMin >= 0 && day < this.dayMin) {
			return false;
		}
		if(this.dayMax >= 0 && day > this.dayMax) {
			return false;
		}

		// Check Season
		if(!"".equals(this.season)) {
			if("valentines".equalsIgnoreCase(this.season) && !Utilities.isValentines()) {
				return false;
			}
			if("easter".equalsIgnoreCase(this.season) && !Utilities.isEaster()) {
				return false;
			}
			if("midsummer".equalsIgnoreCase(this.season) && !Utilities.isMidsummer()) {
				return false;
			}
			if("halloween".equalsIgnoreCase(this.season) && !Utilities.isHalloween()) {
				return false;
			}
			if(("yuletide".equalsIgnoreCase(this.season) || "christmas".equalsIgnoreCase(this.season)) && !Utilities.isYuletide()) {
				return false;
			}
			if("newyear".equalsIgnoreCase(this.season) && !Utilities.isNewYear()) {
				return false;
			}
		}

        return super.isMet(world, player, position);
    }
}
