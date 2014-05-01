package lycanite.lycanitesmobs.swampmobs.item;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPoisonGland extends Item {
	public static String itemName = "PoisonGland";
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemPoisonGland() {
        super();
        setMaxStackSize(64);
        setCreativeTab(LycanitesMobs.creativeTab);
        setUnlocalizedName("PoisonGland");
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	par3List.add("\u00a7a" + "Emits a poisonous");
    	par3List.add("\u00a7a" + "gas cloud when squeezed!");
    	par3List.add("\u00a7a" + "Will fire a Venom Shot");
    	par3List.add("\u00a7a" + "when used in a dispenser.");
    	super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
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
                    par3World.playSoundEffect((double)par4 + 0.5D, (double)par5 + 0.5D, (double)par6 + 0.5D, AssetManager.getSound("PoisonCloud"), 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                    par3World.setBlock(par4, par5, par6, ObjectManager.getBlock("PoisonCloud"));
                }

                if (!par2EntityPlayer.capabilities.isCreativeMode) {
                    --par1ItemStack.stackSize;
                }

                return true;
            }
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int par1) {
        return AssetManager.getIcon(itemName);
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        AssetManager.addIcon(itemName, SwampMobs.domain, "poisongland", iconRegister);
    }
}
