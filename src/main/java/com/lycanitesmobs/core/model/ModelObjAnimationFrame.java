package com.lycanitesmobs.core.model;

public class ModelObjAnimationFrame {
    /** The type of animation to do. Can be: angle, rotate, translate or scale. **/
    public String type;
    /** The amount to animate by, usually 1, but can be used to scale an animation and also used for the angle type. **/
    public float amount = 1;
    /** The x amount this animation frame moves by. **/
    public float x;
    /** The y amount this animation frame moves by. **/
    public float y;
    /** The z amount this animation frame moves by. **/
    public float z;

    // ==================================================
    //                    Constructor
    // ==================================================
    public ModelObjAnimationFrame(String type, float amount, float x, float y, float z) {
        this.type = type;
        this.amount = amount;
        this.x = x;
        this.y = y;
        this.z = z;
    }


    // ==================================================
    //                      Apply
    // ==================================================
    /** Performs this animation frame. **/
    public void apply(Animator animator) {
        if("angle".equals(this.type))
            animator.doAngle(this.amount, this.x, this.y, this.z);
        if("rotate".equals(this.type))
            animator.doRotate(this.x * this.amount, this.y * this.amount, this.z * this.amount);
        if("translate".equals(this.type))
            animator.doTranslate(this.x * this.amount, this.y * this.amount, this.z * this.amount);
        if("scale".equals(this.type))
            animator.doScale(this.x * this.amount, this.y * this.amount, this.z * this.amount);
    }
}
