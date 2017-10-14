package com.lycanitesmobs.plainsmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.item.ItemScepter;
import com.lycanitesmobs.plainsmobs.PlainsMobs;
import com.lycanitesmobs.plainsmobs.entity.EntityQuill;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemScepterQuill extends ItemScepter {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemScepterQuill() {
        super();
    	this.group = PlainsMobs.instance.group;
    	this.itemName = "quillscepter";
        this.setup();
        this.textureName = "scepterquill";
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
        return 5;
    }


    // ==================================================
    //                      Attack
    // ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
        if(!world.isRemote) {
            EntityQuill projectile = new EntityQuill(world, entity);
            world.spawnEntity(projectile);
            this.playSound(itemStack, world, entity, 1, projectile);
        }
        return true;
    }


	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("quill")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
