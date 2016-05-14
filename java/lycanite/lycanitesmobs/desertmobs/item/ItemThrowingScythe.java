package lycanite.lycanitesmobs.desertmobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemCharge;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemThrowingScythe extends ItemCharge {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemThrowingScythe() {
        super();
        this.group = DesertMobs.group;
        this.itemName = "throwingscythe";
        this.setup();
    }
    
    

    // ==================================================
    //                  Get Projectile
    // ==================================================
    @Override
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return new EntityThrowingScythe(world, entityPlayer);
    }
}
