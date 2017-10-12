package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;

public class DamageEquipmentFeature extends EquipmentFeature {
	/** How much damage this part adds to the weapon attack. **/
	public int damageAmount = 1;

	/** How much knockback this part adds to the weapon attack. **/
	public double damageKnockback = 0;

	/** The time (in ticks) that this feature adds to the weapon attack cooldown on use. **/
	public int damageCooldown = 0;

	/** The range (in blocks) that this feature adds to the weapon attack. **/
	public double damageRange = 0;

	/** The angle (in degrees) that this feature applies to the weapon attack. (The feature with the largest range is used.) **/
	public double damageSweep = 45;


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("damageAmount"))
			this.damageAmount = json.get("damageAmount").getAsInt();

		if(json.has("damageKnockback"))
			this.damageKnockback = json.get("damageKnockback").getAsDouble();

		if(json.has("damageCooldown"))
			this.damageCooldown = json.get("damageCooldown").getAsInt();

		if(json.has("damageRange"))
			this.damageRange = json.get("damageRange").getAsDouble();

		if(json.has("damageSweep"))
			this.damageSweep = json.get("damageSweep").getAsDouble();

		super.loadFromJSON(json);
	}
}
