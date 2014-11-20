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
		super(setItemName, group, setTexturePath, feed, saturation);
	}
	public ItemFoodBattleBurrito(String setItemName, GroupInfo group, int feed, float saturation) {
		super(setItemName, group, feed, saturation);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player) {
        super.onFoodEaten(itemStack, world, player);
        player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 10 * 60 * 20, 3));
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 10 * 60 * 20, 3));
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 10 * 60 * 20, 1));
        player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 10 * 60 * 20, 3));
        player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, 10 * 60 * 20, 3));
        player.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 3));
    }
}
