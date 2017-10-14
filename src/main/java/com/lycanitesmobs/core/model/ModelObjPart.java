package com.lycanitesmobs.core.model;

import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelObjPart {
    /** The name of this model part. **/
    public String name;
    /** The parent part of this model part, null if this part has no parent. This will do all animations that the parent does. **/
    public ModelObjPart parent;
    /** The parent name of this model part, used for initial setup and null if this part has no parent. **/
    public String parentName;
    /** The child parts connected to this part, these will all do the animations that this does. **/
    public Map<String, ModelObjPart> children = new HashMap<>();
    /** The x center location of this part for rotating around. **/
    public float centerX;
    /** The y center location of this part for rotating around. **/
    public float centerY;
    /** The z center location of this part for rotating around. **/
    public float centerZ;

    /** A list of animation frames to apply to this part on the next render frame. **/
    public List<ModelObjAnimationFrame> animationFrames = new ArrayList<>();

    // ==================================================
    //                    Constructor
    // ==================================================
    public ModelObjPart(String partName, String parentName, float centerX, float centerY, float centerZ) {
        this.name = partName;
        this.parentName = parentName;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
    }


    // ==================================================
    //                     Children
    // ==================================================
    public void addChildren(ModelObjPart[] parts) {
        for(ModelObjPart part : parts) {
            if(part == null || part == this || part.parentName == null)
                continue;
            if(this.children.containsKey(part.parentName))
                continue;
            if(this.name.equals(part.parentName)) {
                this.children.put(part.name, part);
                part.parent = this;
            }
        }
    }


    // ==================================================
    //                Add Animation Frame
    // ==================================================
    /** Adds a new animation frame to apply during the next render frame. **/
    public void addAnimationFrame(ModelObjAnimationFrame frame) {
        this.animationFrames.add(frame);
    }


    // ==================================================
    //               Apply Animation Frames
    // ==================================================
    /** Applies all animation frames to this part and will then go through any parents and apply theirs also. **/
    public void applyAnimationFrames(Animator animator) {
        // Apply Parent Frames:
        if(this.parent != null) {
            this.parent.applyAnimationFrames(animator);
        }

        // Center Part:
        animator.doTranslate(this.centerX, this.centerY, this.centerZ);

        // Apply Frames:
        for(ModelObjAnimationFrame animationFrame : this.animationFrames) {
            animationFrame.apply(animator);
        }

        // Uncenter Part:
        animator.doTranslate(-this.centerX, -this.centerY, -this.centerZ);
    }
}
