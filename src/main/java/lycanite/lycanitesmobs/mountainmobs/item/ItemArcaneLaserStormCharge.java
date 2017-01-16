package lycanite.lycanitesmobs.mountainmobs.item;

import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.item.ItemCharge;
import lycanite.lycanitesmobs.mountainmobs.MountainMobs;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserStorm;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemArcaneLaserStormCharge extends ItemCharge {

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
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityArcaneLaserStorm(world, entityPlayer);
    }
}
