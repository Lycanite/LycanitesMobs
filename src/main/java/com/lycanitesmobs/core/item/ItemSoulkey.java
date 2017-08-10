package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.AltarInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemSoulkey extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulkey() {
        super();
        this.itemName = "soulkey";
        this.setup();
    }
	
    
	// ==================================================
	//                      Update
	// ==================================================
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		super.onUpdate(itemStack, world, entity, par4, par5);
	}
    
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(!AltarInfo.checkAltarsEnabled() && !player.getEntityWorld().isRemote) {
            String message = I18n.translateToLocal("message.soulkey.disabled");
            player.sendMessage(new TextComponentString(message));
            return EnumActionResult.FAIL;
        }

        // Get Possible Altars:
        List<AltarInfo> possibleAltars = new ArrayList<AltarInfo>();
        if(AltarInfo.altars.isEmpty())
            LycanitesMobs.printWarning("", "No altars have been registered, Soulkeys will not work at all.");
        for(AltarInfo altarInfo : AltarInfo.altars.values()) {
            if(altarInfo.checkBlockEvent(player, world, pos) && altarInfo.quickCheck(player, world, pos)) {
                possibleAltars.add(altarInfo);
            }
        }
        if(possibleAltars.isEmpty()) {
            String message = I18n.translateToLocal("message.soulkey.none");
            player.sendMessage(new TextComponentString(message));
            return EnumActionResult.FAIL;
        }

        // Activate First Valid Altar:
        for(AltarInfo altarInfo : possibleAltars) {
            if(altarInfo.fullCheck(player, world, pos)) {

                // Valid Altar:
                if(!player.getEntityWorld().isRemote) {
                    if (!player.capabilities.isCreativeMode)
                        itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
                    if (itemStack.getCount() <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                }
                if(!altarInfo.activate(player, world, pos)) {
                    String message = I18n.translateToLocal("message.soulkey.badlocation");
                    player.sendMessage(new TextComponentString(message));
                    return EnumActionResult.FAIL;
                }
                String message = I18n.translateToLocal("message.soulkey.active");
                player.sendMessage(new TextComponentString(message));
                return EnumActionResult.SUCCESS;
            }
        }
        String message = I18n.translateToLocal("message.soulkey.invalid");
        player.sendMessage(new TextComponentString(message));

        return EnumActionResult.FAIL;
    }
}
