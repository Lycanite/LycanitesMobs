package com.lycanitesmobs.core.model;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector4f;

public class ModelEquipmentPart extends ModelItemBase {



	// ==================================================
	//                    Constructor
	// ==================================================
	public ModelEquipmentPart(String name, GroupInfo groupInfo) {
		this.initModel(name, groupInfo, "equipment/" + name);
	}


	// ==================================================
	//                   Get Texture
	// ==================================================
	@Override
	public ResourceLocation getTexture(ItemStack itemStack) {
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return null;
		}
		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
		return itemEquipmentPart.getTexture(itemStack);
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	@Override
	public Vector4f getPartColor(String partName, ItemStack itemStack) {
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return super.getPartColor(partName, itemStack);
		}
		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
		return itemEquipmentPart.getColor(itemStack);
	}
}
