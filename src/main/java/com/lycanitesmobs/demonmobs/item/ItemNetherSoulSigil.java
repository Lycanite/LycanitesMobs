package com.lycanitesmobs.demonmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.demonmobs.DemonMobs;
import com.lycanitesmobs.demonmobs.entity.EntityNetherSoul;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemNetherSoulSigil extends ItemScepter {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemNetherSoulSigil() {
        super();
        this.group = DemonMobs.group;
        this.itemName = "nethersoulsigil";
        this.setup();
    }


    // ==================================================
    //                       Use
    // ==================================================
    @Override
    public int getDurability() {
        return 250;
    }

    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 8;
    }


    // ==================================================
    //                      Attack
    // ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
        if(!world.isRemote) {
            for(int i = -2; i <= 2; i++) {
                EntityNetherSoul minion = new EntityNetherSoul(world);
                if (minion == null)
                    return false;
                minion.setPositionAndRotation(entity.posX, entity.posY + 1, entity.posZ, entity.rotationYaw + (5 * i), -entity.rotationPitch);
                world.spawnEntity(minion);
                minion.setMinion(true);
                if(entity instanceof EntityPlayer) {
                    minion.setPlayerOwner((EntityPlayer)entity);
                }
                minion.playAttackSound();
                minion.chargeAttack();
            }
        }
        return true;
    }


    // ==================================================
    //                     Repairs
    // ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == Items.GUNPOWDER) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
