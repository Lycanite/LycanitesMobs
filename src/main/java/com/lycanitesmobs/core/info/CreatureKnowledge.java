package com.lycanitesmobs.core.info;

public class CreatureKnowledge {
	public Beastiary beastiary;
	public String creatureName;
	public int rank;


	/**
	 * Constructor
	 * @param beastiary The Beastiary this knowledge is part of.
	 * @param creatureName The name of the creature that this is knowledge of.
	 * @param rank The rank of this knowledge.
	 */
	public CreatureKnowledge(Beastiary beastiary, String creatureName, int rank) {
		this.beastiary = beastiary;
		this.creatureName = creatureName;
		this.rank = rank;
	}


	/**
	 * Returns the Creature Info of the Knowledge.
	 * @return The creature info.
	 */
	public CreatureInfo getCreatureInfo() {
		return CreatureManager.getInstance().getCreature(this.creatureName);
	}
}
