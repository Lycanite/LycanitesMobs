package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityParticle;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
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
public class RenderParticle extends Render {
    private float scale;
    private int renderTime = 0;
    public EntityParticle entityParticle;
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public RenderParticle(RenderManager renderManager, EntityParticle entityParticle) {
        super(renderManager);
        this.entityParticle = entityParticle;
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
    	if(this.renderTime++ > Integer.MAX_VALUE - 1) this.renderTime = 0;
    	this.renderParticle(entity, par2, par4, par6, par8, par9);
    }
    
    
    // ==================================================
    //                 Render Projectile
    // ==================================================
    public void renderParticle(Entity entity, double x, double y, double z, float par8, float par9) {
    	float scale = 1f;
    	try { scale = ((EntityProjectileBase)entity).getProjectileScale(); }
    	catch(Exception e) {}

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F * scale, 0.5F * scale, 0.5F * scale);

        this.bindTexture(this.getEntityTexture(this.entityParticle));
        this.renderTexture(Tessellator.getInstance(), entity);

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
    	if(entity instanceof EntityParticle)
    		return ((EntityParticle)entity).getTexture();
    	return null;
    }


    // ==================================================
    //                  Render Texture
    // ==================================================
    private void renderTexture(Tessellator tessellator, Entity entity) {
        double minU = 0;
        double maxU = 1;
        double minV = 0;
        double maxV = 1;
        float textureWidth = 0.00390625F;
        float textureHeight = 0.00390625F;
        if(entity instanceof EntityProjectileBase) {
            EntityProjectileBase entityProjectile = (EntityProjectileBase)entity;
            if(entityProjectile.animationFrameMax > 0) {
                minV = (float)entityProjectile.animationFrame / (float)entityProjectile.animationFrameMax;
                maxV = minV + (1F / (float)entityProjectile.animationFrameMax);
            }
        }
        double xCoord = 0;
        double yCoord = 0;
        double zLevel = 0;


        //GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-scale / 2, -scale / 2, -scale / 2);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);

        vertexbuffer.pos((xCoord + 0.0F), (yCoord + (float)maxV), zLevel)
                .tex((minU + 0) * textureWidth, (minV + maxV) * textureHeight).endVertex();

        vertexbuffer.pos((xCoord + (float)maxU), (yCoord + (float)maxV), zLevel)
                .tex((minU + maxU) * textureWidth, (minV + maxV) * textureHeight).endVertex();

        vertexbuffer.pos((xCoord + (float)maxU), (yCoord + 0.0F), zLevel)
                .tex((minU + maxU) * textureWidth, (minV + 0) * textureHeight).endVertex();

        vertexbuffer.pos((xCoord + 0.0F), (yCoord + 0.0F), zLevel)
                .tex((minU + 0) * textureWidth, (minV + 0) * textureHeight).endVertex();

        tessellator.draw();
    }
}
