package com.lycanitesmobs.swampmobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.swampmobs.SwampMobs;
import com.lycanitesmobs.core.item.ItemSwordBase;
import com.lycanitesmobs.swampmobs.entity.EntityRemobra;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSwordVenomAxeblade extends ItemSwordBase {
	int particleTime = 0;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordVenomAxeblade(String itemName, String textureName) {
        super(Item.ToolMaterial.DIAMOND);
    	this.group = SwampMobs.instance.group;
    	this.itemName = itemName;
        this.setup();
        this.textureName = textureName;
        this.setHarvestLevel("axe", 3);
    }


    // ==================================================
    //                      Update
    // ==================================================
    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
        super.onUpdate(itemStack, world, entity, par4, par5);
        if(itemStack == null || itemStack.getItem() != this)
            return;
    }

    /** Called from the main EventListener if the entity is holding this item when damaged. If this returns false, the damage will be blocked all together. **/
    @Override
    public void onEarlyUpdate(ItemStack itemStack, EntityLivingBase entityLiving, EnumHand hand) {
        super.onEarlyUpdate(itemStack, entityLiving, hand);
        if(itemStack == null || itemStack.getItem() != this)
            return;
        Potion potion = MobEffects.POISON;
        if(entityLiving.isPotionActive(potion))
            entityLiving.removePotionEffect(potion);
    }
	
    
    // ==================================================
 	//                     Tool/Weapon
 	// ==================================================
 	// ========== Hit Entity ==========
     @Override
     public boolean hitEntity(ItemStack itemStack, EntityLivingBase entityHit, EntityLivingBase entityUser) {
     	if(!super.hitEntity(itemStack, entityHit, entityUser))
     		return false;
     	if(entityUser.getEntityWorld().isRemote)
     		return true;
     	entityHit.addPotionEffect(new PotionEffect(MobEffects.POISON, 6 * 20, 0));
     	if(entityUser.getRNG().nextFloat() <= this.getSpecialEffectChance()) {
     		Entity entity = new EntityRemobra(entityUser.getEntityWorld());
     		entity.setLocationAndAngles(entityUser.posX, entityUser.posY, entityUser.posZ, entityUser.rotationYaw, 0.0F);
     		if(entity instanceof EntityCreatureBase) {
	    		EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
	    		entityCreature.setMinion(true);
	    		entityCreature.setTemporary(20 * 20);
                entityCreature.setSizeScale(0.4D);
		    	if(entityUser instanceof EntityPlayer && entityCreature instanceof EntityCreatureTameable) {
		    		EntityCreatureTameable entityTameable = (EntityCreatureTameable)entityCreature;
		    		entityTameable.setPlayerOwner((EntityPlayer)entityUser);
		    		entityTameable.setSitting(false);
		    		entityTameable.setFollowing(true);
		    		entityTameable.setPassive(false);
		    		entityTameable.setAggressive(true);
		    		entityTameable.setPVP(entityHit instanceof EntityPlayer);
		    	}
		    	float randomAngle = 45F + (45F * entityUser.getRNG().nextFloat());
		    	if(entityUser.getRNG().nextBoolean())
		    		randomAngle = -randomAngle;
		    	BlockPos spawnPos = entityCreature.getFacingPosition(entityUser, -1, randomAngle);
		    	if(!entity.getEntityWorld().isSideSolid(spawnPos, EnumFacing.UP))
		    		randomAngle = -randomAngle;
		    	if(entity.getEntityWorld().isSideSolid(new BlockPos(spawnPos), EnumFacing.UP))
		    		entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), entityUser.rotationYaw, 0.0F);
	    	}
            this.onSpawnEntity(entity);
     		entityUser.getEntityWorld().spawnEntity(entity);
     	}
     	return true;
     }
     
     // ========== Block Effectiveness ==========
     @Override
     public float getDestroySpeed(ItemStack itemStack, IBlockState state) {
    	 if(state.getMaterial() == Material.WOOD || state.getMaterial() == Material.PLANTS || state.getMaterial() == Material.VINE) {
    		 return Items.DIAMOND_AXE.getDestroySpeed(itemStack, state);
    	 }
    	 return super.getDestroySpeed(itemStack, state);
     }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("poisongland")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
