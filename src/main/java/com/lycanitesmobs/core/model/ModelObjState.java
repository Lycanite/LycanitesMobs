package com.lycanitesmobs.core.model;

import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class ModelObjState {
    /** The entity that this model state is for. **/
    public Entity entity;

    // Attack Animation:
    /** The current progress on an attack animation, increases to 1.0 and then decreases back down to 0.0. **/
    public float attackAnimationProgress = 0.0F;
    /** The current direction that the attack animation progress is going in. **/
    public boolean attackAnimationIncreasing = false;
    /** Set to true when the attack animation starts, set to false when finished, prevents the attack from resetting mid animation. **/
    public boolean attackAnimationPlaying = false;
    /** How much the animation progress increases or decreases by each tick. This will have to count up to 1 and back down to 0 again. **/
    public float attackAnimationSpeed = 1F / 10;

    /** Additional state data. **/
    protected Map<String, Float> additionalFloats = new HashMap<>();

    // ==================================================
    //                    Constructor
    // ==================================================
    public ModelObjState(Entity entity) {
        this.entity = entity;
    }


    // ==================================================
    //                  Additional Data
    // ==================================================
    public void setFloat(String key,  float value) {
        this.additionalFloats.put(key, value);
    }

    public float getFloat(String key) {
        if(this.additionalFloats.containsKey(key))
            return this.additionalFloats.get(key);
        return 0;
    }
}
