package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.model.ModelCustom;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCreature extends RenderLiving<EntityCreatureBase> {
    private static final DynamicTexture textureBrightness = new DynamicTexture(16, 16);
	
	/** A color table for mobs that can be dyed or pet collars. Follows the same pattern as the vanilla sheep. */
	public static final float[][] colorTable = new float[][] {{1.0F, 1.0F, 1.0F}, {0.85F, 0.5F, 0.2F}, {0.7F, 0.3F, 0.85F}, {0.4F, 0.6F, 0.85F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.5F, 0.65F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.5F, 0.6F}, {0.5F, 0.25F, 0.7F}, {0.2F, 0.3F, 0.7F}, {0.4F, 0.3F, 0.2F}, {0.4F, 0.5F, 0.2F}, {0.6F, 0.2F, 0.2F}, {0.1F, 0.1F, 0.1F}};

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public RenderCreature(String entityID, RenderManager renderManager) {
    	super(renderManager, AssetManager.getModel(entityID), 0.5F);
        if(this.mainModel instanceof ModelCustom) {
            ModelCustom modelCustom = (ModelCustom)this.mainModel;
            modelCustom.addCustomLayers(this);
        }
    }
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
    @Override
    protected boolean bindEntityTexture(EntityCreatureBase entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityCreatureBase entity) {
    	if(entity instanceof EntityCreatureBase)
    		return entity.getTexture();
        return null;
    }
    
    // ========== Equipment ==========
    protected void bindEquipmentTexture(Entity entity, String equipmentName) {
        this.bindTexture(this.getEquipmentTexture(entity, equipmentName));
    }
    
    protected ResourceLocation getEquipmentTexture(Entity entity, String equipmentName) {
    	if(entity instanceof EntityCreatureBase)
    		return ((EntityCreatureBase)entity).getEquipmentTexture(equipmentName);
        return null;
    }
    
    
    // ==================================================
  	//                     Effects
  	// ==================================================
    @Override
    protected void preRenderCallback(EntityCreatureBase entity, float particleTickTime) {
        // No effects.
    }
    
    /** If true, display the name of the entity above it. **/
    @Override
    protected boolean canRenderName(EntityCreatureBase entity) {
        if(!Minecraft.isGuiEnabled()) return false;
    	if(entity == this.renderManager.renderViewEntity) return false;
    	if(entity.isInvisibleToPlayer(Minecraft.getMinecraft().player)) return false;
    	if(entity.getControllingPassenger() != null) return false;
    	
    	if(entity.getAlwaysRenderNameTagForRender()) {
    		if(entity instanceof EntityCreatureTameable)
    			if(((EntityCreatureTameable)entity).isTamed())
    				return entity == this.renderManager.pointedEntity;
    		return true;
    	}
    	
    	return entity.hasCustomName() && entity == this.renderManager.pointedEntity;
    }
    
    
    // ==================================================
  	//                     Tools
  	// ==================================================
    /**
    * Returns a rotation angle that is inbetween two other rotation angles. par1 and par2 are the angles between which
    * to interpolate, par3 is probably a float between 0.0 and 1.0 that tells us where "between" the two angles we are.
    * Example: par1 = 30, par2 = 50, par3 = 0.5, then return = 40
    */
	public float interpolateRotation(float par1, float par2, float par3) {
		float f3;

		for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F) {}

		while (f3 >= 180.0F) {
			f3 -= 360.0F;
		}

		return par1 + par3 * f3;
	}
}
