package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ItemDrop {
	// ========== Item ==========
	public ItemStack itemStack = null;
	public ItemStack burningItemStack = null;
	public Map<Integer, ItemStack> effectsItem = new HashMap<>();
	
	public int minAmount = 1;
	public int maxAmount = 1;
	
	public float chance = 0;

    /** The ID of the subspecies that this drop is restricted to. An ID below 0 will have this drop ignore the subspecies. **/
    public int subspeciesID = -1;


	// ==================================================
	//                       JSON
	// ==================================================
	/** Creates a MobDrop from the provided JSON data. **/
	public static ItemDrop createFromJSON(JsonObject json) {
		ItemDrop itemDrop = null;
		int itemMetadata = 0;
		if(json.has("item")) {
			if(json.has("metadata")) {
				itemMetadata = json.get("metadata").getAsInt();
			}
			String itemId = json.get("item").getAsString();

			Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(itemId));
			if(item != null) {
				itemDrop = new ItemDrop(new ItemStack(item, 1, itemMetadata), 1);
				itemDrop.loadFromJSON(json);
			}
			else {
				LycanitesMobs.printWarning("", "[JSON] Unable to load item drop from the item id: " + itemId);
				return null;
			}
		}
		else {
			LycanitesMobs.printWarning("", "[JSON] Unable to load item drop from json as it has no name!");
		}

		return itemDrop;
	}


	// ==================================================
	//                      Config
	// ==================================================
	/** Creates a MobDrop from the provided Config String. **/
	public static ItemDrop createFromConfigString(String itemDropString) {
		if(itemDropString != null && itemDropString.length() > 0) {
			String[] customDropValues = itemDropString.split(",");
			String itemId = customDropValues[0];
			int itemMetadata = 0;
			if (customDropValues.length > 1) {
				itemMetadata = Integer.parseInt(customDropValues[1]);
			}
			int amountMin = 1;
			if (customDropValues.length > 2) {
				amountMin = Integer.parseInt(customDropValues[2]);
			}
			int amountMax = 1;
			if (customDropValues.length > 3) {
				amountMax = Integer.parseInt(customDropValues[3]);
			}
			float chance = 1;
			if (customDropValues.length > 4) {
				chance = Float.parseFloat(customDropValues[4]);
			}

			ItemDrop itemDrop = null;
			Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(itemId));
			if(item != null) {
				itemDrop = new ItemDrop(new ItemStack(item, amountMin, itemMetadata), chance);
				itemDrop.setMinAmount(amountMin);
				itemDrop.setMaxAmount(amountMax);
			}
			return itemDrop;
		}
		return null;
	}

	
    // ==================================================
   	//                     Constructor
   	// ==================================================
	public ItemDrop(ItemStack itemStack, float chance) {
		this.itemStack = itemStack;
		this.minAmount = 1;
		this.maxAmount = 1;
		this.chance = chance;
	}

	public ItemDrop(NBTTagCompound nbtTagCompound) {
		this.readFromNBT(nbtTagCompound);
	}

	public void loadFromJSON(JsonObject json) {
		if (json.has("minAmount"))
			this.minAmount = json.get("minAmount").getAsInt();
		if (json.has("maxAmount"))
			this.maxAmount = json.get("maxAmount").getAsInt();
		if (json.has("chance"))
			this.chance = json.get("chance").getAsFloat();
		if (json.has("subspecies"))
			this.subspeciesID = json.get("subspecies").getAsInt();

		if (json.has("burningItem")) {
			int dropMeta = 0;
			if(json.has("burningMetadata")) {
				dropMeta = json.get("burningMetadata").getAsInt();
			}
			String dropName = json.get("burningItem").getAsString();

			ItemStack itemStack = null;
			if (Item.getByNameOrId(dropName) != null) {
				itemStack = new ItemStack(Item.getByNameOrId(dropName), 1, dropMeta);
			}
			else if (Block.getBlockFromName(dropName) != null) {
				itemStack = new ItemStack(Block.getBlockFromName(dropName), 1, dropMeta);
			}

			this.burningItemStack = itemStack;
		}
	}


    // ==================================================
   	//                     Properties
   	// ==================================================
	public ItemDrop setDrop(ItemStack item) {
		this.itemStack = item;
		return this;
	}

	public ItemDrop setBurningDrop(ItemStack item) {
		this.burningItemStack = item;
		return this;
	}

	public ItemDrop setEffectDrop(int effectID, ItemStack item) {
		effectsItem.put(effectID, item);
		return this;
	}

	public ItemDrop setMinAmount(int amount) {
		this.minAmount = amount;
		return this;
	}

	public ItemDrop setMaxAmount(int amount) {
		this.maxAmount = amount;
		return this;
	}

	public ItemDrop setChance(float chance) {
		this.chance = chance;
		return this;
	}

    public ItemDrop setSubspecies(int subspeciesID) {
        this.subspeciesID = subspeciesID;
        return this;
    }


	/**
	 * Returns a quantity to drop.
	 * @param random The instance of random to use.
	 * @param bonus A bonus multiplier.
	 * @return The randomised amount to drop.
	 */
	public int getQuantity(Random random, int bonus) {
		// Will It Drop?
		float roll = random.nextFloat();
		roll = Math.max(roll, 0);
		if(roll > this.chance)
			return 0;
		
		// How Many?
		int min = this.minAmount;
		int max = this.maxAmount + bonus;
		if(max <= min)
			return min;
		roll = roll / this.chance;
		float dropRange = (max - min) * roll;
		int dropAmount = min + Math.round(dropRange);
		return dropAmount;
	}
	
	public ItemStack getItemStack(EntityLivingBase entity, int quantity) {
		ItemStack drop = this.itemStack;
		if(entity.isBurning()) {
			if(this.burningItemStack != null)
				drop = this.burningItemStack;
		}
		
		for(Object potionEffect : entity.getActivePotionEffects()) {
			if(potionEffect instanceof PotionEffect) {
				int effectID = Potion.getIdFromPotion(((PotionEffect) potionEffect).getPotion());
				if(effectsItem.containsKey(effectID))
					drop = effectsItem.get(effectID);
			}
		}
		
		if(drop != null)
			drop.setCount(quantity);
		return drop;
	}


	/**
	 * Reads this Item Drop from NBT.
	 * @param nbtTagCompound The NBT to load values from.
	 */
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		this.itemStack = new ItemStack(nbtTagCompound);

		this.minAmount = nbtTagCompound.getInteger("MinAmount");
		this.maxAmount = nbtTagCompound.getInteger("MaxAmount");
		this.chance = nbtTagCompound.getFloat("Chance");
	}


	/**
	 * Writes this Item Drop to NBT.
	 * @param nbtTagCompound The NBT to write to.
	 * @return The NBT written to.
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		NBTTagCompound itemNBT = new NBTTagCompound();
		this.itemStack.writeToNBT(itemNBT);
		nbtTagCompound.setTag("ItemStack", itemNBT);

		nbtTagCompound.setInteger("MinAmount", this.minAmount);
		nbtTagCompound.setInteger("MaxAmount", this.maxAmount);
		nbtTagCompound.setFloat("Chance", this.chance);

		return nbtTagCompound;
	}


	/**
	 * Returns this Item Drop as a string value for using in configs.
	 * @return The Item Drop config string.
	 */
	public String toConfigString() {
		return this.itemStack.getItem().getRegistryName().toString() + "," + this.itemStack.getMetadata() + "," + this.minAmount + "," + this.maxAmount + "," + this.chance;
	}
}
