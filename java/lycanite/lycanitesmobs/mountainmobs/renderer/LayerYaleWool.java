package lycanite.lycanitesmobs.mountainmobs.renderer;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.render.RenderCreature;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerYaleWool implements LayerRenderer<EntityCreatureBase> {
    RenderCreature renderer;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerYaleWool(RenderCreature renderer) {
        this.renderer = renderer;
    }


    @Override
    public void doRenderLayer(EntityCreatureBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        //TODO Render wool layer!
    }


    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
