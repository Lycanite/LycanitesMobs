package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;

public class EntityAIStayByWater extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed = 1.0D;
    private double strayDistance = 64.0D;
    
    private double waterX;
    private double waterY;
    private double waterZ;
    private boolean hasWaterPos = false;

    private int waterSearchRate = 0;
    private int updateRate = 0;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public EntityAIStayByWater(EntityCreatureBase setHost) {
    	this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIStayByWater setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }

    public EntityAIStayByWater setStrayDistance(double strayDistance) {
    	this.strayDistance = strayDistance;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
    public boolean shouldExecute() {
    	// Set home when in water or lava (for lava creatures).
    	if(this.host.isInWater()) {
    		Block waterBlock = this.host.worldObj.getBlock((int)this.host.posX, (int)this.host.posY, (int)this.host.posZ - 1);
    		if((!this.host.isLavaCreature && waterBlock.getMaterial() == Material.water) ||
    			(this.host.isLavaCreature && waterBlock.getMaterial() == Material.lava)) {
	    		this.waterX = (int)this.host.posX;
	    		this.waterY = (int)this.host.posY;
	    		this.waterZ = (int)this.host.posZ - 1;
	    		this.hasWaterPos = true;
	    		return false;
    		}
    	}
    	
    	// If we're not in water:
    	if(!this.host.isInWater()) {
    		// If we have a water position but it is no longer water/lava, clear the water position. It is up to the water searcher, wander AI and path weights for find a new water position.
    		if(this.hasWaterPos && !this.host.canBreatheAtLocation((int)this.waterX, (int)this.waterY, (int)this.waterZ)) {
	    		this.hasWaterPos = false;
    		}
    		
    		// If we don't have a water position, search for one every 2 seconds, if we do, check if there is a close one every 5 seconds:
    		if(this.waterSearchRate-- <= 0) {
	    		if(!this.hasWaterPos)
	    			this.waterSearchRate = 40;
	    		else
	    			this.waterSearchRate = 100;
	    		
	    		// Search within a 64x8x64 block area and find the closest water/lava block:
	    		double closestDistance = 99999;
	    		if(this.hasWaterPos)
	    			closestDistance = this.getDistanceFromWater();
	    		int searchRangeX = 32;
	    		int searchRangeY = 8;
	    		int searchRangeZ = 32;
	    		for(int searchX = (int)this.host.posX - searchRangeX; searchX <= (int)this.host.posX + searchRangeX; ++searchX) {
	    			for(int searchY = (int)this.host.posY - searchRangeY; searchY <= (int)this.host.posY + searchRangeY; ++searchY) {
	    				for(int searchZ = (int)this.host.posZ - searchRangeZ; searchZ <= (int)this.host.posZ + searchRangeZ; ++searchZ) {
	    					
	    					// If the block is closer than the last valid location...
	    					double searchDistance = this.host.getDistance(searchX, searchY, searchZ);
    		    			if(!this.hasWaterPos || searchDistance < closestDistance) {
		    					
    		    				// And the host can breathe it...
    		    				if(this.host.canBreatheAtLocation(searchX, searchY, searchZ)) {
		    						
    		    					// If the host has a rounded width larger than 1 then check if it can fit in the target block...
    		    					boolean enoughSpace = Math.round(this.host.width) <= 1;
		    						if(!enoughSpace) {
		    							enoughSpace = true;
			    						int neededSpace = Math.round(this.host.width) - 1;
			    						for(int adjX = searchX - neededSpace; adjX <= searchX + neededSpace; ++adjX) {
			    							for(int adjZ = searchZ - neededSpace; adjZ <= searchZ + neededSpace; ++adjZ) {
			    								if(!this.host.canBreatheAtLocation(adjX, searchY, adjZ)) {
			    									enoughSpace = false;
			    									break;
			    								}
				    						}
			    						}
		    						}
		    						
		    						// If all is good, then set it as the new water position!
		    						if(enoughSpace) {
		    							closestDistance = searchDistance;
		    		    				this.waterX = searchX;
		    		    	    		this.waterY = searchY;
		    		    	    		this.waterZ = searchZ;
		    		    	    		this.hasWaterPos = true;
		    						}
		    					}
		    		    	}
	    	    		}
		    		}
	    		}
    		}
    	}
    	
    	// If it's raining and the host isn't a lava creature, then there's no need to return to the water position, this shouldn't be set as a new water position though.
    	if(!this.host.isLavaCreature && this.host.waterContact() && this.getDistanceFromWater() <= this.strayDistance)
    		return false;
    	
    	// If the host has an attack target and plenty of air, then it is allowed to stray from the water position within the limit.
        if(this.host.hasAttackTarget() && this.host.getAir() > -100 && this.getDistanceFromWater() <= this.strayDistance)
        	return false;
    	
        // At this point the host should return to the water position, if there is one.
        return this.hasWaterPos;
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
    public void startExecuting() {
        this.updateRate = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        if(this.updateRate-- <= 0) {
            this.updateRate = 20;
            double overshot = 1D;
            double overshotX = (this.host.posX > this.waterX ? -overshot : overshot);
            double overshotZ = (this.host.posZ > this.waterZ ? -overshot : overshot);
	    	if(!host.useFlightNavigator()) {
	    		this.host.getNavigator().tryMoveToXYZ(this.waterX + overshotX, this.waterY, this.waterZ + overshotZ, this.speed);
	    	}
	    	else
	    		host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)this.waterX, (int)this.waterY, (int)this.waterZ), this.speed);
    	}
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.clearMovement();
    }

	
	// ==================================================
 	//              Get Distance From Water
 	// ==================================================
    public double getDistanceFromWater() {
    	return this.host.getDistance(this.waterX, this.waterY, this.waterZ);
    }
}
