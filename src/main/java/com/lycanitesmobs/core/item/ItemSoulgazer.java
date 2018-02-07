package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
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
    		if(!player.getEntityWorld().isRemote)
    			player.sendMessage(new TextComponentString(I18n.translateToLocal("message.soulgazer.unknown")));
    		return false;
    	}
    	if(entity instanceof EntityFear) {
    		return false;
    	}
    	CreatureInfo creatureInfo = ((EntityCreatureBase)entity).creatureInfo;
    	if(playerExt.getBeastiary().hasFullKnowledge(creatureInfo.getName())) {
    		if(!player.getEntityWorld().isRemote)
    			player.sendMessage(new TextComponentString(I18n.translateToLocal("message.soulgazer.known")));
    		return false;
    	}
    	
    	if(player.getEntityWorld().isRemote) {
    		for(int i = 0; i < 32; ++i) {
    			entity.getEntityWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
    					entity.posX + (4.0F * player.getRNG().nextFloat()) - 2.0F,
    					entity.posY + (4.0F * player.getRNG().nextFloat()) - 2.0F,
    					entity.posZ + (4.0F * player.getRNG().nextFloat()) - 2.0F,
        				0.0D, 0.0D, 0.0D);
    		}
    	}

    	playerExt.getBeastiary().addToKnowledgeList(new CreatureKnowledge(playerExt.getBeastiary(), creatureInfo.getName(), 1));
		playerExt.getBeastiary().sendAddedMessage(creatureInfo);
    	return true;
    }
}
