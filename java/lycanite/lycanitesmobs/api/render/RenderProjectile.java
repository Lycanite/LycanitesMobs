package lycanite.lycanitesmobs.api.render;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityParticle;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileLaser;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectile extends Render {
	private IIcon icon;
    private float scale;
    private int renderTime = 0;
    
    // Laser Box:
    protected ModelBase laserModel = new ModelBase() {};
    private ModelRenderer laserBox;
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public RenderProjectile() {
    	super();
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
    	if(this.renderTime++ > Integer.MAX_VALUE - 1) this.renderTime = 0;this.renderProjectile(entity, par2, par4, par6, par8, par9);
    	if(entity instanceof EntityProjectileLaser)
    		this.renderLaser((EntityProjectileLaser)entity, par2, par4, par6, par8, par9);
    }
    
    
    // ==================================================
    //                 Render Projectile
    // ==================================================
    public void renderProjectile(Entity entity, double par2, double par4, double par6, float par8, float par9) {
    	float scale = 1f;
    	try { scale = ((EntityProjectileBase)entity).getProjectileScale(); }
    	catch(Exception e) {}
    	
    	this.bindEntityTexture(entity);
    	GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F * scale, 0.5F * scale, 0.5F * scale);
        Tessellator tessellator = Tessellator.instance;
        
        this.renderTexture(tessellator, entity);
        //this.renderIcon(tessellator, icon);
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
    
    
    // ==================================================
    //                 Render Laser
    // ==================================================
    public void renderLaser(EntityProjectileLaser entity, double par2, double par4, double par6, float par8, float par9) {
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
    	float lasti = 0;
    	float laserSize = entity.getLength();
    	if(laserSize <= 0) return;
    	
    	// Render Laser Beam:
    	GL11.glPushMatrix();
        GL11.glTranslated(par2, par4, par6);
    	this.bindTexture(this.getLaserTexture(entity));
        
        // Rotation:
        float[] angles = entity.getBeamAngles();
        GL11.glRotatef(angles[1], 0, 1, 0);
        GL11.glRotatef(angles[3], 1, 0, 0);
    	
    	// Length:
        for(float i = 0; i <= laserSize - 1; ++i) {
                this.laserBox.render(factor);
                GL11.glTranslated(0, 0, 1);
                lasti = i;
        }
        lasti++;
        GL11.glScalef(((float)laserSize - lasti), 1, 1);
        this.laserBox.render(factor);
    	
        GL11.glPopMatrix();
    }
    
    private double func_110828_a(double par1, double par3, double par5) {
        return par1 + (par3 - par1) * par5;
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
    
    
    // ==================================================
    //                  Render Texture
    // ==================================================
    private void renderTexture(Tessellator tessellator, Entity entity) {
    	float uMin = 0;
        float uMax = 1;
        float vMin = 0;
        float vMax = 1;
        if(entity instanceof EntityProjectileBase) {
            EntityProjectileBase entityProjectile = (EntityProjectileBase)entity;
            if(entityProjectile.animationFrameMax > 0) {
                vMin = (float)entityProjectile.animationFrame / (float)entityProjectile.animationFrameMax;
                vMax = vMin + (1F / (float)entityProjectile.animationFrameMax);
            }
        }

        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glTranslated(-scale / 2, -scale / 2, -scale / 2);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV((double)(0.0F - f7), (double)(0.0F - f8), 0.0D, (double)uMin, (double)vMax);
        tessellator.addVertexWithUV((double)(f6 - f7), (double)(0.0F - f8), 0.0D, (double)uMax, (double)vMax);
        tessellator.addVertexWithUV((double)(f6 - f7), (double)(1.0F - f8), 0.0D, (double)uMax, (double)vMin);
        tessellator.addVertexWithUV((double)(0.0F - f7), (double)(1.0F - f8), 0.0D, (double)uMin, (double)vMin);
        tessellator.draw();
    }
    
    
    // ==================================================
    //                    Render Icon
    // ==================================================
    private void renderIcon(Tessellator par1Tessellator, IIcon par2Icon) {
        float f = par2Icon.getMinU();
        float f1 = par2Icon.getMaxU();
        float f2 = par2Icon.getMinV();
        float f3 = par2Icon.getMaxV();
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        par1Tessellator.startDrawingQuads();
        par1Tessellator.setNormal(0.0F, 1.0F, 0.0F);
        par1Tessellator.addVertexWithUV((double)(0.0F - f5), (double)(0.0F - f6), 0.0D, (double)f, (double)f3);
        par1Tessellator.addVertexWithUV((double)(f4 - f5), (double)(0.0F - f6), 0.0D, (double)f1, (double)f3);
        par1Tessellator.addVertexWithUV((double)(f4 - f5), (double)(f4 - f6), 0.0D, (double)f1, (double)f2);
        par1Tessellator.addVertexWithUV((double)(0.0F - f5), (double)(f4 - f6), 0.0D, (double)f, (double)f2);
        par1Tessellator.draw();
    }
}
