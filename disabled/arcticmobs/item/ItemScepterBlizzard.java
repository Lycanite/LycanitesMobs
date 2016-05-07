package lycanite.lycanitesmobs.arcticmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemScepter;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityBlizzard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterBlizzard extends ItemScepter {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterBlizzard() {
        super();
    	this.group = ArcticMobs.group;
    	this.itemName = "blizzardscepter";
        this.setup();
        this.textureName = "scepterblizzard";
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
        return 3;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	if(!world.isRemote) {
        	EntityBlizzard projectile = new EntityBlizzard(world, player);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityBlizzard(world, player);
        	projectile.setPosition(projectile.posX + 1.0D, projectile.posY, projectile.posZ);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityBlizzard(world, player);
        	projectile.setPosition(projectile.posX - 1.0D, projectile.posY, projectile.posZ);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityBlizzard(world, player);
        	projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ + 1.0D);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityBlizzard(world, player);
        	projectile.setPosition(projectile.posX, projectile.posY, projectile.posZ - 1.0D);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityBlizzard(world, player);
        	projectile.setPosition(projectile.posX, projectile.posY + 1.0D, projectile.posZ);
        	world.spawnEntityInWorld(projectile);
        	
        	projectile = new EntityBlizzard(world, player);
        	projectile.setPosition(projectile.posX, projectile.posY - 1.0D, projectile.posZ);
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
        if(repairStack.getItem() == ObjectManager.getItem("BlizzardCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
