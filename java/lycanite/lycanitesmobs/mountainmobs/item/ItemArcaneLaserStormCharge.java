package lycanite.lycanitesmobs.mountainmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserStorm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemArcaneLaserStormCharge extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemArcaneLaserStormCharge() {
        super();
        this.group = MountainMobs.group;
        this.itemName = "arcanelaserstormcharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
     public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
         if(!player.capabilities.isCreativeMode) {
             --itemStack.stackSize;
         }
         
         if(!world.isRemote) {
        	 EntityProjectileBase projectile = new EntityArcaneLaserStorm(world, player);
             world.spawnEntityInWorld(projectile);
             world.playSoundAtEntity(player, projectile.getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         }

         return itemStack;
     }
}
