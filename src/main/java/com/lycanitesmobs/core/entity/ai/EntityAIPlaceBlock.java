package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIPlaceBlock extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed = 1.0D;
    private double range = 2.0D;
    private double maxDistance = 64.0D;
    private boolean replaceSolid = false;
    private boolean replaceLiquid = true;
    
    private BlockPos pos;
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
    	this.maxDistance = setMax;
    	return this;
    }
    public EntityAIPlaceBlock setBlockPlacement(Block block, BlockPos pos) {
        this.block = block;
    	this.pos = pos;
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
        
    	if(!this.canPlaceBlock(this.pos)) {
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
    	if(!host.useDirectNavigator())
    		this.host.getNavigator().tryMoveToXYZ(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.speed);
    	else
    		host.directNavigator.setTargetPosition(this.pos, this.speed);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.getNavigator().clearPath();
        this.host.directNavigator.clearTargetPosition(1.0D);
        this.block = null;
    }
	
    
	// ==================================================
 	//                       Update
 	// ==================================================
    @Override
    public void updateTask() {
    	if(this.repathTime-- <= 0) {
    		this.repathTime = 20;
    		if(!host.useDirectNavigator())
        		this.host.getNavigator().tryMoveToXYZ(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.speed);
        	else
        		host.directNavigator.setTargetPosition(this.pos, this.speed);
    	}
    	
        this.host.getLookHelper().setLookPosition(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 30.0F, 30.0F);
        
        // Place Block:
        if(MathHelper.sqrt(this.host.getDistanceSq(this.pos)) <= this.range) {
        	this.host.getEntityWorld().setBlockState(this.pos, this.block.getStateFromMeta(this.metadata), 3);
            this.block = null;
            this.host.clearMovement();
        }
        
        // Cancel If Too Far:
        if(MathHelper.sqrt(this.host.getDistanceSq(this.pos)) >= this.maxDistance) {
            this.block = null;
            this.host.clearMovement();
        }
    }
    
    
    // ==================================================
   	//                  Can Place Block
   	// ==================================================
    public boolean canPlaceBlock(BlockPos pos) {
    	IBlockState targetState = this.host.getEntityWorld().getBlockState(pos);
        Block targetBlock = targetState.getBlock();
    	if(targetBlock == null)
    		return false;
    	else {
    		if(targetState.getMaterial() == Material.WATER || targetState.getMaterial() == Material.LAVA) {
	    		if(!this.replaceLiquid)
	    			return false;
    		}
    		else if(targetBlock != Blocks.AIR && !this.replaceSolid)
	    		return false;
            if(!this.host.useDirectNavigator() && this.host.getNavigator() != null) {
                if(this.host.getNavigator().getPathToPos(pos) == null)
                    return false;
            }
    	}
    	return true;
    }
}
