package lycanite.lycanitesmobs.api.inventory;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryCreature implements IInventory {
	
	// Properties:
	public EntityCreatureBase creature;
	public String inventoryName = "Creature Inventory";
    protected ItemStack[] items;
    //protected Map<String, ItemStack> equipment = new HashMap<String, ItemStack>();
    protected boolean basicArmor = true;
    
    // Data Watching:
    public Map<String, Integer> equipmentIDs = new HashMap<String, Integer>();
    public Map<Integer, String> equipmentTypes = new HashMap<Integer, String>();
    private int nextID = 0;
    
    // Basic Armor Values: (Copied values from EntityHorse)
    private static final Map<String, Integer> armorValues = new HashMap<String, Integer>();
    static {
    	armorValues.put("Leather", 3);
    	armorValues.put("Iron", 5);
    	armorValues.put("Gold", 7);
    	armorValues.put("Chain", 9);
    	armorValues.put("Diamond", 11);
    }
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public InventoryCreature(String inventoryName, EntityCreatureBase creature) {
		this.inventoryName = inventoryName;
		this.creature = creature;
		this.addEquipmentSlot("chest");
		this.addEquipmentSlot("saddle");
		this.addEquipmentSlot("bag");
		items = new ItemStack[this.getSizeInventory()];
	}
	
	protected void addEquipmentSlot(String type) {
		int id = this.nextID;
		this.equipmentIDs.put(type, id);
		this.equipmentTypes.put(id, type);
		this.creature.getDataWatcher().addObjectByDataType(EntityCreatureBase.WATCHER_ID.EQUIPMENT.id + id, 5);
		this.nextID++;
	}
	
	
	// ==================================================
  	//                     Details
  	// ==================================================
	@Override
	public String getInvName() {
		return this.inventoryName;
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public int getSizeInventory() {
		return this.getItemSlotsSize() + this.getSpecialSlotsSize();
	}

	public int getActiveItemSlotsSize() {
		if(this.getEquipmentStack("bag") != null)
			return this.creature.getBagSize();
		else
			return this.creature.getNoBagSize();
	}

	public int getItemSlotsSize() {
		return this.creature.getInventorySizeMax();
	}
	
	public int getSpecialSlotsSize() {
		return this.equipmentIDs.size();
	}
	
	
	// ==================================================
  	//                      Actions
  	// ==================================================
	@Override
	public void onInventoryChanged() {
		if(this.creature.worldObj.isRemote)
			return;
		
		// Empty Bag if Removed:
		if(this.getEquipmentStack("bag") == null) {
			int bagSizeDiff = this.creature.getBagSize() - this.creature.getNoBagSize();
			if(bagSizeDiff > 0)
				for(int i = this.creature.getNoBagSize(); i <= this.getItemSlotsSize(); i++)
					if(this.getStackInSlot(i - 1) != null) {
						this.creature.dropItem(this.getStackInSlot(i - 1));
						this.setInventorySlotContentsNoUpdate(i - 1, null);
					}
		}
		
		// Update Datawatcher:
		for(String type : this.equipmentIDs.keySet()) {
			ItemStack itemStack = this.getEquipmentStack(type);
			if(itemStack == null)
				itemStack = new ItemStack(1, 1, 0);
			this.creature.getDataWatcher().updateObject(EntityCreatureBase.WATCHER_ID.EQUIPMENT.id + this.equipmentIDs.get(type), itemStack);
		}
		
		this.creature.scheduleGUIRefresh();
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}
	
	@Override
	public void openChest() {}
	
	@Override
	public void closeChest() {}
	
	
	// ==================================================
  	//                      Items
  	// ==================================================
	@Override
	public ItemStack getStackInSlot(int slotID) {
		if(slotID >= this.getSizeInventory() || slotID < 0)
			return null;
		else
			return items[slotID];
	}

	@Override
	public void setInventorySlotContents(int slotID, ItemStack itemStack) {
		this.setInventorySlotContentsNoUpdate(slotID, itemStack);
        this.onInventoryChanged();
	}
	
	public void setInventorySlotContentsNoUpdate(int slotID, ItemStack itemStack) {
		if(slotID >= this.getSizeInventory() || slotID < 0)
			return;
        if(itemStack != null) {
	        if(itemStack.stackSize > this.getInventoryStackLimit())
	        	itemStack.stackSize = this.getInventoryStackLimit();
	        if(itemStack.stackSize < 1)
	        	itemStack = null;
        }
		items[slotID] = itemStack;
	}
	
	// ========== Decrease Stack Size ==========
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack[] splitStacks = this.decrStackSize(this.getStackInSlot(slot), amount);
		this.setInventorySlotContents(slot, splitStacks[0]);
        this.onInventoryChanged();
		return splitStacks[1];
	}
	
	public ItemStack[] decrStackSize(ItemStack itemStack, int amount) {
		ItemStack[] splitStacks = {null, null};
		if(itemStack == null)
			return splitStacks;
		
        if(itemStack.stackSize <= amount) {
            splitStacks[0] = null;
            splitStacks[1] = itemStack;
        }
        else {
        	splitStacks[1] = itemStack.splitStack(amount);
            if(itemStack.stackSize == 0)
            	itemStack = null;
            splitStacks[0] = itemStack;
        }
        return splitStacks;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		String type = this.getTypeFromSlot(slotID);
		if(type != null) {
			if(!this.isEquipmentValidForSlot(type, itemStack))
				return false;
			if(this.getStackInSlot(slotID) != null)
				return false;
				
		}
		return true;
	}
	
	// ========== Check Space ==========
	public int getSpaceForStack(ItemStack itemStack) {
		if(itemStack == null)
			return 0;
		if(itemStack.itemID == 0 || itemStack.stackSize < 1)
			return 0;
		
		int space = 0;
		for(int slotID = 0; slotID < this.items.length; slotID++) {
			if(this.isItemValidForSlot(slotID, itemStack)) {
				ItemStack slotStack = items[slotID];
				if(slotStack != null) {
					if(slotStack.stackSize < slotStack.getMaxStackSize())
						if(slotStack.itemID == itemStack.itemID && slotStack.getItemDamage() == itemStack.getItemDamage())
							space += slotStack.getMaxStackSize() - slotStack.stackSize;
				}
				else
					space += itemStack.getMaxStackSize();
			}
			if(space >= itemStack.stackSize)
				break;
		}
		
		return Math.min(space, itemStack.stackSize);
	}
	
	// ========== Auto Insert Item Stack ==========
	public ItemStack autoInsertStack(ItemStack itemStack) {
		if(itemStack == null)
			return itemStack;
		if(itemStack.itemID == 0 || itemStack.stackSize < 1)
			return null;
		
		for(int slotID = 0; slotID < this.items.length; slotID++) {
			ItemStack slotStack = items[slotID];
			
			// If there is a stack in the slot:
			if(slotStack != null) {
				if(slotStack.stackSize < slotStack.getMaxStackSize())
					if(slotStack.itemID == itemStack.itemID && slotStack.getItemDamage() == itemStack.getItemDamage()) {
						int space = Math.max(slotStack.getMaxStackSize() - slotStack.stackSize, 0);
						
						// If there is more than or just enough room:
						if(space >= itemStack.stackSize) {
							this.getStackInSlot(slotID).stackSize += itemStack.stackSize;
									itemStack = null;
						}
						else {
							this.getStackInSlot(slotID).stackSize += space;
							itemStack.stackSize -= space;
						}
					}
			}
			
			// If the slot is empty:
			else {
				this.setInventorySlotContents(slotID, itemStack);
				itemStack = null;
			}
			
			if(itemStack == null)
				break;
		}
		return itemStack;
	}
	
	
	// ==================================================
  	//                      Equipment
  	// ==================================================
	public void setAdvancedArmor(boolean advanced) {
		this.basicArmor = !advanced;
		if(advanced) {
			this.addEquipmentSlot("head");
			this.addEquipmentSlot("legs");
			this.addEquipmentSlot("feet");
		}
	}
	
	public boolean useAdvancedArmor() {
		return !this.basicArmor;
	}
	
	// ========== Type to Slot Mapping ==========
	public int getSlotFromType(String type) {
		if(this.equipmentIDs.containsKey(type))
			return this.equipmentIDs.get(type) + this.getItemSlotsSize();
		else
			return -1;
	}
	
	// ========== Type to Slot Mapping ==========
	public String getTypeFromSlot(int slotID) {
		if(this.equipmentTypes.containsKey(slotID - this.getItemSlotsSize()))
			return this.equipmentTypes.get(slotID - this.getItemSlotsSize());
		else
			return null;
	}
	
	// ========== Get Equipment ==========
	public ItemStack getEquipmentStack(String type) {
		if(!this.equipmentIDs.containsKey(type))
			return null;
		if(this.creature.worldObj.isRemote) {
			ItemStack itemStack = this.creature.getDataWatcher().getWatchableObjectItemStack(EntityCreatureBase.WATCHER_ID.EQUIPMENT.id + this.equipmentIDs.get(type));
			if(itemStack != null)
				if(itemStack.itemID == 1)
					itemStack = null;
			return itemStack;
		}
		else
			return this.getStackInSlot(this.getSlotFromType(type));
	}
	
	// ========== Set Equipment ==========
	public void setEquipmentStack(String type, ItemStack itemStack) {
		if(!this.creature.worldObj.isRemote && this.equipmentIDs.containsKey(type) && this.isEquipmentValidForSlot(type, itemStack))
			this.setInventorySlotContents(this.getSlotFromType(type), itemStack);
	}
	public void setEquipmentStack(ItemStack itemStack) {
		String type = this.getSlotForEquipment(itemStack);
		if(type != null)
			this.setEquipmentStack(type, itemStack);
	}
	
	// ========== Get Slot for Equipment ==========
	public String getSlotForEquipment(ItemStack itemStack) {
		if(itemStack == null)
			return null;
		
		// Basic Armor:
		if(this.basicArmor) {
			if(itemStack.getItem() == Item.horseArmorIron)
				return "chest";
	    	if(itemStack.getItem() == Item.horseArmorGold)
	    		return "chest";
	    	if(itemStack.getItem() == Item.horseArmorDiamond)
	    		return "chest";
		}
		
		// Advanced Armor:
		if(!this.basicArmor && itemStack.getItem() instanceof ItemArmor) {
			ItemArmor armorstack = (ItemArmor)(itemStack.getItem());
			if(armorstack.armorType == 0)
				return "head";
			if(armorstack.armorType == 1)
				return "chest";
			if(armorstack.armorType == 2)
				return "legs";
			if(armorstack.armorType == 3)
				return "feet";
		}
		
		// Saddle:
		if(itemStack.getItem() instanceof ItemSaddle && this.creature instanceof EntityCreatureRideable)
			return "saddle";
		
		// Bag:
		if(itemStack.itemID == Block.chest.blockID)
			return "bag";
		
		return null;
	}
	
	// ========== Equipment Valid for Slot ==========
	public boolean isEquipmentValidForSlot(String type, ItemStack itemStack) {
		if(itemStack == null)
			return true;
		return type == getSlotForEquipment(itemStack);
	}
	
	// ========== Get Equipment Grade ==========
	public String getEquipmentGrade(String type) {
    	ItemStack equipmentStack = this.getEquipmentStack(type);
    	if(equipmentStack == null)
    		return null;
    	if(equipmentStack.getItem() instanceof ItemArmor) {
    		ItemArmor armor = (ItemArmor)equipmentStack.getItem();
    		if(armor.getArmorMaterial() == EnumArmorMaterial.CLOTH)
    			return "Leather";
    		else if(armor.getArmorMaterial() == EnumArmorMaterial.IRON)
    			return "Iron";
    		else if(armor.getArmorMaterial() == EnumArmorMaterial.CHAIN)
    			return "Chain";
    		else if(armor.getArmorMaterial() == EnumArmorMaterial.GOLD)
    			return "Gold";
    		else if(armor.getArmorMaterial() == EnumArmorMaterial.DIAMOND)
    			return "Diamond";
    	}
    	if(equipmentStack.getItem() == Item.horseArmorIron)
    		return "Iron";
    	if(equipmentStack.getItem() == Item.horseArmorGold)
    		return "Gold";
    	if(equipmentStack.getItem() == Item.horseArmorDiamond)
    		return "Diamond";
    	return null;
    }
	
	// ========== Get Armor Value ==========
	public int getArmorValue() {
		String[] armorSlots = {"head", "chest", "legs", "feet"};
		int totalArmor = 0;
        for(String armorSlot : armorSlots) {
        	ItemStack armorStack = this.getEquipmentStack(armorSlot);
        	if(armorStack != null) {
            	if(armorStack.getItem() instanceof ItemArmor)
	                totalArmor += ((ItemArmor)armorStack.getItem()).damageReduceAmount;
            	else if(this.getEquipmentGrade(armorSlot) != null && this.armorValues.containsKey(this.getEquipmentGrade(armorSlot)))
            		totalArmor += this.armorValues.get(this.getEquipmentGrade(armorSlot));
        	}
        }
        return totalArmor;
	}
	
	
	// ==================================================
  	//                   Drop Inventory
  	// ==================================================
	public void dropInventory() {
		for(ItemStack item : this.items)
			if(item != null)
				this.creature.dropItem(item);
	}
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
    	// Read Items:
    	NBTTagList itemList = nbtTagCompound.getTagList("Items");
    	for(int i = 0; i < itemList.tagCount(); ++i) {
    		NBTTagCompound itemCompound = (NBTTagCompound)itemList.tagAt(i);
    		int slot = itemCompound.getByte("Slot") & 255;
    		if(slot < this.getSizeInventory())
    			this.setInventorySlotContentsNoUpdate(slot, ItemStack.loadItemStackFromNBT(itemCompound));
    	}
    	this.onInventoryChanged();
    }
    
    // ========== Write ==========
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
    	// Write Items:
		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < this.getSizeInventory(); i++) {
			ItemStack entry = this.getStackInSlot(i);
			if(entry != null) {
				NBTTagCompound itemCompound = new NBTTagCompound();
				itemCompound.setByte("Slot", (byte)i);
	    		entry.writeToNBT(itemCompound);
	    		itemList.appendTag(itemCompound);
			}
		}
		nbtTagCompound.setTag("Items", itemList);
    	
    }
}
