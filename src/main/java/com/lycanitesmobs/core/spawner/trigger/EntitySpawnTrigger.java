package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class EntitySpawnTrigger extends SpawnTrigger {

	/** A list of entity types that match this trigger. **/
	public List<EnumCreatureAttribute> entityTypes = new ArrayList<>();

	/** Determines if the entity types list is a blacklist or whitelist. **/
	public String entityTypesListType = "whitelist";

	/** A list of entity ids that match this trigger. **/
	public List<String> entityIds = new ArrayList<>();

	/** Determines if the entity ids list is a blacklist or whitelist. **/
	public String entityIdsListType = "blacklist";


	/** Constructor **/
	public EntitySpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("entityTypes")) {
			JsonArray jsonArray = json.get("entityTypes").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				EnumCreatureAttribute entityType = EnumCreatureAttribute.valueOf(jsonIterator.next().getAsString().toUpperCase());
				if(entityType != null) {
					entityTypes.add(entityType);
				}
			}
		}

		if(json.has("entityTypesListType"))
			this.entityTypesListType = json.get("entityTypesListType").getAsString();

		if(json.has("entityIds")) {
			JsonArray jsonArray = json.get("entityIds").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				String entityId = jsonIterator.next().getAsString();
				if(entityId != null) {
					entityIds.add(entityId);
				}
			}
		}

		if(json.has("entityIdsListType"))
			this.entityIdsListType = json.get("entityIdsListType").getAsString();

		super.loadFromJSON(json);
	}


	/** Returns true fi the provided entity should trigger this Spawn Trigger. **/
	public boolean isMatchingEntity(EntityLiving killedEntity) {

		// Check Entity Type:
		if(this.entityTypes.contains(killedEntity.getCreatureAttribute())) {
			if ("blacklist".equalsIgnoreCase(this.entityTypesListType)) {
				return false;
			}
		}
		else {
			if ("whitelist".equalsIgnoreCase(this.entityTypesListType)) {
				return false;
			}
		}

		// Check Entity Id:
		String entityId = EntityList.getEntityString(killedEntity);
		if(killedEntity instanceof EntityCreatureBase) {
			entityId = ((EntityCreatureBase)killedEntity).getEntityIdName();
		}
		if(entityId == null) {
			if ("whitelist".equalsIgnoreCase(this.entityIdsListType)) {
				return false;
			}
		}
		else {
			if (this.entityIds.contains(entityId)) {
				if ("blacklist".equalsIgnoreCase(this.entityIdsListType)) {
					return false;
				}
			}
			else {
				if ("whitelist".equalsIgnoreCase(this.entityIdsListType)) {
					return false;
				}
			}
		}

		return true;
	}
}
