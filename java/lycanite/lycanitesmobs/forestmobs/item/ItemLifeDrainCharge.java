package lycanite.lycanitesmobs.forestmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;
import lycanite.lycanitesmobs.forestmobs.entity.EntityLifeDrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLifeDrainCharge extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemLifeDrainCharge() {
        super();
        this.group = ForestMobs.group;
        this.itemName = "lifedraincharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
     public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
         if(!entityPlayer.capabilities.isCreativeMode) {
             --itemStack.stackSize;
         }
         
         if(!world.isRemote) {
        	 EntityProjectileBase projectile = new EntityLifeDrain(world, entityPlayer, 20, 10);
        	 world.spawnEntityInWorld(projectile);
        	 world.playSoundAtEntity(entityPlayer, projectile.getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         }

         return itemStack;
     }
}
