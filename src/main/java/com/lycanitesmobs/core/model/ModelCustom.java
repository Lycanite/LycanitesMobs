package com.lycanitesmobs.core.model;

import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.renderer.LayerEquipment;
import com.lycanitesmobs.core.renderer.LayerSaddle;
import com.lycanitesmobs.core.renderer.RenderCreature;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public class ModelCustom extends ModelBase {
	
	// Initial Rotations:
	public Map<ModelRenderer, float[]> initRotations = new HashMap<ModelRenderer, float[]>();
    
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCustom() {
        this(1.0F);
    }
    
    public ModelCustom(float shadowSize) {
    	// Texture:
    	textureWidth = 128;
        textureHeight = 128;
    }


    // ==================================================
    //             Add Custom Render Layers
    // ==================================================
    public void addCustomLayers(RenderCreature renderer) {
        renderer.addLayer(new LayerEquipment(renderer, "chest"));
        renderer.addLayer(new LayerSaddle(renderer));
    }
    
    
    // ==================================================
   	//                  Set Rotation
   	// ==================================================
    public void setRotation(ModelRenderer model, float x, float y, float z) {
    	model.rotateAngleX = x;
      	model.rotateAngleY = y;
      	model.rotateAngleZ = z;
      	
      	if(!initRotations.containsKey(model)) initRotations.put(model, new float[] {x, y, z});
    }
    
    
    // ==================================================
   	//                  Render Model
   	// ==================================================
    @Override
    public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
        this.render(entity, time, distance, loop, lookY, lookX, scale, null);
    }

    public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale, LayerBase layer) {
        float sizeScale = 1F;
		if(entity instanceof EntityCreatureBase) {
            sizeScale *= ((EntityCreatureBase) entity).getRenderScale();
        }
    	GL11.glScalef(sizeScale, sizeScale, sizeScale);
    	GL11.glTranslatef(0, 0.5f - sizeScale / 2, 0);
    	
    	setAngles((EntityLiving)entity, time, distance, loop, lookY, lookX, scale);
    	animate((EntityLiving)entity, time, distance, loop, lookY, lookX, scale);
    }


    // ==================================================
    //                Can Render Part
    // ==================================================
    /** Returns true if the part can be rendered, this can do various checks such as Yale wool only rendering in the YaleWoolLayer or hiding body parts in place of armor parts, etc. **/
    public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
        if(layer == null)
            return this.canBaseRenderPart(partName, entity, trophy);
        if(entity instanceof EntityCreatureBase)
            return layer.canRenderPart(partName, (EntityCreatureBase)entity, trophy);
        return false;
    }

    /** Returns true if the part can be rendered on the base layer. **/
    public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
        return true;
    }


    // ==================================================
    //                Get Part Color
    // ==================================================
    /** Returns the coloring to be used for this part and layer. **/
    public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
        if(layer == null || !(entity instanceof EntityCreatureBase))
            return this.getBasePartColor(partName, entity, trophy, loop);
        return layer.getPartColor(partName, (EntityCreatureBase)entity, trophy);
    }

    /** Returns the coloring to be used for this part on the base layer. **/
    public Vector4f getBasePartColor(String partName, Entity entity, boolean trophy, float loop) {
        return new Vector4f(1, 1, 1, 1);
    }


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	public Vector2f getPartTextureOffset(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		if(layer == null || !(entity instanceof EntityCreatureBase))
			return this.getBaseTextureOffset(partName, entity, trophy, loop);
		return layer.getTextureOffset(partName, (EntityCreatureBase)entity, trophy, loop);
	}

	/** Returns the coloring to be used for this part on the base layer. **/
	public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
		return new Vector2f(0, 0);
	}
    
    
    // ==================================================
   	//                   Set Angles
   	// ==================================================
    public void setAngles(EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	// Set Initial Rotations:
    	for(Entry<ModelRenderer, float[]> initRotation : initRotations.entrySet()) {
    		float[] rotations = initRotation.getValue();
    		setRotation(initRotation.getKey(), rotations[0], rotations[1], rotations[2]);
    	}
    }
    
    
    // ==================================================
   	//                 Animate Model
   	// ==================================================
    public void animate(EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	return;
    }
}
