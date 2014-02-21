package lycanite.lycanitesmobs.demonmobs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.model.ModelBipedCustom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelBelph extends ModelBipedCustom {
	
	// Conditions:
	public int heldItemLeft = 0;
	public int heldItemRight = 0;
    
    // Additional Model Parts:
    ModelRenderer leftshoulder;
    ModelRenderer rightshoulder;
    ModelRenderer lefthorn;
    ModelRenderer righthorn;
    
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelBelph() {
        this(1.0F);
    }
    
    public ModelBelph(float shadowSize) {
    	// Texture:
    	textureWidth = 64;
        textureHeight = 32;
        
        // Head:
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-3F, -6F, -5F, 6, 10, 6);
        head.setRotationPoint(0F, 0F, 0F);
        head.setTextureSize(64, 32);
        setRotation(head, 0F, 0F, 0F);
        
        // Body:
        body = new ModelRenderer(this, 16, 16);
        body.addBox(-4F, 2F, -2F, 8, 12, 4);
        body.setRotationPoint(0F, 0F, 0F);
        body.setTextureSize(64, 32);
        setRotation(body, 0F, 0F, 0F);
        
        leftshoulder = new ModelRenderer(this, 24, 0);
        leftshoulder.addBox(2.5F, -2F, -3F, 7, 4, 6);
        leftshoulder.setRotationPoint(0F, 0F, 0F);
        leftshoulder.setTextureSize(64, 32);
        setRotation(leftshoulder, 0F, 0F, 0.3490659F);
        body.addChild(leftshoulder);
        
        rightshoulder = new ModelRenderer(this, 24, 0);
        rightshoulder.mirror = true;
        rightshoulder.addBox(-9.5F, -2F, -3F, 7, 4, 6);
        rightshoulder.setRotationPoint(0F, 0F, 0F);
        rightshoulder.setTextureSize(64, 32);
        setRotation(rightshoulder, 0F, 0F, -0.3490659F);
        rightshoulder.mirror = false;
        body.addChild(rightshoulder);
        
        lefthorn = new ModelRenderer(this, 24, 11);
        lefthorn.addBox(6F, -5F, -1F, 1, 4, 1);
        lefthorn.setRotationPoint(0F, 0F, 0F);
        lefthorn.setTextureSize(64, 32);
        setRotation(lefthorn, 0F, 0F, 0.3316126F);
        body.addChild(lefthorn);
        
        righthorn = new ModelRenderer(this, 24, 11);
        righthorn.mirror = true;
        righthorn.addBox(-6F, -5F, -1F, 1, 4, 1);
        righthorn.setRotationPoint(0F, 0F, 0F);
        righthorn.setTextureSize(64, 32);
        setRotation(righthorn, 0F, 0F, -0.3316126F);
        righthorn.mirror = false;
        body.addChild(righthorn);
        
        // left Arm:
        leftarm = new ModelRenderer(this, 40, 16);
        leftarm.addBox(-1F, -2F, -2F, 4, 12, 4);
        leftarm.setRotationPoint(5F, 4F, 0F);
        leftarm.setTextureSize(64, 32);
        setRotation(leftarm, 0F, 0F, 0F);
        
        // Right Arm:
        rightarm = new ModelRenderer(this, 40, 16);
        rightarm.mirror = true;
        rightarm.addBox(-3F, -2F, -2F, 4, 12, 4);
        rightarm.setRotationPoint(-5F, 4F, 0F);
        rightarm.setTextureSize(64, 32);
        setRotation(rightarm, 0F, 0F, 0F);
        rightarm.mirror = false;
        
        // Left Leg:
        leftleg = new ModelRenderer(this, 0, 16);
        leftleg.addBox(-2F, 0F, -2F, 4, 10, 4);
        leftleg.setRotationPoint(2F, 14F, 0F);
        leftleg.setTextureSize(64, 32);
        setRotation(leftleg, 0F, 0F, 0F);
        
        // Right Leg:
        rightleg = new ModelRenderer(this, 0, 16);
        rightleg.mirror = true;
        rightleg.addBox(-2F, 0F, -2F, 4, 10, 4);
        rightleg.setRotationPoint(-2F, 14F, 0F);
        rightleg.setTextureSize(64, 32);
        setRotation(rightleg, 0F, 0F, 0F);
        rightleg.mirror = false;
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
