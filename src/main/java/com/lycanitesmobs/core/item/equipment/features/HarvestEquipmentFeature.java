package com.lycanitesmobs.core.item.equipment.features;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.translation.I18n;

public class HarvestEquipmentFeature extends EquipmentFeature {
	/** The type of tool to harvest as. Can be: pickaxe, axe, shovel, hoe, sword or shears. **/
	public String harvestType;

	/** The shape of the harvest. Can be block, cross or random. **/
	public String harvestShape = "block";

	/** The range of the harvest shape, the central block is not affected by this. **/
	public Vec3i harvestRange = new Vec3i(0, 0, 0);

	/** Each extra level of the part that is using this featured increases the range by its base range times by this multiplier per level. **/
	public double harvestRangeLevelMultiplier = 1;


	@Override
	public void loadFromJSON(JsonObject json) {
		this.harvestType = json.get("harvestType").getAsString();

		if(json.has("harvestType"))
			this.harvestType = json.get("harvestType").getAsString();

		if(json.has("harvestShape"))
			this.harvestShape = json.get("harvestShape").getAsString();

		this.harvestRange = JSONHelper.getVec3i(json, "harvestRange");

		if(json.has("harvestRangeLevelMultiplier"))
			this.harvestRangeLevelMultiplier = json.get("harvestRangeLevelMultiplier").getAsDouble();

		super.loadFromJSON(json);
	}

	@Override
	public String getDescription(ItemStack itemStack, int level) {
		if(!this.isActive(itemStack, level)) {
			return null;
		}
		String description = I18n.translateToLocal("equipment.feature." + this.featureType) + " " + this.harvestType;
		description += "\n" + I18n.translateToLocal("equipment.feature.harvest.shape") + " " + this.harvestShape;
		if(this.harvestRange.distanceSq(new Vec3i(0, 0, 0)) > 0) {
			description += "\n" + I18n.translateToLocal("equipment.feature.harvest.range") + " " + this.getHarvestRangeString(level);
		}
		return description;
	}

	public String getHarvestRangeString(int level) {
		String harvestRangeString = "" + Math.round(this.harvestRange.getX() + (this.harvestRange.getX() * (level - 1) * this.harvestRangeLevelMultiplier));
		harvestRangeString += ", " + Math.round(this.harvestRange.getY() + (this.harvestRange.getY() * (level - 1) * this.harvestRangeLevelMultiplier));
		harvestRangeString += ", " + Math.round(this.harvestRange.getZ() + (this.harvestRange.getZ() * (level - 1) * this.harvestRangeLevelMultiplier));
		return harvestRangeString;
	}
}
