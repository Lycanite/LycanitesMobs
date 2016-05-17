package lycanite.lycanitesmobs.mountainmobs.renderer;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustom;
import lycanite.lycanitesmobs.api.render.RenderCreature;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityYale;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerYaleWool implements LayerRenderer<EntityCreatureBase> {
    RenderCreature renderer;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerYaleWool(RenderCreature renderer) {
        this.renderer = renderer;
    }


    @Override
    public void doRenderLayer(EntityCreatureBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(entity != null && entity instanceof EntityYale) {
            if(!((EntityYale)entity).hasFur() || entity.isInvisible()) {
                return;
            }
        }

        if(this.renderer.getMainModel() instanceof ModelCustom)
            ((ModelCustom)this.renderer.getMainModel()).render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, this);
    }


    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

    public boolean canRenderPart(String partName) {
        return "fur".equals(partName);
    }
}
