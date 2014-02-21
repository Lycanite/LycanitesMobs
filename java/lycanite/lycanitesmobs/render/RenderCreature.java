package lycanite.lycanitesmobs.render;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.entity.EntityCreatureRideable;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCreature extends RenderLiving {

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public RenderCreature(String setEntityName) {
    	super(AssetManager.getModel(setEntityName), 0.5F);
    	this.setRenderPassModel(AssetManager.getModel(setEntityName));
    }
    
    
    // ==================================================
 	//                  Render Equipment
 	// ==================================================
    protected int shouldRenderPass(EntityLivingBase entity, int renderPass, float partialTick) {
    	if(!(entity instanceof EntityCreatureBase))
    		return -1;
    	EntityCreatureBase creature = (EntityCreatureBase)entity;
    	// Chest/Body Armor First:
    	if(renderPass == 0 && creature.getEquipmentName("chest") != null) {
			this.bindEquipmentTexture(entity, creature.getEquipmentName("chest"));
    		return 1;
    	}
    	// Saddle Second:
    	if(renderPass == 1 && creature instanceof EntityCreatureRideable)
    		if(((EntityCreatureRideable)creature).hasSaddle()) {
    			this.bindEquipmentTexture(entity, "Saddle");
    			return 1;
    		}
    	// Feet Third:
    	if(renderPass == 2 && creature.getEquipmentName("feet") != null) {
			this.bindEquipmentTexture(entity, creature.getEquipmentName("feet"));
    		return 1;
    	}
    	// Helm Fourth:
    	if(renderPass == 3 && creature.getEquipmentName("head") != null) {
			this.bindEquipmentTexture(entity, creature.getEquipmentName("head"));
    		return 1;
    	}
    	return -1;
    }
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
    @Override
    protected void bindEntityTexture(Entity entity) {
        this.bindTexture(this.getEntityTexture(entity));
    }
    
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
    	if(entity instanceof EntityCreatureBase)
    		return ((EntityCreatureBase)entity).getTexture();
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
    
    // ========== Bind ==========
    @Override
    protected void bindTexture(ResourceLocation texture) {
        this.renderManager.renderEngine.bindTexture(texture);
    }
    
    
    // ==================================================
  	//                     Effects
  	// ==================================================
    @Override
    protected void preRenderCallback(EntityLivingBase entity, float particleTickTime) {
        // No effects.
    }
}
