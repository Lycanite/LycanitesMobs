package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class DropRate {
	// ========== Item ==========
	public int itemID = 0;
	public int burningID = 0;
	public Map<Integer, Integer> effectsID = new HashMap<Integer, Integer>();

	public int itemMeta = 0;
	public int burningMeta = -1;
	public Map<Integer, Integer> effectsMeta = new HashMap<Integer, Integer>();
	
	public int minAmount = 1;
	public int maxAmount = 1;
	
	public float chance = 0;
	
    // ==================================================
   	//                     Constructor
   	// ==================================================
	public DropRate(int itemID, float chance) {
		this(itemID, 0, chance);
	}
	
	public DropRate(int itemID, int metadata, float chance) {
		this.itemID = itemID;
		this.itemMeta = metadata;
		this.minAmount = 1;
		this.maxAmount = 1;
		this.chance = chance;
	}

	
    // ==================================================
   	//                     Properties
   	// ==================================================
	public DropRate setItem(int id, int meta) {
		this.itemID = id;
		this.itemMeta = meta;
		return this;
	}

	public DropRate setBurningItem(int id, int meta) {
		this.burningID = id;
		this.burningMeta = meta;
		return this;
	}

	public DropRate setEffectItem(int effectID, int id, int meta) {
		effectsID.put(effectID, id);
		effectsMeta.put(effectID, meta);
		return this;
	}

	public DropRate setMinAmount(int amount) {
		this.minAmount = amount;
		return this;
	}

	public DropRate setMaxAmount(int amount) {
		this.maxAmount = amount;
		return this;
	}

	public DropRate setChance(float chance) {
		this.chance = chance;
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
		int dropID = this.itemID;
		int dropMeta = Math.max(this.itemMeta, 0);
		if(entity.isBurning()) {
			if(this.burningID > 0)
				dropID = this.burningID;
			if(this.burningMeta > -1)
				dropMeta = this.burningMeta;
		}
		for(Object potionEffect : entity.getActivePotionEffects()) {
			if(potionEffect instanceof PotionEffect) {
				int effectID = ((PotionEffect)potionEffect).getPotionID();
				if(effectsID.containsKey(effectID))
					dropID = effectsID.get(effectID);
				if(effectsMeta.containsKey(effectID))
					dropMeta = effectsMeta.get(effectID);
			}
		}
		if(dropID <= 0)
			return null;
		else
			return new ItemStack(dropID, quantity, dropMeta);
	}
}
