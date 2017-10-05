package com.lycanitesmobs.core.spawner.trigger;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class KillSpawnTrigger extends SpawnTrigger {

	/** A list of entity types that match this trigger. **/
	public List<EnumCreatureAttribute> entityTypes = new ArrayList<>();

	/** Determines if the entity types list is a blacklist or whitelist. **/
	public String entityTypesListType = "whitelist";

	/** A list of entity ids that match this trigger. **/
	public List<String> entityIds = new ArrayList<>();

	/** Determines if the entity ids list is a blacklist or whitelist. **/
	public String entityIdsListType = "blacklist";


	/** Constructor **/
	public KillSpawnTrigger(Spawner spawner) {
		super(spawner);
	}

	/** Called every time a player kills an entity. **/
	public void onKill(EntityPlayer player, EntityLiving killedEntity) {

		// Check Entity Type:
		if(this.entityTypes.contains(killedEntity.getCreatureAttribute())) {
			if ("blacklist".equalsIgnoreCase(this.entityTypesListType)) {
				return;
			}
		}
		else {
			if ("whitelist".equalsIgnoreCase(this.entityTypesListType)) {
				return;
			}
		}

		// Check Entity Id:
		String entityId = EntityList.getEntityString(killedEntity);
		if(killedEntity instanceof EntityCreatureBase) {
			entityId = ((EntityCreatureBase)killedEntity).getEntityIdName();
		}
		if(entityId == null) {
			if ("whitelist".equalsIgnoreCase(this.entityIdsListType)) {
				return;
			}
		}
		else {
			if (this.entityIds.contains(entityId)) {
				if ("blacklist".equalsIgnoreCase(this.entityIdsListType)) {
					return;
				}
			}
			else {
				if ("whitelist".equalsIgnoreCase(this.entityIdsListType)) {
					return;
				}
			}
		}

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		this.trigger(player.getEntityWorld(), player, player.getPosition(), 0);
	}
}
