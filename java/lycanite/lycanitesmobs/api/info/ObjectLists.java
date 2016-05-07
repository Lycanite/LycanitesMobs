package lycanite.lycanitesmobs.api.info;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.item.ItemCustom;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectLists {
	
	// Item List Names:
	public static String[] itemListNames = new String[] {"RawMeat", "CookedMeat", "Vegetables", "RawFish", "CookedFish", "CactusFood", "Mushrooms", "Sweets", "Fuel"};
	
	// Maps:
	public static Map<String, List<ItemStack>> itemLists = new HashMap<String, List<ItemStack>>();
	public static Map<String, List<Class>> entityLists = new HashMap<String, List<Class>>();
	public static Map<String, List<Potion>> effectLists = new HashMap<String, List<Potion>>();
	
	
    // ==================================================
    //                        Add
    // ==================================================
	public static void addItem(String list, Object object) {
		if(object == null || !(object instanceof Item || object instanceof Block || object instanceof ItemStack || object instanceof String))
			return;
		list = list.toLowerCase();
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
		list = list.toLowerCase();
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
	
	public static void addEffect(String list, Potion potion) {
		if(potion == null)
			return;
		list = list.toLowerCase();
		if(!effectLists.containsKey(list))
			effectLists.put(list, new ArrayList<Potion>());
		effectLists.get(list).add(potion);
	}
	

    // ==================================================
    //                        Get
    // ==================================================
	public static ItemStack[] getItems(String list) {
		list = list.toLowerCase();
		if(!itemLists.containsKey(list))
			return new ItemStack[0];
		return itemLists.get(list).toArray(new ItemStack[itemLists.get(list).size()]);
	}

	public static Class[] getEntites(String list) {
		list = list.toLowerCase();
		if(!entityLists.containsKey(list))
			return new Class[0];
		return entityLists.get(list).toArray(new Class[entityLists.get(list).size()]);
	}

	public static Potion[] getEffects(String list) {
		list = list.toLowerCase();
		if(!effectLists.containsKey(list))
			return new Potion[0];
		return effectLists.get(list).toArray(new Potion[effectLists.get(list).size()]);
	}
	

    // ==================================================
    //                      Compare
    // ==================================================
	public static boolean inItemList(String list, ItemStack testStack) {
		list = list.toLowerCase();
        if(testStack == null || testStack.getItem() == null)
            return false;
		if(!itemLists.containsKey(list))
			return false;
		for(ItemStack listStack : itemLists.get(list))
			if(testStack.getItem() == listStack.getItem()
			&& testStack.getItemDamage() == listStack.getItemDamage())
				return true;
		return false;
	}

	public static boolean inEntityList(String list, Class testClass) {
		list = list.toLowerCase();
		if(!entityLists.containsKey(list))
			return false;
		return false;
	}

	public static boolean inEffectList(String list, Potion testPotion) {
		list = list.toLowerCase();
		if(!effectLists.containsKey(list))
			return false;
		return effectLists.get(list).contains(testPotion);
	}

    public static boolean isInOreDictionary(String oreEntry, Item item) {
        return isInOreDictionary(oreEntry, new ItemStack(item));
    }
    public static boolean isInOreDictionary(String oreEntry, Block block) {
        return isInOreDictionary(oreEntry, new ItemStack(block));
    }
    public static boolean isInOreDictionary(String oreEntry, ItemStack itemStack) {
        if(itemStack == null) return false;
        List<ItemStack> ores = OreDictionary.getOres(oreEntry);
        if(ores == null) return false;
        for(ItemStack ore : ores)
            if(ore.getItem() == itemStack.getItem())
                return true;
        return false;
    }


    // ==================================================
    //               Create Custom Items
    // ==================================================
    public static void createCustomItems() {
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "itemlists");
        config.setCategoryComment("Custom Objects", "here you can add your own custom items! These items wont do anything and will have no crafting recipes you can however have them drop from mobs by adding them to the custom mob drops. To add items just create a comma seperated list of names (spaces will be removed), you will need to use the item.youritem.name and item.youritem.description lang file entries in your resource pack to name your item and you will also need to save a texture for your item as: assets/lycanitesmobs/items/youritem.png");
        String customItems = config.getString("Custom Objects", "Custom Items", "");
        if("".equals(customItems))
            return;
        for(String itemEntry : customItems.replace(" ", "").split(",")) {
            ObjectManager.addItem(itemEntry, new ItemCustom(itemEntry, LycanitesMobs.group));
        }
    }
	
	
    // ==================================================
    //                   Create Lists
    // ==================================================
	public static void createLists() {
		// ========== Item Lists ==========
		// Raw Meat: (A bit cold...)
		ObjectLists.addItem("rawmeat", Items.beef);
		ObjectLists.addItem("rawmeat", Items.porkchop);
		ObjectLists.addItem("rawmeat", Items.chicken);
		
		// Cooked Meat: (Meaty goodness for carnivorous pets!)
		ObjectLists.addItem("cookedmeat", Items.cooked_beef);
		ObjectLists.addItem("cookedmeat", Items.cooked_porkchop);
		ObjectLists.addItem("cookedmeat", Items.cooked_chicken);
		
		// Prepared Vegetables: (For most vegetarian pets.)
		ObjectLists.addItem("vegetables", Items.wheat);
		ObjectLists.addItem("vegetables", Items.carrot);
		ObjectLists.addItem("vegetables", Items.potato);
		
		// Fruit: (For exotic pets!)
		ObjectLists.addItem("fruit", Items.apple);
		ObjectLists.addItem("fruit", Items.melon);
		ObjectLists.addItem("fruit", Blocks.pumpkin);
		ObjectLists.addItem("fruit", Items.pumpkin_pie);

		// Raw Fish: (Very smelly!)
		ObjectLists.addItem("rawfish", Items.fish);

		// Cooked Fish: (For those fish fiends!)
		ObjectLists.addItem("cookedfish", Items.cooked_fish);
		
		// Cactus Food: (Jousts love these!)
		ObjectLists.addItem("cactusfood", new ItemStack(Items.dye, 1, 2)); // Cactus Green
		
		// Mushrooms: (Fungi treats!)
		ObjectLists.addItem("mushrooms", Blocks.brown_mushroom);
		ObjectLists.addItem("mushrooms", Blocks.red_mushroom);
		ObjectLists.addItem("mushrooms", Blocks.brown_mushroom_block);
		ObjectLists.addItem("mushrooms", Blocks.red_mushroom_block);
		
		// Sweets: (Sweet sugary goodness!)
		ObjectLists.addItem("sweets", Items.sugar);
		ObjectLists.addItem("sweets", new ItemStack(Items.dye, 1, 15)); // Cocoa Beans
		ObjectLists.addItem("sweets", Items.cookie);
		ObjectLists.addItem("sweets", Blocks.cake);
		ObjectLists.addItem("sweets", Items.pumpkin_pie);
		
		// Fuel: (Fiery awesomeness!)
		ObjectLists.addItem("fuel", Items.coal);
		
		// Custom Entries:
		for(String itemListName : itemListNames) {
			addFromConfig(itemListName.toLowerCase());
		}
		
		// ========== Effects ==========
		// Buffs:
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("strength"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("haste"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("fire_resistance"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("instant_health"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("invisibility"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("jump_boost"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("speed"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("night_vision"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("regeneration"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("resistance"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("water_breathing"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("health_boost"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("absorption"));
		ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("saturation"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("glowing"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("levitation"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("luck"));
		
		// Debuffs:
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("blindness"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("nausea"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("mining_fatigue"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("instant_damage"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("hunger"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("slowness"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("poison"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("weakness"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("wither"));
        ObjectLists.addEffect("buffs", Potion.getPotionFromResourceLocation("unluck"));
	}
	
	// ========== Add From Config Value ==========
	public static void addFromConfig(String listName) {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "itemlists");
		config.setCategoryComment("item lists", "Here you can add items from vanilla Minecraft or other mods to various lists used by this mod. These are mostly food items that can be fed to farmable/tameable mobs. Format is: mod:itemname,metadata Multiple entries should be semicolon separated, be sure to use a colon and semicolon in the correct place.");
		String customDropsString = config.getString("Item Lists", listName).replace(" ", "");
		LycanitesMobs.printDebug("ItemSetup", "~O========== Custom " + listName + " ==========O~");
		if(customDropsString != null && customDropsString.length() > 0)
    		for(String customDropEntryString : customDropsString.replace(" ", "").split(";")) {
    			LycanitesMobs.printDebug("ItemSetup", "Adding: " + customDropEntryString);
    			String[] customDropValues = customDropEntryString.split(",");
				String dropName = customDropValues[0];
				int dropMeta = 0;
				if(customDropValues.length > 1)
					dropMeta = Integer.parseInt(customDropValues[1]);
				if(Item.itemRegistry.getObject(new ResourceLocation(dropName)) != null) {
					Item customItem = (Item)Item.itemRegistry.getObject(new ResourceLocation(dropName));
					ObjectLists.addItem(listName, new ItemStack(customItem, 1, dropMeta));
	    			LycanitesMobs.printDebug("ItemSetup", "As Item: " + customItem);
				}
				else if(Block.blockRegistry.getObject(new ResourceLocation(dropName)) != null) {
					Block customBlock = (Block)Block.blockRegistry.getObject(new ResourceLocation(dropName));
					ObjectLists.addItem(listName, new ItemStack(customBlock, 1, dropMeta));
	    			LycanitesMobs.printDebug("ItemSetup", "As Block: " + customBlock);
				}
    		}
	}
	
	
    // ==================================================
    //                   Check Tools
    // ==================================================
    // ========== Sword ==========
	public static boolean isSword(Item item) {
		if(item == null)
			return false;
		if(item instanceof ItemSword)
			return true;
		if(item instanceof ItemShears)
			return false;
		return item.getStrVsBlock(new ItemStack(item), Blocks.melon_block.getDefaultState()) > 1F;
	}

    // ========== Pickaxe ==========
	public static boolean isPickaxe(Item item) {
		if(item == null)
			return false;
        try {

            // Check Tinkers Tool:
            String[] toolNameParts = item.getUnlocalizedName().split("\\.");
            if(toolNameParts.length >= 3 && "InfiTool".equalsIgnoreCase(toolNameParts[1])) {
                String toolName = toolNameParts[2];
                if("Pickaxe".equalsIgnoreCase(toolName) || "Hammer".equalsIgnoreCase(toolName))
                    return true;
                return false;
            }

            // Vanilla Based Checks:
            if(item instanceof ItemPickaxe)
                return true;
            if(item.getHarvestLevel(new ItemStack(item), "pickaxe") != -1)
                return true;
            return item.getStrVsBlock(new ItemStack(item), Blocks.stone.getDefaultState()) > 1F;

        }
        catch(Exception e) {}
        return false;
	}

    // ========== Axe ==========
	public static boolean isAxe(Item item) {
        if(item == null)
            return false;
        try {

            // Check Tinkers Tool:
            String[] toolNameParts = item.getUnlocalizedName().split("\\.");
            for(String toolNamePart : toolNameParts)
            if(toolNameParts.length >= 3 && "InfiTool".equalsIgnoreCase(toolNameParts[1])) {
                String toolName = toolNameParts[2];
                if("Axe".equalsIgnoreCase(toolName) || "LumberAxe".equalsIgnoreCase(toolName) || "Mattock".equalsIgnoreCase(toolName) || "Battleaxe".equalsIgnoreCase(toolName))
                    return true;
                return false;
            }

            // Vanilla Based Checks:
            if(item instanceof ItemAxe)
                return true;
            if(item.getHarvestLevel(new ItemStack(item), "axe") != -1)
                return true;
            return item.getStrVsBlock(new ItemStack(item), Blocks.log.getDefaultState()) > 1F;

        }
        catch(Exception e) {}
        return false;
	}

    // ========== Shovel ==========
	public static boolean isShovel(Item item) {
		if(item == null)
            return false;
        try {

            // Check Tinkers Tool:
            String[] toolNameParts = item.getUnlocalizedName().split("\\.");
            if(toolNameParts.length >= 3 && "InfiTool".equalsIgnoreCase(toolNameParts[1])) {
                String toolName = toolNameParts[2];
                if("Shovel".equalsIgnoreCase(toolName) || "Excavator".equalsIgnoreCase(toolName) || "Mattock".equalsIgnoreCase(toolName))
                    return true;
                return false;
            }

            // Vanilla Based Checks:
            if(item instanceof ItemSpade)
                return true;
            if(item.getHarvestLevel(new ItemStack(item), "shovel") != -1)
                return true;
            return item.getStrVsBlock(new ItemStack(item), Blocks.dirt.getDefaultState()) > 1F;

        }
        catch(Exception e) {}
        return false;
	}

	
    // ==================================================
    //                   Check Names
    // ==================================================
	public static boolean isName(Item item, String name) {
		if(item == null)
			return false;
		String itemName = item.getUnlocalizedName().toLowerCase();
		if(itemName.contains(name))
			return true;
		return false;
	}

	public static boolean isName(Block block, String name) {
		if(block == null)
			return false;
		name = name.toLowerCase();
		String blockName = block.getUnlocalizedName().toLowerCase();
		if(blockName.contains(name)) {
			return true;
		}
		return false;
	}
}
