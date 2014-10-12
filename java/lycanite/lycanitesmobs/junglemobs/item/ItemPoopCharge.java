package lycanite.lycanitesmobs.junglemobs.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import lycanite.lycanitesmobs.junglemobs.entity.EntityPoop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPoopCharge extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemPoopCharge() {
        super();
        this.group = JungleMobs.group;
        this.itemName = "poopcharge";
        this.setup();
    }
    
    
	// ==================================================
	//                    Item Use
	// ==================================================
    // ========== Use ==========
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
    	if(ItemDye.applyBonemeal(itemStack, world, x, y, z, player)) {
            if(!world.isRemote) {
            	world.playAuxSFX(2005, x, y, z, 0);
            }
            return true;
        }
    	return super.onItemUse(itemStack, player, world, x, y, z, p_77648_7_, p_77648_8_, p_77648_9_, p_77648_10_);
    }
    
    // ========== Start ==========
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if(!par3EntityPlayer.capabilities.isCreativeMode) {
            --par1ItemStack.stackSize;
        }
        
        if(!par2World.isRemote) {
        	EntityThrowable projectile = new EntityPoop(par2World, par3EntityPlayer);
            par2World.spawnEntityInWorld(projectile);
            par2World.playSoundAtEntity(par3EntityPlayer, ((EntityProjectileBase)projectile).getLaunchSound(), 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }

        return par1ItemStack;
    }
}
