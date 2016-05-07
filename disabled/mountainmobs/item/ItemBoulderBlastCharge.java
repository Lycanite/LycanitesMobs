package lycanite.lycanitesmobs.mountainmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityBoulderBlast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBoulderBlastCharge extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBoulderBlastCharge() {
        super();
        this.group = MountainMobs.group;
        this.itemName = "boulderblastcharge";
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
        	 EntityProjectileBase projectile = new EntityBoulderBlast(par2World, par3EntityPlayer);
             par2World.spawnEntityInWorld(projectile);
             par2World.playSoundAtEntity(par3EntityPlayer, projectile.getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         }

         return par1ItemStack;
     }
}
