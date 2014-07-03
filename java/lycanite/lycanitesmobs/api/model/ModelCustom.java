package lycanite.lycanitesmobs.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        
        // Create Animator:
        //animator = new Animator(this);
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
    	//animator.update((IAnimatedEntity)entity);
    	setAngles((EntityLiving)entity, time, distance, loop, lookY, lookX, scale);
    	animate((EntityLiving)entity, time, distance, loop, lookY, lookX, scale);
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
