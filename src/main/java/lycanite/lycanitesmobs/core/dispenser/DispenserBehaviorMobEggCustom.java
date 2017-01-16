package lycanite.lycanitesmobs.core.dispenser;

import lycanite.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class DispenserBehaviorMobEggCustom extends BehaviorDefaultDispenseItem {
    @Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof ItemCustomSpawnEgg))
            return itemStack;

        EnumFacing facing = blockSource.getBlockState().getValue(BlockDispenser.FACING);
        double x = blockSource.getX() + (double)facing.getFrontOffsetX();
        double y = (double)((float)blockSource.getY() + 0.2F);
        double z = blockSource.getZ() + (double)facing.getFrontOffsetZ();

        ItemCustomSpawnEgg itemCustomSpawnEgg = (ItemCustomSpawnEgg)itemStack.getItem();
        Entity entity = ((ItemCustomSpawnEgg)itemStack.getItem()).spawnCreature(blockSource.getWorld(), ItemCustomSpawnEgg.getEntityIdFromItem(itemStack), x, y, z);
        if (itemStack.hasDisplayName())
            entity.setCustomNameTag(itemStack.getDisplayName());
        
        itemStack.splitStack(1);
        return itemStack;
    }
}
