package lycanite.lycanitesmobs.entity.ai;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lycanite.lycanitesmobs.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.entity.EntityCreatureTameable;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAITargetParent extends EntityAITarget {
	// Targets:
	private EntityCreatureAgeable host;
    private Class targetClass = EntityLivingBase.class;
    
    // Properties:
    private IEntitySelector targetSelector;
    private EntityAITargetSorterNearest targetSorter;
    private boolean tameTargeting = true;
    
    private int targetChance = 0;
    private double targetDistance = -1D;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetParent(EntityCreatureAgeable setHost) {
        super(setHost);
        this.setMutexBits(2);
        this.host = setHost;
        this.targetSelector = new EntityAITargetSelector(this, (IEntitySelector)null);
        this.targetSorter = new EntityAITargetSorterNearest(setHost);
        this.targetClass = this.host.getClass();
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetParent setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    public EntityAITargetParent setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAITargetParent setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetParent setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetParent setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    public EntityAITargetParent setDistance(double setDist) {
    	this.targetDistance = setDist;
    	return this;
    }
    public EntityAITargetParent setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getParentTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setParentTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
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
    	if(targetDistance > -1)
    		return targetDistance;
        AttributeInstance attributeinstance = this.host.getEntityAttribute(SharedMonsterAttributes.followRange);
        return attributeinstance == null ? 16.0D : attributeinstance.getAttributeValue();
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	this.target = null;
    	if(this.host.getGrowingAge() >= 0) {
    		this.host.setParentTarget(null);
    		return false;
    	}
    	
        if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0)
            return false;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.canFly()) heightDistance = distance;
        List possibleTargets = this.host.worldObj.selectEntitiesWithinAABB(this.targetClass, this.host.boundingBox.expand(distance, heightDistance, distance), this.targetSelector);
        Collections.sort(possibleTargets, this.targetSorter);
        
        if(possibleTargets.isEmpty())
            return false;
        else
            this.target = (EntityLivingBase)possibleTargets.get(0);
        
        return this.target != null;
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
    	if(this.host.getGrowingAge() >= 0) {
    		this.host.setParentTarget(null);
    		return false;
    	}
    	
    	return super.continueExecuting();
    }
}
