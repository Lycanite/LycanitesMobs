package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAITargetMaster extends EntityAITarget {
	// Targets:
    private Class targetClass = EntityLivingBase.class;
    
    // Properties:
    private boolean tameTargeting = false;
    private int targetChance = 0;
    private double targetDistance = -1D;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetMaster(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(4);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetMaster setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetMaster setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    public EntityAITargetMaster setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAITargetMaster setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetMaster setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetMaster setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    public EntityAITargetMaster setDistance(double setDist) {
    	this.targetDistance = setDist;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getMasterTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setMasterTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

        if(target instanceof EntityAnimal && ((EntityAnimal)target).getGrowingAge() < 0)
            return false;
    	if(target instanceof EntityCreatureAgeable && ((EntityCreatureAgeable)target).getGrowingAge() < 0)
            return false;
        
        // Tamed Checks:
        if(!this.tameTargeting && this.host instanceof EntityCreatureTameable && ((EntityCreatureTameable)this.host).isTamed())
        	return false;
    	return true;
    }


    // ==================================================
 	//                 Get Target Distance
 	// ==================================================
    @Override
    protected double getTargetDistance() {
    	if(this.targetDistance > -1)
    		return this.targetDistance;
    	IAttributeInstance attributeinstance = this.host.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        return attributeinstance == null ? 16.0D : attributeinstance.getAttributeValue();
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	this.target = null;
        if(this.host.updateTick % 20 != 0 && this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0)
            return false;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        return this.target != null;
    }
}
