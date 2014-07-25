package lycanite.lycanitesmobs.arcticmobs.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFrostyFur extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFrostyFur() {
        super();
        this.domain = ArcticMobs.domain;
        this.itemName = "frostyfur";
        this.setup();
    }
    
    
	// ==================================================
	//                    Item Use
	// ==================================================
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        if (par3World.isRemote) {
            return true;
        }
        else {
            if (par7 == 0) {
                --par5;
            }

            if (par7 == 1)  {
                ++par5;
            }

            if (par7 == 2) {
                --par6;
            }

            if (par7 == 3) {
                ++par6;
            }

            if (par7 == 4) {
                --par4;
            }

            if (par7 == 5) {
                ++par4;
            }

            if(!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)) {
                return false;
            }
            else {
                Block block = par3World.getBlock(par4, par5, par6);

                if (block == Blocks.air) {
                    par3World.playSoundEffect((double)par4 + 0.5D, (double)par5 + 0.5D, (double)par6 + 0.5D, AssetManager.getSound("FrostCloud"), 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                    par3World.setBlock(par4, par5, par6, ObjectManager.getBlock("FrostCloud"));
                }

                if (!par2EntityPlayer.capabilities.isCreativeMode) {
                    --par1ItemStack.stackSize;
                }

                return true;
            }
        }
    }
}
