package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemImmunizer extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemImmunizer() {
        super();
        this.group = LycanitesMobs.group;
        this.itemName = "immunizer";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
			if(!player.capabilities.isCreativeMode) {
				itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
			}

			if(!world.isRemote && ObjectManager.getPotionEffect("immunization") != null) {
				player.addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("immunization"), 10 * 20));
			}

			return new ActionResult(EnumActionResult.SUCCESS, itemStack);
		}
}
