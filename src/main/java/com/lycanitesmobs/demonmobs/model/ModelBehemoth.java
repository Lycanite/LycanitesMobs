package com.lycanitesmobs.demonmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelBipedCustom;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBehemoth extends ModelBipedCustom {
	
	// Conditions:
	public int heldItemLeft = 0;
	public int heldItemRight = 0;
    
    // Additional Model Parts:
    ModelRenderer mouth;
    ModelRenderer lefthorn01;
    ModelRenderer lefthorn02;
    ModelRenderer lefthorn03;
    ModelRenderer righthorn01;
    ModelRenderer righthorn02;
    ModelRenderer righthorn03;
    
    ModelRenderer leftshoulder;
    ModelRenderer rightshoulder;
    
    ModelRenderer leftlowerarm;
    ModelRenderer rightlowerarm;
    
    ModelRenderer leftlowerleg;
    ModelRenderer rightlowerleg;
    ModelRenderer leftfoot;
    ModelRenderer rightfoot;
    
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelBehemoth() {
        this(1.0F);
    }
    
    public ModelBehemoth(float shadowSize) {
    	// Texture:
    	textureWidth = 128;
        textureHeight = 128;
        
        // Head:
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4F, -10F, -4F, 8, 10, 8);
        head.setRotationPoint(0F, -21F, 0F);
        head.setTextureSize(128, 128);
        setRotation(head, 0F, 0F, 0F);
        
        mouth = new ModelRenderer(this, 32, 0);
        head.addChild(mouth);
        mouth.addBox(-2F, -2F, -4.5F, 4, 3, 2);
        mouth.setRotationPoint(0F, 0F, 0F);
        mouth.setTextureSize(128, 128);
        setRotation(mouth, 0.2094395F, 0F, 0F);
        
        lefthorn01 = new ModelRenderer(this, 32, 5);
        head.addChild(lefthorn01);
        lefthorn01.addBox(3F, -5F, -0.5F, 5, 5, 6);
        lefthorn01.setRotationPoint(0F, 0F, 0F);
        lefthorn01.setTextureSize(128, 128);
        setRotation(lefthorn01, 0.1745329F, 0.3141593F, -0.4363323F);
        
        lefthorn02 = new ModelRenderer(this, 50, 0);
        head.addChild(lefthorn02);
        lefthorn02.addBox(-3F, -10F, 6F, 3, 7, 4);
        lefthorn02.setRotationPoint(0F, 0F, 0F);
        lefthorn02.setTextureSize(128, 128);
        setRotation(lefthorn02, 1.204277F, -0.1396263F, 1.012291F);
        
        lefthorn03 = new ModelRenderer(this, 53, 0);
        head.addChild(lefthorn03);
        lefthorn03.addBox(3.5F, -7.5F, 11F, 2, 7, 2);
        lefthorn03.setRotationPoint(0F, 0F, 0F);
        lefthorn03.setTextureSize(128, 128);
        setRotation(lefthorn03, 1.989675F, 0F, 0.5235988F);
        
        righthorn01 = new ModelRenderer(this, 32, 5);
        head.addChild(righthorn01);
        righthorn01.mirror = true;
        righthorn01.addBox(-8F, -5F, -0.5F, 5, 5, 6);
        righthorn01.setRotationPoint(0F, 0F, 0F);
        righthorn01.setTextureSize(128, 128);
        setRotation(righthorn01, 0.1745329F, -0.3141593F, 0.4363323F);
        righthorn01.mirror = false;
        
        righthorn02 = new ModelRenderer(this, 50, 0);
        head.addChild(righthorn02);
        righthorn02.mirror = true;
        righthorn02.addBox(0F, -10F, 6F, 3, 7, 4);
        righthorn02.setRotationPoint(0F, 0F, 0F);
        righthorn02.setTextureSize(128, 128);
        setRotation(righthorn02, 1.204277F, 0.1396263F, -1.012291F);
        righthorn02.mirror = false;
        
        righthorn03 = new ModelRenderer(this, 53, 0);
        head.addChild(righthorn03);
        righthorn03.mirror = true;
        righthorn03.addBox(-5.5F, -7.5F, 11F, 2, 7, 2);
        righthorn03.setRotationPoint(0F, 0F, 0F);
        righthorn03.setTextureSize(128, 128);
        setRotation(righthorn03, 1.989675F, 0F, -0.5235988F);
        righthorn03.mirror = false;
        
        // Body:
        body = new ModelRenderer(this, 0, 18);
        body.addBox(-8F, 0F, -2F, 16, 24, 7);
        body.setRotationPoint(0F, -21F, 0F);
        body.setTextureSize(128, 128);
        setRotation(body, 0F, 0F, 0F);
        
        leftshoulder = new ModelRenderer(this, 46, 16);
        body.addChild(leftshoulder);
        leftshoulder.addBox(4F, -3F, -3F, 8, 8, 9);
        leftshoulder.setRotationPoint(0F, 0F, 0F);
        leftshoulder.setTextureSize(128, 128);
        setRotation(leftshoulder, 0F, 0F, 0.2443461F);
        
        rightshoulder = new ModelRenderer(this, 46, 16);
        body.addChild(rightshoulder);
        rightshoulder.mirror = true;
        rightshoulder.addBox(-12F, -3F, -3F, 8, 8, 9);
        rightshoulder.setRotationPoint(0F, 0F, 0F);
        rightshoulder.setTextureSize(128, 128);
        setRotation(rightshoulder, 0F, 0F, -0.2443461F);
        rightshoulder.mirror = false;
        
        // Left Arm:
        leftarm = new ModelRenderer(this, 80, 0);
        leftarm.addBox(0F, -2F, -3F, 5, 13, 6);
        leftarm.setRotationPoint(8F, -16F, 1F);
        leftarm.setTextureSize(128, 128);
        setRotation(leftarm, (float)Math.toRadians(15), 0F, (float)Math.toRadians(-10));

        leftlowerarm = new ModelRenderer(this, 80, 19);
        leftarm.addChild(leftlowerarm);
        leftlowerarm.addBox(3F, 8F, 1F, 5, 14, 6);
        leftlowerarm.setRotationPoint(0F, 0F, 0F);
        leftlowerarm.setTextureSize(128, 128);
        setRotation(leftlowerarm, (float)Math.toRadians(-15 + -15), 0F, (float)Math.toRadians(10 + 8));
        
        // Right Arm:
        rightarm = new ModelRenderer(this, 80, 0);
        rightarm.mirror = true;
        rightarm.addBox(-5F, -2F, -3F, 5, 13, 6);
        rightarm.setRotationPoint(-8F, -16F, 1F);
        rightarm.setTextureSize(128, 128);
        setRotation(rightarm, (float)Math.toRadians(15), 0F, (float)Math.toRadians(10));
        rightarm.mirror = false;

        rightlowerarm = new ModelRenderer(this, 80, 19);
        rightarm.addChild(rightlowerarm);
        rightlowerarm.mirror = true;
        rightlowerarm.addBox(-8F, 8F, 1F, 5, 14, 6);
        rightlowerarm.setRotationPoint(0F, 0F, 0F);
        rightlowerarm.setTextureSize(128, 128);
        setRotation(rightlowerarm, (float)Math.toRadians(-15 + -15), 0F, (float)Math.toRadians(-10 + -8));
        rightlowerarm.mirror = false;
        
        // Left Leg:
        leftleg = new ModelRenderer(this, 0, 49);
        leftleg.addBox(-4F, -1F, -2F, 8, 13, 7);
        leftleg.setRotationPoint(4F, 3F, 1F);
        leftleg.setTextureSize(128, 128);
        setRotation(leftleg, (float)Math.toRadians(28), 0F, 0F);

        leftlowerleg = new ModelRenderer(this, 0, 69);
        leftleg.addChild(leftlowerleg);
        leftlowerleg.addBox(-3.9F, 3F, 5.5F, 8, 14, 7);
        leftlowerleg.setRotationPoint(0F, 0F, 0F);
        leftlowerleg.setTextureSize(128, 128);
        setRotation(leftlowerleg, (float)Math.toRadians(-28 + -28), 0F, 0F);

        leftfoot = new ModelRenderer(this, 0, 90);
        leftleg.addChild(leftfoot);
        leftfoot.addBox(-4F, 14F, -3F, 9, 7, 10);
        leftfoot.setRotationPoint(0F, 0F, 0F);
        leftfoot.setTextureSize(128, 128);
        setRotation(leftfoot, (float)Math.toRadians(-28), 0F, 0F);
        
        // Right Leg:
        rightleg = new ModelRenderer(this, 0, 49);
        rightleg.mirror = true;
        rightleg.addBox(-4F, -1F, -2F, 8, 13, 7);
        rightleg.setRotationPoint(-4F, 3F, 1F);
        rightleg.setTextureSize(128, 128);
        setRotation(rightleg, (float)Math.toRadians(28), 0F, 0F);
        rightleg.mirror = false;

        rightlowerleg = new ModelRenderer(this, 0, 69);
        rightleg.addChild(rightlowerleg);
        rightlowerleg.mirror = true;
        rightlowerleg.addBox(-4.1F, 3F, 5.5F, 8, 14, 7);
        rightlowerleg.setRotationPoint(0F, 0F, 0F);
        rightlowerleg.setTextureSize(128, 128);
        setRotation(rightlowerleg, (float)Math.toRadians(-28 + -28), 0F, 0F);
        rightlowerleg.mirror = false;

        rightfoot = new ModelRenderer(this, 0, 90);
        rightleg.addChild(rightfoot);
        rightfoot.mirror = true;
        rightfoot.addBox(-5F, 14F, -3F, 9, 7, 10);
        rightfoot.setRotationPoint(0F, 0F, 0F);
        rightfoot.setTextureSize(128, 128);
        setRotation(rightfoot, (float)Math.toRadians(-28), 0F, 0F);
        rightfoot.mirror = false;
    }
    
    
    // ==================================================
   	//                  Render Model
   	// ==================================================
    @Override
    public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.render(entity, time, distance, loop, lookY, lookX, scale);
    }
    
    
    // ==================================================
   	//                   Set Angles
   	// ==================================================
    @Override
    public void setAngles(EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.setAngles(entity, time, distance, loop, lookY, lookX, scale);
    }
    
    
    // ==================================================
   	//                 Animate Model
   	// ==================================================
    @Override
    public void animate(EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animate(entity, time, distance, loop, lookY, lookX, scale);
    	float pi = (float)Math.PI;
        
    	// Custom Mob Check:
        if(!(entity instanceof EntityCreatureBase)) return;
        
        // Attack Target:
        if(((EntityCreatureBase)entity).hasAttackTarget()) {
        	float offsetArmHead = 0.0F;
        	rightarm.rotateAngleY += -(0.1F - offsetArmHead * 0.6F) + head.rotateAngleY;
        	rightarm.rotateAngleX += -((float)Math.PI / 2F) + head.rotateAngleX;
        	rightarm.rotateAngleZ += MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F;
        	rightarm.rotateAngleX += MathHelper.sin(loop * 0.067F) * 0.05F;
        }
    }
}
