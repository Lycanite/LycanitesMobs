package lycanite.lycanitesmobs.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBase extends Slot {
	
    // ==================================================
  	//                    Constructor
  	// ==================================================
	public SlotBase(IInventory inventory, int slotIndex, int x, int y) {
		super(inventory, slotIndex, x, y);
	}
	
	
    // ==================================================
  	//                    Validation
  	// ==================================================
	public boolean isItemValid(ItemStack itemStack) {
		if(this.inventory instanceof InventoryCreature)
			return ((InventoryCreature)this.inventory).isItemValidForSlot(this.getSlotIndex(), itemStack);
        return true;
    }
	
	public int getSlotStackLimit() {
		if(this.inventory instanceof InventoryCreature)
			if(((InventoryCreature)this.inventory).getTypeFromSlot(this.getSlotIndex()) != null)
				return 1;
        return this.inventory.getInventoryStackLimit();
    }
}
