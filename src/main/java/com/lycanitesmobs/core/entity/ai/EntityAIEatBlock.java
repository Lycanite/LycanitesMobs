package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIEatBlock extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private Block[] blocks = new Block[0];
    private Material[] materials = new Material[0];
    private Block replaceBlock = Blocks.AIR;
    private int eatTime = 40;
    private int eatTimeMax = 40;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIEatBlock(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(7);
    }
    
    
    // ==================================================
   	//                  Set Properties
   	// ==================================================
     public EntityAIEatBlock setBlocks(Block... setBlocks) {
    	this.blocks = setBlocks;
     	return this;
     }

     public EntityAIEatBlock setMaterials(Material... setMaterials) {
    	this.materials = setMaterials;
     	return this;
     }

     public EntityAIEatBlock setReplaceBlock(Block block) {
    	this.replaceBlock = block;
     	return this;
     }

     public EntityAIEatBlock setEatTime(int setTime) {
    	this.eatTimeMax = setTime;
     	return this;
     }
 	
     
 	// ==================================================
  	//                   Should Execute
  	// ==================================================
     public boolean shouldExecute() {
    	 if(this.host.getRNG().nextInt(this.host.isChild() ? 50 : 1000) != 0)
             return false;
    	 
    	 int i = MathHelper.floor(this.host.posX);
         int j = MathHelper.floor(this.host.posY);
         int k = MathHelper.floor(this.host.posZ);

         IBlockState blockState = this.host.getEntityWorld().getBlockState(new BlockPos(i, j - 1, k));
         return this.isValidBlock(blockState);
     }
  	
     
  	// ==================================================
   	//                 Valid Block Check
   	// ==================================================
     public boolean isValidBlock(IBlockState blockState) {
         for(Block edibleBlock : this.blocks) {
        	 if(edibleBlock == blockState.getBlock())
        		 return true;
         }
         
         Material material = blockState.getMaterial();
         for(Material edibleMaterial : this.materials) {
        	 if(edibleMaterial == material)
        		 return true;
         }
         
         return false;
     }
 	
     
 	// ==================================================
  	//                      Start
  	// ==================================================
     public void startExecuting() {
    	 this.eatTime = 40;
         this.host.clearMovement();
     }
 	
     
 	// ==================================================
  	//                       Reset
  	// ==================================================
     public void resetTask() {
    	 this.eatTime = 40;
     }
  	
     
  	// ==================================================
   	//                      Continue
   	// ==================================================
      public boolean shouldContinueExecuting() {
    	  return this.eatTime > 0;
      }
 	
     
 	// ==================================================
  	//                      Update
  	// ==================================================
     public void updateTask() {
         if(--this.eatTime != 0) return;
         
         int i = MathHelper.floor(this.host.posX);
         int j = MathHelper.floor(this.host.posY);
         int k = MathHelper.floor(this.host.posZ);
         IBlockState blockState = this.host.getEntityWorld().getBlockState(new BlockPos(i, j - 1, k));
         
         if(this.isValidBlock(blockState)) {
             //if(this.host.getEntityWorld().getGameRules().getGameRuleBooleanValue("mobGriefing"))
        	 this.host.getEntityWorld().setBlockToAir(new BlockPos(i, j - 1, k)); // Might be something else was x, y, z, false
         }

         this.host.getEntityWorld().playEvent(2001, new BlockPos(i, j - 1, k), Block.getIdFromBlock(blockState.getBlock()));
         this.host.getEntityWorld().setBlockState(new BlockPos(i, j - 1, k), this.replaceBlock.getDefaultState(), 2);
         this.host.onEat();
     }
}
