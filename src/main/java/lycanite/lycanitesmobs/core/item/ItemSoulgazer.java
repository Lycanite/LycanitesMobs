package lycanite.lycanitesmobs.core.item;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.EntityFear;
import lycanite.lycanitesmobs.core.info.CreatureKnowledge;
import lycanite.lycanitesmobs.core.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemSoulgazer extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulgazer() {
        super();
        this.setMaxStackSize(1);
        this.itemName = "soulgazer";
        this.setup();
        this.setContainerItem(this); // Infinite use in the crafting grid.
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
	@Override
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null)
    		return false;
    	if(!(entity instanceof EntityCreatureBase)) {
    		if(!player.worldObj.isRemote)
    			player.addChatMessage(new TextComponentString(I18n.translateToLocal("message.soulgazer.unknown")));
    		return false;
    	}
    	if(entity instanceof EntityFear) {
    		return false;
    	}
    	MobInfo mobInfo = ((EntityCreatureBase)entity).mobInfo;
    	if(playerExt.getBeastiary().hasFullKnowledge(mobInfo.name)) {
    		if(!player.worldObj.isRemote)
    			player.addChatMessage(new TextComponentString(I18n.translateToLocal("message.soulgazer.known")));
    		return false;
    	}
    	
    	if(player.worldObj.isRemote) {
    		for(int i = 0; i < 32; ++i) {
    			entity.worldObj.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
    					entity.posX + (4.0F * player.getRNG().nextFloat()) - 2.0F,
    					entity.posY + (4.0F * player.getRNG().nextFloat()) - 2.0F,
    					entity.posZ + (4.0F * player.getRNG().nextFloat()) - 2.0F,
        				0.0D, 0.0D, 0.0D);
    		}
    	}
    	
    	if(!player.worldObj.isRemote) {
    		String message = I18n.translateToLocal("message.soulgazer.new");
    		message = message.replace("%creature%", mobInfo.getTitle());
    		player.addChatMessage(new TextComponentString(message));
    		if(mobInfo.isSummonable()) {
        		String summonMessage = I18n.translateToLocal("message.soulgazer.summonable");
        		summonMessage = summonMessage.replace("%creature%", mobInfo.getTitle());
        		player.addChatMessage(new TextComponentString(summonMessage));
    		}
            player.addStat(ObjectManager.getAchievement(mobInfo.name + ".learn"), 1);
    	}

    	playerExt.getBeastiary().addToKnowledgeList(new CreatureKnowledge(playerExt.getBeastiary(), mobInfo.name, 1));
    	return true;
    }


    // ==================================================
    //                      Crafting
    // ==================================================
    //TODO Figure out how this is now done!
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack) {
        return false;
    }
}
