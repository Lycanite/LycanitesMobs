package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.item.ItemSwordBase;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import lycanite.lycanitesmobs.infernomobs.entity.EntityCinder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSwordCinderfall extends ItemSwordBase {
	int particleTime = 0;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordCinderfall() {
        super(Item.ToolMaterial.EMERALD);
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
     	if(entityUser.getRNG().nextFloat() <= 0.2F) {
     		Entity entity = new EntityCinder(entityUser.worldObj);
     		entity.setLocationAndAngles(entityUser.posX, entityUser.posY, entityUser.posZ, entityUser.rotationYaw, 0.0F);
     		if(entity instanceof EntityCreatureBase) {
	    		EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
	    		entityCreature.setMinion(true);
	    		entityCreature.setTemporary(20 * 20);
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
		    	double[] spawnPos = entityCreature.getFacingPosition(entityUser, -1, randomAngle);
		    	if(!entity.worldObj.isSideSolid((int)spawnPos[0], (int)spawnPos[1], (int)spawnPos[2], ForgeDirection.UP))
		    		randomAngle = -randomAngle;
		    	if(entity.worldObj.isSideSolid((int)spawnPos[0], (int)spawnPos[1], (int)spawnPos[2], ForgeDirection.UP))
		    		entity.setLocationAndAngles((int)spawnPos[0], (int)spawnPos[1], (int)spawnPos[2], entityUser.rotationYaw, 0.0F);
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
        if(repairStack.getItem() == ObjectManager.getItem("EmberCharge")) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
