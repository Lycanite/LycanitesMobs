package com.lycanitesmobs.core.item;

import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFoodExplorersRisotto extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodExplorersRisotto(String setItemName, GroupInfo group, String setTexturePath, int feed, float saturation) {
		super(setItemName, group, setTexturePath, feed, saturation, FOOD_CLASS.FEAST);
	}
	public ItemFoodExplorersRisotto(String setItemName, GroupInfo group, int feed, float saturation) {
		super(setItemName, group, feed, saturation, FOOD_CLASS.FEAST);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    @Override
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player) {
        super.onFoodEaten(itemStack, world, player);
        player.addPotionEffect(new PotionEffect(MobEffects.SPEED, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(MobEffects.HASTE, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, this.getEffectDuration(), 1));
        player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, this.getEffectDuration(), 1));
    }
}
