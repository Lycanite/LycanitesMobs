package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.info.AltarInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.pets.PetEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemSoulkey extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulkey() {
        super();
        this.itemName = "soulkey";
        this.setup();
    }
	
    
	// ==================================================
	//                      Update
	// ==================================================
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		super.onUpdate(itemStack, world, entity, par4, par5);
	}
    
    
	// ==================================================
	//                       Use
	// ==================================================
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int facing, float par8, float par9, float par10) {
        if(!AltarInfo.checkAltarsEnabled())
            return false;

        // Apply Facing Direction to Block Coords:
        if (facing == 0) {
            --y;
        }
        if (facing == 1)  {
            ++y;
        }
        if (facing == 2) {
            --z;
        }
        if (facing == 3) {
            ++z;
        }
        if (facing == 4) {
            --x;
        }
        if (facing == 5) {
            ++x;
        }

        // Get Possible Altars:
        List<AltarInfo> possibleAltars = new ArrayList<AltarInfo>();
        for(AltarInfo altarInfo : AltarInfo.altars.values()) {
            if(altarInfo.quickCheck(player, world, x, y, z))
                possibleAltars.add(altarInfo);
        }
        if(possibleAltars.size() < 1)
            return false;

        // Activate First Valid Altar:
        for(AltarInfo altarInfo : possibleAltars) {
            if(altarInfo.fullCheck(player, world, x, y, z)) {

                // Valid Altar:
                if(!player.worldObj.isRemote) {
                    if (!player.capabilities.isCreativeMode)
                        itemStack.stackSize -= 1;
                    if (itemStack.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                }
                altarInfo.activate(player, world, x, y, z);
                return true;
            }
        }

        return false;
    }
}
