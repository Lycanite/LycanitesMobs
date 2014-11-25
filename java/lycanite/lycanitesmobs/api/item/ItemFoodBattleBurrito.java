package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFoodBattleBurrito extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodBattleBurrito(String setItemName, GroupInfo group, String setTexturePath, int feed, float saturation) {
		super(setItemName, group, setTexturePath, feed, saturation, FOOD_CLASS.FEAST);
	}
	public ItemFoodBattleBurrito(String setItemName, GroupInfo group, int feed, float saturation) {
		super(setItemName, group, feed, saturation, FOOD_CLASS.FEAST);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    @Override
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player) {
        super.onFoodEaten(itemStack, world, player);
        player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, this.getEffectDuration(), 1));
        player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, this.getEffectDuration(), 3)); // Absorption
        player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, this.getEffectDuration(), 3)); // Health Boost
        player.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 3));
    }
}
