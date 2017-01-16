package lycanite.lycanitesmobs.shadowmobs.item;

import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.item.ItemCharge;
import lycanite.lycanitesmobs.shadowmobs.ShadowMobs;
import lycanite.lycanitesmobs.shadowmobs.entity.EntitySpectralbolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSpectralboltCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSpectralboltCharge() {
        super();
        this.group = ShadowMobs.group;
        this.itemName = "spectralboltcharge";
        this.setup();
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntitySpectralbolt(world, entityPlayer);
    }
}
