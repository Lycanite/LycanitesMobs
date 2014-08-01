package lycanite.lycanitesmobs.demonmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDoomfireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterDoomfire extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterDoomfire() {
        super();
    	this.group = DemonMobs.group;
    	this.itemName = "doomfirescepter";
        this.setup();
        this.textureName = "scepterdoomfire";
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 10;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	if(!world.isRemote) {
        	EntityDoomfireball projectile = new EntityDoomfireball(world, player);
        	world.spawnEntityInWorld(projectile);
            world.playSoundAtEntity(player, ((EntityProjectileBase)projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("DoomfireCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
