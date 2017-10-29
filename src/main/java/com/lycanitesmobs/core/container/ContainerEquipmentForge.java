package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.inventory.ContainerBase;
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
		this.drawPlayerSlots(playerInventory, 0, 0);

		// Forge Inventory
		this.inventoryStart = this.inventorySlots.size();
		if(equipmentForge.getInventorySize() > 0) {
			this.drawSlotsByColumn(equipmentForge, 8 + (18 * 4), 18, 5, 0, equipmentForge.getInventorySize() - 1);
		}
		this.inventoryFinish = this.inventorySlots.size() - 1;
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
