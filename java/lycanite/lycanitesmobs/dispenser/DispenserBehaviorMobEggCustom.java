package lycanite.lycanitesmobs.dispenser;

import lycanite.lycanitesmobs.item.ItemCustomSpawnEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class DispenserBehaviorMobEggCustom extends BehaviorDefaultDispenseItem {
    public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack itemStack) {
        EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
        double d0 = par1IBlockSource.getX() + (double)enumfacing.getFrontOffsetX();
        double d1 = (double)((float)par1IBlockSource.getYInt() + 0.2F);
        double d2 = par1IBlockSource.getZ() + (double)enumfacing.getFrontOffsetZ();
        Entity entity = ((ItemCustomSpawnEgg)itemStack.getItem()).spawnCreature(par1IBlockSource.getWorld(), itemStack.getItemDamage(), d0, d1, d2);

        if(entity instanceof EntityLivingBase && itemStack.hasDisplayName())
            ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());
        
        itemStack.splitStack(1);
        return itemStack;
    }
}
