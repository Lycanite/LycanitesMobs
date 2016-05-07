package lycanite.lycanitesmobs.freshwatermobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemCharge;
import lycanite.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import lycanite.lycanitesmobs.freshwatermobs.entity.EntityAquaPulse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAquaPulseCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemAquaPulseCharge() {
        super();
        this.group = FreshwaterMobs.group;
        this.itemName = "aquapulsecharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityAquaPulse(world, entityPlayer);
    }
}
