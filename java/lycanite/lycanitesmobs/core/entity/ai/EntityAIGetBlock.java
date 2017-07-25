package lycanite.lycanitesmobs.core.entity.ai;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityAIGetBlock extends EntityAIBase {
	// Targets:
	private EntityCreatureBase host;
	private BlockPos target;
	private int targetingTime = 0;
    private TargetSorterNearest targetSorter;
	
	// Properties:
    private int distanceMax = 8;
    double speed = 1.0D;
    private boolean checkSight = true;
    private int cantSeeTime = 0;
    protected int cantSeeTimeMax = 60;
    private int updateRate = 0;
    public Block targetBlock = Blocks.TORCH;
    public String targetBlockName = "";
    public boolean tamedLooting = true;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAIGetBlock(EntityCreatureBase setHost) {
        super();
        this.setMutexBits(1);
        this.host = setHost;
        this.targetSorter = new TargetSorterNearest(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIGetBlock setDistanceMax(int setInt) {
    	this.distanceMax = setInt;
    	return this;
    }

    public EntityAIGetBlock setSpeed(double setDouble) {
    	this.speed = setDouble;
    	return this;
    }
    
    public EntityAIGetBlock setCheckSight(boolean setBool) {
    	this.checkSight = setBool;
    	return this;
    }
    
    public EntityAIGetBlock setBlock(Block block) {
    	this.targetBlock = block;
    	return this;
    }
    
    public EntityAIGetBlock setBlockName(String name) {
    	this.targetBlockName = name.toLowerCase();
    	return this;
    }
    
    public EntityAIGetBlock setTamedLooting(boolean bool) {
    	this.tamedLooting = bool;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.canPickupItems() || !this.host.getEntityWorld().getGameRules().getBoolean("mobGriefing"))
    		return false;
    	
    	if(!this.host.getEntityWorld().getGameRules().getBoolean("mobGriefing"))
    		return false;

    	if(!this.tamedLooting) {
    		if(this.host instanceof EntityCreatureTameable)
    			if(((EntityCreatureTameable)this.host).isTamed())
    				return false;
    	}
    	
    	// Search Delay:
    	if(this.targetingTime-- <= 0) {
    		this.targetingTime = 60;
    	}
    	else {
    		return false;
    	}
    	
        int heightDistance = 2;
        List<BlockPos> possibleTargets = new ArrayList<BlockPos>();
        for(int x = (int)this.host.posX - this.distanceMax; x < (int)this.host.posX + this.distanceMax; x++) {
        	for(int y = (int)this.host.posY - heightDistance; y < (int)this.host.posY + heightDistance; y++) {
        		for(int z = (int)this.host.posZ - this.distanceMax; z < (int)this.host.posZ + this.distanceMax; z++) {
        			Block searchBlock = this.host.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                	if(searchBlock != null && searchBlock != Blocks.AIR) {
                        BlockPos possibleTarget = null;
                		if(!"".equalsIgnoreCase(this.targetBlockName)) {
                			if(ObjectLists.isName(searchBlock, this.targetBlockName)) {
                				possibleTarget = new BlockPos(x, y, z);
                			}
                		}
                		else {
                			if(searchBlock == this.targetBlock)
                				possibleTarget = new BlockPos(x + 1, y, z);
                		}
                		if(possibleTarget != null) {
                			possibleTargets.add(possibleTarget);
                		}
                	}
                }
            }
        }
        
        if(possibleTargets.isEmpty())
            return false;
        Collections.sort(possibleTargets, this.targetSorter);
        this.target = possibleTargets.get(0);
        
        return this.continueExecuting();
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
    	if(this.target == null)
            return false;
    	
    	if(!this.host.getEntityWorld().getGameRules().getBoolean("mobGriefing"))
    		return false;
        
        double distance = this.host.getDistanceSq(this.target.getX(), this.target.getY(), this.target.getZ());
        if(distance > this.distanceMax)
        	return false;
        
        /*if(this.checkSight)
            if(this.host.getEntitySenses().canSee(this.target))
                this.cantSeeTime = 0;
            else if(++this.cantSeeTime > this.cantSeeTimeMax)
                return false;*/
        
        return true;
    }
    
    
    // ==================================================
 	//                      Reset
 	// ==================================================
    @Override
    public void resetTask() {
        this.target = null;
        this.host.clearMovement();
    }
    
    
    // ==================================================
  	//                       Start
  	// ==================================================
    public void startExecuting() {
        this.updateRate = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        if(this.updateRate-- <= 0) {
            this.updateRate = 10;
        	if(!this.host.useDirectNavigator())
        		this.host.getNavigator().tryMoveToXYZ(this.target.getX(), this.target.getY(), this.target.getZ(), this.speed);
        	else
        		this.host.directNavigator.setTargetPosition(this.target, this.speed);
        }
    }
}
