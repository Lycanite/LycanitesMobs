package lycanite.lycanitesmobs.arcticmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemCharge;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityTundra;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTundraCharge extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemTundraCharge() {
        super();
        this.group = ArcticMobs.group;
        this.itemName = "tundracharge";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityTundra(world, entityPlayer);
    }
}
