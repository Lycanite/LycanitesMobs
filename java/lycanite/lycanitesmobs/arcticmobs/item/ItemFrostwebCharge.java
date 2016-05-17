package lycanite.lycanitesmobs.arcticmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemCharge;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityFrostweb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFrostwebCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemFrostwebCharge() {
        super();
        this.group = ArcticMobs.group;
        this.itemName = "frostwebcharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityFrostweb(world, entityPlayer);
    }
}
