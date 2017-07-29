package lycanite.lycanitesmobs.saltwatermobs.renderer;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.model.ModelCustom;
import lycanite.lycanitesmobs.core.renderer.LayerBase;
import lycanite.lycanitesmobs.core.renderer.RenderCreature;
import lycanite.lycanitesmobs.saltwatermobs.entity.EntityRoa;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerRoa extends LayerBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerRoa(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(EntityCreatureBase entity, float scale) {
        if(entity instanceof EntityRoa) {
            EntityRoa entityRoa = (EntityRoa)entity;
            return entityRoa.canWhirlpool();
        }
        return false;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public boolean canRenderPart(String partName, EntityCreatureBase entity, boolean trophy) {
        return "effect".equals(partName);
    }

    @Override
    public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1); // TODO Alpha not working.
    }

    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
        return entity.getSubTexture("effect");
    }
}
