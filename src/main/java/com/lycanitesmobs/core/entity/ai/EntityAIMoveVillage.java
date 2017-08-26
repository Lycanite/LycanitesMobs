package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.navigate.CreaturePathNavigate;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityAIMoveVillage extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed;
    private boolean isNocturnal = true;
    private Path entityPathNavigate;
    private VillageDoorInfo doorInfo;
    private List doorList = new ArrayList();
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIMoveVillage(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIMoveVillage setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIMoveVillage setNocturnal(boolean flag) {
    	this.isNocturnal = flag;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        this.shuffleDoorList();
        
        if(this.isNocturnal && this.host.worldObj.isDaytime())
            return false;
        
        Village village = this.host.worldObj.villageCollectionObj.getNearestVillage(new BlockPos(MathHelper.floor_double(this.host.posX), MathHelper.floor_double(this.host.posY), MathHelper.floor_double(this.host.posZ)), 0);
        if(village == null)
            return false;
        
        this.doorInfo = this.getDoorInfo(village);
        if(this.doorInfo == null)
            return false;

        if(!(this.host.getNavigator() instanceof CreaturePathNavigate))
            return false;
        CreaturePathNavigate pathNavigate = (CreaturePathNavigate)this.host.getNavigator();
        boolean flag = pathNavigate.getEnterDoors();
        pathNavigate.setBreakDoors(false);
        this.entityPathNavigate = this.host.getNavigator().getPathToXYZ((double)this.doorInfo.getInsideOffsetX(), (double)this.doorInfo.getInsidePosY(), (double)this.doorInfo.getInsideOffsetZ());
        pathNavigate.setBreakDoors(flag);

        if(this.entityPathNavigate != null)
            return true;
        
        Vec3d vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 10, 7, new Vec3d((double)this.doorInfo.getInsideOffsetX(), (double)this.doorInfo.getInsidePosY(), (double)this.doorInfo.getInsideOffsetZ()));
        if(vec3 == null)
            return false;
        pathNavigate.setBreakDoors(false);
        this.entityPathNavigate = this.host.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        pathNavigate.setBreakDoors(flag);
        return this.entityPathNavigate != null;
    }
	
    
	// ==================================================
 	//                Continue Executing
 	// ==================================================
    public boolean continueExecuting()
    {
        if (this.host.getNavigator().noPath())
        {
            return false;
        }
        else
        {
            float f = this.host.width + 4.0F;
            return this.host.getDistanceSq((double)this.doorInfo.getInsideOffsetX(), (double)this.doorInfo.getInsidePosY(), (double)this.doorInfo.getInsideOffsetZ()) > (double)(f * f);
        }
    }
	
    
	// ==================================================
 	//                       Start
 	// ==================================================
    public void startExecuting()
    {
        this.host.getNavigator().setPath(this.entityPathNavigate, this.speed);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask()
    {
        if (this.host.getNavigator().noPath() || this.host.getDistanceSq((double)this.doorInfo.getInsideOffsetX(), (double)this.doorInfo.getInsidePosY(), (double)this.doorInfo.getInsideOffsetZ()) < 16.0D)
        {
            this.doorList.add(this.doorInfo);
        }
    }
	
    
	// ==================================================
 	//                  Get Door Info
 	// ==================================================
    private VillageDoorInfo getDoorInfo(Village par1Village) {
        VillageDoorInfo villagedoorinfo = null;
        int i = Integer.MAX_VALUE;
        List list = par1Village.getVillageDoorInfoList();
        Iterator iterator = list.iterator();

        while(iterator.hasNext()) {
            VillageDoorInfo villagedoorinfo1 = (VillageDoorInfo)iterator.next();
            int j = villagedoorinfo1.getDistanceSquared(MathHelper.floor_double(this.host.posX), MathHelper.floor_double(this.host.posY), MathHelper.floor_double(this.host.posZ));

            if(j < i && !this.func_75413_a(villagedoorinfo1)) {
                villagedoorinfo = villagedoorinfo1;
                i = j;
            }
        }

        return villagedoorinfo;
    }

    private boolean func_75413_a(VillageDoorInfo par1VillageDoorInfo) {
        Iterator iterator = this.doorList.iterator();
        VillageDoorInfo villagedoorinfo1;

        do {
            if(!iterator.hasNext())
                return false;
            villagedoorinfo1 = (VillageDoorInfo)iterator.next();
        }
        while(par1VillageDoorInfo.getInsideOffsetX() != villagedoorinfo1.getInsideOffsetX() || par1VillageDoorInfo.getInsidePosY() != villagedoorinfo1.getInsidePosY() || par1VillageDoorInfo.getInsideOffsetZ() != villagedoorinfo1.getInsideOffsetZ());

        return true;
    }

    private void shuffleDoorList() {
        if(this.doorList.size() > 15)
            this.doorList.remove(0);
    }
}
