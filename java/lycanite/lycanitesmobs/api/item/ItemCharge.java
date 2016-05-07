package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemCharge extends ItemBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public ItemCharge() {
        super();
    }


    // ==================================================
    //                    Item Use
    // ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer, EnumHand hand) {
        if(!entityPlayer.capabilities.isCreativeMode) {
            --itemStack.stackSize;
        }

        if(!world.isRemote) {
            EntityProjectileBase projectile = this.getProjectile(itemStack, world, entityPlayer);
            if(projectile == null)
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
            world.spawnEntityInWorld(projectile);
            world.playSound(entityPlayer, entityPlayer.getPosition(), projectile.getLaunchSound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
    }


    // ==================================================
    //                  Get Projectile
    // ==================================================
    public EntityProjectileBase getProjectile(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        return null;
    }
}
