package com.lycanitesmobs.core.container;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.inventory.ContainerBase;
import com.lycanitesmobs.core.inventory.SlotEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

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
			SlotEquipment slotEquipmentPiece = new SlotEquipment(this, slots++, x + (slotSize * 6), y, "piece");
			this.addSlotToContainer(slotEquipmentPiece);

			// Base:
			SlotEquipment slotEquipmentBase = new SlotEquipment(this, slots++, x + slotSize, y, "base");
			this.addSlotToContainer(slotEquipmentBase);

			// Head:
			SlotEquipment slotEquipmentHead = new SlotEquipment(this, slots++, x + (slotSize * 2), y, "none");
			this.addSlotToContainer(slotEquipmentHead);
			slotEquipmentBase.addChildSlot(slotEquipmentHead);

			// Tips:
			SlotEquipment slotEquipmentTipA = new SlotEquipment(this, slots++, x + (slotSize * 3), y, "none");
			this.addSlotToContainer(slotEquipmentTipA);
			slotEquipmentHead.addChildSlot(slotEquipmentTipA);

			SlotEquipment slotEquipmentTipB = new SlotEquipment(this, slots++, x + (slotSize * 2), y + slotSize, "none");
			this.addSlotToContainer(slotEquipmentTipB);
			slotEquipmentHead.addChildSlot(slotEquipmentTipB);

			SlotEquipment slotEquipmentTipC = new SlotEquipment(this, slots++, x + (slotSize * 2), y - slotSize, "none");
			this.addSlotToContainer(slotEquipmentTipC);
			slotEquipmentHead.addChildSlot(slotEquipmentTipC);

			// Pommel:
			SlotEquipment slotEquipmentPommel = new SlotEquipment(this, slots++, x, y, "none");
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
	 * Called when an equipment slot's contents is changed.
	 */
	public void onEquipmentSlotChanged(SlotEquipment slotEquipment) {
		if(this.equipmentForge == null || this.equipmentForge.getWorld().isRemote) {
			return;
		}

		// Piece Changed:
		if("piece".equals(slotEquipment.type)) {
			// TODO Clear all the parts!
			return;
		}

		// Parts Changed:
		Slot slotPiece = this.getSlot(this.inventoryStart);
		Slot slotBase = this.getSlot(this.inventoryStart + 1);
		Slot slotHead = this.getSlot(this.inventoryStart + 2);
		Slot slotTipA = this.getSlot(this.inventoryStart + 3);
		Slot slotTipB = this.getSlot(this.inventoryStart + 4);
		Slot slotTipC = this.getSlot(this.inventoryStart + 5);
		Slot slotPommel = this.getSlot(this.inventoryStart + 6);
		if(!slotBase.getHasStack() || !slotHead.getHasStack()) {
			return;
		}

		// Create Equipment Piece:
		ItemEquipment itemEquipment = (ItemEquipment)ObjectManager.getItem("equipment");
		ItemStack pieceStack = new ItemStack(itemEquipment);

		// Add Parts:
		itemEquipment.addEquipmentPart(pieceStack, slotBase.getStack(), 0);
		itemEquipment.addEquipmentPart(pieceStack, slotHead.getStack(), 1);
		if(slotTipA.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotTipA.getStack(), 2);
		}
		if(slotTipB.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotTipB.getStack(), 3);
		}
		if(slotTipC.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotTipC.getStack(), 4);
		}
		if(slotPommel.getHasStack()) {
			itemEquipment.addEquipmentPart(pieceStack, slotPommel.getStack(), 5);
		}

		// Put Piece Stack:
		slotPiece.putStack(pieceStack);
	}
}
