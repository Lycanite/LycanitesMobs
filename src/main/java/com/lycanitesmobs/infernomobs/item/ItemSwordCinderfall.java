package com.lycanitesmobs.infernomobs.item;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.item.ItemSwordBase;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import com.lycanitesmobs.infernomobs.entity.EntityCinder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ItemSwordCinderfall extends ItemSwordBase {
	int particleTime = 0;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordCinderfall() {
        super(ToolMaterial.DIAMOND);
    	this.group = InfernoMobs.group;
    	this.itemName = "cinderfallsword";
        this.setup();
        this.textureName = "swordcinderfall";
    }
	
    
    // ==================================================
 	//                     Tool/Weapon
 	// ==================================================
 	// ========== Hit Entity ==========
     @Override
     public boolean hitEntity(ItemStack itemStack, EntityLivingBase entityHit, EntityLivingBase entityUser) {
     	if(!super.hitEntity(itemStack, entityHit, entityUser))
     		return false;
     	if(entityUser.worldObj.isRemote)
     		return true;
     	entityHit.hurtResistantTime = 0;
     	entityHit.attackEntityFrom(DamageSource.causeMobDamage(entityUser).setFireDamage().setDamageBypassesArmor(), 2);
     	entityHit.setFire(4);
     	if(entityUser.getRNG().nextFloat() <= this.getSpecialEffectChance()) {
     		Entity entity = new EntityCinder(entityUser.worldObj);
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
                if(!entity.worldObj.isSideSolid(spawnPos, EnumFacing.UP))
                    randomAngle = -randomAngle;
                if(entity.worldObj.isSideSolid(new BlockPos(spawnPos), EnumFacing.UP))
                    entity.setLocationAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), entityUser.rotationYaw, 0.0F);
            }
            this.onSpawnEntity(entity);
     		entityUser.worldObj.spawnEntityInWorld(entity);
     	}
     	return true;
     }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.getItem() == ObjectManager.getItem("embercharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
