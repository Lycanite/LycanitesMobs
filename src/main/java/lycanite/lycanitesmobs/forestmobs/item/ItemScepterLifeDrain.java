package lycanite.lycanitesmobs.forestmobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.entity.EntityProjectileLaser;
import lycanite.lycanitesmobs.core.item.ItemScepter;
import lycanite.lycanitesmobs.forestmobs.ForestMobs;
import lycanite.lycanitesmobs.forestmobs.entity.EntityLifeDrain;
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
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
    	this.projectileTarget = null;
        return super.onItemRightClick(itemStack, world, player, hand);
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
    			world.spawnEntityInWorld(this.projectileTarget);
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
