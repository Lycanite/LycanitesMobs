package lycanite.lycanitesmobs.api.model;

import java.util.Map.Entry;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBipedCustom extends ModelCustom {
	
	// Conditions:
	public int heldItemLeft = 0;
	public int heldItemRight = 0;
    
    // Model Parts:
	public ModelRenderer head;
	public ModelRenderer body;
	public ModelRenderer leftarm;
	public ModelRenderer rightarm;
	public ModelRenderer leftleg;
	public ModelRenderer rightleg;
    
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelBipedCustom() {
        this(1.0F);
    }
    
    public ModelBipedCustom(float shadowSize) {
    	// Texture:
    	textureWidth = 128;
        textureHeight = 128;
        
        // Head:
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4F, -10F, -4F, 8, 10, 8);
        head.setRotationPoint(0F, -21F, 0F);
        head.setTextureSize(128, 128);
        setRotation(head, 0F, 0F, 0F);
        
        // Body:
        body = new ModelRenderer(this, 0, 18);
        body.addBox(-8F, 0F, -2F, 16, 24, 7);
        body.setRotationPoint(0F, -21F, 0F);
        body.setTextureSize(128, 128);
        setRotation(body, 0F, 0F, 0F);
        
        // Left Arm:
        leftarm = new ModelRenderer(this, 80, 0);
        leftarm.addBox(0F, -2F, -3F, 5, 21, 6);
        leftarm.setRotationPoint(8F, -16F, 1F);
        leftarm.setTextureSize(128, 128);
        setRotation(leftarm, 0F, 0F, 0F);
        
        // Right Arm:
        rightarm = new ModelRenderer(this, 80, 0);
        rightarm.mirror = true;
        rightarm.addBox(-5F, -2F, -3F, 5, 21, 6);
        rightarm.setRotationPoint(-8F, -16F, 1F);
        rightarm.setTextureSize(128, 128);
        setRotation(rightarm, 0F, 0F, 0F);
        rightarm.mirror = false;
        
        // Left Leg:
        leftleg = new ModelRenderer(this, 0, 49);
        leftleg.addBox(-4F, 0F, -3F, 8, 20, 7);
        leftleg.setRotationPoint(4F, 3F, 1F);
        leftleg.setTextureSize(128, 128);
        setRotation(leftleg, 0F, 0F, 0F);
        
        // Right Leg:
        rightleg = new ModelRenderer(this, 0, 49);
        rightleg.mirror = true;
        rightleg.addBox(-4F, 0F, -3F, 8, 20, 7);
        rightleg.setRotationPoint(-4F, 3F, 1F);
        rightleg.setTextureSize(128, 128);
        setRotation(rightleg, 0F, 0F, 0F);
        rightleg.mirror = false;
    }
    
    
    // ==================================================
   	//                  Render Model
   	// ==================================================
    @Override
    public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.render(entity, time, distance, loop, lookY, lookX, scale);
        
        // Baby Resizing:
        if(this.isChild) {
        	GL11.glTranslatef(0.0f, 0.5f, 0.25f);
        }
    	
    	// Render Head Parts (Parent Only):
    	head.render(scale);
        
        // Baby Resizing:
        if(this.isChild) {
        	GL11.glTranslatef(0.0f, 0.25f, -0.25f);
        	GL11.glScalef(0.5f, 0.5f, 0.5f);
        }
    	
    	// Render Parts (Parents Only):
        body.render(scale);
        leftarm.render(scale);
        rightarm.render(scale);
        leftleg.render(scale);
        rightleg.render(scale);
    }
    
    
    // ==================================================
   	//                   Set Angles
   	// ==================================================
    @Override
    public void setAngles(EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	// Set Initial Rotations:
    	for(Entry<ModelRenderer, float[]> initRotation : initRotations.entrySet()) {
    		float[] rotations = initRotation.getValue();
    		setRotation(initRotation.getKey(), rotations[0], rotations[1], rotations[2]);
    	}
    	
    	// Idle:
        head.rotateAngleX += lookX / (180F / (float)Math.PI);
    	head.rotateAngleY += lookY / (180F / (float)Math.PI);
        leftarm.rotateAngleZ += -MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F;
        leftarm.rotateAngleX += -MathHelper.sin(loop * 0.067F) * 0.05F;
        rightarm.rotateAngleZ += MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F;
        rightarm.rotateAngleX += MathHelper.sin(loop * 0.067F) * 0.05F;
    	
    	// Walking:
    	rightarm.rotateAngleX += MathHelper.cos(time * 0.6662F + (float)Math.PI) * 2.0F * distance * 0.5F;
        leftarm.rotateAngleX += MathHelper.cos(time * 0.6662F) * 2.0F * distance * 0.5F;
        leftleg.rotateAngleX += MathHelper.cos(time * 0.6662F + (float)Math.PI) * 1.4F * distance;
        leftleg.rotateAngleY += 0.0F;
    	rightleg.rotateAngleX += MathHelper.cos(time * 0.6662F) * 1.4F * distance;
        rightleg.rotateAngleY += 0.0F;
        
        // Riding:
        if(this.isRiding) {
            leftarm.rotateAngleX += -((float)Math.PI / 5F);
            rightarm.rotateAngleX += -((float)Math.PI / 5F);
            leftleg.rotateAngleX += -((float)Math.PI * 2F / 5F);
            leftleg.rotateAngleY += -((float)Math.PI / 10F);
            rightleg.rotateAngleX += -((float)Math.PI * 2F / 5F);
            rightleg.rotateAngleY += ((float)Math.PI / 10F);
        }
        
        // Holding items:
        if(this.heldItemLeft != 0)
        	leftarm.rotateAngleX += leftarm.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemLeft;

        if(this.heldItemRight != 0)
        	rightarm.rotateAngleX += rightarm.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (float)this.heldItemRight;
    }
    
    
    // ==================================================
   	//                 Animate Model
   	// ==================================================
    @Override
    public void animate(EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	return;
    }
}
