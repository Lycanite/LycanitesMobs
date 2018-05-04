package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.inventory.ContainerBase;
import com.lycanitesmobs.core.inventory.SlotEquipment;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerEquipmentForge extends ContainerBase {
	public TileEntityEquipmentForge equipmentForge;

	// ========================================
	//                Constructor
	// ========================================
	public ContainerEquipmentForge(TileEntityEquipmentForge equipmentForge, InventoryPlayer playerInventory) {
		super();
		this.equipmentForge = equipmentForge;

		// Player Inventory
		this.addPlayerSlots(playerInventory, 0, 0);

		// Forge Inventory
		this.inventoryStart = this.inventorySlots.size();
		int slots = 0;
		if(equipmentForge.getInventorySize() > 0) {
			int slotSize = 18;
			int x = 8 + slotSize;
			int y = 38;

			// Crafted Piece:
			this.addSlotToContainer(new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 6), y, "piece"));

			// Base:
			this.addSlotToContainer(new SlotEquipment(this.equipmentForge, slots++, x + slotSize, y, "base"));

			// Head:
			this.addSlotToContainer(new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 2), y, "none"));

			// Tips:
			this.addSlotToContainer(new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 3), y, "none"));
			this.addSlotToContainer(new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 3), y + slotSize, "none"));
			this.addSlotToContainer(new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 3), y - slotSize, "none"));

			// Pommel:
			this.addSlotToContainer(new SlotEquipment(this.equipmentForge, slots++, x, y, "none"));
		}
		this.inventoryFinish = this.inventoryStart + slots;
		//this.inventoryFinish = this.inventorySlots.size() - 1;
	}


	// ========================================
	//                  Interact
	// ========================================
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if(this.equipmentForge == null) {
			return false;
		}
		return true;
	}
}
