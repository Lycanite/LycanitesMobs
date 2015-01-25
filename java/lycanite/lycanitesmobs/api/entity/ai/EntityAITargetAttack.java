package lycanite.lycanitesmobs.api.entity.ai;

import java.util.Collections;
import java.util.List;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;

import org.apache.commons.lang3.StringUtils;

public class EntityAITargetAttack extends EntityAITarget {
	// Targets:
    private Class targetClass = EntityLivingBase.class;
    private List<Class> targetClasses = null;
    
    // Properties:
    private int targetChance = 0;
    private EntityAITargetSorterNearest targetSorter;
    protected boolean tameTargeting = false;
    private int allySize = 0;
    private int enemySize = 0;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetAttack(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(1);
        this.targetSelector = new EntityAITargetSelector(this, (IEntitySelector)null);
        this.targetSorter = new EntityAITargetSorterNearest(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetAttack setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    
    public EntityAITargetAttack setCheckSight(boolean bool) {
    	this.checkSight = bool;
    	return this;
    }
    
    public EntityAITargetAttack setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    
    public EntityAITargetAttack setTargetClasses(List<Class> classList) {
    	this.targetClasses = classList;
    	return this;
    }
    
    public EntityAITargetAttack setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    
    public EntityAITargetAttack setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    
    public EntityAITargetAttack setSelector(IEntitySelector selector) {
    	this.targetSelector = new EntityAITargetSelector(this, selector);
    	return this;
    }
    
    public EntityAITargetAttack setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    /** If both values are above 0 then this mob will consider the size of the enemy pack and it's pack before attacking.
     * setAllySize How many of this mob vs the enemy pack.
     * setEnemySize How many of the enemy vs this mobs size.
     * For example allySize of this mob will attack up to enemySize of the enemy at once.
     * Setting either value at or below 0 will disable this functionality.
    **/
    public EntityAITargetAttack setPackHuntingScale(int setAllySize, int setEnemySize) {
    	this.allySize = setAllySize;
    	this.enemySize = setEnemySize;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setAttackTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
    	// Own Class Check:
    	if(this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass())
            return false;
    	
    	// Class Check:
    	if(!this.host.canAttackClass(target.getClass()))
            return false;

        // Entity Check:
        if(!this.host.canAttackEntity(target))
        	return false;
        
        // Ownable Checks:
        if(this.host instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)this.host).func_152113_b())) { //getOwnerName()
            if(target instanceof IEntityOwnable && ((IEntityOwnable)this.host).func_152113_b().equals(((IEntityOwnable)target).func_152113_b())) //getOwnerName()
                return false;
            if(target == ((IEntityOwnable)this.host).getOwner())
                return false;
        }
        
        // Tamed Checks:
        if(!this.tameTargeting && this.host instanceof EntityCreatureTameable && ((EntityCreatureTameable)this.host).isTamed())
        	return false;
        
        // Pack Size Check:
        if(this.allySize > 0 && this.enemySize > 0) {
        	double hostPackRange = 32D;
        	double hostPackSize = this.host.worldObj.getEntitiesWithinAABB(this.host.getClass(), this.host.boundingBox.expand(hostPackRange, hostPackRange, hostPackRange)).size();
        	double hostPackScale = hostPackSize / this.allySize;

        	double targetPackRange = 64D;
        	double targetPackSize = target.worldObj.getEntitiesWithinAABB(this.targetClass, target.boundingBox.expand(targetPackRange, targetPackRange, targetPackRange)).size();
        	double targetPackScale = targetPackSize / this.enemySize;
        	
        	if(hostPackScale < targetPackScale)
        		return false;
        }
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	this.target = null;
    	
    	if(!this.host.isAggressive())
    		return false;
    	
        if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0)
            return false;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D + this.host.height;
        if(this.host.useFlightNavigator()) heightDistance = distance;
        List possibleTargets = this.host.worldObj.selectEntitiesWithinAABB(this.targetClass, this.host.boundingBox.expand(distance, heightDistance, distance), this.targetSelector);
        Collections.sort(possibleTargets, this.targetSorter);
        
        if(possibleTargets.isEmpty())
            this.target = null;
        else
            this.target = (EntityLivingBase)possibleTargets.get(0);
        
        return this.target != null;
    }
}
