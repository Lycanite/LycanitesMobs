package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.inventory.ContainerBase;
import com.lycanitesmobs.core.inventory.SlotEquipment;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerEquipmentForge extends ContainerBase {
	public TileEntityEquipmentForge equipmentForge;

	/**
	 * Constructor
	 * @param equipmentForge The Equipment Forge Tile Entity.
	 * @param playerInventory The Invetory of the accessing player.
	 */
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
			SlotEquipment slotEquipmentPiece = new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 6), y, "piece");
			this.addSlotToContainer(slotEquipmentPiece);

			// Base:
			SlotEquipment slotEquipmentBase = new SlotEquipment(this.equipmentForge, slots++, x + slotSize, y, "base");
			this.addSlotToContainer(slotEquipmentBase);

			// Head:
			SlotEquipment slotEquipmentHead = new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 2), y, "none");
			this.addSlotToContainer(slotEquipmentHead);
			slotEquipmentBase.addChildSlot(slotEquipmentHead);

			// Tips:
			SlotEquipment slotEquipmentTipA = new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 3), y, "none");
			this.addSlotToContainer(slotEquipmentTipA);
			slotEquipmentHead.addChildSlot(slotEquipmentTipA);

			SlotEquipment slotEquipmentTipB = new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 2), y + slotSize, "none");
			this.addSlotToContainer(slotEquipmentTipB);
			slotEquipmentHead.addChildSlot(slotEquipmentTipB);

			SlotEquipment slotEquipmentTipC = new SlotEquipment(this.equipmentForge, slots++, x + (slotSize * 2), y - slotSize, "none");
			this.addSlotToContainer(slotEquipmentTipC);
			slotEquipmentHead.addChildSlot(slotEquipmentTipC);

			// Pommel:
			SlotEquipment slotEquipmentPommel = new SlotEquipment(this.equipmentForge, slots++, x, y, "none");
			this.addSlotToContainer(slotEquipmentPommel);
			slotEquipmentBase.addChildSlot(slotEquipmentPommel);
		}
		this.inventoryFinish = this.inventoryStart + slots;
		//this.inventoryFinish = this.inventorySlots.size() - 1;
	}


	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if(this.equipmentForge == null) {
			return false;
		}
		return true;
	}


	/**
	 * Updates the child to parent relationships with each slot. Must be called on both the client and server.
	 */
	public void updateSlotConnections() {

	}
}
