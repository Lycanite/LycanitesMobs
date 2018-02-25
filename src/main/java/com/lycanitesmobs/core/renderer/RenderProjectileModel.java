package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.model.ModelCustom;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderProjectileModel extends Render {
	public ModelBase mainModel;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public RenderProjectileModel(String entityID, RenderManager renderManager) {
    	super(renderManager);
    	this.mainModel = AssetManager.getModel(entityID);
        if(this.mainModel instanceof ModelCustom) {
            //ModelCustom modelCustom = (ModelCustom)this.mainModel;
            //modelCustom.addCustomLayers(this);
        }
    }


	// ==================================================
	//                    Do Render
	// ==================================================
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		boolean shouldSit = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		this.mainModel.isRiding = shouldSit;

		try {
			GlStateManager.enableAlpha();
			if (!this.bindEntityTexture(entity)) {
				return;
			}
			GlStateManager.translate((float)x, (float)y - 1, (float)z);
			GlStateManager.rotate(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
			this.mainModel.render(entity, 0, 0, partialTicks, 0, 0, 1);
			GlStateManager.depthMask(true);
			GlStateManager.disableRescaleNormal();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
	@Override
    protected boolean bindEntityTexture(Entity entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
    	if(entity instanceof EntityProjectileBase) {
			return ((EntityProjectileBase)entity).getTexture();
		}
		return null;
    }
}
