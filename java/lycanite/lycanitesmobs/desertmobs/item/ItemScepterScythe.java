package lycanite.lycanitesmobs.desertmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityRapidFire;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import lycanite.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterScythe extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterScythe(int itemID) {
        super(itemID);
    	this.domain = DesertMobs.domain;
    	this.itemName = "ScytheScepter";
    	this.textureName = "scepterscythe";
        this.setUnlocalizedName(this.itemName);
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack par1ItemStack) {
        return 20;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	if(!world.isRemote) {
        	EntityRapidFire projectile = new EntityRapidFire(EntityThrowingScythe.class, world, player, 15, 5);
        	world.spawnEntityInWorld(projectile);
        }
    	return true;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.itemID == ObjectManager.getItem("ThrowingScythe").itemID) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
