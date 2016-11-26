package lycanite.lycanitesmobs.demonmobs.item;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.core.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.item.ItemSoulstone;
import lycanite.lycanitesmobs.demonmobs.entity.EntityCacodemon;
import lycanite.lycanitesmobs.demonmobs.entity.EntityPinky;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemSoulstoneDemonic extends ItemSoulstone {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulstoneDemonic(GroupInfo group) {
        super();
        this.itemName = "soulstonedemonic";
        this.group = group;
        this.setup();
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null)
            return new ActionResult(EnumActionResult.SUCCESS, itemStack);

        EntityCreatureTameable entity;
        if(player.getRNG().nextBoolean())
            entity = new EntityCacodemon(world);
        else
            entity = new EntityPinky(world);
        if(!player.worldObj.isRemote) {
            entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            world.spawnEntityInWorld(entity);
            entity.setPlayerOwner(player);
        }

		super.onItemRightClickOnEntity(player, entity, itemStack);
        return new ActionResult(EnumActionResult.SUCCESS, itemStack);
    }


    @Override
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
        return false;
    }
}
