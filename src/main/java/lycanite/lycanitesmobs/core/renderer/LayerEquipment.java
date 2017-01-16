package lycanite.lycanitesmobs.core.renderer;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerEquipment extends LayerBase {
    public String equipmentSlot;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerEquipment(RenderCreature renderer, String equipmentSlot) {
        super(renderer);
        this.equipmentSlot = equipmentSlot;
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(EntityCreatureBase entity, float scale) {
        if(!super.canRenderLayer(entity, scale) || this.equipmentSlot == null)
            return false;
        return entity.getEquipmentName(this.equipmentSlot) != null;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
        return entity.getEquipmentTexture(entity.getEquipmentName(this.equipmentSlot));
    }
}
