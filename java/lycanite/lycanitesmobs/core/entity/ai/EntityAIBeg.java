package lycanite.lycanitesmobs.core.entity.ai;

import lycanite.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityAIBeg extends EntityAIBase {
	// Targets:
    private EntityCreatureTameable host;
    private EntityPlayer player;
    
    // Properties:
    private float range = 8.0F * 8.0F;
    private int begTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIBeg(EntityCreatureTameable setHost) {
        this.host = setHost;
        this.setMutexBits(2);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIBeg setRange(float setRange) {
    	this.range = setRange * setRange;
    	return this;
    }
	
    
	// ==================================================
 	//                   Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        this.player = this.host.getEntityWorld().getClosestPlayerToEntity(this.host, (double)this.range);
        return this.player == null ? false : this.gotBegItem(this.player);
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
        return !this.player.isEntityAlive() ? false : (this.host.getDistanceSqToEntity(this.player) > (double)(this.range * this.range) ? false : this.begTime > 0 && this.gotBegItem(this.player));
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        this.host.setSitting(true);
        this.begTime = 40 + this.host.getRNG().nextInt(40);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.setSitting(false);
        this.player = null;
    }
	
    
	// ==================================================
 	//                      Update
 	// ==================================================
    public void updateTask() {
        this.host.getLookHelper().setLookPosition(this.player.posX, this.player.posY + (double)this.player.getEyeHeight(), this.player.posZ, 10.0F, (float)this.host.getVerticalFaceSpeed());
        --this.begTime;
    }
	
    
	// ==================================================
 	//                    Got Beg Item
 	// ==================================================
    private boolean gotBegItem(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if(itemstack == null)
        	return false;
        
        if(!this.host.isTamed())
        	return this.host.isTamingItem(itemstack);
        
        return this.host.isBreedingItem(itemstack);
    }
}
