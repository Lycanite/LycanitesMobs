package lycanite.lycanitesmobs.core.info;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DropRate {
	// ========== Item ==========
	public ItemStack item = null;
	public ItemStack burningItem = null;
	public Map<Integer, ItemStack> effectsItem = new HashMap<Integer, ItemStack>();
	
	public int minAmount = 1;
	public int maxAmount = 1;
	
	public float chance = 0;

    /** The ID of the subspecies that this drop is restricted to. An ID below 0 will have this drop ignore the subspecies. **/
    public int subspeciesID = -1;
	
    // ==================================================
   	//                     Constructor
   	// ==================================================
	public DropRate(ItemStack item, float chance) {
		this.item = item;
		this.minAmount = 1;
		this.maxAmount = 1;
		this.chance = chance;
	}

	
    // ==================================================
   	//                     Properties
   	// ==================================================
	public DropRate setDrop(ItemStack item) {
		this.item = item;
		return this;
	}

	public DropRate setBurningDrop(ItemStack item) {
		this.burningItem = item;
		return this;
	}

	public DropRate setEffectDrop(int effectID, ItemStack item) {
		effectsItem.put(effectID, item);
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

    public DropRate setSubspecies(int subspeciesID) {
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
		ItemStack drop = this.item;
		if(entity.isBurning()) {
			if(this.burningItem != null)
				drop = this.burningItem;
		}
		
		for(Object potionEffect : entity.getActivePotionEffects()) {
			if(potionEffect instanceof PotionEffect) {
				int effectID = Potion.getIdFromPotion(((PotionEffect) potionEffect).getPotion());
				if(effectsItem.containsKey(effectID))
					drop = effectsItem.get(effectID);
			}
		}
		
		if(drop != null)
			drop.stackSize = quantity;
		return drop;
	}
}
