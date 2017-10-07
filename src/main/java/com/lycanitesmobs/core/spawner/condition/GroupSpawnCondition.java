package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupSpawnCondition extends SpawnCondition {

    /** A list of child Conditions that this Condition will check. **/
    public List<SpawnCondition> conditions = new ArrayList<>();

	/** Determines how many child Conditions must be met. If 0 or less all are required. **/
    public int conditionsRequired = 0;


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("conditionsRequired"))
			this.conditionsRequired = json.get("conditionsRequired").getAsInt();

		if(json.has("conditions")) {
			JsonArray jsonArray = json.get("conditions").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject conditionJson = jsonIterator.next().getAsJsonObject();
				SpawnCondition spawnCondition = SpawnCondition.createFromJSON(conditionJson);
				this.conditions.add(spawnCondition);
			}
		}

		super.loadFromJSON(json);
	}


    @Override
    public boolean isMet(World world, EntityPlayer player) {
		int conditionsMet = 0;
		for(SpawnCondition condition : this.conditions) {
			if(condition.isMet(world, player)) {
				conditionsMet++;
				if(this.conditionsRequired > 0 && conditionsMet >= this.conditionsRequired) {
					return super.isMet(world, player);
				}
			}
			else {
				if(this.conditionsRequired <= 0) {
					return false;
				}
			}
		}

		return false;
    }
}
