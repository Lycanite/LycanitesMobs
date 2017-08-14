package com.lycanitesmobs.core.model;

public class ModelObjAnimationFrame {
    /** The type of animation to do. Can be: angle, rotate, translate or scale. **/
    public String type;
    /** The part to animation around. **/
    public ModelObjPart part;
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
    public ModelObjAnimationFrame(ModelObjPart part, String type, float amount, float x, float y, float z) {
        this.type = type;
        this.part = part;
        this.amount = amount;
        this.x = x;
        this.y = y;
        this.z = z;
    }


    // ==================================================
    //                      Apply
    // ==================================================
    /** Performs this animation frame. **/
    public void apply(ModelObj model) {
        // Center Part:
        model.doTranslate(part.centerX, part.centerY, part.centerZ);

        // Apply Movement:
        if("angle".equals(this.type))
            model.doAngle(this.amount, this.x, this.y, this.z);
        if("rotate".equals(this.type))
            model.doRotate(this.x * this.amount, this.y * this.amount, this.z * this.amount);
        if("translate".equals(this.type))
            model.doTranslate(this.x * this.amount, this.y * this.amount, this.z * this.amount);
        if("scale".equals(this.type))
            model.doScale(this.x * this.amount, this.y * this.amount, this.z * this.amount);

        // Uncenter Part:
        model.doTranslate(-part.centerX, -part.centerY, -part.centerZ);
    }
}
