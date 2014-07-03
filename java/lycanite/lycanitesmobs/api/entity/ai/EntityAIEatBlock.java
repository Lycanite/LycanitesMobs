package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

public class EntityAIEatBlock extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private Block[] blocks = new Block[0];
    private Material[] materials = new Material[0];
    private Block replaceBlock = Blocks.air;
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
    	 
    	 int i = MathHelper.floor_double(this.host.posX);
         int j = MathHelper.floor_double(this.host.posY);
         int k = MathHelper.floor_double(this.host.posZ);
         
         Block block = this.host.worldObj.getBlock(i, j - 1, k);
         return this.isValidBlock(block);
     }
  	
     
  	// ==================================================
   	//                 Valid Block Check
   	// ==================================================
     public boolean isValidBlock(Block block) {
         for(Block edibleBlock : this.blocks) {
        	 if(edibleBlock == block)
        		 return true;
         }
         
         Material material = block.getMaterial();
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
      public boolean continueExecuting() {
    	  return this.eatTime > 0;
      }
 	
     
 	// ==================================================
  	//                      Update
  	// ==================================================
     public void updateTask() {
         if(--this.eatTime != 0) return;
         
         int i = MathHelper.floor_double(this.host.posX);
         int j = MathHelper.floor_double(this.host.posY);
         int k = MathHelper.floor_double(this.host.posZ);
         Block block = this.host.worldObj.getBlock(i, j - 1, k);
         
         if(this.isValidBlock(block)) {
             //if(this.host.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
        	 this.host.worldObj.func_147480_a(i, j - 1, k, false);
         }

         this.host.worldObj.playAuxSFX(2001, i, j - 1, k, Block.getIdFromBlock(block));
         this.host.worldObj.setBlock(i, j - 1, k, this.replaceBlock, 0, 2);
         this.host.onEat();
     }
}
