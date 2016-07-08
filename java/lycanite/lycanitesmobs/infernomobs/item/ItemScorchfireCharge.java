package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.item.ItemCharge;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import lycanite.lycanitesmobs.infernomobs.entity.EntityScorchfireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScorchfireCharge extends ItemCharge {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScorchfireCharge() {
        super();
        this.group = InfernoMobs.group;
        this.itemName = "scorchfirecharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityScorchfireball(world, entityPlayer);
    }
}
