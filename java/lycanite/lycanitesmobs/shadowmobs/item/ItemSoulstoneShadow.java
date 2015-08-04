package lycanite.lycanitesmobs.shadowmobs.item;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.item.ItemSoulstone;
import lycanite.lycanitesmobs.shadowmobs.entity.EntityChupacabra;
import lycanite.lycanitesmobs.shadowmobs.entity.EntityShade;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSoulstoneShadow extends ItemSoulstone {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulstoneShadow(GroupInfo group) {
        super();
        this.itemName = "soulstoneshadow";
        this.group = group;
        this.setup();
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null)
    		return itemStack;

        EntityCreatureTameable entity;
        if(player.getRNG().nextBoolean())
            entity = new EntityChupacabra(world);
        else
            entity = new EntityShade(world);
        if(!player.worldObj.isRemote) {
            entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            world.spawnEntityInWorld(entity);
            entity.setPlayerOwner(player);
        }

        super.onItemRightClickOnEntity(player, entity, itemStack);
        return itemStack;
    }


    @Override
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
        return false;
    }
}
