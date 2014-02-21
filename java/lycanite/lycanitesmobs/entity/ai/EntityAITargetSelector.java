package lycanite.lycanitesmobs.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

class EntityAITargetSelector implements IEntitySelector {
	// Properties:
    final EntityAIBase targetAI;
    final IEntitySelector selector;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    EntityAITargetSelector(EntityAIBase setTargetAI, IEntitySelector setSelector) {
        this.targetAI = setTargetAI;
        this.selector = setSelector;
    }
    
    
    // ==================================================
  	//                  Is Applicable
  	// ==================================================
    public boolean isEntityApplicable(Entity target) {
        if(this.selector != null && !this.selector.isEntityApplicable(target))
        	return false;
        if(this.targetAI instanceof EntityAITarget) {
            if(!(target instanceof EntityLivingBase))
            	return false;
	    	if(!((EntityAITarget)this.targetAI).isSuitableTarget((EntityLivingBase)target, false))
	    		return false;
        }
    	return true;
    }
}
