package lycanite.lycanitesmobs.api.renderer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderNone extends Render {
    private float scale;
    private int renderTime = 0;
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public RenderNone(RenderManager renderManager) {
    	super(renderManager);
    }
    
    
    // ==================================================
    //                     Do Render
    // ==================================================
    @Override
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
    	return;
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    // ========== Get Texture ==========
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
    	return null;
    }
    
    
    // ==================================================
    //                  Render Texture
    // ==================================================
    private void renderTexture(Tessellator tessellator) {
    	return;
    }
}
