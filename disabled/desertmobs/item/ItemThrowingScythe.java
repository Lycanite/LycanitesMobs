package lycanite.lycanitesmobs.desertmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemThrowingScythe extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemThrowingScythe() {
        super();
        this.group = DesertMobs.group;
        this.itemName = "throwingscythe";
        this.setup();
    }
    
    
	// ==================================================
	//                    Item Use
	// ==================================================
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if(!par3EntityPlayer.capabilities.isCreativeMode) {
            --par1ItemStack.stackSize;
        }
        
        if(!par2World.isRemote) {
        	EntityThrowable projectile = new EntityThrowingScythe(par2World, par3EntityPlayer);
            par2World.spawnEntityInWorld(projectile);
            par2World.playSoundAtEntity(par3EntityPlayer, ((EntityProjectileBase)projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }

        return par1ItemStack;
    }
}
