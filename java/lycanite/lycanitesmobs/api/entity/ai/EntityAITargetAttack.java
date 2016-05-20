package lycanite.lycanitesmobs.api.entity.ai;

import com.google.common.base.Predicate;
import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;

import java.util.Collections;
import java.util.List;
public class EntityAITargetAttack extends EntityAITarget {
	// Targets:
    public Class targetClass = EntityLivingBase.class;
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
        this.targetSelector = new Predicate<Entity>() {
            @Override
            public boolean apply(Entity input) {
                if(!(input instanceof EntityLivingBase))
                    return false;
                return EntityAITargetAttack.this.isSuitableTarget((EntityLivingBase)input, false);
            }
        };
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
    
    public EntityAITargetAttack setSelector(Predicate<Entity> selector) {
    	this.targetSelector = selector;
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
        // Pickup Check:
        if(this.host.hasPickupEntity() && target != this.host.getPickupEntity())
            return false;

        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

    	// Own Class Check:
    	if(this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass())
            return false;

        // Predator Animal/Alpha Check:
        if(this.targetClass == IGroupAnimal.class || this.targetClass == IGroupAlpha.class) {
            if(target instanceof IGroupPredator)
                return false;
        }
    	
    	// Class Check:
    	if(!this.host.canAttackClass(target.getClass()))
            return false;

        // Entity Check:
        if(!this.host.canAttackEntity(target))
        	return false;
        
        // Ownable Checks:
        if(this.host instanceof IEntityOwnable && ((IEntityOwnable)this.host).getOwner() != null) {
            if(target instanceof IEntityOwnable && ((IEntityOwnable)this.host).getOwner() == ((IEntityOwnable)target).getOwner())
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
        	double hostPackSize = this.host.worldObj.getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().expand(hostPackRange, hostPackRange, hostPackRange)).size();
        	double hostPackScale = hostPackSize / this.allySize;

        	double targetPackRange = 64D;
        	double targetPackSize = target.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().expand(targetPackRange, targetPackRange, targetPackRange), new Predicate<EntityLivingBase>() {
                @Override
                public boolean apply(EntityLivingBase entity) {
                    return entity.getClass().isAssignableFrom(EntityAITargetAttack.this.targetClass);
                }
            }).size();
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
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        List possibleTargets = this.host.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.host.getEntityBoundingBox().expand(distance, heightDistance, distance), this.targetSelector);
        Collections.sort(possibleTargets, this.targetSorter);

        if(possibleTargets.isEmpty())
            this.target = null;
        else
            this.target = (EntityLivingBase)possibleTargets.get(0);
        
        return this.target != null;
    }
}
