package lycanite.lycanitesmobs.shadowmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemCharge;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import lycanite.lycanitesmobs.shadowmobs.entity.EntityBloodleech;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBloodleechCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBloodleechCharge() {
        super();
        this.group = ShadowMobs.group;
        this.itemName = "bloodleechcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityBloodleech(world, entityPlayer);
    }
}
