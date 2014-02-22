package lycanite.lycanitesmobs.api.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveVillage extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed;
    private boolean isNocturnal = true;
    private PathEntity entityPathNavigate;
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
        
        Village village = this.host.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.host.posX), MathHelper.floor_double(this.host.posY), MathHelper.floor_double(this.host.posZ), 0);
        if(village == null)
            return false;
        
        this.doorInfo = this.getDoorInfo(village);
        if(this.doorInfo == null)
            return false;
        
        boolean flag = this.host.getNavigator().getCanBreakDoors();
        this.host.getNavigator().setBreakDoors(false);
        this.entityPathNavigate = this.host.getNavigator().getPathToXYZ((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ);
        this.host.getNavigator().setBreakDoors(flag);

        if(this.entityPathNavigate != null)
            return true;
        
        Vec3 vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 10, 7, this.host.worldObj.getWorldVec3Pool().getVecFromPool((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ));
        if(vec3 == null)
            return false;
        this.host.getNavigator().setBreakDoors(false);
        this.entityPathNavigate = this.host.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        this.host.getNavigator().setBreakDoors(flag);
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
            return this.host.getDistanceSq((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ) > (double)(f * f);
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
        if (this.host.getNavigator().noPath() || this.host.getDistanceSq((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ) < 16.0D)
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
        while(par1VillageDoorInfo.posX != villagedoorinfo1.posX || par1VillageDoorInfo.posY != villagedoorinfo1.posY || par1VillageDoorInfo.posZ != villagedoorinfo1.posZ);

        return true;
    }

    private void shuffleDoorList() {
        if(this.doorList.size() > 15)
            this.doorList.remove(0);
    }
}
