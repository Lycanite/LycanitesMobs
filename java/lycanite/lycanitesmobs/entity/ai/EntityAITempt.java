package lycanite.lycanitesmobs.entity.ai;

import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.entity.EntityCreatureTameable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

public class EntityAITempt extends EntityAIBase {
    // Targets:
    private EntityCreatureBase host;
    private EntityPlayer player;
    
    // Properties:
    private double speed = 1.0D;
    private int temptID = 0;
    private int temptMeta = -1;
    private String temptList = null;
    private int retemptTime;
    private int retemptTimeMax = 100;
    private double temptDistanceMin = 1.0D * 1.0D;
    private double temptDistanceMax = 10.0D;
    private boolean scaredByPlayerMovement = false;
    private boolean stopAttack = false;
    
    private double targetX;
    private double targetY;
    private double targetZ;
    private double targetPitch;
    private double targetYaw;
    private boolean avoidWater;
    private boolean isRunning;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITempt(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(3);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITempt setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAITempt setItemID(int id) {
    	this.temptID = id;
    	return this;
    }
    public EntityAITempt setItemMeta(int meta) {
    	this.temptMeta = meta;
    	return this;
    }
    public EntityAITempt setItemList(String list) {
    	this.temptList = list;
    	return this;
    }
    public EntityAITempt setRetemptTime(int time) {
    	this.retemptTimeMax = time;
    	return this;
    }
    public EntityAITempt setTemptDistanceMin(double dist) {
    	this.temptDistanceMin = dist * dist;
    	return this;
    }
    public EntityAITempt setTemptDistanceMax(double dist) {
    	this.temptDistanceMax = dist * dist;
    	return this;
    }
    public EntityAITempt setScaredByMovement(boolean scared) {
    	this.scaredByPlayerMovement = scared;
    	return this;
    }
    public EntityAITempt setStopAttack(boolean setStopAttack) {
    	this.stopAttack = setStopAttack;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
        if(this.retemptTime > 0) {
            --this.retemptTime;
            return false;
        }
        
        if(!this.host.canBeTempted())
        	return false;
        
        if(this.host instanceof EntityCreatureTameable && ((EntityCreatureTameable)this.host).isTamed())
        	return false;
		
        this.player = this.host.worldObj.getClosestPlayerToEntity(this.host, this.temptDistanceMax);
        if(this.player == null)
            return false;
        
        ItemStack itemstack = this.player.getCurrentEquippedItem();
        if(itemstack == null)
        	return false;
        if(this.temptList != null) {
        	if(!ObjectLists.inItemList(this.temptList, itemstack))
        		return false;
        }
        else {
	        if(itemstack.itemID != this.temptID)
	        	return false;
	        if(this.temptMeta >= 0 && itemstack.getItemDamage() != this.temptMeta)
	        	return false;
        }
        
        this.host.setStealth(0.0F);
        return true;
    }
    
    
    // ==================================================
  	//                 Continue Executing
  	// ==================================================
    public boolean continueExecuting() {
        if(this.scaredByPlayerMovement) {
            if(this.host.getDistanceSqToEntity(this.player) < 36.0D) {
                if(this.player.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D)
                    return false;
                if(Math.abs((double)this.player.rotationPitch - this.targetPitch) > 5.0D || Math.abs((double)this.player.rotationYaw - this.targetYaw) > 5.0D)
                    return false;
            }
            else {
                this.targetX = this.player.posX;
                this.targetY = this.player.posY;
                this.targetZ = this.player.posZ;
            }

            this.targetPitch = (double)this.player.rotationPitch;
            this.targetYaw = (double)this.player.rotationYaw;
        }

        return this.shouldExecute();
    }
    
    
    // ==================================================
  	//                      Start
  	// ==================================================
    public void startExecuting() {
        this.targetX = this.player.posX;
        this.targetY = this.player.posY;
        this.targetZ = this.player.posZ;
        this.isRunning = true;
        this.avoidWater = this.host.getNavigator().getAvoidsWater();
        this.host.getNavigator().setAvoidsWater(false);
        if(this.stopAttack)
        	this.host.setAttackTarget(null);
    }
    
    
    // ==================================================
  	//                      Reset
  	// ==================================================
    public void resetTask() {
        this.player = null;
        this.host.getNavigator().clearPathEntity();
        this.retemptTime = this.retemptTimeMax;
        this.isRunning = false;
        this.host.getNavigator().setAvoidsWater(this.avoidWater);
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        if(this.stopAttack)
        	this.host.setAttackTarget(null);
        this.host.getLookHelper().setLookPositionWithEntity(this.player, 30.0F, (float)this.host.getVerticalFaceSpeed());
        if(this.host.getDistanceSqToEntity(this.player) < this.temptDistanceMin)
            this.host.clearMovement();
        else {
        	if(!this.host.canFly())
        		this.host.getNavigator().tryMoveToEntityLiving(this.player, this.speed);
        	else
        		this.host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)this.player.posX, (int)this.player.posY, (int)this.player.posZ), speed);
        }
    }
    
    /**
     * @see #isRunning ???
     */
    public boolean isRunning() {
        return this.isRunning;
    }
}
