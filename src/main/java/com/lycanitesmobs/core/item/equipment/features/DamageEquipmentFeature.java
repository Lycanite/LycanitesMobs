package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class DamageEquipmentFeature extends EquipmentFeature {
	/** How much damage this part adds to the weapon attack. **/
	public int damageAmount = 1;

	/** The time (in ticks) that this feature adds to the weapon attack cooldown on use. **/
	public int damageCooldown = 0;

	/** How much knockback this part adds to the weapon attack. **/
	public double damageKnockback = 0;

	/** The range (in blocks) that this feature adds to the weapon attack. **/
	public double damageRange = 0;

	/** The angle (in degrees) that this feature applies to the weapon attack. (The feature with the largest range is used.) **/
	public double damageSweep = 45;


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("damageAmount"))
			this.damageAmount = json.get("damageAmount").getAsInt();

		if(json.has("damageCooldown"))
			this.damageCooldown = json.get("damageCooldown").getAsInt();

		if(json.has("damageKnockback"))
			this.damageKnockback = json.get("damageKnockback").getAsDouble();

		if(json.has("damageRange"))
			this.damageRange = json.get("damageRange").getAsDouble();

		if(json.has("damageSweep"))
			this.damageSweep = json.get("damageSweep").getAsDouble();

		super.loadFromJSON(json);
	}

	@Override
	public String getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		String description = I18n.translateToLocal("equipment.feature." + this.featureType) + " " + this.damageAmount;
		if(this.damageCooldown > 0) {
			description += "\n" + I18n.translateToLocal("equipment.feature.damage.cooldown") + " " + this.damageCooldown;
		}
		if(this.damageKnockback > 0) {
			description += "\n" + I18n.translateToLocal("equipment.feature.damage.knockback") + " " + this.damageKnockback;
		}
		if(this.damageRange > 0) {
			description += "\n" + I18n.translateToLocal("equipment.feature.damage.range") + " " + this.damageRange;
		}
		if(this.damageSweep > 45) {
			description += "\n" + I18n.translateToLocal("equipment.feature.damage.sweep") + " " + this.damageSweep;
		}
		return description;
	}
}
