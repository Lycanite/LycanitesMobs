package com.lycanitesmobs.core.model;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.renderer.EquipmentRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector4f;

public class ModelEquipment {

	/**
	 * Constructor
	 */
	public ModelEquipment() {

	}


	/**
	 * Renders an Equipment Item Stack.
	 * @param itemStack The Equipment Item Stack to render models from.
	 * @param hand The hand that the equipment is held in.
	 * @param renderer The renderer to render with.
	 */
	public void render(ItemStack itemStack, EnumHand hand, EquipmentRenderer renderer) {
		if(!(itemStack.getItem() instanceof ItemEquipment)) {
			return;
		}
		ItemEquipment itemEquipment = (ItemEquipment)itemStack.getItem();
		NonNullList<ItemStack> equipmentPartStacks = itemEquipment.getEquipmentPartStacks(itemStack);
		for(ItemStack partStack : equipmentPartStacks) { // TODO Include the slot index for positioning each part.
			this.renderPart(partStack, hand, renderer);
		}
	}


	/**
	 * Renders an Equipment Part.
	 * @param partStack The ItemStack to render the part from.
	 * @param hand The hand that the part is held in.
	 * @param renderer The renderer to render with.
	 */
	public void renderPart(ItemStack partStack, EnumHand hand, EquipmentRenderer renderer) {
		if(partStack.isEmpty() || !(partStack.getItem() instanceof ItemEquipmentPart)) {
			return;
		}

		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)partStack.getItem();
		ModelItemBase modelItemBase = AssetManager.getItemModel(itemEquipmentPart.itemName);
		modelItemBase.render(partStack, hand, renderer);
	}
}
