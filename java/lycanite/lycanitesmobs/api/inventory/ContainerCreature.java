package lycanite.lycanitesmobs.api.inventory;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerCreature extends ContainerBase {
	public EntityCreatureBase creature;
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public ContainerCreature(EntityCreatureBase creature, InventoryPlayer playerInventory) {
		super();
		this.drawPlayerSlots(playerInventory, 0, 0);
		this.creature = creature;
		
		// Creature Equipment:
		this.specialStart = this.inventorySlots.size();
		this.drawCreatureEquipment(creature, 8, 18);
		this.specialFinish = this.inventorySlots.size() - 1;
		
		// Creature Inventory
		this.inventoryStart = this.inventorySlots.size();
		if(creature.inventory.getItemSlotsSize() > 0)
			this.drawSlotsByColumn(creature.inventory, 8 + (18 * 4), 18, 5, 0, creature.inventory.getActiveItemSlotsSize() - 1);
		this.inventoryFinish = this.inventorySlots.size() - 1;
	}
	
	
	// ==================================================
  	//                    Draw Slots
  	// ==================================================
	public void drawCreatureEquipment(EntityCreatureBase creature, int equipX, int equipY) {
		// Creature Accessories:
		if(creature instanceof EntityCreatureRideable) {
			this.drawSlot(creature.inventory, creature.inventory.getSlotFromType("saddle"), equipX, equipY);
			equipY += 18;
		}
		if(creature.getBagSize() > 0) {
			this.drawSlot(creature.inventory, creature.inventory.getSlotFromType("bag"), equipX, equipY);
			equipY += 18;
		}
		// Weapon and Dye slots will go here.
		
		// Creature Armor:
		equipX += 18;
		equipY = 18;
		if(creature.inventory.useAdvancedArmor()) {
			this.drawSlot(creature.inventory, creature.inventory.getSlotFromType("head"), equipX, equipY);
			equipY += 18;
		}
		this.drawSlot(creature.inventory, creature.inventory.getSlotFromType("chest"), equipX, equipY);
		equipY += 18;
		if(creature.inventory.useAdvancedArmor()) {
			this.drawSlot(creature.inventory, creature.inventory.getSlotFromType("legs"), equipX, equipY);
			equipY += 18;
			this.drawSlot(creature.inventory, creature.inventory.getSlotFromType("feet"), equipX, equipY);
			equipY += 18;
		}
	}
	
	
	// ==================================================
  	//                  Container Closed
  	// ==================================================
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
	}
}
