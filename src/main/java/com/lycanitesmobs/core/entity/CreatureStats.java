package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.world.EnumDifficulty;

/** Manages the stats of an EntityCreature. This applies difficulty multipliers, subspecies, levels, etc also. **/
public class CreatureStats {

	/** The entity using these stats. **/
	public EntityCreatureBase entity;


	/**
	 * Constructor
	 * @param entity The entity instance that will use this stats instance.
	 */
	public CreatureStats(EntityCreatureBase entity) {
		this.entity = entity;
	}


	/**
	 * Returns the maximum health the entity should have.
	 * @return Maximum base health.
	 */
	public double getHealth() {
		String statName = "health";
		double statValue = this.entity.creatureInfo.health;

		// Wild:
		if(this.entity.getOwner() != null) {
			statValue *= this.getDifficultyMultiplier(statName);
			statValue *= this.getSubspeciesMultiplier(statName);
			if(entity.extraMobBehaviour != null) {
				statValue *= entity.extraMobBehaviour.multiplierHealth;
				statValue += entity.extraMobBehaviour.boostHealth;
			}
		}

		// Tamed:
		else {
			statValue *= CreatureManager.getInstance().tamedHealthMultiplier;
		}

		statValue *= this.getLevelMultiplier(statName);

		return Math.max(0, statValue);
	}


	/**
	 * Returns the defense this entity should use. Damage taken is reduced by defense (to a minimum cap of 1 damage).
	 * @return Base defense.
	 */
	public double getDefense() {
		String statName = "defense";
		double statValue = this.entity.creatureInfo.defense;

		// Wild:
		if(this.entity.getOwner() != null) {
			statValue *= this.getDifficultyMultiplier(statName);
			statValue *= this.getSubspeciesMultiplier(statName);
			if(entity.extraMobBehaviour != null) {
				statValue *= entity.extraMobBehaviour.multiplierDefense;
				statValue += entity.extraMobBehaviour.boostDefense;
			}
		}

		return statValue;
	}


	/**
	 * Returns the armor this entity should use. This armor value is applied through the vanilla system and can be pierced by piercing effects unlike defense.
	 * @return Base armor.
	 */
	public double getArmor() {
		String statName = "armor";
		double statValue = this.entity.creatureInfo.armor;

		// Wild:
		if(this.entity.getOwner() != null) {
			statValue *= this.getDifficultyMultiplier(statName);
			statValue *= this.getSubspeciesMultiplier(statName);
			if(entity.extraMobBehaviour != null) {
				statValue *= entity.extraMobBehaviour.multiplierArmor;
				statValue += entity.extraMobBehaviour.boostArmor;
			}
		}

		return statValue;
	}


	/**
	 * Returns the speed this entity should use. Speed affects how fast an entity can travel.
	 * @return Base speed.
	 */
	public double getSpeed() {
		String statName = "speed";
		double statValue = this.entity.creatureInfo.speed;

		// Wild:
		if(this.entity.getOwner() != null) {
			statValue *= this.getDifficultyMultiplier(statName);
			statValue *= this.getSubspeciesMultiplier(statName);
			if(entity.extraMobBehaviour != null) {
				statValue *= entity.extraMobBehaviour.multiplierSpeed;
				statValue += entity.extraMobBehaviour.boostSpeed;
			}
		}

		return statValue;
	}


	/**
	 * Returns the damage this entity should use. This is how much melee damage this creature applies to entities.
	 * @return Base damage.
	 */
	public double getDamage() {
		String statName = "damage";
		double statValue = this.entity.creatureInfo.damage;

		// Wild:
		if(this.entity.getOwner() != null) {
			statValue *= this.getDifficultyMultiplier(statName);
			statValue *= this.getSubspeciesMultiplier(statName);
			if(entity.extraMobBehaviour != null) {
				statValue *= entity.extraMobBehaviour.multiplierDamage;
				statValue += entity.extraMobBehaviour.boostDamage;
			}
		}

		return statValue;
	}


	/**
	 * Returns the haste this entity should use. Haste affects how quickly this entity attacks, at 1 it is at default at 2 twice as fast. Capped at 1 attack per 10 ticks.
	 * @return Base haste.
	 */
	public double getHaste() {
		String statName = "haste";
		double statValue = this.entity.creatureInfo.haste;

		// Wild:
		if(this.entity.getOwner() != null) {
			statValue *= this.getDifficultyMultiplier(statName);
			statValue *= this.getSubspeciesMultiplier(statName);
			if(entity.extraMobBehaviour != null) {
				statValue *= entity.extraMobBehaviour.multiplierHaste;
				statValue += entity.extraMobBehaviour.boostHaste;
			}
		}

		return statValue;
	}


	/**
	 * Returns the effect this entity should use. This affects how long any debuffs applied by this entity last for.
	 * @return Base effect.
	 */
	public double getEffect() {
		String statName = "effect";
		double statValue = this.entity.creatureInfo.effect;

		// Wild:
		if(this.entity.getOwner() != null) {
			statValue *= this.getDifficultyMultiplier(statName);
			statValue *= this.getSubspeciesMultiplier(statName);
			if(entity.extraMobBehaviour != null) {
				statValue *= entity.extraMobBehaviour.multiplierEffect;
				statValue += entity.extraMobBehaviour.boostEffect;
			}
		}

		return statValue;
	}


	/**
	 * Returns the pierce this entity should use. Pierce affects how much damage dealt by this entity will ignore the targets armor.
	 * @return Base pierce.
	 */
	public double getPierce() {
		String statName = "pierce";
		double statValue = this.entity.creatureInfo.pierce;

		// Wild:
		if(this.entity.getOwner() != null) {
			statValue *= this.getDifficultyMultiplier(statName);
			statValue *= this.getSubspeciesMultiplier(statName);
			if(entity.extraMobBehaviour != null) {
				statValue *= entity.extraMobBehaviour.multiplierPierce;
				statValue += entity.extraMobBehaviour.boostPierce;
			}
		}

		return statValue;
	}


	/**
	 * Returns the knockback resistance this entity should use. The chance from 0.0-1.0 of an entity being knocked back when hit.
	 * @return Base knockback resistance.
	 */
	public double getKnockbackResistance() {
		return this.entity.creatureInfo.knockbackResistance;
	}


	/**
	 * Returns the sight this entity should use. How far this entity can see other entities. Aka Follow Range.
	 * @return Base sight.
	 */
	public double getSight() {
		return this.entity.creatureInfo.sight;
	}


	/**
	 * Returns a difficulty stat multiplier for the provided stat name and the world that the entity is in.
	 * @param stat The name of the stat to get the multiplier for.
	 * @return The stat multiplier.
	 */
	protected double getDifficultyMultiplier(String stat) {
		EnumDifficulty difficulty = this.entity.getEntityWorld().getDifficulty();
		String difficultyName = "Easy";
		if(difficulty.getDifficultyId() >= 3)
			difficultyName = "Hard";
		else if(difficulty == EnumDifficulty.NORMAL)
			difficultyName = "Normal";
		return CreatureManager.getInstance().getDifficultyMultiplier(difficultyName.toUpperCase(), stat.toUpperCase());
	}


	/**
	 * Returns a subspecies stat multiplier for the provided stat name and the subspecies that the entity is.
	 * @param stat The name of the stat to get the multiplier for.
	 * @return The stat multiplier.
	 */
	protected double getSubspeciesMultiplier(String stat) {
		if(this.entity.getSubspecies() != null) {
			return Subspecies.statMultipliers.get(this.entity.getSubspecies().type.toUpperCase() + "-" + stat.toUpperCase());

		}
		return 1;
	}


	/**
	 * Returns a level stat multiplier for the provided stat name and the creature's current level.
	 * @param stat The name of the stat to get the multiplier for.
	 * @return The stat multiplier.
	 */
	protected double getLevelMultiplier(String stat) {
		double statLevel = Math.max(0, this.entity.getLevel() - 1);
		return 1 + (statLevel * CreatureManager.getInstance().getLevelMultiplier(stat.toUpperCase()));
	}
}
