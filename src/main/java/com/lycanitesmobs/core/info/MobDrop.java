package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MobDrop {
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
	public static MobDrop createFromJSON(JsonObject json) {
		MobDrop mobDrop = null;
		int dropMeta = 0;
		if(json.has("item")) {
			if(json.has("metadata")) {
				dropMeta = json.get("metadata").getAsInt();
			}
			String dropName = json.get("item").getAsString();

			ItemStack itemStack = null;
			if (Item.getByNameOrId(dropName) != null) {
				itemStack = new ItemStack(Item.getByNameOrId(dropName), 1, dropMeta);
			}
			else if (Block.getBlockFromName(dropName) != null) {
				itemStack = new ItemStack(Block.getBlockFromName(dropName), 1, dropMeta);
			}

			if(itemStack == null) {
				return mobDrop;
			}
			mobDrop = new MobDrop(itemStack, 1);
			mobDrop.loadFromJSON(json);
		}

		return mobDrop;
	}

	
    // ==================================================
   	//                     Constructor
   	// ==================================================
	public MobDrop(ItemStack itemStack, float chance) {
		this.itemStack = itemStack;
		this.minAmount = 1;
		this.maxAmount = 1;
		this.chance = chance;
	}

	public MobDrop(NBTTagCompound nbtTagCompound) {
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
	public MobDrop setDrop(ItemStack item) {
		this.itemStack = item;
		return this;
	}

	public MobDrop setBurningDrop(ItemStack item) {
		this.burningItemStack = item;
		return this;
	}

	public MobDrop setEffectDrop(int effectID, ItemStack item) {
		effectsItem.put(effectID, item);
		return this;
	}

	public MobDrop setMinAmount(int amount) {
		this.minAmount = amount;
		return this;
	}

	public MobDrop setMaxAmount(int amount) {
		this.maxAmount = amount;
		return this;
	}

	public MobDrop setChance(float chance) {
		this.chance = chance;
		return this;
	}

    public MobDrop setSubspecies(int subspeciesID) {
        this.subspeciesID = subspeciesID;
        return this;
    }

	
    // ==================================================
   	//                       Drop
   	// ==================================================
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


	// ==================================================
	//                    Read From NBT
	// ==================================================
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		this.itemStack = new ItemStack(nbtTagCompound);

		this.minAmount = nbtTagCompound.getInteger("MinAmount");
		this.maxAmount = nbtTagCompound.getInteger("MaxAmount");
		this.chance = nbtTagCompound.getFloat("Chance");
	}


	// ==================================================
	//                    Write To NBT
	// ==================================================
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		NBTTagCompound itemNBT = new NBTTagCompound();
		this.itemStack.writeToNBT(itemNBT);
		nbtTagCompound.setTag("ItemStack", itemNBT);

		nbtTagCompound.setInteger("MinAmount", this.minAmount);
		nbtTagCompound.setInteger("MaxAmount", this.maxAmount);
		nbtTagCompound.setFloat("Chance", this.chance);

		return nbtTagCompound;
	}
}
