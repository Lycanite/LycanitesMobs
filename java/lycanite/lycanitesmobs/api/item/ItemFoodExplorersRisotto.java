package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
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
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, this.getEffectDuration(), 3));
        player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, this.getEffectDuration(), 1));
        player.addPotionEffect(new PotionEffect(Potion.jump.id, this.getEffectDuration(), 1));
    }
}
