package lycanite.lycanitesmobs.api.render;

import lycanite.lycanitesmobs.api.entity.EntityParticle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderNone extends Render {
	private IIcon icon;
	public ResourceLocation texture;
    private float scale;
    private int renderTime = 0;
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public RenderNone() {
    	super();
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
    	if(entity instanceof EntityParticle)
    		this.texture = ((EntityParticle)entity).getTexture();
    	return texture;
    }
    
    
    // ==================================================
    //                  Render Texture
    // ==================================================
    private void renderTexture(Tessellator tessellator) {
    	return;
    }
    
    
    // ==================================================
    //                    Render Icon
    // ==================================================
    private void renderIcon(Tessellator par1Tessellator, IIcon par2Icon) {
        return;
    }
}
