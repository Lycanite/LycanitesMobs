package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.info.CreatureKnowledge;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

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

		MobInfo mobInfo = ((EntityCreatureTameable)entity).mobInfo;

		if(!player.capabilities.isCreativeMode)
			itemStack.stackSize -= 1;
		if(itemStack.stackSize <= 0)
			player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);

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
    		String message = StatCollector.translateToLocal("message.soulstone.added");
    		message = message.replace("%creature%", mobInfo.getTitle());
    		player.addChatMessage(new ChatComponentText(message));
    		if(mobInfo.isSummonable()) {
        		String summonMessage = StatCollector.translateToLocal("message.soulgazer.summonable");
        		summonMessage = summonMessage.replace("%creature%", mobInfo.getTitle());
        		player.addChatMessage(new ChatComponentText(summonMessage));
    		}
            player.addStat(ObjectManager.getAchievement(mobInfo.name + ".learn"), 1);
    	}

    	playerExt.getBeastiary().addToKnowledgeList(new CreatureKnowledge(player, mobInfo.name, 1));
    	return true;
    }
}
