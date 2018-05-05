package com.lycanitesmobs.core.inventory;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import com.lycanitesmobs.core.item.equipment.features.SlotEquipmentFeature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SlotEquipment extends SlotBase {
	public String type;
	public List<SlotEquipment> childSlots = new ArrayList<>();

	/**
	 * Constructor
	 * @param inventory The invetory using this slot.
	 * @param slotIndex THe index of this slot.
	 * @param x The x display position.
	 * @param y The y display position.
	 * @param type The Equipment item type for this slot. Can be any part slot type or "piece" for an assembled piece of equipment or "none" when unavailable.
	 */
	public SlotEquipment(IInventory inventory, int slotIndex, int x, int y, String type) {
		super(inventory, slotIndex, x, y);
		this.type = type;
	}


	@Override
	public boolean isItemValid(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if(item instanceof ItemEquipmentPart) {
			return this.type.equals(((ItemEquipmentPart)item).slotType);
		}
		else if(item instanceof ItemEquipment) {
			return this.type.equals("piece");
		}
        return false;
    }

	
	public int getSlotStackLimit() {
		if(this.inventory instanceof InventoryCreature)
			if(((InventoryCreature)this.inventory).getTypeFromSlot(this.getSlotIndex()) != null)
				return 1;
        return this.inventory.getInventoryStackLimit();
    }


	/**
	 * Sets the Equipment Type that this slot can hold. If an item is present and doesn't match the new type it is destroyed.
	 * @param type The type to set the slot to.
	 */
	public void setType(String type) {
		this.type = type;
		// TODO Destroy existing parts that don't fit!
	}


	/**
	 * Adds a child equipment slot to this slot. Child slots have their type updated by their parents based on what is made available by the parent.
	 * @param slot The child slot to add. The first added should be the center, then left and then right (for all slots provided by Head pieces).
	 */
	public void addChildSlot(SlotEquipment slot) {
		if(this.childSlots.contains(slot)) {
			return;
		}
		this.childSlots.add(slot);
		this.updateChildSlots();
	}


	/**
	 * Updates a child equipment slot to the provided type and from the provided index if available.
	 * @param index The index of the child slot to update. If out of bounds then false is returned.
	 * @param type The type to update the slot to.
	 * @return True if a slot was updated.
	 */
	public boolean updateChildSlot(int index, String type) {
		if(this.childSlots.size() <= index) {
			return false;
		}
		this.childSlots.get(index).setType(type);
		return true;
	}


	/**
	 * Called when an ItemStack is inserted into this slot and updates any child slots.
	 * @param itemStack The ItemStack being inserted.
	 */
	@Override
	public void putStack(ItemStack itemStack) {
		super.putStack(itemStack);
		Item item = itemStack.getItem();

		// Equipment Part:
		if(item instanceof ItemEquipmentPart) {
			this.updateChildSlots();
		}

		// Equipment Piece:
		else if(item instanceof ItemEquipment) {
			// TODO Edit existing Equipment Piece.
		}
	}


	@Override
	public boolean canTakeStack(EntityPlayer player) {
		if("piece".equals(this.type)) {
			return super.canTakeStack(player);
		}
		return false;
	}


	@Override
	public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
		if("piece".equals(this.type)) {
			// TODO Clear all parts from the Equipment Container.
		}
		return super.onTake(player, itemStack);
	}


	/**
	 * Updates the type of each child slot that is connected to this slot.
	 */
	public void updateChildSlots() {
		Item item = this.getStack().getItem();

		// Update Child Slots:
		List<Integer> updatedChildSlots = new ArrayList<>();
		if(item instanceof ItemEquipmentPart) {
			ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart) item;

			int axeSlots = 0;
			for (EquipmentFeature feature : itemEquipmentPart.features) {
				if (feature instanceof SlotEquipmentFeature) {
					SlotEquipmentFeature slotFeature = (SlotEquipmentFeature) feature;
					int level = itemEquipmentPart.getLevel(this.getStack());
					if (slotFeature.isActive(this.getStack(), level)) {
						int index = 0;
						if (slotFeature.slotType.equals("axe")) {
							index = ++axeSlots;
						}
						if (!updatedChildSlots.contains(index) && this.updateChildSlot(index, slotFeature.slotType)) {
							updatedChildSlots.add(index);
						}
					}
				}
			}
		}

		// Set Unavailable Slots To None:
		for(int index = 0; index < this.childSlots.size(); index++) {
			if(!updatedChildSlots.contains(index)) {
				this.updateChildSlot(index, "none");
			}
		}
	}
}
