package lycanite.lycanitesmobs.forestmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemCharge;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;
import lycanite.lycanitesmobs.forestmobs.entity.EntityLifeDrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLifeDrainCharge extends ItemCharge {
	
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
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityLifeDrain(world, entityPlayer, 20, 10);
    }
}
