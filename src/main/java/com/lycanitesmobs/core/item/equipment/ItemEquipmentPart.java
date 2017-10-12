package com.lycanitesmobs.core.item.equipment;

import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;

import java.util.ArrayList;
import java.util.List;

public class ItemEquipmentPart extends ItemBase {
	/** A list of all features this part has. **/
	public List<EquipmentFeature> features = new ArrayList<>();

	/** The name of this part. **/
	public String name;

	/** The slot type that this part must fit into. Can be: base, head, blade, axe, pike or jewel. **/
	public String slotType;

	/** The id of the mob that drops this part. **/
	public String dropMobId;

	/** The default chance of the part being dropped by a mob. **/
	public double dropChance;

	/** The minimum random level that this part can be. **/
	public int levelMin;

	/** The maximum random level that this part can be. **/
	public int levelMax;

	/** The level of this equipment part. TODO Must be loaded from the item data. **/
	public int level = 1;
}
