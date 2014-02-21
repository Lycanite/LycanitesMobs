package lycanite.lycanitesmobs.demonmobs.item;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHellfireball extends Item {
	public static String itemName = "HellfireCharge";
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemHellfireball(int itemID) {
        super(itemID - 256);
        setMaxStackSize(64);
        setCreativeTab(LycanitesMobs.creativeTab);
        setUnlocalizedName("HellfireCharge");
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	par3List.add("\u00a7a" + "Can be used to create");
    	par3List.add("\u00a7a" + "hellfires or fired from a");
    	par3List.add("\u00a7a" + "dispenser as a hellfire");
    	par3List.add("\u00a7a" + "blast!");
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
                int i1 = par3World.getBlockId(par4, par5, par6);

                if (i1 == 0) {
                    par3World.playSoundEffect((double)par4 + 0.5D, (double)par5 + 0.5D, (double)par6 + 0.5D, AssetManager.getSound("Hellfireball"), 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                    par3World.setBlock(par4, par5, par6, DemonMobs.config.blockIDs.get("Hellfire"));
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
    public Icon getIconFromDamage(int par1) {
        return AssetManager.getIcon(itemName);
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister iconRegister) {
        AssetManager.addIcon(itemName, DemonMobs.domain, "hellfireball", iconRegister);
    }
}
