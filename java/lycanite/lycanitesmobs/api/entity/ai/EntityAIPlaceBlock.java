package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class EntityAIPlaceBlock extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed = 1.0D;
    private double range = 2.0D;
    private double maxDistance = 64.0D * 64.0D;
    private boolean replaceSolid = false;
    private boolean replaceLiquid = true;
    
    private int xPosition;
    private int yPosition;
    private int zPosition;
    public Block block;
    public int metadata = 0;
    
    private int repathTime = 0;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public EntityAIPlaceBlock(EntityCreatureBase setHost) {
    	this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIPlaceBlock setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIPlaceBlock setRange(double setRange) {
    	this.range = setRange;
    	return this;
    }
    public EntityAIPlaceBlock setMaxDistance(double setMax) {
    	this.maxDistance = setMax * setMax;
    	return this;
    }
    public EntityAIPlaceBlock setBlockPlacement(Block block, int x, int y, int z) {
    	this.xPosition = x;
    	this.yPosition = y;
    	this.zPosition = z;
    	this.block = block;
    	return this;
    }
    public EntityAIPlaceBlock setMetadata(int setMetadata) {
    	this.metadata = setMetadata;
    	return this;
    }
    public EntityAIPlaceBlock setReplaceSolid(boolean bool) {
    	this.replaceSolid = bool;
    	return this;
    }
    public EntityAIPlaceBlock setReplaceLiquid(boolean bool) {
    	this.replaceLiquid = bool;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
    @Override
    public boolean shouldExecute() {
        if(this.block == null)
            return false;
        
    	if(!this.canPlaceBlock(this.xPosition, this.yPosition, this.zPosition)) {
            this.block = null;
    		return false;
    	}
    	
        return true;
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
    @Override
    public void startExecuting() {
    	if(!host.useFlightNavigator())
    		this.host.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    	else
    		host.flightNavigator.setTargetPosition(new BlockPos(xPosition, yPosition, zPosition), speed);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.getNavigator().clearPathEntity();
        this.host.flightNavigator.clearTargetPosition(1.0D);
        this.block = null;
    }
	
    
	// ==================================================
 	//                       Update
 	// ==================================================
    @Override
    public void updateTask() {
    	if(this.repathTime-- <= 0) {
    		this.repathTime = 20;
    		if(!host.useFlightNavigator())
        		this.host.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
        	else
        		host.flightNavigator.setTargetPosition(new BlockPos(xPosition, yPosition, zPosition), speed);
    	}
    	
        this.host.getLookHelper().setLookPosition(this.xPosition, this.yPosition, this.zPosition, 30.0F, 30.0F);
        
        // Place Block:
        if(this.host.getDistanceSq(this.xPosition, this.yPosition, this.zPosition) <= this.range) {
        	this.host.worldObj.setBlockState(new BlockPos(this.xPosition, this.yPosition, this.zPosition), this.block.getDefaultState(), 3); // TODO Metadata!
            this.block = null;
            this.host.clearMovement();
        }
        
        // Cancel If Too Far:
        if(this.host.getDistanceSq(this.xPosition, this.yPosition, this.zPosition) >= this.maxDistance) {
            this.block = null;
            this.host.clearMovement();
        }
    }
    
    
    // ==================================================
   	//                  Can Place Block
   	// ==================================================
    public boolean canPlaceBlock(int x, int y, int z) {
    	IBlockState targetState = this.host.worldObj.getBlockState(new BlockPos(x, y, z));
        Block targetBlock = targetState.getBlock();
    	if(targetBlock == null)
    		return false;
    	else {
    		if(targetState.getMaterial() == Material.water || targetState.getMaterial() == Material.lava) {
	    		if(!this.replaceLiquid)
	    			return false;
    		}
    		else if(targetBlock != Blocks.air && !this.replaceSolid)
	    		return false;
    	}
    	return true;
    }
}
