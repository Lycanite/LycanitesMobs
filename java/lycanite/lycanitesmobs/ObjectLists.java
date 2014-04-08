package lycanite.lycanitesmobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ObjectLists {
	
	// Item List Names:
	public static String[] itemListNames = new String[] {"RawMeat", "CookedMeat", "Vegetables", "RawFish", "CookedFish", "CactusFood", "Mushrooms", "Sweets"};
	
	// Maps:
	public static Map<String, List<ItemStack>> itemLists = new HashMap<String, List<ItemStack>>();
	public static Map<String, List<Class>> entityLists = new HashMap<String, List<Class>>();
	
	
    // ==================================================
    //                        Add
    // ==================================================
	public static void addItem(String list, Object object) {
		if(!(object instanceof Item || object instanceof Block || object instanceof ItemStack || object instanceof String))
			return;
		if(!itemLists.containsKey(list))
			itemLists.put(list, new ArrayList<ItemStack>());
		ItemStack itemStack = null;
		
		if(object instanceof Item)
			itemStack = new ItemStack((Item)object);
		else if(object instanceof Block)
			itemStack = new ItemStack((Block)object);
		else if(object instanceof ItemStack)
			itemStack = (ItemStack)object;
		else if(object instanceof String)
			if(ObjectManager.getItem((String)object) != null)
				itemStack = new ItemStack(ObjectManager.getItem((String)object));
			else if(ObjectManager.getBlock((String)object) != null)
				itemStack = new ItemStack(ObjectManager.getBlock((String)object));
		
		if(itemStack != null)
			itemLists.get(list).add(itemStack);
	}
	
	public static void addEntity(String list, Object object) {
		if(!(object instanceof Entity || object instanceof String))
			return;
		if(!entityLists.containsKey(list))
			entityLists.put(list, new ArrayList<Class>());
		Class entity = null;
		
		if(object instanceof Class)
			entity = (Class)object;
		else if(object instanceof String)
			entity = ObjectManager.getMob((String)object);
		
		if(entity != null)
			entityLists.get(list).add(entity);
	}
	

    // ==================================================
    //                        Get
    // ==================================================
	public static ItemStack[] getItems(String list) {
		if(!itemLists.containsKey(list))
			return (ItemStack[])itemLists.get(list).toArray();
		return new ItemStack[0];
	}

	public static Class[] getEntites(String list) {
		if(!entityLists.containsKey(list))
			return (Class[])entityLists.get(list).toArray();
		return new Class[0];
	}
	

    // ==================================================
    //                      Compare
    // ==================================================
	public static boolean inItemList(String list, ItemStack testStack) {
		if(!itemLists.containsKey(list))
			return false;
		for(ItemStack listStack : itemLists.get(list))
			if(testStack.itemID == listStack.itemID
			&& testStack.getItemDamage() == listStack.getItemDamage())
				return true;
		return false;
	}

	public static boolean inEntityList(String list, Class testClass) {
		if(!entityLists.containsKey(list))
			return false;
		for(Class listClass : entityLists.get(list))
			if(testClass == listClass)
				return true;
		return false;
	}
	
	
    // ==================================================
    //                   Create Lists
    // ==================================================
	public static void createLists() {
		// Raw Meat: (A bit cold...)
		ObjectLists.addItem("RawMeat", Item.beefRaw);
		ObjectLists.addItem("RawMeat", Item.porkRaw);
		ObjectLists.addItem("RawMeat", Item.chickenRaw);
		
		// Cooked Meat: (Meaty goodness for carnivorous pets!)
		ObjectLists.addItem("CookedMeat", Item.beefCooked);
		ObjectLists.addItem("CookedMeat", Item.porkCooked);
		ObjectLists.addItem("CookedMeat", Item.chickenCooked);
		
		// Prepared Vegetables: (For most vegetarian pets.)
		ObjectLists.addItem("Vegetables", Item.wheat);
		ObjectLists.addItem("Vegetables", Item.carrot);
		ObjectLists.addItem("Vegetables", Item.potato);
		
		// Fruit: (For exotic pets!)
		ObjectLists.addItem("Fruit", Item.appleRed);
		ObjectLists.addItem("Fruit", Item.melon);
		ObjectLists.addItem("Fruit", Block.pumpkin);
		ObjectLists.addItem("Fruit", Item.pumpkinPie);

		// Raw Fish: (Very smelly!)
		ObjectLists.addItem("RawFish", Item.fishRaw);

		// Cooked Fish: (For those fish fiends!)
		ObjectLists.addItem("CookedFish", Item.fishCooked);
		
		// Cactus Food: (Jousts love these!)
		ObjectLists.addItem("CactusFood", new ItemStack(Item.dyePowder, 1, 2)); // Cactus Green
		
		// Mushrooms: (Fungi treats!)
		ObjectLists.addItem("Mushrooms", Block.mushroomBrown);
		ObjectLists.addItem("Mushrooms", Block.mushroomRed);
		
		// Sweets: (Sweet sugary goodness!)
		ObjectLists.addItem("Sweets", Item.sugar);
		ObjectLists.addItem("Sweets", new ItemStack(Item.dyePowder, 1, 15)); // Cocoa Beans
		ObjectLists.addItem("Sweets", Item.cookie);
		ObjectLists.addItem("Sweets", Block.cake);
		ObjectLists.addItem("Sweets", Item.pumpkinPie);
		
		// Custom Entries:
		for(String itemListName : itemListNames) {
			addFromConfig(itemListName);
		}
	}
	
	// ========== Add From Config Value ==========
	public static void addFromConfig(String listName) {
		Map<String, String> itemListConfig = LycanitesMobs.config.itemLists;
		if(!itemListConfig.containsKey(listName))
			return;
		
		String customDropsString = itemListConfig.get(listName);
		if(customDropsString != null && customDropsString.length() > 0)
    		for(String customDropEntryString : customDropsString.split(",")) {
    			String[] customDropValues = customDropEntryString.split(":");
    			if(customDropValues.length >= 2) {
					int dropID = Integer.parseInt(customDropValues[0]);
					int dropMeta = Integer.parseInt(customDropValues[1]);
					ObjectLists.addItem(listName, new ItemStack(dropID, 1, dropMeta));
    			}
    		}
	}
}
