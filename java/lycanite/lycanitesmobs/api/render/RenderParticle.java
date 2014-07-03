package lycanite.lycanitesmobs.api.render;

import lycanite.lycanitesmobs.api.entity.EntityParticle;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
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
public class RenderParticle extends Render {
	private IIcon icon;
	public ResourceLocation texture;
    private float scale;
    private int renderTime = 0;
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public RenderParticle() {
    	super();
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
    	System.out.println("Rendering particle!");
    	if(this.renderTime++ > Integer.MAX_VALUE - 1) this.renderTime = 0;
    	this.renderParticle(entity, par2, par4, par6, par8, par9);
    }
    
    
    // ==================================================
    //                 Render Projectile
    // ==================================================
    public void renderParticle(Entity entity, double par2, double par4, double par6, float par8, float par9) {
    	float scale = 1f;
    	try { scale = ((EntityProjectileBase)entity).getProjectileScale(); }
    	catch(Exception e) {}
    	
    	this.bindEntityTexture(entity);
    	GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F * scale, 0.5F * scale, 0.5F * scale);
        Tessellator tessellator = Tessellator.instance;
        
        this.renderTexture(tessellator);
        //this.renderIcon(tessellator, icon);
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
    	if(entity instanceof EntityParticle)
    		this.texture = ((EntityParticle)entity).getTexture();
    	return texture;
    }
    
    
    // ==================================================
    //                  Render Texture
    // ==================================================
    private void renderTexture(Tessellator tessellator) {
    	float uMin = 0;
        float uMax = 1;
        float vMin = 0;
        float vMax = 1;
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
