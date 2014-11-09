package lycanite.lycanitesmobs.swampmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.item.ItemSwordBase;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import lycanite.lycanitesmobs.swampmobs.entity.EntityRemobra;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class ItemSwordVenomAxeblade extends ItemSwordBase {
	int particleTime = 0;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordVenomAxeblade() {
        super(Item.ToolMaterial.EMERALD);
    	this.group = SwampMobs.group;
    	this.itemName = "venomaxeblade";
        this.setup();
        this.textureName = "swordvenomaxeblade";
        this.setHarvestLevel("axe", 3);
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
     	entityHit.addPotionEffect(new PotionEffect(Potion.poison.id, 6 * 20, 0));
     	if(entityUser.getRNG().nextFloat() <= 0.2F) {
     		Entity entity = new EntityRemobra(entityUser.worldObj);
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
	    	}
     		entityUser.worldObj.spawnEntityInWorld(entity);
     	}
     	return true;
     }
     
     // ========== Block Effectiveness ==========
     @Override
     public float func_150893_a(ItemStack itemStack, Block block) {
    	 if(block.getMaterial() == Material.wood || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine) {
    		 return Items.diamond_axe.func_150893_a(itemStack, block);
    	 }
    	 return super.func_150893_a(itemStack, block);
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
