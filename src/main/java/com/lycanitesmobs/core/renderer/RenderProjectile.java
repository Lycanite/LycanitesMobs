package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityParticle;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectile extends Render {
    private float scale;
    private int renderTime = 0;
    Class projectileClass;
    
    // Laser Box:
    protected ModelBase laserModel = new ModelBase() {};
    private ModelRenderer laserBox;
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public RenderProjectile(RenderManager renderManager, Class projectileClass) {
    	super(renderManager);
        this.projectileClass = projectileClass;
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
    public void doRender(Entity entity, double x, double y, double z, float par8, float par9) {
    	if(this.renderTime++ > Integer.MAX_VALUE - 1)
            this.renderTime = 0;
        this.renderProjectile(entity, x, y, z, par8, par9);
    	if(entity instanceof EntityProjectileLaser)
    		this.renderLaser((EntityProjectileLaser)entity, x, y, z, par8, par9);
    }
    
    
    // ==================================================
    //                 Render Projectile
    // ==================================================
    public void renderProjectile(Entity entity, double x, double y, double z, float par8, float par9) {
    	double scale = 0.5d;
        if(entity instanceof EntityProjectileBase) {
            EntityProjectileBase entityProjectileBase = (EntityProjectileBase)entity;
            scale *= entityProjectileBase.getProjectileScale();
            y += entityProjectileBase.getTextureOffsetY();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(scale, scale, scale);

        this.bindTexture(this.getEntityTexture(entity));
        this.renderTexture(Tessellator.getInstance(), entity);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }


    // ==================================================
    //                  Render Texture
    // ==================================================
    private void renderTexture(Tessellator tessellator, Entity entity) {
        double minU = 0;
        double maxU = 1;
        double minV = 0;
        double maxV = 1;
        double textureWidth = 0.5D;
        double textureHeight = 0.5D;
        if(entity instanceof EntityProjectileBase) {
            EntityProjectileBase entityProjectile = (EntityProjectileBase)entity;
            if(entityProjectile.animationFrameMax > 0) {
                minV = (float)entityProjectile.animationFrame / (float)entityProjectile.animationFrameMax;
                maxV = minV + (1F / (float)entityProjectile.animationFrameMax);
            }
        }

        //GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        //GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-scale / 2, -scale / 2, -scale / 2);

        if(this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        vertexbuffer.pos(-textureWidth, -textureHeight + (textureHeight / 2), 0.0D)
                .tex(minU, maxV)
                .normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(textureWidth, -textureHeight + (textureHeight / 2), 0.0D)
                .tex(maxU, maxV)
                .normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(textureWidth, textureHeight + (textureHeight / 2), 0.0D)
                .tex(maxU, minV)
                .normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(-textureWidth, textureHeight + (textureHeight / 2), 0.0D)
                .tex(minU, minV)
                .normal(0.0F, 1.0F, 0.0F).endVertex();

        tessellator.draw();
    }
    
    
    // ==================================================
    //                 Render Laser
    // ==================================================
    public void renderLaser(EntityProjectileLaser entity, double x, double y, double z, float par8, float par9) {
    	float scale = entity.getLaserWidth();
    	
    	// Create Laser Model If Null:
    	if(this.laserBox == null) {
    		laserBox = new ModelRenderer(laserModel, 0, 0);
    		laserBox.addBox(-(scale / 2), -(scale / 2), 0, (int)scale, (int)scale, 16);
    		laserBox.rotationPointX = 0;
    		laserBox.rotationPointY = 0;
    		laserBox.rotationPointZ = 0;
    	}
        
    	float factor = (float)(1.0 / 16.0);
    	float lastSegment = 0;
    	float laserSize = entity.getLength();
    	if(laserSize <= 0)
            return;
    	
    	// Render Laser Beam:
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1, entity.getLaserAlpha());
        GlStateManager.translate(x, y, z);
    	this.bindTexture(this.getLaserTexture(entity));
        
        // Rotation:
        float[] angles = entity.getBeamAngles();
        GlStateManager.rotate(angles[1], 0, 1, 0);
        GlStateManager.rotate(angles[3], 1, 0, 0);
    	
    	// Length:
        for(float segment = 0; segment <= laserSize - 1; ++segment) {
                this.laserBox.render(factor);
                GlStateManager.translate(0, 0, 1);
                lastSegment = segment;
        }
        lastSegment++;
        GlStateManager.scale((laserSize - lastSegment), 1, 1);
        this.laserBox.render(factor);

        GlStateManager.popMatrix();
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
    	if(entity instanceof EntityProjectileBase)
    		return ((EntityProjectileBase)entity).getTexture();
    	else if(entity instanceof EntityParticle)
    		return ((EntityParticle)entity).getTexture();
    	return null;
    }

    // ========== Get Laser Texture ==========
    protected ResourceLocation getLaserTexture(EntityProjectileLaser entity) {
    	return entity.getBeamTexture();
    }
}
