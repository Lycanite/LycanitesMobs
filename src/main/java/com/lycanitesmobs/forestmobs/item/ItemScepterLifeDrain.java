package com.lycanitesmobs.forestmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.forestmobs.ForestMobs;
import com.lycanitesmobs.forestmobs.entity.EntityLifeDrain;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemScepterLifeDrain extends ItemScepter {
	private EntityProjectileLaser projectileTarget;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterLifeDrain() {
        super();
    	this.group = ForestMobs.group;
    	this.itemName = "lifedrainscepter";
        this.setup();
        this.textureName = "scepterlifedrain";
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Start ==========
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
    	this.projectileTarget = null;
        return super.onItemRightClick(world, player, hand);
    }
    
    @Override
    public int getDurability() {
    	return 250;
    }

    @Override
    public int getRapidTime(ItemStack par1ItemStack) {
        return 10;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
    	if(!world.isRemote) {
    		if(this.projectileTarget != null && this.projectileTarget.isEntityAlive()) {
    			projectileTarget.setTime(20);
    		}
    		else {
    			this.projectileTarget = new EntityLifeDrain(world, entity, 20, 10);
    			world.spawnEntity(this.projectileTarget);
                this.playSound(itemStack, world, entity, 1, this.projectileTarget);
    		}
    	}
    	return true;
    }


	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("lifedraincharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
