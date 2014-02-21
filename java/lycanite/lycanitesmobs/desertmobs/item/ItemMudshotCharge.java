package lycanite.lycanitesmobs.desertmobs.item;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.ICustomProjectile;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.desertmobs.entity.EntityMudshot;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMudshotCharge extends Item {
	public static String itemName = "MudshotCharge";
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMudshotCharge(int itemID) {
        super(itemID - 256);
        setMaxStackSize(64);
        setCreativeTab(LycanitesMobs.creativeTab);
        setUnlocalizedName(itemName);
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	par3List.add("\u00a7a" + "Throw at your enemies!");
    	par3List.add("\u00a7a" + "Can also be fired from");
    	par3List.add("\u00a7a" + "dispensers.");
    	super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
    }
    
    
	// ==================================================
	//                    Item Use
	// ==================================================
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if(!par3EntityPlayer.capabilities.isCreativeMode) {
            --par1ItemStack.stackSize;
        }
        
        if(!par2World.isRemote) {
        	EntityThrowable projectile = new EntityMudshot(par2World, par3EntityPlayer);
            par2World.spawnEntityInWorld(projectile);
            par2World.playSoundAtEntity(par3EntityPlayer, ((ICustomProjectile) projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }

        return par1ItemStack;
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
        AssetManager.addIcon(itemName, DesertMobs.domain, "mudshot", iconRegister);
    }
}
