package lycanite.lycanitesmobs.forestmobs.item;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFoodPaleoSalad extends ItemCustomFood {
	
    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemFoodPaleoSalad(String setItemName, GroupInfo group, String setTexturePath, int feed, float saturation) {
		super(setItemName, group, setTexturePath, feed, saturation);
	}
	public ItemFoodPaleoSalad(String setItemName, GroupInfo group, int feed, float saturation) {
		super(setItemName, group, feed, saturation);
	}
	
	
    // ==================================================
  	//                     Effects
  	// ==================================================
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, this.effectDuration, 2));
        player.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 3));
    }
}
