package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.Subspecies;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class EntityAIFollowFuse extends EntityAIFollow {
	// Targets:
	EntityCreatureBase host;

	// Fusion:
	double fuseRange = 2;

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIFollowFuse(EntityCreatureBase setHost) {
    	super(setHost);
        this.setMutexBits(1);
        this.host = setHost;
        this.strayDistance = 0;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIFollowFuse setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIFollowFuse setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIFollowFuse setStrayDistance(double setDist) {
    	this.strayDistance = setDist;
    	return this;
    }
    public EntityAIFollowFuse setLostDistance(double setDist) {
    	this.lostDistance = setDist;
    	return this;
    }
	public EntityAIFollowFuse setFuseRange(double setDist) {
		this.fuseRange = setDist;
		return this;
	}
    
	
	// ==================================================
 	//                       Target
 	// ==================================================
    @Override
    public Entity getTarget() {
    	if(this.host instanceof IFusable) {
    		return (Entity)((IFusable)this.host).getFusionTarget();
		}
    	return null;
    }

	@Override
	public void setTarget(Entity entity) {
		if(this.host instanceof IFusable && entity instanceof IFusable) {
			((IFusable)this.host).setFusionTarget((IFusable)entity);
		}
	}

	@Override
	public void onTargetDistance(double distance, Entity followTarget) {
		if(distance > this.fuseRange)
			return;

		// Do Fusion:
		if(this.host instanceof IFusable && followTarget instanceof IFusable) {
			Class fusionClass = ((IFusable)this.host).getFusionClass((IFusable)followTarget);
			if(fusionClass == null) {
				return;
			}

			Entity fusionEntity = null;
			try {
				fusionEntity = (Entity)fusionClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{this.host.getEntityWorld()});
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if(fusionEntity == null) {
				return;
			}

			// Creature Base:
			if(fusionEntity instanceof EntityCreatureBase && followTarget instanceof EntityCreatureBase) {
				EntityCreatureBase fusionCreature = (EntityCreatureBase)fusionEntity;
				EntityCreatureBase fusionPartnerCreature = (EntityCreatureBase)followTarget;
				Subspecies fusionSubspecies = fusionCreature.mobInfo.getChildSubspecies(this.host, this.host.getSubspeciesIndex(), fusionPartnerCreature.getSubspecies());
				fusionCreature.setSubspecies(fusionSubspecies != null ? fusionSubspecies.index : 0, true);
				fusionCreature.setSizeScale(this.host.sizeScale + fusionPartnerCreature.sizeScale);
				fusionCreature.setLevel(this.host.getLevel() + fusionPartnerCreature.getLevel());
				fusionCreature.firstSpawn = false;
			}

			// Fusion Entity:
			fusionEntity.setLocationAndAngles(this.host.posX, this.host.posY, this.host.posZ, this.host.rotationYaw, this.host.rotationPitch);
			this.host.getEntityWorld().spawnEntity(fusionEntity);

			// Remove Parts:
			this.host.setDead();
			followTarget.setDead();
		}
	}
}
