package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.info.CreatureKnowledge;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.pets.PetEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.UUID;

public class ItemSoulstone extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulstone() {
        super();
        this.itemName = "soulstone";
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
	// ========== Entity Interaction ==========
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null)
    		return false;
    	if(!(entity instanceof EntityCreatureTameable)) {
    		if(!player.worldObj.isRemote)
    			player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("message.soulstone.invalid")));
    		return false;
    	}

		EntityCreatureTameable entityTameable = (EntityCreatureTameable)entity;
		MobInfo mobInfo = entityTameable.mobInfo;
	 	if(!mobInfo.isTameable() || entityTameable.getOwner() != player) {
			if(!player.worldObj.isRemote)
				player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("message.soulstone.invalid")));
			return false;
		}
		if(entityTameable.getPetEntry() != null) {
			if(!player.worldObj.isRemote)
				player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("message.soulstone.exists")));
			return false;
		}

		if(!player.worldObj.isRemote) {
			if (!player.capabilities.isCreativeMode)
				itemStack.stackSize -= 1;
			if (itemStack.stackSize <= 0)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
		}

    	if(player.worldObj.isRemote) {
    		for(int i = 0; i < 32; ++i) {
    			entity.worldObj.spawnParticle("happyVillager",
    					entity.posX + (4.0F * player.getRNG().nextFloat()) - 2.0F,
    					entity.posY + (4.0F * player.getRNG().nextFloat()) - 2.0F,
    					entity.posZ + (4.0F * player.getRNG().nextFloat()) - 2.0F,
        				0.0D, 0.0D, 0.0D);
    		}
    	}
    	
    	if(!player.worldObj.isRemote) {
			String petType = "pet";
			if(entity instanceof EntityCreatureRideable)
				petType = "mount";

    		String message = StatCollector.translateToLocal("message.soulstone." + petType + ".added");
    		message = message.replace("%creature%", mobInfo.getTitle());
    		player.addChatMessage(new ChatComponentText(message));
            //player.addStat(ObjectManager.getAchievement("soulstone"), 1);

			// Add Pet Entry:
			PetEntry petEntry = PetEntry.createFromEntity(player, entityTameable, petType);
			playerExt.petManager.addEntry(petEntry);
			playerExt.sendPetEntriesToPlayer(petType);
			petEntry.assignEntity(entity);
			entityTameable.setPetEntry(petEntry);
    	}
    	return true;
    }
}
