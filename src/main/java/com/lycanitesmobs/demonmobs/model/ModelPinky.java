package com.lycanitesmobs.demonmobs.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.model.ModelBipedCustom;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPinky extends ModelBipedCustom {
	
	// Conditions:
	public int heldItemLeft = 0;
	public int heldItemRight = 0;
    
    // Additional Model Parts:
    public ModelRenderer topjaw;
    public ModelRenderer leftmouth;
    public ModelRenderer rightmouth;
    public ModelRenderer bottomjaw;
    public ModelRenderer topteeth;
    public ModelRenderer bottomteeth;
    public ModelRenderer lefthorn01;
    public ModelRenderer lefthorn02;
    public ModelRenderer lefthorn03;
    public ModelRenderer righthorn01;
    public ModelRenderer righthorn02;
    public ModelRenderer righthorn03;
    
    public ModelRenderer leftshoulder;
    public ModelRenderer rightshoulder;
    
    public ModelRenderer leftlowerleg;
    public ModelRenderer rightlowerleg;
    public ModelRenderer leftfoot;
    public ModelRenderer rightfoot;
    
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelPinky() {
        this(1.0F);
    }
    
    public ModelPinky(float shadowSize) {
    	// Texture:
    	textureWidth = 128;
        textureHeight = 64;
        
        // Head:
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-5F, -4F, -9F, 10, 11, 9);
        head.setRotationPoint(0F, -4F, -11F);
        head.setTextureSize(128, 64);
        setRotation(head, 0.7853982F, 0F, 0F);

        topjaw = new ModelRenderer(this, 38, 0);
        head.addChild(topjaw);
        topjaw.addBox(-6F, 0F, -12F, 12, 6, 6);
        topjaw.setRotationPoint(0F, 0F, 0F);
        topjaw.setTextureSize(128, 64);
        setRotation(topjaw, -0.7853982F + 0.2094395F, 0F, 0F);
        
        topteeth = new ModelRenderer(this, 38, 12);
        head.addChild(topteeth);
        topteeth.addBox(-5F, 5F, -11F, 10, 3, 0);
        topteeth.setRotationPoint(0F, 0F, 0F);
        topteeth.setTextureSize(128, 64);
        setRotation(topteeth, -0.7853982F + 0.2094395F, 0F, 0F);

        leftmouth = new ModelRenderer(this, 120, 0);
        head.addChild(leftmouth);
        leftmouth.addBox(4.9F, -2F, -11F, 1, 8, 3);
        leftmouth.setRotationPoint(0F, 0F, 0F);
        leftmouth.setTextureSize(128, 64);
        setRotation(leftmouth, -0.7853982F + 0.7853982F, 0F, 0F);

        rightmouth = new ModelRenderer(this, 120, 0);
        head.addChild(rightmouth);
        rightmouth.mirror = true;
        rightmouth.addBox(-5.9F, -2F, -11F, 1, 8, 3);
        rightmouth.setRotationPoint(0F, 0F, 0F);
        rightmouth.setTextureSize(128, 64);
        setRotation(rightmouth, -0.7853982F + 0.7853982F, 0F, 0F);
        rightmouth.mirror = false;
        
        bottomjaw = new ModelRenderer(this, 74, 0);
        head.addChild(bottomjaw);
        bottomjaw.addBox(-6F, 8F, -13F, 12, 4, 12);
        bottomjaw.setRotationPoint(0F, 0F, 0F);
        bottomjaw.setTextureSize(128, 64);
        setRotation(bottomjaw, -0.7853982F + 0.4537856F, 0F, 0F);
        
        bottomteeth = new ModelRenderer(this, 38, 15);
        bottomjaw.addChild(bottomteeth);
        bottomteeth.addBox(-5F, 5F, -12F, 10, 4, 0);
        bottomteeth.setRotationPoint(0F, 0F, 0F);
        bottomteeth.setTextureSize(128, 64);
        setRotation(bottomteeth, -0.4537856F + 0.4537856F, 0F, 0F);
        
        lefthorn01 = new ModelRenderer(this, 110, 17);
        head.addChild(lefthorn01);
        lefthorn01.addBox(5F, -2F, -7F, 4, 4, 3);
        lefthorn01.setRotationPoint(0F, 0F, 0F);
        lefthorn01.setTextureSize(128, 64);
        setRotation(lefthorn01, -0.7853982F + 0.4537856F, 0F, 0F);
        
        lefthorn02 = new ModelRenderer(this, 110, 17);
        head.addChild(lefthorn02);
        lefthorn02.addBox(-1F, -2F, -15F, 3, 2, 6);
        lefthorn02.setRotationPoint(0F, 0F, 0F);
        lefthorn02.setTextureSize(128, 64);
        setRotation(lefthorn02, (float)Math.toRadians(-8), (float)Math.toRadians(-49), (float)Math.toRadians(3));
        
        lefthorn03 = new ModelRenderer(this, 110, 17);
        head.addChild(lefthorn03);
        lefthorn03.addBox(13F, -3F, -9F, 2, 2, 7);
        lefthorn03.setRotationPoint(0F, 0F, 0F);
        lefthorn03.setTextureSize(128, 64);
        setRotation(lefthorn03, (float)Math.toRadians(-4), (float)Math.toRadians(30), (float)Math.toRadians(-3));
        
        righthorn01 = new ModelRenderer(this, 110, 17);
        head.addChild(righthorn01);
        righthorn01.mirror = true;
        righthorn01.addBox(-9F, -2F, -7F, 4, 4, 3);
        righthorn01.setRotationPoint(0F, 0F, 0F);
        righthorn01.setTextureSize(128, 64);
        setRotation(righthorn01, -0.7853982F + 0.4537856F, 0F, 0F);
        righthorn01.mirror = false;
        
        righthorn02 = new ModelRenderer(this, 110, 17);
        head.addChild(righthorn02);
        righthorn02.mirror = true;
        righthorn02.addBox(-2F, -2F, -15F, 3, 2, 6);
        righthorn02.setRotationPoint(0F, 0F, 0F);
        righthorn02.setTextureSize(128, 64);
        setRotation(righthorn02, (float)Math.toRadians(-8), (float)Math.toRadians(49), (float)Math.toRadians(-3));
        righthorn02.mirror = false;
        
        righthorn03 = new ModelRenderer(this, 110, 17);
        head.addChild(righthorn03);
        righthorn03.mirror = true;
        righthorn03.addBox(-15F, -3F, -9F, 2, 2, 7);
        righthorn03.setRotationPoint(0F, 0F, 0F);
        righthorn03.setTextureSize(128, 64);
        setRotation(righthorn03, (float)Math.toRadians(-4), (float)Math.toRadians(-30), (float)Math.toRadians(3));
        righthorn03.mirror = false;
        
        // Body:
        body = new ModelRenderer(this, 0, 20);
        body.addBox(-6F, 0F, -4F, 12, 22, 10);
        body.setRotationPoint(0F, -9F, -12F);
        body.setTextureSize(128, 64);
        setRotation(body, 0.7853982F, 0F, 0F);

        leftshoulder = new ModelRenderer(this, 44, 20);
        body.addChild(leftshoulder);
        leftshoulder.addBox(6F, 0F, -1F, 5, 6, 6);
        leftshoulder.setRotationPoint(0F, 0F, 0F);
        leftshoulder.setTextureSize(128, 64);
        setRotation(leftshoulder, -0.7853982F + 0.7853982F, 0F, 0F);
        
        rightshoulder = new ModelRenderer(this, 44, 20);
        body.addChild(rightshoulder);
        rightshoulder.mirror = true;
        rightshoulder.addBox(-11F, 0F, -1F, 5, 6, 6);
        rightshoulder.setRotationPoint(0F, 0F, 0F);
        rightshoulder.setTextureSize(128, 64);
        setRotation(rightshoulder, -0.7853982F + 0.7853982F, 0F, 0F);
        rightshoulder.mirror = false;
        
        // left Arm:
        leftarm = new ModelRenderer(this, 66, 20);
        leftarm.addBox(-1F, 0F, -2F, 3, 12, 4);
        leftarm.setRotationPoint(8F, -8F, -8F);
        leftarm.setTextureSize(128, 64);
        setRotation(leftarm, 0F, 0F, 0F);
        
        // Right Arm:
        rightarm = new ModelRenderer(this, 66, 20);
        rightarm.mirror = true;
        rightarm.addBox(-2F, 0F, -2F, 3, 12, 4);
        rightarm.setRotationPoint(-8F, -8F, -8F);
        rightarm.setTextureSize(128, 64);
        setRotation(rightarm, 0F, 0F, 0F);
        rightarm.mirror = false;
        
        // Left Leg:
        leftleg = new ModelRenderer(this, 85, 17);
        leftleg.addBox(-3F, 0F, -3F, 6, 12, 6);
        leftleg.setRotationPoint(8F, 2F, 1F);
        leftleg.setTextureSize(128, 64);
        setRotation(leftleg, 0.5410521F, 0F, 0F);

        leftlowerleg = new ModelRenderer(this, 87, 35);
        leftleg.addChild(leftlowerleg);
        leftlowerleg.addBox(-2.5F, 4F, 6F, 5, 13, 5);
        leftlowerleg.setRotationPoint(0F, 0F, 0F);
        leftlowerleg.setTextureSize(128, 64);
        setRotation(leftlowerleg, -0.5410521F + -0.4886922F, 0F, 0F);

        leftfoot = new ModelRenderer(this, 83, 53);
        leftleg.addChild(leftfoot);
        leftfoot.addBox(-3.5F, 18F, -4F, 7, 4, 7);
        leftfoot.setRotationPoint(0F, 0F, 0F);
        leftfoot.setTextureSize(128, 64);
        setRotation(leftfoot, -0.5410521F + 0F, 0F, 0F);
        
        // Right Leg:
        rightleg = new ModelRenderer(this, 85, 17);
        rightleg.mirror = true;
        rightleg.addBox(-3F, 0F, -3F, 6, 12, 6);
        rightleg.setRotationPoint(-8F, 2F, 1F);
        rightleg.setTextureSize(128, 64);
        setRotation(rightleg, 0.5410521F, 0F, 0F);
        rightleg.mirror = false;

        rightlowerleg = new ModelRenderer(this, 87, 35);
        rightleg.addChild(rightlowerleg);
        rightlowerleg.mirror = true;
        rightlowerleg.addBox(-2.5F, 4F, 6F, 5, 13, 5);
        rightlowerleg.setRotationPoint(0F, 0F, 0F);
        rightlowerleg.setTextureSize(128, 64);
        setRotation(rightlowerleg, -0.5410521F + -0.4886922F, 0F, 0F);
        rightlowerleg.mirror = false;

        rightfoot = new ModelRenderer(this, 83, 53);
        rightleg.addChild(rightfoot);
        rightfoot.mirror = true;
        rightfoot.addBox(-3.5F, 18F, -4F, 7, 4, 7);
        rightfoot.setRotationPoint(0F, 0F, 0F);
        rightfoot.setTextureSize(128, 64);
        setRotation(rightfoot, -0.5410521F + 0F, 0F, 0F);
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
        if(((EntityCreatureBase)entity).justAttacked()) {
        	bottomjaw.rotateAngleX -= (float)Math.toRadians(18);
        }
    }
}
