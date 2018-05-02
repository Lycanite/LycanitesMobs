package com.lycanitesmobs.core.inventory;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryCreature implements IInventory {

    // Basic Armor Values: (Copied values from EntityHorse)
    public static final Map<String, Integer> armorValues = new HashMap<>();
    static {
        armorValues.put("Leather", 3);
        armorValues.put("Iron", 5);
        armorValues.put("Gold", 7);
        armorValues.put("Chain", 9);
        armorValues.put("Diamond", 11);
    }

    // Equipment Types:
    public static final List<String> equipmentTypes = new ArrayList<>();
    static {
        equipmentTypes.add("head");
        equipmentTypes.add("chest");
        equipmentTypes.add("legs");
        equipmentTypes.add("feet");
        equipmentTypes.add("saddle");
        equipmentTypes.add("bag");
    }
	
	// Properties:
	public EntityCreatureBase creature;
	public String inventoryName = "Creature Inventory";
	protected NonNullList<ItemStack> inventoryContents;
    protected boolean basicArmor = true;
    protected int nextEquipmentSlot = 0;

    // Equipment Slots:
    public Map<String, Integer> equipmentTypeToSlot = new HashMap<>();
    public Map<Integer, String> equipmentSlotToType = new HashMap<>();

    // ==================================================
    //                    Data Parameters
    // ==================================================
    public static DataParameter<ItemStack> getEquipmentDataParameter(String type) {
        if(type.equals("head"))
            return EntityCreatureBase.EQUIPMENT_HEAD;
        if(type.equals("chest"))
            return EntityCreatureBase.EQUIPMENT_CHEST;
        if(type.equals("legs"))
            return EntityCreatureBase.EQUIPMENT_LEGS;
        if(type.equals("feet"))
            return EntityCreatureBase.EQUIPMENT_FEET;
        if(type.equals("saddle"))
            return EntityCreatureBase.EQUIPMENT_SADDLE;
        if(type.equals("bag"))
            return EntityCreatureBase.EQUIPMENT_BAG;
        return null;
    }

    /** Registers parameters to the provided datamanager. **/
    public static void registerDataParameters(EntityDataManager dataManager) {
        for(String equipmentType : equipmentTypes)
            dataManager.register(getEquipmentDataParameter(equipmentType), ItemStack.EMPTY);
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
		this.inventoryContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
	}

    protected void addEquipmentSlot(String type) {
        this.equipmentTypeToSlot.put(type, this.nextEquipmentSlot);
        this.equipmentSlotToType.put(this.nextEquipmentSlot, type);
        this.nextEquipmentSlot++;
    }
	
	
	// ==================================================
  	//                     Details
  	// ==================================================
    @Override
    public String getName() {
        return this.inventoryName;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    @Override
	public boolean hasCustomName() {
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
		return this.equipmentTypeToSlot.size();
	}
	
	/** Returns true if this mob has any items in it's bag slots. **/
	public boolean hasBagItems() {
		for(ItemStack itemStack : this.inventoryContents) {
			if(itemStack != null)
				return true;
		}
		return false;
	}

    /** Returns true if this inventory is empty. **/
    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventoryContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
	
	
	// ==================================================
  	//                      Actions
  	// ==================================================
	public void onInventoryChanged() {
		if(this.creature.getEntityWorld().isRemote)
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
        for(String type : this.equipmentSlotToType.values()) {
			ItemStack itemStack = this.getEquipmentStack(type);
            DataParameter<ItemStack> dataParameter = getEquipmentDataParameter(type);
            if(dataParameter == null)
                continue;
			if(itemStack == null)
                this.creature.getDataManager().set(dataParameter, ItemStack.EMPTY);
            else
			    this.creature.getDataManager().set(dataParameter, itemStack);
		}
		
		this.creature.scheduleGUIRefresh();
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return true;
	}
	
	@Override
	public void openInventory(EntityPlayer player) {}
	
	@Override
	public void closeInventory(EntityPlayer player) {}

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }
	
	
	// ==================================================
  	//                      Items
  	// ==================================================
	@Override
	public ItemStack getStackInSlot(int slotID) {
		if(slotID >= this.getSizeInventory() || slotID < 0)
			return null;
		else
			return this.inventoryContents.get(slotID);
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
	        if(itemStack.getCount() > this.getInventoryStackLimit())
	        	itemStack.setCount(this.getInventoryStackLimit());
	        if(itemStack.getCount() < 1)
	        	itemStack = null;
        }
        if(itemStack == null)
        	itemStack = ItemStack.EMPTY;
		this.inventoryContents.set(slotID, itemStack);
	}
	
	// ========== Decrease Stack Size ==========
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack[] splitStacks = this.decrStackSize(this.getStackInSlot(slot), amount);
		this.setInventorySlotContents(slot, splitStacks[0]);
        this.onInventoryChanged();
		return splitStacks[1];
	}

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    public ItemStack[] decrStackSize(ItemStack itemStack, int amount) {
		ItemStack[] splitStacks = {null, null};
		if(itemStack == null)
			return splitStacks;
		
        if(itemStack.getCount() <= amount) {
            splitStacks[0] = null;
            splitStacks[1] = itemStack;
        }
        else {
        	splitStacks[1] = itemStack.splitStack(amount);
            if(itemStack.getCount() == 0)
            	itemStack = null;
            splitStacks[0] = itemStack;
        }
        return splitStacks;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemStack) {
		String type = this.getTypeFromSlot(slotID);
		if(type != null) {
			if(!this.isEquipmentValidForSlot(type, itemStack))
				return false;
			ItemStack equipedStack = this.getStackInSlot(slotID);
			if(equipedStack != null && !equipedStack.isEmpty())
				return false;
				
		}
		return true;
	}

    // ========== Check Space ==========
	public int getSpaceForStack(ItemStack itemStack) {
		if(itemStack == null)
			return 0;
		if(itemStack.getItem() == null || itemStack.getCount() < 1)
			return 0;
		
		int space = 0;
		for(int slotID = 0; slotID < this.inventoryContents.size(); slotID++) {
			if(this.isItemValidForSlot(slotID, itemStack)) {
				ItemStack slotStack = this.getStackInSlot(slotID);
				if(slotStack != null && slotStack != ItemStack.EMPTY && !slotStack.isEmpty()) {
					if(slotStack.getCount() < slotStack.getMaxStackSize())
						if(slotStack.getItem() == itemStack.getItem() && slotStack.getItemDamage() == itemStack.getItemDamage())
							space += slotStack.getMaxStackSize() - slotStack.getCount();
				}
				else
					space += itemStack.getMaxStackSize();
			}
			if(space >= itemStack.getCount())
				break;
		}

		return Math.min(space, itemStack.getCount());
	}
	
	// ========== Auto Insert Item Stack ==========
	public ItemStack autoInsertStack(ItemStack itemStack) {
		if(itemStack == null)
			return itemStack;
		if(itemStack.getItem() == null || itemStack.getCount() < 1)
			return null;

		for(int slotID = 0; slotID < this.inventoryContents.size(); slotID++) {
			ItemStack slotStack = this.inventoryContents.get(slotID);
			
			// If there is a stack in the slot:
			if(slotStack != null && slotStack != ItemStack.EMPTY && !slotStack.isEmpty()) {
				if(slotStack.getCount() < slotStack.getMaxStackSize())
					if(slotStack.getItem() == itemStack.getItem() && slotStack.getItemDamage() == itemStack.getItemDamage()) {
						int space = Math.max(slotStack.getMaxStackSize() - slotStack.getCount(), 0);
						
						// If there is more than or just enough room:
						if(space >= itemStack.getCount()) {
							this.getStackInSlot(slotID).setCount(this.getStackInSlot(slotID).getCount() + itemStack.getCount());
									itemStack = null;
						}
						else {
                            this.getStackInSlot(slotID).setCount(this.getStackInSlot(slotID).getCount() + space);
                            itemStack.setCount(itemStack.getCount() - space);
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
		if(this.equipmentTypeToSlot.containsKey(type))
			return this.equipmentTypeToSlot.get(type) + this.getItemSlotsSize();
		else
			return -1;
	}

	public String getTypeFromSlot(int slotID) {
		if(this.equipmentSlotToType.containsKey(slotID - this.getItemSlotsSize()))
			return this.equipmentSlotToType.get(slotID - this.getItemSlotsSize());
		else
			return null;
	}
	
	// ========== Get Equipment ==========
	public ItemStack getEquipmentStack(String type) {
		if(getEquipmentDataParameter(type) == null)
			return ItemStack.EMPTY;
		if(this.creature.getEntityWorld().isRemote) {
			try {
				return this.creature.getDataManager().get(getEquipmentDataParameter(type));
			}
			catch(Exception e) {
				return ItemStack.EMPTY;
			}
		}
		else
			return this.getStackInSlot(this.getSlotFromType(type));
	}
	
	// ========== Set Equipment ==========
	public void setEquipmentStack(String type, ItemStack itemStack) {
		if(!this.creature.getEntityWorld().isRemote && this.equipmentTypeToSlot.containsKey(type) && this.isEquipmentValidForSlot(type, itemStack))
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
			if(itemStack.getItem() == Items.IRON_HORSE_ARMOR)
				return "chest";
	    	if(itemStack.getItem() == Items.GOLDEN_HORSE_ARMOR)
	    		return "chest";
	    	if(itemStack.getItem() == Items.DIAMOND_HORSE_ARMOR)
	    		return "chest";
		}
		
		// Advanced Armor:
		if(!this.basicArmor && itemStack.getItem() instanceof ItemArmor) {
			ItemArmor armorstack = (ItemArmor)(itemStack.getItem());
			if(armorstack.armorType == EntityEquipmentSlot.HEAD)
				return "head";
			if(armorstack.armorType == EntityEquipmentSlot.CHEST)
				return "chest";
			if(armorstack.armorType == EntityEquipmentSlot.LEGS)
				return "legs";
			if(armorstack.armorType == EntityEquipmentSlot.FEET)
				return "feet";
		}
		
		// Saddle:
		if(itemStack.getItem() instanceof ItemSaddle && this.creature instanceof EntityCreatureRideable)
			return "saddle";
		
		// Bag:
		if(itemStack.getItem() == Item.getItemFromBlock(Blocks.CHEST))
			return "bag";
		
		return null;
	}
	
	// ========== Equipment Valid for Slot ==========
	public boolean isEquipmentValidForSlot(String type, ItemStack itemStack) {
		if(itemStack == null)
			return true;
		return type.equals(getSlotForEquipment(itemStack));
	}
	
	// ========== Get Equipment Grade ==========
	public String getEquipmentGrade(String type) {
    	ItemStack equipmentStack = this.getEquipmentStack(type);
    	if(equipmentStack == null)
    		return null;
    	if(equipmentStack.getItem() instanceof ItemArmor) {
    		ItemArmor armor = (ItemArmor)equipmentStack.getItem();
    		if(armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER)
    			return "Leather";
    		else if(armor.getArmorMaterial() == ItemArmor.ArmorMaterial.IRON)
    			return "Iron";
    		else if(armor.getArmorMaterial() == ItemArmor.ArmorMaterial.CHAIN)
    			return "Chain";
    		else if(armor.getArmorMaterial() == ItemArmor.ArmorMaterial.GOLD)
    			return "Gold";
    		else if(armor.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND)
    			return "Diamond";
    	}
    	if(equipmentStack.getItem() == Items.IRON_HORSE_ARMOR)
    		return "Iron";
    	if(equipmentStack.getItem() == Items.GOLDEN_HORSE_ARMOR)
    		return "Gold";
    	if(equipmentStack.getItem() == Items.DIAMOND_HORSE_ARMOR)
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
		for(int slotID = 0; slotID < this.inventoryContents.size(); slotID++) {
			ItemStack itemStack = this.inventoryContents.get(slotID);
			if (itemStack != null) {
				this.creature.dropItem(itemStack);
				this.setInventorySlotContentsNoUpdate(slotID, ItemStack.EMPTY);
			}
		}
	}
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
    	// Read Items:
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbtTagCompound, itemStacks); // Reads ItemStack into a List from "Items" tag.

    	for(int i = 0; i < itemStacks.size(); ++i) {
    		if(i < this.getSizeInventory()) {
                ItemStack itemStack = itemStacks.get(i);
                if(itemStack.isEmpty())
                    this.setInventorySlotContentsNoUpdate(i, ItemStack.EMPTY);
                else {
					this.setInventorySlotContentsNoUpdate(i, itemStack);
				}
            }
    	}
    	this.onInventoryChanged();
    }
    
    // ========== Write ==========
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
    	// Write Items:
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        for(int i = 0; i < this.getSizeInventory(); i++) {
            if(this.getStackInSlot(i) != null) {
				itemStacks.set(i, this.getStackInSlot(i));
			}
        }
        ItemStackHelper.saveAllItems(nbtTagCompound, itemStacks); // Adds ItemStack NBT into the NBT Tag Compound.
    	
    }

	@Override
	public void markDirty() {}
}
